package test;

import domain.core.TheDopoHardestGameException;
import domain.core.Nivel;
import domain.player.ControlHumano;
import domain.player.Direction;
import domain.player.Jugador;
import domain.skins.Blinky;
import domain.skins.Clyde;
import domain.skins.ColorJuego;
import domain.skins.Skin;
import domain.world.Pared;
import domain.world.ZonaIntermedia;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JugadorTest {

    private Jugador crearJugador(Skin skin) {
        return new Jugador(100, 100, 20, 20, new ControlHumano(), skin, ColorJuego.NEGRO);
    }

    @Test
    void deberiaActualizarVidasYVelocidadAlAplicarSkin() {
        Jugador j = crearJugador(new Blinky());
        assertEquals(1, j.obtenerVidas());

        j.aplicarSkin(new Clyde());
        assertEquals(2, j.obtenerVidas());
    }

    @Test
    void deberiaDecrementarYAlMoverseAlNorte() {
        Jugador j = crearJugador(new Blinky());
        Nivel nivel = new Nivel("t", 800, 600, 60);
        int yInicial = j.obtenerPosY();
        j.mover(Direction.NORTE, nivel);
        assertTrue(j.obtenerPosY() < yInicial);
    }

    @Test
    void noDeberiaAtravesarUnaPared() {
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
    void deberiaIncrementarMuertesYReubicarAlMorir() {
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
    void noDeberiaContarMuerteAlRecibirGolpeConEscudo() {
        Jugador j = crearJugador(new Clyde()); // 2 vidas
        boolean murio = j.recibirGolpe();
        assertFalse(murio);
        assertEquals(0, j.obtenerMuertes());
        assertEquals(1, j.obtenerVidas());
    }

    @Test
    void deberiaContarMuerteAlRecibirGolpeSinEscudo() {
        Jugador j = crearJugador(new Blinky()); // 1 vida
        boolean murio = j.recibirGolpe();
        assertTrue(murio);
        assertEquals(1, j.obtenerMuertes());
    }

    @Test
    void deberiaActualizarElPuntoDeReaparicion() {
        Jugador j = crearJugador(new Blinky());
        // Zona en (200,200,100,100): respawnX = 200+(100-20)/2=240, respawnY=240
        j.setRespawn(new ZonaIntermedia(200, 200, 100, 100));
        j.morir();
        assertEquals(240, j.obtenerPosX());
        assertEquals(240, j.obtenerPosY());
    }

    @Test
    void deberiaIncrementarVidasAlAgregarEscudo() {
        Jugador j = crearJugador(new Blinky()); // 1 vida
        j.agregarEscudo();
        assertEquals(2, j.obtenerVidas());
    }

    @Test
    void noDeberiaAplicarSkinNull() {
        Jugador j = crearJugador(new Blinky());
        assertThrows(TheDopoHardestGameException.class, () -> j.aplicarSkin(null));
    }
}
