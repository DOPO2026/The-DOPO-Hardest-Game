package domain.player;

import domain.core.Nivel;

public class ControlHumano implements ControlJugador {
    private Direction direccionActual = Direction.QUIETO;

    @Override
    public Direction decidirMovimiento(Nivel nivel) {
        return direccionActual;
    }

    /** Llamado desde InputManager cuando se presiona/suelta una tecla. */
    public void registrarDireccion(Direction dir) {
        this.direccionActual = dir;
    }
}
