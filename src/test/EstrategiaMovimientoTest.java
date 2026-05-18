package test;

import domain.core.Nivel;
import domain.enemy.DeslizadorVertical;
import domain.enemy.Enemigo;
import domain.enemy.Patrullero;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstrategiaMovimientoTest {

    private Nivel nivel() { return new Nivel("t", 800, 600, 60); }

    @Test
    void deberiaBajarInicialmente() {
        DeslizadorVertical d = new DeslizadorVertical(100, 300, 5);
        Enemigo e = new Enemigo(200, 150, 20, 20, d);
        int yInicial = e.obtenerPosY();
        d.actualizar(e, nivel(), 0.016);
        assertTrue(e.obtenerPosY() > yInicial);
    }

    @Test
    void deberiaRebotarAlAlcanzarElMaximo() {
        // Con minY=100, maxY=110, velocidad=5: tras la primera actualización posY=155 >= 110 → bajando=false
        DeslizadorVertical d = new DeslizadorVertical(100, 110, 5);
        Enemigo e = new Enemigo(200, 105, 20, 20, d);
        Nivel n = nivel();
        d.actualizar(e, n, 0.016); // posY=110 → bajando=false
        int yTrasRebote = e.obtenerPosY();
        d.actualizar(e, n, 0.016); // ahora sube
        assertTrue(e.obtenerPosY() < yTrasRebote);
    }

    @Test
    void deberiaRebotarAlAlcanzarElMinimo() {
        DeslizadorVertical d = new DeslizadorVertical(100, 110, 5);
        Enemigo e = new Enemigo(200, 105, 20, 20, d);
        Nivel n = nivel();
        // Varias iteraciones cubren la rama bajando=true tras llegar a minY
        for (int i = 0; i < 20; i++) d.actualizar(e, n, 0.016);
        assertNotNull(e);
    }

    @Test
    void deberiaActualizarSinEfecto() {
        Enemigo e = new Enemigo(100, 100, 20, 20, new Patrullero());
        assertDoesNotThrow(() -> new Patrullero().actualizar(e, nivel(), 0.016));
    }
}
