package domain.core;

import domain.collectibles.Moneda;
import domain.player.ControlHumano;
import domain.player.ControlJugador;
import domain.player.Jugador;
import domain.ai.MaquinaAleatoria;
import domain.ai.MaquinaExperta;
import domain.skins.Blinky;
import domain.skins.Clyde;
import domain.skins.ColorJuego;
import domain.skins.Inky;
import domain.skins.Skin;
import domain.world.Zona;
import domain.world.ZonaFinal;
import domain.world.ZonaInicial;

import java.io.*;
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
    private boolean maquinaExperta;

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
                        List<Skin> skins, List<ColorJuego> coloresBorde,
                        boolean maquinaExperta) {
        if (modo == null)        throw TheDopoHardestGameException.modoNoEspecificado();
        if (skins == null || skins.isEmpty()) {
            throw TheDopoHardestGameException.skinRequerida();
        }
        this.modo                = modo;
        this.rutaNivelActual     = rutaNivel;
        this.skinsConfiguradas   = new ArrayList<>(skins);
        this.coloresConfigurados = new ArrayList<>(coloresBorde);
        this.maquinaExperta      = maquinaExperta;
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
                control = maquinaExperta ? new MaquinaExperta() : new MaquinaAleatoria();
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
        if (rutaNivelActual != null)
            iniciar(modo, rutaNivelActual, skinsConfiguradas, coloresConfigurados, maquinaExperta);
    }

    /** Carga el siguiente nivel en la rotación; tras el último vuelve al primero. */
    public void avanzarNivel() {
        if (rutaNivelActual != null)
            iniciar(modo, siguienteRuta(rutaNivelActual), skinsConfiguradas, coloresConfigurados, maquinaExperta);
    }

    private static String siguienteRuta(String ruta) {
        String base = "resources/configuraciones/";
        if (ruta.endsWith("nivel1.txt")) return base + "nivel2.txt";
        if (ruta.endsWith("nivel2.txt")) return base + "nivel3.txt";
        return base + "nivel1.txt";
    }

    public void terminar() { estado = EstadoJuego.MENU; }

    public void guardarPartida(String ruta) {
        if (nivelActual == null) return;
        try {
            File f = new File(ruta);
            if (f.getParentFile() != null) f.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                pw.println("VERSION=1");
                pw.println("MODO=" + modo.name());
                pw.println("NIVEL=" + rutaNivelActual);
                pw.println("TIEMPO=" + tiempoTranscurrido);
                pw.println("MAQUINA_EXPERTA=" + maquinaExperta);
                List<Jugador> jugadores = nivelActual.getJugadores();
                pw.println("JUGADORES=" + jugadores.size());
                for (int i = 0; i < jugadores.size(); i++) {
                    pw.println("SKIN" + i + "=" + nombreSkin(skinsConfiguradas.size() > i ? skinsConfiguradas.get(i) : new Blinky()));
                    pw.println("COLOR" + i + "=" + (coloresConfigurados.size() > i ? coloresConfigurados.get(i).name() : "NEGRO"));
                    pw.println("J" + i + "_MUERTES=" + jugadores.get(i).obtenerMuertes());
                }
                List<Moneda> monedas = nivelActual.getMonedas();
                pw.println("MONEDAS=" + monedas.size());
                for (int i = 0; i < monedas.size(); i++) {
                    Moneda m = monedas.get(i);
                    pw.println("M" + i + "=" + (m.estaRecolectada() ? 1 : 0)
                            + "," + m.getColectorIndex()
                            + "," + (m.estaGuardadaEnCheckpoint() ? 1 : 0));
                }
                pw.println("CHECKPOINT=" + (nivelActual.estaCheckpointGuardado() ? 1 : 0));
                pw.println("CHECKPOINT_PENDIENTES=" + nivelActual.getMonedasPendientesEnCheckpoint());
            }
        } catch (IOException e) {
            throw TheDopoHardestGameException.errorGuardando(ruta);
        }
    }

    public void cargarPartida(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            java.util.Properties p = new java.util.Properties();
            br.lines().filter(l -> l.contains("=")).forEach(l -> {
                int idx = l.indexOf('=');
                p.setProperty(l.substring(0, idx).trim(), l.substring(idx + 1).trim());
            });
            ModoJuego modoGuardado    = ModoJuego.valueOf(p.getProperty("MODO", "PLAYER"));
            String nivelGuardado      = p.getProperty("NIVEL");
            double tiempoGuardado     = Double.parseDouble(p.getProperty("TIEMPO", "0"));
            boolean expertoGuardado   = "true".equalsIgnoreCase(p.getProperty("MAQUINA_EXPERTA", "false"));
            int nJugadores = Integer.parseInt(p.getProperty("JUGADORES", "1"));
            List<Skin> skins   = new ArrayList<>();
            List<ColorJuego> colores = new ArrayList<>();
            for (int i = 0; i < nJugadores; i++) {
                skins.add(crearSkin(p.getProperty("SKIN" + i, "Blinky")));
                colores.add(ColorJuego.valueOf(p.getProperty("COLOR" + i, "NEGRO")));
            }
            iniciar(modoGuardado, nivelGuardado, skins, colores, expertoGuardado);
            tiempoTranscurrido = tiempoGuardado;
            // Restaurar monedas
            List<Moneda> monedas = nivelActual.getMonedas();
            int nMonedas = Integer.parseInt(p.getProperty("MONEDAS", "0"));
            for (int i = 0; i < Math.min(nMonedas, monedas.size()); i++) {
                String[] parts = p.getProperty("M" + i, "0,-1,0").split(",");
                if ("1".equals(parts[0])) {
                    int colector = Integer.parseInt(parts[1]);
                    monedas.get(i).recolectarPor(colector);
                    nivelActual.registrarRecoleccion();
                    if (parts.length > 2 && "1".equals(parts[2])) monedas.get(i).guardarEnCheckpoint();
                }
            }
            // Restaurar checkpoint y muertes
            boolean ckpt = "1".equals(p.getProperty("CHECKPOINT", "0"));
            int ckptPendientes = Integer.parseInt(p.getProperty("CHECKPOINT_PENDIENTES", "0"));
            nivelActual.restaurarEstadoCheckpoint(ckpt, ckptPendientes);
            List<Jugador> jugadores = nivelActual.getJugadores();
            for (int i = 0; i < jugadores.size(); i++) {
                String key = "J" + i + "_MUERTES";
                if (p.containsKey(key)) jugadores.get(i).setMuertes(Integer.parseInt(p.getProperty(key)));
            }
        } catch (IOException e) {
            throw TheDopoHardestGameException.errorCargando(ruta);
        } catch (Exception e) {
            throw TheDopoHardestGameException.partidaCorrupta(e.getMessage());
        }
    }

    private static String nombreSkin(Skin s) {
        if (s instanceof Clyde) return "Clyde";
        if (s instanceof Inky)  return "Inky";
        return "Blinky";
    }

    private static Skin crearSkin(String nombre) {
        return switch (nombre) {
            case "Clyde" -> new Clyde();
            case "Inky"  -> new Inky();
            default      -> new Blinky();
        };
    }

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
