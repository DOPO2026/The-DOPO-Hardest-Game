package domain.skins;

public class Inky implements Skin {
    @Override public double obtenerVelocidad() { return 1.5; }
    @Override public double obtenerTamanio()   { return 1.5; }
    @Override public int    obtenerVidas()     { return 1; }
    @Override public ColorJuego obtenerColor() { return ColorJuego.AZUL; }
    @Override public String obtenerNombre()    { return "Inky"; }
}
