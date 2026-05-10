package domain;

public class Jugador extends Elemento {
    private int muertes;
    private int vidas;
    private double velocidad;
    private double velocidadBase;
    private ControlJugador control;
    private int respawnX, respawnY;

    public Jugador(int posX, int posY, int ancho, int alto, ControlJugador control) {
        super(posX, posY, ancho, alto);
        this.control = control;
        this.velocidadBase = 3;
        this.velocidad = velocidadBase;
        this.vidas = 3;
        this.respawnX = posX;
        this.respawnY = posY;
    }

    public void mover(Direction dir) {
        int speed = (int) velocidad;
        switch (dir) {
            case ARRIBA    -> posY -= speed;
            case ABAJO     -> posY += speed;
            case IZQUIERDA -> posX -= speed;
            case DERECHA   -> posX += speed;
            default        -> {}
        }
        actualizarHitBox();
    }

    public void morir() {
        muertes++;
        posX = respawnX;
        posY = respawnY;
        actualizarHitBox();
    }

    public int obtenerMuertes()        { return muertes; }
    public int obtenerVidas()          { return vidas; }
    public ControlJugador getControl() { return control; }

    public void setRespawn(Zona zona) {
        respawnX = zona.obtenerPosX() + 10;
        respawnY = zona.obtenerPosY() + 10;
    }

    public void aplicarSkin(Skin skin) {
        velocidad = skin.obtenerVelocidad() * velocidadBase;
        vidas = skin.obtenerVidas();
    }

    public void restaurarSkin() { velocidad = velocidadBase; }
}
