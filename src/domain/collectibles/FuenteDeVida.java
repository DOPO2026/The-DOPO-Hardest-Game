package domain.collectibles;

import domain.common.Elemento;
import domain.player.Jugador;

public class FuenteDeVida extends Elemento {
    private boolean usada;

    public FuenteDeVida(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public void activar(Jugador jugador) {
        if (!usada) {
            usada = true;
            jugador.agregarEscudo();
        }
    }

    @Override
    public boolean estaActivo() { return !usada; }
}
