package domain;

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

    /** Compatibilidad con nombre original del diagrama. */
    public void registrarTecla(String tecla) {
        direccionActual = switch (tecla.toUpperCase()) {
            case "W", "UP"    -> Direction.ARRIBA;
            case "S", "DOWN"  -> Direction.ABAJO;
            case "A", "LEFT"  -> Direction.IZQUIERDA;
            case "D", "RIGHT" -> Direction.DERECHA;
            default           -> Direction.QUIETO;
        };
    }
}
