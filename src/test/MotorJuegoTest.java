package test;

import domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MotorJuegoTest {

    private Jugador crearJugadorEn(int x, int y) {
        return new Jugador(x, y, 20, 20, new ControlHumano(), new Blinky(), ColorJuego.NEGRO);
    }

    @Test
    void victoriaRequiereMonedasYZonaFinal() {
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
    void colisionConEnemigoMataAlJugadorSinEscudo() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Jugador j = crearJugadorEn(100, 100);
        n.agregarJugador(j);
        n.agregarEnemigo(new Enemigo(105, 100, 20, 20, new Basico(0, 0, 0, 800, 0, 600)));

        MotorJuego m = new MotorJuego();
        m.procesarInteracciones(n);
        assertEquals(1, j.obtenerMuertes());
    }

    @Test
    void monedaSeRecolectaAlTocarla() {
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
    void morirReiniciaMonedasRecolectadas() {
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
