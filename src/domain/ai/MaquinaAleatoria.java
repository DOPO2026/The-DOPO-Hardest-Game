package domain.ai;

import domain.core.Nivel;
import domain.player.ControlJugador;
import domain.player.Direction;
import java.util.Random;

public class MaquinaAleatoria implements ControlJugador {
    private static final Direction[] DIRS = {
        Direction.NORTE,    Direction.SUR,      Direction.ESTE,     Direction.OESTE,
        Direction.NORESTE,  Direction.NOROESTE, Direction.SURESTE,  Direction.SUROESTE
    };
    private final Random random = new Random();
    private Direction actual = Direction.ESTE;
    private int pasosRestantes;

    @Override
    public Direction decidirMovimiento(Nivel nivel) {
        if (pasosRestantes <= 0) {
            actual = DIRS[random.nextInt(DIRS.length)];
            pasosRestantes = 8 + random.nextInt(20);
        }
        pasosRestantes--;
        return actual;
    }
}
