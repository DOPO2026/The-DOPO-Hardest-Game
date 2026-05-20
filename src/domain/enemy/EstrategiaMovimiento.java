package domain.enemy;

import domain.core.Nivel;

public interface EstrategiaMovimiento {
    void actualizar(Enemigo enemigo, Nivel nivel, double deltaTime);
    default String obtenerTipo() { return "ESTANDAR"; }
}
