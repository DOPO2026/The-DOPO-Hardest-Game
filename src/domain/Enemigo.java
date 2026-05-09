package domain;

public class Enemigo extends Elemento {
    private int velocidad;
    private String direccion;
    private boolean activo;
    private EstrategiaMovimiento estrategia;

    public Enemigo(int posX, int posY, int ancho, int alto, EstrategiaMovimiento estrategia) {
        super(posX, posY, ancho, alto);
        this.estrategia = estrategia;
        this.activo = true;
        this.velocidad = 2;
    }

    public void actualizar(double deltaTime, Nivel nivel) {
        if (activo && estrategia != null) {
            estrategia.actualizar(this, nivel, deltaTime);
        }
    }

    /** Llamado por las estrategias para desplazar el enemigo. */
    public void mover(int dx, int dy) {
        posX += dx;
        posY += dy;
        actualizarHitBox();
    }

    public void desactivar() { activo = false; }

    @Override
    public boolean estaActivo() { return activo; }
}
