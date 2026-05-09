package domain;

public class Clyde implements Skin {
    private double velocidad = 1.0;

    @Override public double obtenerVelocidad() { return velocidad; }
    @Override public double obtenerTamanio()   { return 1.0; }
    @Override public int    obtenerVidas()      { return 2; }
}
