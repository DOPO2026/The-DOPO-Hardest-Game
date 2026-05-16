package test;

import domain.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void cardinalesTienenComponenteUnico() {
        assertArrayEquals(new int[]{ 0, -1}, new int[]{Direction.NORTE.dx, Direction.NORTE.dy});
        assertArrayEquals(new int[]{ 0,  1}, new int[]{Direction.SUR.dx,   Direction.SUR.dy});
        assertArrayEquals(new int[]{ 1,  0}, new int[]{Direction.ESTE.dx,  Direction.ESTE.dy});
        assertArrayEquals(new int[]{-1,  0}, new int[]{Direction.OESTE.dx, Direction.OESTE.dy});
    }

    @Test
    void diagonalesCombinanDosEjes() {
        assertEquals( 1, Direction.NORESTE.dx);
        assertEquals(-1, Direction.NORESTE.dy);
        assertEquals(-1, Direction.SUROESTE.dx);
        assertEquals( 1, Direction.SUROESTE.dy);
    }

    @Test
    void quietoEsCero() {
        assertEquals(0, Direction.QUIETO.dx);
        assertEquals(0, Direction.QUIETO.dy);
    }

    @Test
    void hayNueveValores() {
        assertEquals(9, Direction.values().length);
    }
}
