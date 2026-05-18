package domain.common;

public abstract class Elemento {
    protected int posX, posY, ancho, alto;
    private HitBox hitBox;

    protected Elemento(int posX, int posY, int ancho, int alto) {
        this.posX = posX; this.posY = posY;
        this.ancho = ancho; this.alto = alto;
        actualizarHitBox();
    }

    // Constructor sin args para subclases stub que no usan hitBox aún
    protected Elemento() {}

    public HitBox getHitBox()  { return hitBox; }
    public int obtenerPosX()   { return posX; }
    public int obtenerPosY()   { return posY; }
    public int obtenerAncho()  { return ancho; }
    public int obtenerAlto()   { return alto; }
    public boolean estaActivo() { return true; }

    protected void actualizarHitBox() {
        hitBox = new HitBox(posX, posY, ancho, alto);
    }
}
