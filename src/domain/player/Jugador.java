package domain.player;

import domain.common.Elemento;
import domain.common.HitBox;
import domain.core.TheDopoHardestGameException;
import domain.core.Nivel;
import domain.skins.ColorJuego;
import domain.skins.Skin;
import domain.world.Pared;
import domain.world.Zona;

public class Jugador extends Elemento {
    private int muertes;
    private int vidas;
    private double velocidad;
    private double velocidadBase;
    private ControlJugador control;
    private Skin skinOriginal;
    private Skin skinActual;
    private ColorJuego colorBorde;
    private int respawnX, respawnY;
    private int anchoBase, altoBase;

    public Jugador(int posX, int posY, int ancho, int alto, ControlJugador control,
                   Skin skin, ColorJuego colorBorde) {
        super(posX, posY, ancho, alto);
        this.control = control;
        this.skinOriginal = skin;
        this.colorBorde = colorBorde;
        this.velocidadBase = 3;
        this.anchoBase = ancho;
        this.altoBase = alto;
        this.respawnX = posX;
        this.respawnY = posY;
        aplicarSkin(skin);
    }

    public void mover(Direction dir, Nivel nivel) {
        int speed = (int) Math.round(velocidad);
        int dx = dir.dx * speed;
        int dy = dir.dy * speed;

        if (dx != 0 && !colisionaConParedes(posX + dx, posY, nivel)) {
            posX += dx;
        }
        if (dy != 0 && !colisionaConParedes(posX, posY + dy, nivel)) {
            posY += dy;
        }
        actualizarHitBox();
    }

    private boolean colisionaConParedes(int x, int y, Nivel nivel) {
        HitBox prueba = new HitBox(x, y, ancho, alto);
        for (Pared p : nivel.getParedes()) {
            if (prueba.intersecta(p.getHitBox())) return true;
        }
        return false;
    }

    /**
     * Pierde un escudo si tiene; si no, acumula una muerte y reaparece.
     * En ambos casos el jugador es teletransportado al respawn para evitar
     * que el mismo enemigo lo golpee repetidamente cada frame.
     */
    public boolean recibirGolpe() {
        if (vidas > 1) {
            vidas--;
            posX = respawnX;
            posY = respawnY;
            actualizarHitBox();
            return false;
        }
        morir();
        return true;
    }

    public void morir() {
        muertes++;
        posX = respawnX;
        posY = respawnY;
        restaurarSkin();
        actualizarHitBox();
    }

    public void agregarEscudo()           { vidas++; }
    public void setMuertes(int m)         { muertes = m; }
    public int obtenerMuertes()           { return muertes; }
    public int obtenerVidas()             { return vidas; }
    public ControlJugador getControl()    { return control; }
    public Skin obtenerSkinActual()       { return skinActual; }
    public Skin obtenerSkinOriginal()     { return skinOriginal; }
    public ColorJuego obtenerColorBorde() { return colorBorde; }

    public void setColorBorde(ColorJuego c) { this.colorBorde = c; }

    public void setRespawn(Zona zona) {
        respawnX = zona.obtenerPosX() + (zona.obtenerAncho() - ancho) / 2;
        respawnY = zona.obtenerPosY() + (zona.obtenerAlto()  - alto)  / 2;
    }

    public void aplicarSkin(Skin skin) {
        if (skin == null) throw TheDopoHardestGameException.skinNull();
        this.skinActual = skin;
        this.velocidad  = velocidadBase * skin.obtenerVelocidad();
        this.vidas      = skin.obtenerVidas();
        double factor   = skin.obtenerTamanio();
        this.ancho      = (int) Math.round(anchoBase * factor);
        this.alto       = (int) Math.round(altoBase  * factor);
        actualizarHitBox();
    }

    public void restaurarSkin() {
        aplicarSkin(skinOriginal);
    }
}
