package domain;

public class Inky implements Skin {
    @Override public double obtenerVelocidad() { return 1.5; }
    @Override public double obtenerTamanio()   { return 1.5; }
    @Override public int    obtenerVidas()      { return 1; }
}
