package domain;

public class HitBox {
    private int x, y, ancho, alto;

    public HitBox(int x, int y, int ancho, int alto) {
        this.x = x; this.y = y; this.ancho = ancho; this.alto = alto;
    }

    public boolean intersecta(HitBox otra) {
        return x < otra.x + otra.ancho && x + ancho > otra.x
            && y < otra.y + otra.alto  && y + alto  > otra.y;
    }

    public int getX()     { return x; }
    public int getY()     { return y; }
    public int getAncho() { return ancho; }
    public int getAlto()  { return alto; }
}
