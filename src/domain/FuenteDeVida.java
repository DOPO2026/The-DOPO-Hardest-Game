package domain;

public class FuenteDeVida extends Elemento {
    private boolean usada;

    public FuenteDeVida(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public void activar(Jugador jugador) {
        if (!usada) usada = true;
    }

    @Override
    public boolean estaActivo() { return !usada; }
}
