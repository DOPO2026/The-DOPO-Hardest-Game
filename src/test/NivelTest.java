package test;

import domain.collectibles.Moneda;
import domain.collectibles.MonedaAmarilla;
import domain.core.Nivel;
import domain.enemy.Basico;
import domain.enemy.Enemigo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NivelTest {

    @Test
    void registrarRecoleccionDecrementaPendientes() {
        Nivel n = new Nivel("t", 800, 600, 60);
        n.agregarMoneda(new MonedaAmarilla(0, 0, 14, 14));
        n.agregarMoneda(new MonedaAmarilla(20, 20, 14, 14));
        assertEquals(2, n.obtenerMonedasPendientes());

        n.registrarRecoleccion();
        assertEquals(1, n.obtenerMonedasPendientes());
    }

    @Test
    void estaCompletoCuandoNoQuedanMonedas() {
        Nivel n = new Nivel("t", 800, 600, 60);
        assertTrue(n.estaCompleto());
        n.agregarMoneda(new MonedaAmarilla(0, 0, 14, 14));
        assertFalse(n.estaCompleto());
        n.registrarRecoleccion();
        assertTrue(n.estaCompleto());
    }

    @Test
    void actualizarMueveLosEnemigos() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Enemigo e = new Enemigo(100, 100, 20, 20, new Basico(1, 0, 0, 800, 0, 600));
        n.agregarEnemigo(e);
        int xInicial = e.obtenerPosX();
        n.actualizar(0.016);
        assertNotEquals(xInicial, e.obtenerPosX());
    }

    @Test
    void resetReiniciaMonedas() {
        Nivel n = new Nivel("t", 800, 600, 60);
        n.agregarMoneda(new MonedaAmarilla(0, 0, 14, 14));
        n.registrarRecoleccion();
        assertEquals(0, n.obtenerMonedasPendientes());
        n.reset();
        assertEquals(1, n.obtenerMonedasPendientes());
    }

    @Test
    void reiniciarMonedasRestauraEstado() {
        Nivel n = new Nivel("t", 800, 600, 60);
        Moneda m = new MonedaAmarilla(0, 0, 14, 14);
        n.agregarMoneda(m);
        m.recolectar();
        n.registrarRecoleccion();
        assertTrue(m.estaRecolectada());
        assertEquals(0, n.obtenerMonedasPendientes());

        n.reiniciarMonedas();
        assertFalse(m.estaRecolectada());
        assertEquals(1, n.obtenerMonedasPendientes());
    }
}
