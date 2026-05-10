package domain;

import java.util.ArrayList;
import java.util.List;

public class Nivel {
    private String id;
    private int ancho, alto;
    private double tiempoLimite;
    private int monedasPendientes;

    private List<Jugador> jugadores = new ArrayList<>();
    private List<Enemigo>  enemigos  = new ArrayList<>();
    private List<Moneda>   monedas   = new ArrayList<>();
    private List<Pared>    paredes   = new ArrayList<>();
    private List<Zona>     zonas     = new ArrayList<>();

    public Nivel(String id, int ancho, int alto, double tiempoLimite) {
        this.id = id;
        this.ancho = ancho;
        this.alto = alto;
        this.tiempoLimite = tiempoLimite;
    }

    public void actualizar(double deltaTime) {
        for (Enemigo e : enemigos) e.actualizar(deltaTime, this);
    }

    public void reset() {
        monedasPendientes = monedas.size();
    }

    public void agregarJugador(Jugador j) { jugadores.add(j); }
    public void agregarEnemigo(Enemigo e) { enemigos.add(e); }
    public void agregarMoneda(Moneda m)   { monedas.add(m); monedasPendientes++; }
    public void agregarPared(Pared p)     { paredes.add(p); }
    public void agregarZona(Zona z)       { zonas.add(z); }

    public List<Jugador> getJugadores() { return jugadores; }
    public List<Enemigo> getEnemigos()  { return enemigos; }
    public List<Moneda>  getMonedas()   { return monedas; }
    public List<Pared>   getParedes()   { return paredes; }
    public List<Zona>    getZonas()     { return zonas; }

    public int    obtenerAncho()           { return ancho; }
    public int    obtenerAlto()            { return alto; }
    public double obtenerTiempoLimite()    { return tiempoLimite; }
    public void   registrarRecoleccion()   { monedasPendientes--; }
    public boolean estaCompleto()          { return monedasPendientes <= 0; }
}
