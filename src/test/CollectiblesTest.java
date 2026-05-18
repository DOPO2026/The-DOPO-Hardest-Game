package test;

import domain.collectibles.Bomba;
import domain.collectibles.FuenteDeVida;
import domain.collectibles.MonedaSkin;
import domain.player.ControlHumano;
import domain.player.Jugador;
import domain.skins.Blinky;
import domain.skins.Clyde;
import domain.skins.ColorJuego;
import domain.skins.Inky;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CollectiblesTest {

    private Jugador jugador() {
        return new Jugador(100, 100, 20, 20, new ControlHumano(), new Blinky(), ColorJuego.NEGRO);
    }

    @Test
    void deberiaActivarEscudoUnaVez() {
        FuenteDeVida f = new FuenteDeVida(50, 50, 20, 20);
        Jugador j = jugador();
        assertTrue(f.estaActivo());
        f.activar(j);
        assertFalse(f.estaActivo());
        assertEquals(2, j.obtenerVidas()); // Blinky tiene 1 vida, +1 escudo
    }

    @Test
    void noDeberiaActivarseDosVeces() {
        FuenteDeVida f = new FuenteDeVida(50, 50, 20, 20);
        Jugador j = jugador();
        f.activar(j);
        int vidasTras1 = j.obtenerVidas();
        f.activar(j); // ya usada → no suma escudo
        assertEquals(vidasTras1, j.obtenerVidas());
    }

    @Test
    void deberiaRecolectarYExponerLaSkin() {
        Inky inky = new Inky();
        MonedaSkin ms = new MonedaSkin(50, 50, 14, 14, inky);
        assertFalse(ms.estaRecolectada());
        ms.recolectar();
        assertTrue(ms.estaRecolectada());
        assertEquals(inky, ms.obtenerSkinOtorgada());
    }

    @Test
    void deberiaEstarActivaAlCrearse() {
        assertTrue(new Bomba(50, 50, 20, 20).estaActivo());
    }

    @Test
    void deberiaExplotarSinEfecto() {
        assertDoesNotThrow(() -> new Bomba(50, 50, 20, 20).explotar(jugador()));
    }

    @Test
    void deberiaReducirVelocidadTrasRecibirGolpe() {
        Clyde c = new Clyde();
        double velOriginal = c.obtenerVelocidad();
        c.aplicarPenalizacionGolpe();
        assertTrue(c.obtenerVelocidad() < velOriginal);
    }

    @Test
    void deberiaTenerPropiedadesCorrectas() {
        Inky inky = new Inky();
        assertEquals(1.5, inky.obtenerVelocidad(), 0.001);
        assertEquals(1.5, inky.obtenerTamanio(), 0.001);
        assertEquals(1,   inky.obtenerVidas());
        assertEquals(ColorJuego.AZUL, inky.obtenerColor());
        assertEquals("Inky", inky.obtenerNombre());
    }
}
