package test;

import domain.collectibles.Bomba;
import domain.collectibles.FuenteDeVida;
import domain.collectibles.Moneda;
import domain.collectibles.MonedaAmarilla;
import domain.collectibles.MonedaSkin;
import domain.core.MotorJuego;
import domain.core.Nivel;
import domain.enemy.Basico;
import domain.enemy.Enemigo;
import domain.player.ControlHumano;
import domain.player.Jugador;
import domain.skins.Blinky;
import domain.skins.ColorJuego;
import domain.skins.Inky;
import domain.world.ZonaFinal;
import domain.world.ZonaIntermedia;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MotorJuegoTest {

    private Jugador crearJugadorEn(int x, int y) {
        return new Jugador(x, y, 20, 20, new ControlHumano(), new Blinky(), ColorJuego.NEGRO);
    }

    @Test
    void deberiaRequirirMonedasYZonaFinalParaVictoria() {
        Nivel n = new Nivel("t", 800, 600, 60);
        ZonaFinal zf = new ZonaFinal(500, 100, 100, 100);
        n.agregarZona(zf);
        n.agregarMoneda(new MonedaAmarilla(50, 50, 14, 14));
        Jugador j = crearJugadorEn(530, 130);
        n.agregarJugador(j);

        MotorJuego m = new MotorJuego();
        // Aún quedan monedas → no victoria
        assertFalse(m.evaluarEstado(n));

        n.registrarRecoleccion();
        // Todas las monedas recolectadas y jugador en ZonaFinal → victoria
        assertTrue(m.evaluarEstado(n));
    }

    @Test
    void deberiaMataAlJugadorAlColisionarConEnemigo() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100);
        n.agregarJugador(j);
        n.agregarEnemigo(new Enemigo(105, 100, 20, 20, new Basico(0, 0, 0, 800, 0, 600)));

        MotorJuego m = new MotorJuego();
        m.procesarInteracciones(n);
        assertEquals(1, j.obtenerMuertes());
    }

    @Test
    void deberiaRecolectarMonedaAlTocarla() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100);
        n.agregarJugador(j);
        Moneda mon = new MonedaAmarilla(105, 105, 14, 14);
        n.agregarMoneda(mon);

        new MotorJuego().procesarInteracciones(n);
        assertTrue(mon.estaRecolectada());
        assertEquals(0, n.obtenerMonedasPendientes());
    }

    @Test
    void deberiaAplicarSkinAlJugadorAlRecolectarMonedaSkin() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100);
        n.agregarJugador(j);
        MonedaSkin ms = new MonedaSkin(105, 105, 14, 14, new Inky());
        n.agregarMoneda(ms);

        new MotorJuego().procesarInteracciones(n);

        assertTrue(ms.estaRecolectada());
        assertEquals("Inky", j.obtenerSkinActual().obtenerNombre());
    }

    @Test
    void deberiaOtorgarEscudoAlJugadorConFuenteDeVida() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100); // Blinky: 1 vida
        n.agregarJugador(j);
        n.agregarFuenteDeVida(new FuenteDeVida(105, 105, 14, 14));

        new MotorJuego().procesarInteracciones(n);

        assertEquals(2, j.obtenerVidas());
    }

    @Test
    void deberiaMataAlJugadorYReiniciarMonedasConBomba() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100);
        n.agregarJugador(j);
        n.agregarMoneda(new MonedaAmarilla(300, 300, 14, 14));
        n.registrarRecoleccion(); // simular que ya estaba recogida → pendientes=0
        n.agregarBomba(new Bomba(105, 105, 14, 14));

        new MotorJuego().procesarInteracciones(n);

        assertEquals(1, j.obtenerMuertes());
        assertEquals(1, n.obtenerMonedasPendientes()); // reiniciarMonedas la restauró
    }

    @Test
    void deberiaCambiarElRespawnAlPasarPorZonaIntermedia() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100); // respawn original (100,100)
        n.agregarJugador(j);
        n.agregarZona(new ZonaIntermedia(50, 50, 200, 200)); // contiene a (100,100)

        new MotorJuego().procesarInteracciones(n); // activa checkpoint
        j.morir(); // va al nuevo respawn: 50+(200-20)/2=140, 50+(200-20)/2=140

        assertEquals(140, j.obtenerPosX());
        assertEquals(140, j.obtenerPosY());
    }

    @Test
    void deberiaMataAmbosJugadoresEnColisionPvP() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j1 = crearJugadorEn(100, 100);
        Jugador j2 = crearJugadorEn(105, 105); // superpuestos con j1
        n.agregarJugador(j1);
        n.agregarJugador(j2);

        new MotorJuego().procesarInteracciones(n);

        assertEquals(1, j1.obtenerMuertes());
        assertEquals(1, j2.obtenerMuertes());
    }

    @Test
    void deberiaReiniciarMonedasRecolectadasAlMorir() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100);
        n.agregarJugador(j);
        Moneda mon = new MonedaAmarilla(150, 150, 14, 14);
        n.agregarMoneda(mon);
        mon.recolectar();
        n.registrarRecoleccion();

        // Enemigo encima del jugador → muerte
        n.agregarEnemigo(new Enemigo(100, 100, 20, 20, new Basico(0, 0, 0, 800, 0, 600)));
        new MotorJuego().procesarInteracciones(n);

        assertFalse(mon.estaRecolectada(), "La moneda debe volver a su estado inicial tras morir");
        assertEquals(1, n.obtenerMonedasPendientes());
    }
}
