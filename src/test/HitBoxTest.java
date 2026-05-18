package test;

import domain.common.HitBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HitBoxTest {

    @Test
    void cajasSuperpuestasSeIntersectan() {
        HitBox a = new HitBox(0, 0, 20, 20);
        HitBox b = new HitBox(10, 10, 20, 20);
        assertTrue(a.intersecta(b));
        assertTrue(b.intersecta(a));
    }

    @Test
    void cajasSeparadasNoSeIntersectan() {
        HitBox a = new HitBox(0, 0, 20, 20);
        HitBox b = new HitBox(40, 40, 20, 20);
        assertFalse(a.intersecta(b));
    }

    @Test
    void contactoEnBordeNoCuentaComoInterseccion() {
        HitBox a = new HitBox(0, 0, 20, 20);
        HitBox b = new HitBox(20, 0, 20, 20);
        assertFalse(a.intersecta(b));
    }
}
