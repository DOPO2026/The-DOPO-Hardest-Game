package domain;

public class Blinky implements Skin {
    @Override public double obtenerVelocidad() { return 1.0; }
    @Override public double obtenerTamanio()   { return 1.0; }
    @Override public int    obtenerVidas()     { return 1; }
    @Override public ColorJuego obtenerColor() { return ColorJuego.ROJO; }
    @Override public String obtenerNombre()    { return "Blinky"; }
}
