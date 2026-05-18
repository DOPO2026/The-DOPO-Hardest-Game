package domain.enemy;

import domain.core.Nivel;

public class Basico implements EstrategiaMovimiento {
    protected int dx, dy;
    protected final int limIzq, limDer, limSup, limInf;
    protected final int velocidad;

    public Basico(int dx, int dy, int limIzq, int limDer, int limSup, int limInf) {
        this(dx, dy, limIzq, limDer, limSup, limInf, 2);
    }

    protected Basico(int dx, int dy, int limIzq, int limDer, int limSup, int limInf, int velocidad) {
        this.dx = dx;
        this.dy = dy;
        this.limIzq = limIzq;
        this.limDer = limDer;
        this.limSup = limSup;
        this.limInf = limInf;
        this.velocidad = velocidad;
    }

    @Override
    public void actualizar(Enemigo enemigo, Nivel nivel, double deltaTime) {
        enemigo.mover(dx * velocidad, dy * velocidad);

        int x = enemigo.obtenerPosX();
        int y = enemigo.obtenerPosY();
        int w = enemigo.obtenerAncho();
        int h = enemigo.obtenerAlto();

        if (x + w >= limDer) dx = -Math.abs(dx);
        if (x       <= limIzq) dx =  Math.abs(dx);
        if (y + h >= limInf) dy = -Math.abs(dy);
        if (y       <= limSup) dy =  Math.abs(dy);
    }
}
