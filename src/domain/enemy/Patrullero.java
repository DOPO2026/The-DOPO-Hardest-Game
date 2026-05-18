package domain.enemy;

import domain.core.Nivel;
import java.util.List;

public class Patrullero implements EstrategiaMovimiento {
    private List<int[]> ruta;
    private int indiceRuta;

    @Override
    public void actualizar(Enemigo enemigo, Nivel nivel, double deltaTime) {
    }
}
