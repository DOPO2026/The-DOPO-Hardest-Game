package domain.collectibles;

import domain.common.Elemento;

public abstract class Moneda extends Elemento {
    private boolean recolectada          = false;
    private boolean guardadaEnCheckpoint = false;
    private int     colectorIndex        = -1;

    protected Moneda(int posX, int posY, int ancho, int alto) {
        super(posX, posY, ancho, alto);
    }

    public void recolectarPor(int jugadorIndex) { recolectada = true; colectorIndex = jugadorIndex; }
    public void recolectar()                    { recolectarPor(-1); }
    public void guardarEnCheckpoint()           { if (recolectada) guardadaEnCheckpoint = true; }
    public void reiniciar()                     { if (!guardadaEnCheckpoint) { recolectada = false; colectorIndex = -1; } }
    public void reiniciarDeJugador(int idx)     { if (colectorIndex == idx && !guardadaEnCheckpoint) { recolectada = false; colectorIndex = -1; } }
    public boolean estaRecolectada()            { return recolectada; }
    public int    getColectorIndex()            { return colectorIndex; }

    @Override
    public boolean estaActivo() { return !recolectada; }
}
