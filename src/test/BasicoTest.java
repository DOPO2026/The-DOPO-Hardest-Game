package test;

import domain.core.Nivel;
import domain.enemy.Acelerado;
import domain.enemy.Basico;
import domain.enemy.Enemigo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BasicoTest {

    @Test
    void deberiaRebotarHorizontalmente() {
        // limIzq=100, limDer=200, enemigo de 20px → debe rebotar al llegar a x+20 >= 200
        Basico estrategia = new Basico(1, 0, 100, 200, 0, 600);
        Enemigo e = new Enemigo(180, 100, 20, 20, estrategia);
        Nivel nivel = new Nivel("t", 800, 600, 60);

        // Avanza unas iteraciones — debe terminar moviéndose hacia la izquierda
        for (int i = 0; i < 20; i++) e.actualizar(0.016, nivel);
        // En algún punto x debe haber empezado a decrecer
        assertTrue(e.obtenerPosX() < 200);
    }

    @Test
    void deberiaMoverseMasRapidoQueElBasico() {
        Nivel nivel = new Nivel("t", 800, 600, 60);
        Enemigo bas = new Enemigo(100, 100, 20, 20, new Basico   (1, 0, 50, 500, 0, 600));
        Enemigo ace = new Enemigo(100, 100, 20, 20, new Acelerado(1, 0, 50, 500, 0, 600));
        bas.actualizar(0.016, nivel);
        ace.actualizar(0.016, nivel);
        assertTrue(ace.obtenerPosX() > bas.obtenerPosX(),
                "Acelerado debería avanzar más por frame que Basico");
    }
}
