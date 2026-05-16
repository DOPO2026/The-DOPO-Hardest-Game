package domain;

import java.util.ArrayList;
import java.util.List;

public class Nivel {
    private String id;
    private int ancho, alto;
    private double tiempoLimite;
    private int monedasTotales;
    private int monedasPendientes;

    private List<Jugador>      jugadores = new ArrayList<>();
    private List<Enemigo>      enemigos  = new ArrayList<>();
    private List<Moneda>       monedas   = new ArrayList<>();
    private List<Pared>        paredes   = new ArrayList<>();
    private List<Zona>         zonas     = new ArrayList<>();
    private List<Bomba>        bombas    = new ArrayList<>();
    private List<FuenteDeVida> fuentes   = new ArrayList<>();

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
        reiniciarMonedas();
    }

    /** Devuelve todas las monedas a su estado inicial (no recolectadas). */
    public void reiniciarMonedas() {
        for (Moneda m : monedas) m.reiniciar();
        monedasPendientes = monedasTotales;
    }

    public void agregarJugador(Jugador j)        { jugadores.add(j); }
    public void agregarEnemigo(Enemigo e)        { enemigos.add(e); }
    public void agregarMoneda(Moneda m)          { monedas.add(m); monedasTotales++; monedasPendientes++; }
    public void agregarPared(Pared p)            { paredes.add(p); }
    public void agregarZona(Zona z)              { zonas.add(z); }
    public void agregarBomba(Bomba b)            { bombas.add(b); }
    public void agregarFuenteDeVida(FuenteDeVida f) { fuentes.add(f); }

    public String         obtenerId()        { return id; }
    public List<Jugador>  getJugadores()     { return jugadores; }
    public List<Enemigo>  getEnemigos()      { return enemigos; }
    public List<Moneda>   getMonedas()       { return monedas; }
    public List<Pared>    getParedes()       { return paredes; }
    public List<Zona>     getZonas()         { return zonas; }
    public List<Bomba>    getBombas()        { return bombas; }
    public List<FuenteDeVida> getFuentes()   { return fuentes; }

    public int    obtenerAncho()           { return ancho; }
    public int    obtenerAlto()            { return alto; }
    public double obtenerTiempoLimite()    { return tiempoLimite; }
    public int    obtenerMonedasPendientes() { return monedasPendientes; }
    public int    obtenerMonedasTotales()    { return monedasTotales; }
    public void   registrarRecoleccion()   { if (monedasPendientes > 0) monedasPendientes--; }
    public boolean estaCompleto()          { return monedasPendientes <= 0; }
}
