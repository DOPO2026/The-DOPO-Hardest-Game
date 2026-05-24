package presentation;

import domain.core.EstadoJuego;
import domain.core.ModoJuego;
import domain.core.Nivel;
import domain.core.TheDOPOHardestGame;
import domain.player.ControlHumano;
import domain.player.Jugador;
import domain.skins.ColorJuego;
import domain.skins.Inky;
import domain.skins.Skin;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Punto de entrada y controlador principal de la interfaz gráfica.
 * Usa CardLayout a nivel de JFrame para alternar entre menú y juego.
 */
public class TheDOPOHardestGameGUI implements Runnable, KeyListener {
    private static final int ESCALA        = 1;
    private static final int ANCHO_VENTANA = 800;
    private static final int ALTO_VENTANA  = 600;

    private JFrame frame;
    private CardLayout layoutRaiz;
    private JPanel contenedor;

    private MenuManager menu;
    private BarraMenu barraMenu;
    private JPanel panelJuegoContainer;
    private GamePanel panelJuego;
    private ControlPanel panelControl;

    private static final String RUTA_MUSICA = "resources/audio/Snayk_-_Growing_On_Me_01.09.2007__mp3.pm_.wav";

    private TheDOPOHardestGame juego;
    private List<InputManager> inputManagers;
    private Thread hilo;
    private volatile boolean corriendo;
    private final GestorSonido audio = new GestorSonido();

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

        menu = new MenuManager(this::iniciarJuegoConfigurado, this::cargarPartidaGuardada);

        panelJuegoContainer = new JPanel(new BorderLayout());
        panelJuego   = new GamePanel(juego, ESCALA);
        panelControl = new ControlPanel(juego);
        panelJuegoContainer.add(panelJuego,   BorderLayout.CENTER);
        panelJuegoContainer.add(panelControl, BorderLayout.SOUTH);

        contenedor.add(menu,                "menu");
        contenedor.add(panelJuegoContainer, "juego");

        barraMenu = new BarraMenu(this::abrirPartidaDesdeArchivo,
                this::guardarPartidaComo, this::cerrarAplicacion, this::puedeGuardar);
        frame.setJMenuBar(barraMenu);

