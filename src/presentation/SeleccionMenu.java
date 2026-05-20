package presentation;

import domain.core.ModoJuego;
import domain.skins.ColorJuego;
import domain.skins.Skin;

public class SeleccionMenu {
    public ModoJuego  modo           = ModoJuego.PLAYER;
    public Skin       skinJ1;
    public Skin       skinJ2;
    public ColorJuego colorJ1        = ColorJuego.NEGRO;
    public ColorJuego colorJ2        = ColorJuego.BLANCO;
    public String     rutaNivel;
    public boolean    maquinaExperta = false;
}
