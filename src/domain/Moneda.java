package domain;

public abstract class Moneda extends Elemento {
    private boolean recolectada;

    protected Moneda(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    // Constructor sin args para subclases stub
    protected Moneda() {}

    public void recolectar()             { recolectada = true; }
    public boolean estaRecolectada()     { return recolectada; }
}
