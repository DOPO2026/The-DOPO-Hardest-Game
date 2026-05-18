package domain.skins;

public enum ColorJuego {
    ROJO     (220,  40,  40),
    AZUL     ( 40,  80, 220),
    VERDE    ( 40, 180,  80),
    AMARILLO (230, 220,  40),
    NARANJA  (230, 130,  40),
    MORADO   (160,  80, 200),
    BLANCO   (240, 240, 240),
    NEGRO    ( 20,  20,  20);

    public final int r, g, b;

    ColorJuego(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
