package domain.enemy;

import domain.core.Nivel;

/**
 * Patrulla una elipse en sentido antihorario.
 * x1,y1 = esquina sup-izq / x2,y2 = esquina inf-der del rectángulo que circunscribe la elipse.
 */
public class Patrullero implements EstrategiaMovimiento {

    private static final double VELOCIDAD_ANGULAR = Math.toRadians(2.0); // 2°/frame

    private final int cx, cy, rx, ry;
    private double angulo;

    public Patrullero(int x1, int y1, int x2, int y2, int velocidad) {
        this.cx = (x1 + x2) / 2;
        this.cy = (y1 + y2) / 2;
        this.rx = Math.abs(x2 - x1) / 2;
        this.ry = Math.abs(y2 - y1) / 2;
        this.angulo = 0.0;
    }

    @Override
    public void actualizar(Enemigo enemigo, Nivel nivel, double deltaTime) {
        int halfW = enemigo.obtenerAncho() / 2;
        int halfH = enemigo.obtenerAlto() / 2;
        int targetX = (int)(cx + rx * Math.cos(angulo)) - halfW;
        int targetY = (int)(cy + ry * Math.sin(angulo)) - halfH;
        enemigo.mover(targetX - enemigo.obtenerPosX(), targetY - enemigo.obtenerPosY());
        angulo += VELOCIDAD_ANGULAR;
        if (angulo >= 2 * Math.PI) angulo -= 2 * Math.PI;
    }

    @Override
    public String obtenerTipo() { return "PATRULLERO"; }
}
