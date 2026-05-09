package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Punto de entrada y controlador principal de la interfaz gráfica.
 * Crea la ventana, conecta la fachada TheDOPOHardestGame y ejecuta
 * el game loop a 60 FPS en un hilo dedicado.
 *
 *  Flujo de conexión fachada ↔ GUI
 *  ─────────────────────────────────────────────────────────────────
 *  KeyEvent → keyPressed() → InputManager.procesarTecla()
 *           → ControlHumano.registrarDireccion()          [dominio]
 *
 *  run() cada ~16 ms:
 *    juego.actualizarJuego(delta)                         [dominio]
 *      ├─ j.mover( controlJugador.decidirMovimiento() )
 *      ├─ nivel.actualizar(delta)  ← enemigos se mueven
 *      └─ motor.procesarInteracciones() ← colisiones
 *    SwingUtilities.invokeLater → actualizarVista()
 *      ├─ panelJuego.actualizarPantalla() → repaint()
 *      └─ panelControl.actualizarEstado(tiempo, muertes, ...)
 */
public class TheDOPOHardestGameGUI implements Runnable, KeyListener {
    private JFrame frame;
    private GamePanel panelJuego;
    private ControlPanel panelControl;
    private TheDOPOHardestGame juego;
    private List<InputManager> inputManagers;
    private Thread hilo;
    private GestorSonido gestorSonido;
    private volatile boolean corriendo;

    public TheDOPOHardestGameGUI() {
        juego = new TheDOPOHardestGame();
        inputManagers = new ArrayList<>();
        gestorSonido = new GestorSonido();
        configurarVentana();
    }

    private void configurarVentana() {
        frame = new JFrame("The DOPO Hardest Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        panelJuego   = new GamePanel(juego, 1);
        panelControl = new ControlPanel(juego);

        frame.add(panelJuego,   BorderLayout.CENTER);
        frame.add(panelControl, BorderLayout.SOUTH);
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    // ── Métodos de navegación ────────────────────────────────────────────────
    public void mostrarMenuInicial()          { iniciarJuego(); }
    public void mostrarSeleccionModo()        {}
    public void mostrarSeleccionPersonaje()   {}
    public void mostrarSeleccionConfiguracion() {}

    public void iniciarJuego() {
        juego.iniciar(ModoJuego.UN_JUGADOR);

        inputManagers.clear();
        List<ControlHumano> controles = juego.getControles();
        // Jugador 1: WASD
        inputManagers.add(new InputManager(juego, controles.get(0),
                KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D));

        frame.setVisible(true);
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

    // ── Actualización de vista ───────────────────────────────────────────────
    public void actualizarVista() {
        panelJuego.actualizarPantalla();
        Nivel nivel = juego.getNivelActual();
        if (nivel == null) return;
        List<Integer> muertes = new ArrayList<>();
        for (Jugador j : nivel.getJugadores()) muertes.add(j.obtenerMuertes());
        panelControl.actualizarEstado(juego.obtenerTiempoRestante(), muertes, 0, 0);
    }

    public void mostrarPausa()                  { panelControl.mostrarMensaje("PAUSADO  —  ESC para continuar"); }
    public void mostrarVictoria(double tiempo)   { panelControl.mostrarMensaje(String.format("¡VICTORIA!  T: %.0fs restantes  |  R para reiniciar", tiempo)); }
    public void mostrarDerrota()                 { panelControl.mostrarMensaje("¡TIEMPO AGOTADO!  |  R para reiniciar"); }
    public void cerrarJuego()                    { corriendo = false; System.exit(0); }

    public void reiniciarNivel() { juego.reiniciar(); }

    // ── Teclado ──────────────────────────────────────────────────────────────
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            if (juego.getEstado() == EstadoJuego.JUGANDO)  juego.pausar();
            else if (juego.getEstado() == EstadoJuego.PAUSADO) juego.reanudar();
        } else if (code == KeyEvent.VK_R && juego.getEstado() != EstadoJuego.JUGANDO) {
            reiniciarNivel();
        }
        for (InputManager im : inputManagers) im.procesarTecla(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (InputManager im : inputManagers) im.procesarTeclaLiberada(e);
    }

    @Override public void keyTyped(KeyEvent e) {}

    public void guardarPartida() {
        new GestorArchivos().guardarPartida(juego, "partida.dopo");
    }

    // ── Punto de entrada ─────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TheDOPOHardestGameGUI().mostrarMenuInicial());
    }
}
