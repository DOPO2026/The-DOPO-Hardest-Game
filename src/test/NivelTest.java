package test;

import domain.*;
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
