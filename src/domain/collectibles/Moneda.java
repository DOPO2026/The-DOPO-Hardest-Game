package domain.collectibles;

import domain.common.Elemento;

public abstract class Moneda extends Elemento {
    private boolean recolectada;

    protected Moneda(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public void recolectar()         { recolectada = true; }
    public void reiniciar()          { recolectada = false; }
    public boolean estaRecolectada() { return recolectada; }

    @Override
    public boolean estaActivo() { return !recolectada; }
}
