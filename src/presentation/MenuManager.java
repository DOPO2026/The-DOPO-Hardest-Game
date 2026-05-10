package presentation;

import domain.*;
import java.awt.Color;

public class MenuManager {
    public int    mostrarMenuPrincipal()          { return 1; }
    public int    mostrarSeleccionModo()          { return 1; }
    public Skin   mostrarSeleccionPersonaje()     { return null; }
    public Color  mostrarSeleccionColor()         { return Color.BLUE; }
    public String mostrarSeleccionConfiguracion() { return ""; }
    public int    mostrarMenuPausa()              { return 0; }
}
