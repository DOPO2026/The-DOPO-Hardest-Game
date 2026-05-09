package domain;

public class MaquinaAleatoria implements ControlJugador {
    private static final Direction[] DIRS = Direction.values();

    @Override
    public Direction decidirMovimiento(Nivel nivel) {
        return DIRS[(int)(Math.random() * 4)];
    }
}
