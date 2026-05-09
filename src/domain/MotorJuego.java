package domain;

public class MotorJuego {

    public void procesarInteracciones(Nivel nivel) {
        for (Jugador j : nivel.getJugadores()) {
            for (Enemigo e : nivel.getEnemigos()) {
                if (e.estaActivo() && detectarColision(j, e)) {
                    j.morir();
                    break;
                }
            }
        }
    }

    /** Retorna true cuando un jugador ha llegado a la ZonaFinal. */
    public boolean evaluarEstado(Nivel nivel) {
        for (Zona z : nivel.getZonas()) {
            if (z instanceof ZonaFinal) {
                for (Jugador j : nivel.getJugadores()) {
                    if (z.contiene(j.obtenerPosX(), j.obtenerPosY())) return true;
                }
            }
        }
        return false;
    }

    private boolean detectarColision(Elemento a, Elemento b) {
        return a.getHitBox().intersecta(b.getHitBox());
    }
}
