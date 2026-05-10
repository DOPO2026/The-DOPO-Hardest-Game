package domain;

public abstract class Zona extends Elemento {
    protected Zona(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public boolean contiene(int x, int y) {
        return x >= posX && x <= posX + ancho
            && y >= posY && y <= posY + alto;
    }
}