        frame.setContentPane(contenedor);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED)   keyPressed(e);
            else if (e.getID() == KeyEvent.KEY_RELEASED) keyReleased(e);
            return false;
        });
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
        audio.reproducir(RUTA_MUSICA);
    }

    private void iniciarJuegoConfigurado(SeleccionMenu sel) {
        List<Skin>       skins   = new ArrayList<>(Arrays.asList(sel.skinJ1));
        List<ColorJuego> colores = new ArrayList<>(Arrays.asList(sel.colorJ1));
        if (sel.modo != ModoJuego.PLAYER) {
            skins.add(sel.skinJ2 != null ? sel.skinJ2 : new Inky());
            colores.add(sel.colorJ2);
        }
        juego.iniciar(sel.modo, sel.rutaNivel, skins, colores, sel.maquinaExperta);
        ajustarLienzoYInputs();
        layoutRaiz.show(contenedor, "juego");
        arrancarLoopSiNecesario();
    }

    private void cargarPartidaGuardada() {
        JFileChooser selector = crearSelectorArchivo("Abrir partida");
        if (selector.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            cargarDesde(selector.getSelectedFile().getPath());
    }

    private void cargarDesde(String ruta) {
        try {
            juego.cargarPartida(ruta);
            ajustarLienzoYInputs();
            layoutRaiz.show(contenedor, "juego");
            arrancarLoopSiNecesario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "No se pudo cargar la partida:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Menú Archivo: abrir / guardar como / cerrar ──────────────────────────
    /** Hay algo que guardar mientras exista un nivel cargado fuera del menú. */
    private boolean puedeGuardar() {
        EstadoJuego e = juego.getEstado();
        return e == EstadoJuego.JUGANDO || e == EstadoJuego.PAUSADO || e == EstadoJuego.VICTORIA;
    }

    /** Permite elegir un archivo y guardar la partida actual en él. Pausa el juego
     *  mientras el diálogo está abierto para capturar un estado consistente. */
    private void guardarPartidaComo() {
        if (!puedeGuardar()) return;
        boolean estabaJugando = juego.getEstado() == EstadoJuego.JUGANDO;
        if (estabaJugando) juego.pausar();

        JFileChooser selector = crearSelectorArchivo("Guardar partida como");
        if (selector.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File destino = asegurarExtensionTxt(selector.getSelectedFile());
            try {
                juego.guardarPartida(destino.getPath());
                JOptionPane.showMessageDialog(frame, "Partida guardada en:\n" + destino.getPath(),
                        "Guardado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "No se pudo guardar la partida:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (estabaJugando) juego.reanudar();
    }

    /** Permite elegir un archivo guardado y continuar la partida desde ese punto. */
    private void abrirPartidaDesdeArchivo() {
        boolean estabaJugando = juego.getEstado() == EstadoJuego.JUGANDO;
        if (estabaJugando) juego.pausar();

        JFileChooser selector = crearSelectorArchivo("Abrir partida");
        if (selector.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            cargarDesde(selector.getSelectedFile().getPath());
        } else if (estabaJugando) {
            juego.reanudar();
        }
    }

    private void cerrarAplicacion() {
        int r = JOptionPane.showConfirmDialog(frame,
                "¿Cerrar el juego? Se perderá el progreso no guardado.",
                "Cerrar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) System.exit(0);
    }

    private JFileChooser crearSelectorArchivo(String titulo) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(titulo);
        selector.setFileFilter(new FileNameExtensionFilter("Partidas guardadas (*.txt)", "txt"));
        return selector;
    }

    private static File asegurarExtensionTxt(File f) {
        return f.getName().toLowerCase().endsWith(".txt")
                ? f : new File(f.getParentFile(), f.getName() + ".txt");
    }

    /** Ajusta el panel a las dimensiones del nivel y reconecta los InputManagers. */
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
                    case VICTORIA -> mostrarVictoria(juego.obtenerTiempoRestante());
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

    public void mostrarPausa() {
        panelControl.mostrarMensaje("PAUSADO  |  ESC: continuar  |  S: guardar  |  M: menú  |  Q: salir");
    }

    public void mostrarVictoria(double tiempo) {
        String ganador = juego.obtenerMensajeGanador();
        String base = ganador.isEmpty()
                ? String.format("¡VICTORIA!  T: %.0fs", tiempo)
                : String.format("¡VICTORIA!  %s  |  T: %.0fs", ganador, tiempo);
        panelControl.mostrarMensaje(base + "  |  N: siguiente nivel  |  R: reiniciar  |  M: menú");
    }

    public void mostrarDerrota() {
        String ganador = juego.obtenerMensajeGanador();
        String base = "¡TIEMPO AGOTADO!  |  R: reiniciar  |  M: menú";
        panelControl.mostrarMensaje(ganador.isEmpty() ? base : ganador + "  |  " + base);
    }

    public void reiniciarNivel()  { juego.reiniciar(); ajustarLienzoYInputs(); }
    public void siguienteNivel()  { juego.avanzarNivel(); ajustarLienzoYInputs(); }

    public void volverAlMenu() {
        juego.terminar();
        inputManagers.clear();
        layoutRaiz.show(contenedor, "menu");
        menu.mostrarPrincipal();
    }

    // ── Teclado ──────────────────────────────────────────────────────────────
    @Override
    public void keyPressed(KeyEvent e) {
        if (!frameEsVentanaActiva()) return;
        int code = e.getKeyCode();
        EstadoJuego estado = juego.getEstado();

        if (code == KeyEvent.VK_ESCAPE) {
            if (estado == EstadoJuego.JUGANDO)  juego.pausar();
            else if (estado == EstadoJuego.PAUSADO) juego.reanudar();
        } else if (code == KeyEvent.VK_R
                && estado != EstadoJuego.JUGANDO && estado != EstadoJuego.MENU) {
            reiniciarNivel();
        } else if ((code == KeyEvent.VK_N || code == KeyEvent.VK_ENTER)
                && estado == EstadoJuego.VICTORIA) {
            siguienteNivel();
        } else if (code == KeyEvent.VK_M && estado != EstadoJuego.MENU) {
            volverAlMenu();
        } else if (code == KeyEvent.VK_Q) {
            System.exit(0);
        } else if (code == KeyEvent.VK_S
                && (estado == EstadoJuego.PAUSADO || estado == EstadoJuego.VICTORIA)) {
            guardarPartidaComo();
        }

        for (InputManager im : inputManagers) im.procesarTecla(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!frameEsVentanaActiva()) return;
        for (InputManager im : inputManagers) im.procesarTeclaLiberada(e);
    }

    @Override public void keyTyped(KeyEvent e) {}

    /** Las teclas globales solo aplican cuando la ventana principal está activa;
     *  evita que un diálogo modal (selector de archivo, confirmación) dispare
     *  atajos como Q (salir) o M (menú) al teclear. */
    private boolean frameEsVentanaActiva() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() == frame;
    }

    // ── Punto de entrada ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TheDOPOHardestGameGUI().mostrarMenuInicial());
    }
}
