package test;

import domain.core.ConstructorNivel;
import domain.core.TheDopoHardestGameException;
import domain.core.Nivel;
import domain.world.ZonaFinal;
import domain.world.ZonaInicial;
import domain.world.ZonaIntermedia;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstructorNivelTest {

    @Test
    void deberiaCargarNivel1Correctamente() {
        Nivel n = new ConstructorNivel().construirDesdeArchivo("resources/configuraciones/nivel1.txt");
        assertEquals("1", n.obtenerId());
        assertEquals(800, n.obtenerAncho());
        assertEquals(558, n.obtenerAlto());
        assertEquals(90.0, n.obtenerTiempoLimite(), 0.001);
        assertEquals(4, n.obtenerMonedasTotales());
        assertEquals(5, n.getEnemigos().size()); // 4 BASICO + 1 PATRULLERO
        // Al menos las paredes perimetrales + las internas del archivo
        assertTrue(n.getParedes().size() >= 4);
        // Inicio y fin
        boolean tieneInicio = n.getZonas().stream().anyMatch(z -> z instanceof ZonaInicial);
        boolean tieneFin    = n.getZonas().stream().anyMatch(z -> z instanceof ZonaFinal);
        assertTrue(tieneInicio);
        assertTrue(tieneFin);
    }

    @Test
    void noDeberiaCargarRutaInexistente() {
        ConstructorNivel c = new ConstructorNivel();
        assertThrows(TheDopoHardestGameException.class,
                () -> c.construirDesdeArchivo("resources/configuraciones/nivel_inexistente.txt"));
    }

    @Test
    void noDeberiaCargarRutaNula() {
        ConstructorNivel c = new ConstructorNivel();
        assertThrows(TheDopoHardestGameException.class,
                () -> c.construirDesdeArchivo(null));
    }

    @Test
    void deberiaContenerCheckpointIntermedioEnNivel2() {
        Nivel n = new ConstructorNivel().construirDesdeArchivo("resources/configuraciones/nivel2.txt");
        boolean tieneCheckpoint = n.getZonas().stream().anyMatch(z -> z instanceof ZonaIntermedia);
        assertTrue(tieneCheckpoint, "El nivel 2 debe contener una ZonaIntermedia");
    }

    @Test
    void deberiaContenerBombaYFuenteEnNivel3() {
        Nivel n = new ConstructorNivel().construirDesdeArchivo("resources/configuraciones/nivel3.txt");
        assertFalse(n.getBombas().isEmpty(),  "Nivel 3 debe tener bombas");
        assertFalse(n.getFuentes().isEmpty(), "Nivel 3 debe tener fuentes de vida");
    }
}
