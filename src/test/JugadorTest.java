package test;

import domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JugadorTest {

    private Jugador crearJugador(Skin skin) {
        return new Jugador(100, 100, 20, 20, new ControlHumano(), skin, ColorJuego.NEGRO);
    }

    @Test
    void aplicarSkinActualizaVidasYVelocidad() {
        Jugador j = crearJugador(new Blinky());
        assertEquals(1, j.obtenerVidas());

        j.aplicarSkin(new Clyde());
        assertEquals(2, j.obtenerVidas());
    }

    @Test
    void moverNorteDecrementaY() {
        Jugador j = crearJugador(new Blinky());
        Nivel nivel = new Nivel("t", 800, 600, 60);
        int yInicial = j.obtenerPosY();
        j.mover(Direction.NORTE, nivel);
        assertTrue(j.obtenerPosY() < yInicial);
    }

    @Test
    void paredBloqueaMovimiento() {
        Jugador j = crearJugador(new Blinky());
        Nivel nivel = new Nivel("t", 800, 600, 60);
        // Pared pegada arriba del jugador (su hitbox empieza en y=100); cualquier
        // intento de avanzar al norte debe entrar en colisión.
        nivel.agregarPared(new Pared(90, 90, 40, 15));

        int yInicial = j.obtenerPosY();
        j.mover(Direction.NORTE, nivel);
        assertEquals(yInicial, j.obtenerPosY(), "El jugador no debe atravesar la pared");
    }

    @Test
    void morirIncrementaMuertesYReubica() {
        Jugador j = crearJugador(new Blinky());
        Nivel nivel = new Nivel("t", 800, 600, 60);
        j.mover(Direction.ESTE, nivel);
        int xMovido = j.obtenerPosX();
        assertNotEquals(100, xMovido);

        j.morir();
        assertEquals(1, j.obtenerMuertes());
        assertEquals(100, j.obtenerPosX());
    }

    @Test
    void recibirGolpeConEscudoNoCuentaMuerte() {
        Jugador j = crearJugador(new Clyde()); // 2 vidas
        boolean murio = j.recibirGolpe();
        assertFalse(murio);
        assertEquals(0, j.obtenerMuertes());
        assertEquals(1, j.obtenerVidas());
    }

    @Test
    void recibirGolpeSinEscudoCuentaMuerte() {
        Jugador j = crearJugador(new Blinky()); // 1 vida
        boolean murio = j.recibirGolpe();
        assertTrue(murio);
        assertEquals(1, j.obtenerMuertes());
    }

    @Test
    void aplicarSkinNullLanzaExcepcion() {
        Jugador j = crearJugador(new Blinky());
        assertThrows(TheDopoHardestGameException.class, () -> j.aplicarSkin(null));
    }
}
