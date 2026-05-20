package test;

import domain.player.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void deberiaTenerUnSoloComponenteParaCadaCardinal() {
        assertArrayEquals(new int[]{ 0, -1}, new int[]{Direction.NORTE.dx, Direction.NORTE.dy});
        assertArrayEquals(new int[]{ 0,  1}, new int[]{Direction.SUR.dx,   Direction.SUR.dy});
        assertArrayEquals(new int[]{ 1,  0}, new int[]{Direction.ESTE.dx,  Direction.ESTE.dy});
        assertArrayEquals(new int[]{-1,  0}, new int[]{Direction.OESTE.dx, Direction.OESTE.dy});
    }

    @Test
    void deberiaCombinarDosEjesParaDiagonales() {
        assertEquals( 1, Direction.NORESTE.dx);
        assertEquals(-1, Direction.NORESTE.dy);
        assertEquals(-1, Direction.SUROESTE.dx);
        assertEquals( 1, Direction.SUROESTE.dy);
    }

    @Test
    void deberiaSerCeroEnAmbosEjes() {
        assertEquals(0, Direction.QUIETO.dx);
        assertEquals(0, Direction.QUIETO.dy);
    }

    @Test
    void deberiaTenerNueveValoresPosibles() {
        assertEquals(9, Direction.values().length);
    }
}
