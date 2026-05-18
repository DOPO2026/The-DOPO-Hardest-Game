package domain.core;

import domain.player.ControlHumano;
import domain.player.ControlJugador;
import domain.player.Jugador;
import domain.ai.MaquinaAleatoria;
import domain.skins.Blinky;
import domain.skins.ColorJuego;
import domain.skins.Skin;
import domain.world.Zona;
import domain.world.ZonaInicial;

import java.util.ArrayList;
import java.util.List;

public class TheDOPOHardestGame {
    private ModoJuego modo;
    private EstadoJuego estado;
    private double tiempoTranscurrido;
    private Nivel nivelActual;
    private MotorJuego motor;
    private List<ControlHumano> controles;
    private String rutaNivelActual;
    private List<Skin> skinsConfiguradas;
    private List<ColorJuego> coloresConfigurados;

    public TheDOPOHardestGame() {
        motor               = new MotorJuego();
        controles           = new ArrayList<>();
        estado              = EstadoJuego.MENU;
        modo                = ModoJuego.PLAYER;
        skinsConfiguradas   = new ArrayList<>();
        coloresConfigurados = new ArrayList<>();
    }

    /**
     * Inicia una partida cargando el nivel desde archivo y configurando los jugadores
     * de acuerdo al modo: PLAYER (1 humano), PvsP (2 humanos), PvsM (1 humano + 1 IA).
     */
    public void iniciar(ModoJuego modo, String rutaNivel,
                        List<Skin> skins, List<ColorJuego> coloresBorde) {
        if (modo == null)        throw TheDopoHardestGameException.modoNoEspecificado();
        if (skins == null || skins.isEmpty()) {
            throw TheDopoHardestGameException.skinRequerida();
        }
        this.modo                = modo;
        this.rutaNivelActual     = rutaNivel;
        this.skinsConfiguradas   = new ArrayList<>(skins);
        this.coloresConfigurados = new ArrayList<>(coloresBorde);
        tiempoTranscurrido       = 0;

        nivelActual = new ConstructorNivel().construirDesdeArchivo(rutaNivel);
        spawnJugadores();

        estado = EstadoJuego.JUGANDO;
    }

    private void spawnJugadores() {
        controles.clear();
        List<ZonaInicial> inicios = new ArrayList<>();
        for (Zona z : nivelActual.getZonas()) {
            if (z instanceof ZonaInicial zi) inicios.add(zi);
        }
        if (inicios.isEmpty()) {
            throw TheDopoHardestGameException.nivelSinZonaInicial();
        }

        int cantidad = switch (modo) {
            case PLAYER -> 1;
            case PvsP, PvsM -> 2;
        };

        for (int i = 0; i < cantidad; i++) {
            Skin       skin   = skinsConfiguradas.size()   > i ? skinsConfiguradas.get(i)   : new Blinky();
            ColorJuego color  = coloresConfigurados.size() > i ? coloresConfigurados.get(i) : (i == 0 ? ColorJuego.NEGRO : ColorJuego.BLANCO);
            // En multijugador usa zonas opuestas (primera y última inicial).
            ZonaInicial zona  = inicios.get(Math.min(i, inicios.size() - 1));
            if (modo != ModoJuego.PLAYER && inicios.size() > 1 && i == 1) zona = inicios.get(inicios.size() - 1);

            int tam = 18;
            int spawnX = zona.obtenerPosX() + (zona.obtenerAncho() - tam) / 2;
            int spawnY = zona.obtenerPosY() + (zona.obtenerAlto()  - tam) / 2;

            ControlJugador control;
            if (modo == ModoJuego.PvsM && i == 1) {
                control = new MaquinaAleatoria();
            } else {
                ControlHumano humano = new ControlHumano();
                controles.add(humano);
                control = humano;
            }
            Jugador j = new Jugador(spawnX, spawnY, tam, tam, control, skin, color);
            nivelActual.agregarJugador(j);
        }
    }

    public void actualizarJuego(double deltaTime) {
        if (estado != EstadoJuego.JUGANDO) return;
        tiempoTranscurrido += deltaTime;

        for (Jugador j : nivelActual.getJugadores()) {
            j.mover(j.getControl().decidirMovimiento(nivelActual), nivelActual);
        }

        nivelActual.actualizar(deltaTime);
        motor.procesarInteracciones(nivelActual);

        if (motor.evaluarEstado(nivelActual))                        estado = EstadoJuego.VICTORIA;
        if (tiempoTranscurrido >= nivelActual.obtenerTiempoLimite()) estado = EstadoJuego.DERROTA;
    }

    public void pausar()   { if (estado == EstadoJuego.JUGANDO)  estado = EstadoJuego.PAUSADO;  }
    public void reanudar() { if (estado == EstadoJuego.PAUSADO)  estado = EstadoJuego.JUGANDO;  }

    public void reiniciar() {
        if (rutaNivelActual != null) {
            iniciar(modo, rutaNivelActual, skinsConfiguradas, coloresConfigurados);
        }
    }

    /** Carga el siguiente nivel en la rotación; tras el último vuelve al primero. */
    public void avanzarNivel() {
        if (rutaNivelActual != null) {
            iniciar(modo, siguienteRuta(rutaNivelActual), skinsConfiguradas, coloresConfigurados);
        }
    }

    private static String siguienteRuta(String ruta) {
        String base = "resources/configuraciones/";
        if (ruta.endsWith("nivel1.txt")) return base + "nivel2.txt";
        if (ruta.endsWith("nivel2.txt")) return base + "nivel3.txt";
        return base + "nivel1.txt";
    }

    public void terminar() { estado = EstadoJuego.MENU; }

    public String obtenerNivelId()       { return nivelActual == null ? "?" : nivelActual.obtenerId(); }
    public double obtenerTiempoRestante() {
        if (nivelActual == null) return 0;
        return Math.max(0, nivelActual.obtenerTiempoLimite() - tiempoTranscurrido);
    }
    public ModoJuego           getModo()         { return modo; }
    public EstadoJuego         getEstado()       { return estado; }
    public Nivel               getNivelActual()  { return nivelActual; }
    public List<ControlHumano> getControles()    { return controles; }
}
