package domain.enemy;

import domain.core.Nivel;

/**
 * Patrulla un rectángulo en sentido horario.
 * x1,y1 = esquina superior-izquierda  /  x2,y2 = esquina inferior-derecha
 */
public class Patrullero implements EstrategiaMovimiento {

    private enum Lado { DERECHA, ABAJO, IZQUIERDA, ARRIBA }

    private final int x1, y1, x2, y2, velocidad;
    private Lado lado = Lado.DERECHA;

    public Patrullero(int x1, int y1, int x2, int y2, int velocidad) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.velocidad = velocidad;
    }

    @Override
    public void actualizar(Enemigo enemigo, Nivel nivel, double deltaTime) {
        int px = enemigo.obtenerPosX();
        int py = enemigo.obtenerPosY();
        switch (lado) {
            case DERECHA   -> { enemigo.mover(velocidad,  0);          if (px >= x2) lado = Lado.ABAJO;     }
            case ABAJO     -> { enemigo.mover(0,          velocidad);  if (py >= y2) lado = Lado.IZQUIERDA; }
            case IZQUIERDA -> { enemigo.mover(-velocidad, 0);          if (px <= x1) lado = Lado.ARRIBA;    }
            case ARRIBA    -> { enemigo.mover(0,          -velocidad); if (py <= y1) lado = Lado.DERECHA;   }
        }
    }

    @Override
    public String obtenerTipo() { return "PATRULLERO"; }
}
