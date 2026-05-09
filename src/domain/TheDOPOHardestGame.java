package domain;

import java.util.ArrayList;
import java.util.List;

public class TheDOPOHardestGame {
    private ModoJuego modo;
    private EstadoJuego estado;
    private double tiempoTranscurrido;
    private Nivel nivelActual;
    private MotorJuego motor;
    private List<ControlHumano> controles;

    public TheDOPOHardestGame() {
        motor = new MotorJuego();
        controles = new ArrayList<>();
        estado = EstadoJuego.MENU;
        modo = ModoJuego.UN_JUGADOR;
    }

    /** Inicializa y arranca una partida nueva en el modo indicado. */
    public void iniciar(ModoJuego modo) {
        this.modo = modo;
        tiempoTranscurrido = 0;
        nivelActual = construirNivelDemo(modo);
        estado = EstadoJuego.JUGANDO;
    }

    // ── Nivel de demostración hardcoded ──────────────────────────────────────
    private Nivel construirNivelDemo(ModoJuego modo) {
        Nivel nivel = new Nivel("demo", 800, 600, 90);
        controles.clear();

        ControlHumano ctrl1 = new ControlHumano();
        controles.add(ctrl1);
        nivel.agregarJugador(new Jugador(40, 275, 30, 30, ctrl1));

        if (modo == ModoJuego.DOS_JUGADORES) {
            ControlHumano ctrl2 = new ControlHumano();
            controles.add(ctrl2);
            nivel.agregarJugador(new Jugador(40, 325, 30, 30, ctrl2));
        }

        // Bordes del nivel
        nivel.agregarPared(new Pared(0,   0,   800, 20));
        nivel.agregarPared(new Pared(0,   580, 800, 20));
        nivel.agregarPared(new Pared(0,   0,   20,  600));
        nivel.agregarPared(new Pared(780, 0,   20,  600));
        // Paredes internas que crean pasillos
        nivel.agregarPared(new Pared(200, 20,  20,  380));
        nivel.agregarPared(new Pared(400, 200, 20,  380));
        nivel.agregarPared(new Pared(600, 20,  20,  380));

        // Enemigos con patrullaje vertical
        nivel.agregarEnemigo(new Enemigo(260, 50,  20, 20, new DeslizadorVertical(30,  545, 4)));
        nivel.agregarEnemigo(new Enemigo(350, 120, 20, 20, new DeslizadorVertical(30,  545, 3)));
        nivel.agregarEnemigo(new Enemigo(460, 250, 20, 20, new DeslizadorVertical(215, 565, 5)));
        nivel.agregarEnemigo(new Enemigo(660, 50,  20, 20, new DeslizadorVertical(30,  545, 4)));

        // Zonas seguras
        nivel.agregarZona(new ZonaInicial(20,  20, 160, 560));
        nivel.agregarZona(new ZonaFinal(620, 20, 140, 560));

        return nivel;
    }

    // ── Ciclo de juego ───────────────────────────────────────────────────────
    public void actualizarJuego(double deltaTime) {
        if (estado != EstadoJuego.JUGANDO) return;
        tiempoTranscurrido += deltaTime;

        for (Jugador j : nivelActual.getJugadores()) {
            j.mover(j.getControl().decidirMovimiento(nivelActual));
        }

        nivelActual.actualizar(deltaTime);
        motor.procesarInteracciones(nivelActual);

        if (motor.evaluarEstado(nivelActual))                        estado = EstadoJuego.VICTORIA;
        if (tiempoTranscurrido >= nivelActual.obtenerTiempoLimite()) estado = EstadoJuego.DERROTA;
    }

    public void procesarMovimiento(Jugador jugador) {
        jugador.mover(jugador.getControl().decidirMovimiento(nivelActual));
    }

    public void pausar()   { if (estado == EstadoJuego.JUGANDO) estado = EstadoJuego.PAUSADO; }
    public void reanudar() { if (estado == EstadoJuego.PAUSADO) estado = EstadoJuego.JUGANDO; }

    public void reiniciar() {
        tiempoTranscurrido = 0;
        nivelActual.reset();
        for (Jugador j : nivelActual.getJugadores()) j.morir();
        estado = EstadoJuego.JUGANDO;
    }

    public void terminar() { estado = EstadoJuego.MENU; }

    // ── Getters para la capa de presentación ─────────────────────────────────
    public int    obtenerNivel()          { return 1; }
    public double obtenerTiempoRestante() {
        if (nivelActual == null) return 0;
        return Math.max(0, nivelActual.obtenerTiempoLimite() - tiempoTranscurrido);
    }
    public EstadoJuego         getEstado()      { return estado; }
    public Nivel               getNivelActual() { return nivelActual; }
    public List<ControlHumano> getControles()   { return controles; }
}
