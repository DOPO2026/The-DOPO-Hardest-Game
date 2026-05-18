package test;

import domain.ai.MaquinaAleatoria;
import domain.ai.MaquinaExperta;
import domain.core.Nivel;
import domain.player.Direction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AITest {

    private Nivel nivel() { return new Nivel("t", 800, 600, 60); }

    @Test
    void deberiaRetornarDireccionEnElPrimerLlamado() {
        Direction d = new MaquinaAleatoria().decidirMovimiento(nivel());
        assertNotNull(d);
    }

    @Test
    void deberiaMantenerLaDireccionMientrasTengaPasos() {
        MaquinaAleatoria m = new MaquinaAleatoria();
        Nivel n = nivel();
        Direction primera = m.decidirMovimiento(n);
        assertEquals(primera, m.decidirMovimiento(n));
        assertEquals(primera, m.decidirMovimiento(n));
    }

    @Test
    void deberiaReelegirDireccionAlAgorarLosPasos() {
        MaquinaAleatoria m = new MaquinaAleatoria();
        Nivel n = nivel();
        // El primer call asigna 8-27 pasos; 30 llamadas garantizan que llegue a 0 y reelija
        for (int i = 0; i < 30; i++) m.decidirMovimiento(n);
        assertNotNull(m.decidirMovimiento(n));
    }

    @Test
    void deberiaRetornarQuietoSiempre() {
        assertEquals(Direction.QUIETO, new MaquinaExperta().decidirMovimiento(nivel()));
    }
}
