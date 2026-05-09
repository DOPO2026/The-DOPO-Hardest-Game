package domain;

public class Bomba extends Elemento {
    private boolean activa;

    public Bomba(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
        this.activa = true;
    }

    public void explotar(Elemento elemento) {
    }

    @Override
    public boolean estaActivo() { return activa; }
}
