package domain.world;

import domain.player.Jugador;

public class ZonaIntermedia extends Zona {
    public ZonaIntermedia(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public void activarCheckpoint(Jugador jugador) {
        jugador.setRespawn(this);
    }
}
