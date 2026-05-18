package test;

import domain.common.GestorArchivos;
import domain.common.TheDopoHardestGameException;
import domain.core.EstadoJuego;
import domain.core.ModoJuego;
import domain.core.TheDOPOHardestGame;
import domain.skins.Blinky;
import domain.skins.Clyde;
import domain.skins.ColorJuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TheDOPOHardestGameTest {

    private static final String NIVEL1 = "resources/configuraciones/nivel1.txt";
    private static final String NIVEL2 = "resources/configuraciones/nivel2.txt";
    private static final String NIVEL3 = "resources/configuraciones/nivel3.txt";

    private TheDOPOHardestGame juego;

    @BeforeEach
    void setUp() { juego = new TheDOPOHardestGame(); }

    // ── Estado inicial ────────────────────────────────────────────────────────

    @Test
    void estadoInicialEsMenu() {
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void nivelIdSinIniciarEsInterrogacion() {
        assertEquals("?", juego.obtenerNivelId());
    }

    @Test
    void tiempoRestanteSinIniciarEsCero() {
        assertEquals(0.0, juego.obtenerTiempoRestante(), 0.001);
    }

    // ── iniciar() ─────────────────────────────────────────────────────────────

    @Test
    void iniciarModoPlayerCreaUnJugadorHumano() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
        assertEquals("1", juego.obtenerNivelId());
        assertEquals(1, juego.getNivelActual().getJugadores().size());
        assertEquals(1, juego.getControles().size());
        assertEquals(ModoJuego.PLAYER, juego.getModo());
    }

    @Test
    void iniciarModoPvPCreaDosJugadoresHumanos() {
        juego.iniciar(ModoJuego.PvsP, NIVEL1,
                List.of(new Blinky(), new Clyde()), List.of(ColorJuego.NEGRO, ColorJuego.BLANCO));
        assertEquals(2, juego.getNivelActual().getJugadores().size());
        assertEquals(2, juego.getControles().size());
    }

    @Test
    void iniciarModoPvMCreaHumanoYMaquina() {
        juego.iniciar(ModoJuego.PvsM, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        assertEquals(2, juego.getNivelActual().getJugadores().size());
        assertEquals(1, juego.getControles().size()); // solo el humano
    }

    @Test
    void iniciarConSkinSinColorUsaColorPorDefecto() {
        // coloresBorde vacío → usa NEGRO para player 0 por defecto
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of());
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
    }

    @Test
    void iniciarConMenosSkinQueJugadoresUsaBlinkyPorDefecto() {
        // PvsP con solo 1 skin → player 1 usa Blinky por defecto
        juego.iniciar(ModoJuego.PvsP, NIVEL1, List.of(new Blinky()), List.of());
        assertEquals(2, juego.getNivelActual().getJugadores().size());
    }

    @Test
    void iniciarConModoNullLanzaExcepcion() {
        assertThrows(TheDopoHardestGameException.class, () ->
                juego.iniciar(null, NIVEL1, List.of(new Blinky()), List.of()));
    }

    @Test
    void iniciarConSkinsNullLanzaExcepcion() {
        assertThrows(TheDopoHardestGameException.class, () ->
                juego.iniciar(ModoJuego.PLAYER, NIVEL1, null, List.of()));
    }

    @Test
    void iniciarConSkinsVaciaLanzaExcepcion() {
        assertThrows(TheDopoHardestGameException.class, () ->
                juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(), List.of()));
    }

    // ── actualizarJuego() ─────────────────────────────────────────────────────

    @Test
    void actualizarCuandoNoEstaJugandoNoHaceNada() {
        assertDoesNotThrow(() -> juego.actualizarJuego(0.016));
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void actualizarNormalmenteNoLanzaExcepcion() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        assertDoesNotThrow(() -> juego.actualizarJuego(0.016));
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
    }

    @Test
    void actualizarSuperandoTiempoCambiaADerrota() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        double limite = juego.getNivelActual().obtenerTiempoLimite();
        juego.actualizarJuego(limite + 1);
        assertEquals(EstadoJuego.DERROTA, juego.getEstado());
    }

    @Test
    void tiempoRestanteDecreceAlActualizar() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        double antes = juego.obtenerTiempoRestante();
        juego.actualizarJuego(1.0);
        assertTrue(juego.obtenerTiempoRestante() < antes);
    }

    // ── pausar / reanudar ─────────────────────────────────────────────────────

    @Test
    void pausarCambiaAPausado() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.pausar();
        assertEquals(EstadoJuego.PAUSADO, juego.getEstado());
    }

    @Test
    void pausarFueraDeModoJugandoNoTieneEfecto() {
        juego.pausar(); // estado MENU
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void reanudarDesdePausadoVuelveAJugando() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.pausar();
        juego.reanudar();
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
    }

    @Test
    void reanudarFueraDePausadoNoTieneEfecto() {
        juego.reanudar(); // estado MENU
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void actualizarEstandoPausadoNoAvanzaTiempo() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.pausar();
        double antes = juego.obtenerTiempoRestante();
        juego.actualizarJuego(5.0);
        assertEquals(antes, juego.obtenerTiempoRestante(), 0.001);
    }

    // ── terminar / reiniciar ──────────────────────────────────────────────────

    @Test
    void terminarCambiaAMenu() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.terminar();
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void reiniciarRecargaElNivel() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.terminar();
        juego.reiniciar();
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
        assertEquals("1", juego.obtenerNivelId());
    }

    @Test
    void reiniciarSinHaberIniciadoNoFalla() {
        assertDoesNotThrow(() -> juego.reiniciar());
    }

    // ── avanzarNivel ──────────────────────────────────────────────────────────

    @Test
    void avanzarDesdeNivel1CargaNivel2() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.avanzarNivel();
        assertEquals("2", juego.obtenerNivelId());
    }

    @Test
    void avanzarDesdeNivel2CargaNivel3() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL2, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.avanzarNivel();
        assertEquals("3", juego.obtenerNivelId());
    }

    @Test
    void avanzarDesdeNivel3VuelveANivel1() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL3, List.of(new Blinky()), List.of(ColorJuego.NEGRO));
        juego.avanzarNivel();
        assertEquals("1", juego.obtenerNivelId());
    }

    @Test
    void avanzarSinHaberIniciadoNoFalla() {
        assertDoesNotThrow(() -> juego.avanzarNivel());
    }

    // ── GestorArchivos (stubs) ────────────────────────────────────────────────

    @Test
    void gestorArchivoGuardarEsNoOp() {
        assertDoesNotThrow(() -> new GestorArchivos().guardarPartida(juego, "test.dat"));
    }

    @Test
    void gestorArchivoCargarRetornaNull() {
        assertNull(new GestorArchivos().cargarPartida("test.dat"));
    }
}
