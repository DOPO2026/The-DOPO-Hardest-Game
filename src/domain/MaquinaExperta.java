package domain;

import java.util.List;

public class MaquinaExperta implements ControlJugador {
    private List<Direction> ruta;

    @Override
    public Direction decidirMovimiento(Nivel nivel) {
        return Direction.QUIETO;
    }

    private void calcularRuta(Nivel nivel) {
    }
}
