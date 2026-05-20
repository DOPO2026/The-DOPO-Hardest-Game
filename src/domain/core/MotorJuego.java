package domain.core;

import domain.collectibles.Bomba;
import domain.collectibles.FuenteDeVida;
import domain.collectibles.Moneda;
import domain.collectibles.MonedaSkin;
import domain.enemy.Enemigo;
import domain.player.Jugador;
import domain.world.Zona;
import domain.world.ZonaFinal;
import domain.world.ZonaIntermedia;

import java.util.List;

public class MotorJuego {

    public void procesarInteracciones(Nivel nivel) {
        List<Jugador> jugadores = nivel.getJugadores();

        for (Jugador j : jugadores) {
            // Enemigo
            for (Enemigo e : nivel.getEnemigos()) {
                if (e.estaActivo() && j.getHitBox().intersecta(e.getHitBox())) {
                    if (j.recibirGolpe()) nivel.reiniciarMonedas();
                    break;
                }
            }

            // Moneda
            for (Moneda m : nivel.getMonedas()) {
                if (!m.estaRecolectada() && j.getHitBox().intersecta(m.getHitBox())) {
                    m.recolectar();
                    nivel.registrarRecoleccion();
                    if (m instanceof MonedaSkin ms) {
                        j.aplicarSkin(ms.obtenerSkinOtorgada());
                    } else {
                        j.restaurarSkin();
                    }
                }
            }

            // Fuente de vida
            for (FuenteDeVida f : nivel.getFuentes()) {
                if (f.estaActivo() && j.getHitBox().intersecta(f.getHitBox())) {
                    f.activar(j);
                }
            }

            // Bomba
            for (Bomba b : nivel.getBombas()) {
                if (b.estaActivo() && j.getHitBox().intersecta(b.getHitBox())) {
                    b.explotar(j);
                    j.morir();
                    nivel.reiniciarMonedas();
                }
            }

            // Checkpoint
            for (Zona z : nivel.getZonas()) {
                if (z instanceof ZonaIntermedia zi
                        && z.contiene(j.obtenerPosX(), j.obtenerPosY())) {
                    zi.activarCheckpoint(j);
                    nivel.guardarEstadoCheckpoint();
                }
            }
        }

        // Colisión jugador↔jugador (PvsP / PvsM): cuenta muerte para ambos.
        for (int i = 0; i < jugadores.size(); i++) {
            for (int k = i + 1; k < jugadores.size(); k++) {
                Jugador a = jugadores.get(i);
                Jugador b = jugadores.get(k);
                if (a.getHitBox().intersecta(b.getHitBox())) {
                    a.morir();
                    b.morir();
                    nivel.reiniciarMonedas();
                }
            }
        }
    }

    /** Retorna true si algún jugador alcanzó la ZonaFinal con todas las monedas recolectadas. */
    public boolean evaluarEstado(Nivel nivel) {
        if (!nivel.estaCompleto()) return false;
        for (Zona z : nivel.getZonas()) {
            if (z instanceof ZonaFinal) {
                for (Jugador j : nivel.getJugadores()) {
                    if (z.contiene(j.obtenerPosX(), j.obtenerPosY())) return true;
                }
            }
        }
        return false;
    }
}
