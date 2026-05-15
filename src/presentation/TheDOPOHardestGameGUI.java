package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Punto de entrada y controlador principal de la interfaz gráfica.
 * Usa CardLayout a nivel de JFrame para alternar entre menú y juego.
 */
public class TheDOPOHardestGameGUI implements Runnable, KeyListener {
    private static final int ESCALA = 1;
    private static final int ANCHO_VENTANA = 800;
    private static final int ALTO_VENTANA  = 600;
    private static final int DELAY_AVANCE_MS = 2200;

    private JFrame frame;
    private CardLayout layoutRaiz;
    private JPanel contenedor;

    private MenuManager menu;
    private JPanel panelJuegoContainer;
    private GamePanel panelJuego;
    private ControlPanel panelControl;

    private TheDOPOHardestGame juego;
    private List<InputManager> inputManagers;
    private Thread hilo;
    private volatile boolean corriendo;
    private volatile boolean avanceProgramado;

    public TheDOPOHardestGameGUI() {
        juego = new TheDOPOHardestGame();
        inputManagers = new ArrayList<>();
        configurarVentana();
    }

    private void configurarVentana() {
        frame = new JFrame("The DOPO Hardest Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(ANCHO_VENTANA, ALTO_VENTANA));

        layoutRaiz = new CardLayout();
        contenedor = new JPanel(layoutRaiz);

        menu = new MenuManager(this::iniciarJuegoConfigurado);

        panelJuegoContainer = new JPanel(new BorderLayout());
        panelJuego   = new GamePanel(juego, ESCALA);
        panelControl = new ControlPanel(juego);
        panelJuegoContainer.add(panelJuego,   BorderLayout.CENTER);
        panelJuegoContainer.add(panelControl, BorderLayout.SOUTH);

        contenedor.add(menu,                "menu");
        contenedor.add(panelJuegoContainer, "juego");

        frame.setContentPane(contenedor);
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        layoutRaiz.show(contenedor, "menu");
    }

    // ── Inicio desde menú ────────────────────────────────────────────────────
    public void mostrarMenuInicial() {
        menu.mostrarPrincipal();
        layoutRaiz.show(contenedor, "menu");
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }

    private void iniciarJuegoConfigurado(SeleccionMenu sel) {
        List<Skin>       skins   = new ArrayList<>(Arrays.asList(sel.skinJ1));
        List<ColorJuego> colores = new ArrayList<>(Arrays.asList(sel.colorJ1));
        if (sel.modo != ModoJuego.PLAYER) {
            skins.add(sel.skinJ2 != null ? sel.skinJ2 : new Inky());
            colores.add(sel.colorJ2);
        }
        juego.iniciar(sel.modo, sel.rutaNivel, skins, colores);
        ajustarLienzoYInputs();

        layoutRaiz.show(contenedor, "juego");
        frame.requestFocusInWindow();
        arrancarLoopSiNecesario();
    }

    /** Ajusta el panel a las dimensiones del nivel y reconecta los InputManagers
     *  a las nuevas instancias de ControlHumano que crea spawnJugadores(). */
    private void ajustarLienzoYInputs() {
        Nivel nivel = juego.getNivelActual();
        panelJuego.ajustarTamanio(nivel.obtenerAncho(), nivel.obtenerAlto());
        frame.pack();
        frame.setLocationRelativeTo(null);

        inputManagers.clear();
        List<ControlHumano> controles = juego.getControles();
        if (!controles.isEmpty()) {
            inputManagers.add(new InputManager(juego, controles.get(0),
                    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D));
        }
        if (juego.getModo() == ModoJuego.PvsP && controles.size() > 1) {
            inputManagers.add(new InputManager(juego, controles.get(1),
                    KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT));
        }
    }

    private void arrancarLoopSiNecesario() {
        if (corriendo) return;
        corriendo = true;
        hilo = new Thread(this, "GameLoop");
        hilo.setDaemon(true);
        hilo.start();
    }

    // ── Game loop a 60 FPS ───────────────────────────────────────────────────
    @Override
    public void run() {
        final long TARGET_NS = 1_000_000_000L / 60;
        long lastTime = System.nanoTime();

        while (corriendo) {
            long now   = System.nanoTime();
            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime   = now;

            juego.actualizarJuego(delta);
            EstadoJuego estado = juego.getEstado();

            SwingUtilities.invokeLater(() -> {
                actualizarVista();
                switch (estado) {
                    case VICTORIA -> {
                        mostrarVictoria(juego.obtenerTiempoRestante());
                        programarAvanceNivel();
                    }
                    case DERROTA  -> mostrarDerrota();
                    case PAUSADO  -> mostrarPausa();
                    default       -> {}
                }
            });

            long sleep = TARGET_NS - (System.nanoTime() - now);
            if (sleep > 0) {
                try { Thread.sleep(sleep / 1_000_000, (int)(sleep % 1_000_000)); }
                catch (InterruptedException ignored) {}
            }
        }
    }

    /** Programa una transición al siguiente nivel tras una breve pausa para
     *  mostrar el cartel de VICTORIA. Reentrante-seguro vía bandera. */
    private void programarAvanceNivel() {
        if (avanceProgramado) return;
        avanceProgramado = true;
        Timer t = new Timer(DELAY_AVANCE_MS, ev -> {
            if (juego.getEstado() != EstadoJuego.VICTORIA) {
                avanceProgramado = false;
                return;
            }
            juego.avanzarNivel();
            ajustarLienzoYInputs();
            frame.requestFocusInWindow();
            avanceProgramado = false;
        });
        t.setRepeats(false);
        t.start();
    }

    public void actualizarVista() {
        panelJuego.actualizarPantalla();
        Nivel nivel = juego.getNivelActual();
        if (nivel == null) return;
        List<Integer> muertes = new ArrayList<>();
        List<Integer> vidas   = new ArrayList<>();
        for (Jugador j : nivel.getJugadores()) {
            muertes.add(j.obtenerMuertes());
            vidas.add(j.obtenerVidas());
        }
        panelControl.actualizarEstado(juego.obtenerTiempoRestante(), muertes, vidas,
                nivel.obtenerMonedasTotales() - nivel.obtenerMonedasPendientes(),
                nivel.obtenerMonedasTotales());
    }

    public void mostrarPausa()                  { panelControl.mostrarMensaje("PAUSADO  —  ESC para continuar"); }
    public void mostrarVictoria(double tiempo)  { panelControl.mostrarMensaje(String.format("¡VICTORIA!  T: %.0fs  |  cargando siguiente nivel...", tiempo)); }
    public void mostrarDerrota()                { panelControl.mostrarMensaje("¡TIEMPO AGOTADO!  |  R reinicia  |  M menú"); }

    public void reiniciarNivel() { juego.reiniciar(); ajustarLienzoYInputs(); }
    public void volverAlMenu() {
        juego.terminar();
        layoutRaiz.show(contenedor, "menu");
        menu.mostrarPrincipal();
        frame.requestFocusInWindow();
    }

    // ── Teclado ──────────────────────────────────────────────────────────────
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        EstadoJuego estado = juego.getEstado();
        if (code == KeyEvent.VK_ESCAPE) {
            if (estado == EstadoJuego.JUGANDO)   juego.pausar();
            else if (estado == EstadoJuego.PAUSADO) juego.reanudar();
        } else if (code == KeyEvent.VK_R && estado != EstadoJuego.JUGANDO && estado != EstadoJuego.MENU) {
            reiniciarNivel();
        } else if (code == KeyEvent.VK_M && estado != EstadoJuego.MENU) {
            volverAlMenu();
        }
        for (InputManager im : inputManagers) im.procesarTecla(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (InputManager im : inputManagers) im.procesarTeclaLiberada(e);
    }

    @Override public void keyTyped(KeyEvent e) {}

    // ── Punto de entrada ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TheDOPOHardestGameGUI().mostrarMenuInicial());
    }
}
