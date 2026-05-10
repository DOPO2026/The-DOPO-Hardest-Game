package domain;

public class DeslizadorVertical implements EstrategiaMovimiento {
    private final int minY, maxY, velocidad;
    private boolean bajando = true;

    public DeslizadorVertical(int minY, int maxY, int velocidad) {
        this.minY = minY;
        this.maxY = maxY;
        this.velocidad = velocidad;
    }

    @Override
    public void actualizar(Enemigo enemigo, Nivel nivel, double deltaTime) {
        enemigo.mover(0, bajando ? velocidad : -velocidad);
        if (enemigo.obtenerPosY() >= maxY) bajando = false;
        if (enemigo.obtenerPosY() <= minY) bajando = true;
    }
}
