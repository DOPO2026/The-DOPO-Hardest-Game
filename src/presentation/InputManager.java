package presentation;

import domain.core.TheDOPOHardestGame;
import domain.player.ControlHumano;
import domain.player.Direction;
import java.awt.event.*;
import java.util.*;

public class InputManager {
    private final ControlHumano control;
    private final Map<Integer, Boolean> teclasPresionadas = new HashMap<>();
    private final int teclaArriba, teclaAbajo, teclaIzquierda, teclaDerecha;

    public InputManager(TheDOPOHardestGame juego, ControlHumano control,
                        int arriba, int abajo, int izquierda, int derecha) {
        this.control = control;
        this.teclaArriba = arriba;
        this.teclaAbajo = abajo;
        this.teclaIzquierda = izquierda;
        this.teclaDerecha = derecha;
    }

    public boolean esTeclaPresionada(int codigoTecla) {
        return teclasPresionadas.getOrDefault(codigoTecla, false);
    }

    public void procesarTecla(KeyEvent evento) {
        teclasPresionadas.put(evento.getKeyCode(), true);
        actualizarDireccion();
    }

    public void procesarTeclaLiberada(KeyEvent evento) {
        teclasPresionadas.put(evento.getKeyCode(), false);
        actualizarDireccion();
    }

    private void actualizarDireccion() {
        boolean n = esTeclaPresionada(teclaArriba);
        boolean s = esTeclaPresionada(teclaAbajo);
        boolean e = esTeclaPresionada(teclaDerecha);
        boolean w = esTeclaPresionada(teclaIzquierda);

        Direction dir;
        if      (n && e) dir = Direction.NORESTE;
        else if (n && w) dir = Direction.NOROESTE;
        else if (s && e) dir = Direction.SURESTE;
        else if (s && w) dir = Direction.SUROESTE;
        else if (n)      dir = Direction.NORTE;
        else if (s)      dir = Direction.SUR;
        else if (e)      dir = Direction.ESTE;
        else if (w)      dir = Direction.OESTE;
        else             dir = Direction.QUIETO;

        control.registrarDireccion(dir);
    }
}
