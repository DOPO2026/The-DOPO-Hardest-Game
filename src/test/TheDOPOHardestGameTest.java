package test;

import domain.core.GestorArchivos;
import domain.core.TheDopoHardestGameException;
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
    void deberiaEmpezarEnEstadoMenu() {
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void deberiaRetornarInterrogacionComoNivelIdSinIniciar() {
        assertEquals("?", juego.obtenerNivelId());
    }

    @Test
    void deberiaRetornarCeroComoTiempoRestanteSinIniciar() {
        assertEquals(0.0, juego.obtenerTiempoRestante(), 0.001);
    }

    // ── iniciar() ─────────────────────────────────────────────────────────────

    @Test
    void deberiaCrearUnJugadorHumanoEnModoPlayer() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
        assertEquals("1", juego.obtenerNivelId());
        assertEquals(1, juego.getNivelActual().getJugadores().size());
        assertEquals(1, juego.getControles().size());
        assertEquals(ModoJuego.PLAYER, juego.getModo());
    }

    @Test
    void deberiaCrearDosJugadoresHumanosEnModoPvP() {
        juego.iniciar(ModoJuego.PvsP, NIVEL1,
                List.of(new Blinky(), new Clyde()), List.of(ColorJuego.NEGRO, ColorJuego.BLANCO), false);
        assertEquals(2, juego.getNivelActual().getJugadores().size());
        assertEquals(2, juego.getControles().size());
    }

    @Test
    void deberiaCrearHumanoYMaquinaEnModoPvM() {
        juego.iniciar(ModoJuego.PvsM, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        assertEquals(2, juego.getNivelActual().getJugadores().size());
        assertEquals(1, juego.getControles().size()); // solo el humano
    }

    @Test
    void deberiaUsarColorPorDefectoCuandoFaltaColor() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(), false);
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
    }

    @Test
    void deberiaUsarBlinkyPorDefectoCuandoFaltanSkins() {
        juego.iniciar(ModoJuego.PvsP, NIVEL1, List.of(new Blinky()), List.of(), false);
        assertEquals(2, juego.getNivelActual().getJugadores().size());
    }

    @Test
    void noDeberiaIniciarConModoNull() {
        assertThrows(TheDopoHardestGameException.class, () ->
                juego.iniciar(null, NIVEL1, List.of(new Blinky()), List.of(), false));
    }

    @Test
    void noDeberiaIniciarConSkinsNull() {
        assertThrows(TheDopoHardestGameException.class, () ->
                juego.iniciar(ModoJuego.PLAYER, NIVEL1, null, List.of(), false));
    }

    @Test
    void noDeberiaIniciarConSkinsVacias() {
        assertThrows(TheDopoHardestGameException.class, () ->
                juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(), List.of(), false));
    }

    // ── actualizarJuego() ─────────────────────────────────────────────────────

    @Test
    void noDeberiaActualizarCuandoNoEstaJugando() {
        assertDoesNotThrow(() -> juego.actualizarJuego(0.016));
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void deberiaActualizarSinExcepcionCuandoEstaJugando() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        assertDoesNotThrow(() -> juego.actualizarJuego(0.016));
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
    }

    @Test
    void deberiaCambiarADerrotaCuandoSeAgotaElTiempo() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        double limite = juego.getNivelActual().obtenerTiempoLimite();
        juego.actualizarJuego(limite + 1);
        assertEquals(EstadoJuego.DERROTA, juego.getEstado());
    }

    @Test
    void deberiaDecrecerElTiempoRestanteAlActualizar() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        double antes = juego.obtenerTiempoRestante();
        juego.actualizarJuego(1.0);
        assertTrue(juego.obtenerTiempoRestante() < antes);
    }

    // ── pausar / reanudar ─────────────────────────────────────────────────────

    @Test
    void deberiaPasarAEstadoPausadoAlPausar() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.pausar();
        assertEquals(EstadoJuego.PAUSADO, juego.getEstado());
    }

    @Test
    void noDeberiaPausarFueraDeModoJugando() {
        juego.pausar(); // estado MENU
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void deberiaVolverAJugandoAlReanudar() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.pausar();
        juego.reanudar();
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
    }

    @Test
    void noDeberiaReanudarFueraDePausado() {
        juego.reanudar(); // estado MENU
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void noDeberiaAvanzarElTiempoCuandoEstaPausado() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.pausar();
        double antes = juego.obtenerTiempoRestante();
        juego.actualizarJuego(5.0);
        assertEquals(antes, juego.obtenerTiempoRestante(), 0.001);
    }

    // ── terminar / reiniciar ──────────────────────────────────────────────────

    @Test
    void deberiaCambiarAMenuAlTerminar() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.terminar();
        assertEquals(EstadoJuego.MENU, juego.getEstado());
    }

    @Test
    void deberiaRecargarElNivelAlReiniciar() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.terminar();
        juego.reiniciar();
        assertEquals(EstadoJuego.JUGANDO, juego.getEstado());
        assertEquals("1", juego.obtenerNivelId());
    }

    @Test
    void noDeberiaFallarAlReiniciarSinHaberIniciado() {
        assertDoesNotThrow(() -> juego.reiniciar());
    }

    // ── avanzarNivel ──────────────────────────────────────────────────────────

    @Test
    void deberiaCargarNivel2DesdeNivel1() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL1, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.avanzarNivel();
        assertEquals("2", juego.obtenerNivelId());
    }

    @Test
    void deberiaCargarNivel3DesdeNivel2() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL2, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.avanzarNivel();
        assertEquals("3", juego.obtenerNivelId());
    }

    @Test
    void deberiaVolverANivel1DesdeNivel3() {
        juego.iniciar(ModoJuego.PLAYER, NIVEL3, List.of(new Blinky()), List.of(ColorJuego.NEGRO), false);
        juego.avanzarNivel();
        assertEquals("1", juego.obtenerNivelId());
    }

    @Test
    void noDeberiaFallarAlAvanzarSinHaberIniciado() {
        assertDoesNotThrow(() -> juego.avanzarNivel());
    }

    // ── GestorArchivos (stubs) ────────────────────────────────────────────────

    @Test
    void deberiaGuardarPartidaSinEfecto() {
        assertDoesNotThrow(() -> new GestorArchivos().guardarPartida(juego, "test.dat"));
    }

    @Test
    void deberiaRetornarNullAlCargarPartida() {
        assertNull(new GestorArchivos().cargarPartida("test.dat"));
    }
}
