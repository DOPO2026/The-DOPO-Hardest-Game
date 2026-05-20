package domain.core;

import domain.player.ControlHumano;
import domain.player.ControlJugador;
import domain.player.Jugador;
import domain.ai.MaquinaAleatoria;
import domain.skins.Blinky;
import domain.skins.ColorJuego;
import domain.skins.Skin;
import domain.world.Zona;
import domain.world.ZonaFinal;
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
        List<ZonaFinal>   fines   = new ArrayList<>();
        for (Zona z : nivelActual.getZonas()) {
            if (z instanceof ZonaInicial zi) inicios.add(zi);
            if (z instanceof ZonaFinal   zf) fines.add(zf);
        }
        if (inicios.isEmpty()) {
            throw TheDopoHardestGameException.nivelSinZonaInicial();
        }

        int cantidad = switch (modo) {
            case PLAYER -> 1;
            case PvsP, PvsM -> 2;
        };

        for (int i = 0; i < cantidad; i++) {
            Skin       skin  = skinsConfiguradas.size()   > i ? skinsConfiguradas.get(i)   : new Blinky();
            ColorJuego color = coloresConfigurados.size() > i ? coloresConfigurados.get(i) : (i == 0 ? ColorJuego.NEGRO : ColorJuego.BLANCO);

            Zona zonaSpawn;
            if (i == 0) {
                zonaSpawn = inicios.get(0);
            } else if (inicios.size() > 1) {
                zonaSpawn = inicios.get(inicios.size() - 1);
            } else if (!fines.isEmpty()) {
                zonaSpawn = fines.get(0);
            } else {
                zonaSpawn = inicios.get(0);
            }

            int tam    = 18;
            int spawnX = zonaSpawn.obtenerPosX() + (zonaSpawn.obtenerAncho() - tam) / 2;
            int spawnY = zonaSpawn.obtenerPosY() + (zonaSpawn.obtenerAlto()  - tam) / 2;

            ControlJugador control;
            if (modo == ModoJuego.PvsM && i == 1) {
                control = new MaquinaAleatoria();
            } else {
                ControlHumano humano = new ControlHumano();
                controles.add(humano);
                control = humano;
            }
            nivelActual.agregarJugador(new Jugador(spawnX, spawnY, tam, tam, control, skin, color));
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

    public String obtenerMensajeGanador() {
        if (nivelActual == null || modo == ModoJuego.PLAYER) return "";
        List<Jugador> jugadores = nivelActual.getJugadores();
        if (jugadores.size() < 2) return "";
        int ganador = nivelActual.obtenerGanadorIndex();
        int c0 = nivelActual.obtenerMonedasDeJugador(0);
        int c1 = nivelActual.obtenerMonedasDeJugador(1);
        String marcador = String.format("J1: %d  J2: %d", c0, c1);
        if (ganador >= 0) return String.format("J%d GANA  |  %s", ganador + 1, marcador);
        if (c0 > c1)      return "J1 GANA (más monedas)  |  " + marcador;
        if (c1 > c0)      return "J2 GANA (más monedas)  |  " + marcador;
        int d0 = jugadores.get(0).obtenerMuertes(), d1 = jugadores.get(1).obtenerMuertes();
        if (d0 < d1)      return "J1 GANA (menos muertes)  |  " + marcador;
        if (d1 < d0)      return "J2 GANA (menos muertes)  |  " + marcador;
        return "EMPATE  |  " + marcador;
    }

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
