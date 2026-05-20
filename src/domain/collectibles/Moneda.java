package domain.collectibles;

import domain.common.Elemento;

public abstract class Moneda extends Elemento {
    private boolean recolectada          = false;
    private boolean guardadaEnCheckpoint = false;

    protected Moneda(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public void recolectar()         { recolectada = true; }
    public void guardarEnCheckpoint() { if (recolectada) guardadaEnCheckpoint = true; }
    public void reiniciar()          { if (!guardadaEnCheckpoint) recolectada = false; }
    public boolean estaRecolectada() { return recolectada; }

    @Override
    public boolean estaActivo() { return !recolectada; }
}
