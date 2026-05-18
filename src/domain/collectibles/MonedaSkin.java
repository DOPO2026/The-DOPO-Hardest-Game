package domain.collectibles;

import domain.skins.Skin;

public class MonedaSkin extends Moneda {
    private Skin skinOtorgada;

    public MonedaSkin(int posX, int posY, int ancho, int alto, Skin skinOtorgada) {
        super(posX, posY, ancho, alto);
        this.skinOtorgada = skinOtorgada;
    }

    @Override
    public void recolectar() {
        super.recolectar();
    }

    public Skin obtenerSkinOtorgada() { return skinOtorgada; }
}
