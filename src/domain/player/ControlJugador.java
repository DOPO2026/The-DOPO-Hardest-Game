package domain.player;

import domain.core.Nivel;

public interface ControlJugador {
    Direction decidirMovimiento(Nivel nivel);
}
