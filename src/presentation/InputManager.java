package presentation;

import domain.*;
import java.awt.event.*;
import java.util.*;

public class InputManager {
    private TheDOPOHardestGame juego;
    private Map<Integer, Boolean>   teclasPresionadas;
    private Map<Integer, Direction> esquemaTeclas;
    private ControlHumano control;

    public InputManager(TheDOPOHardestGame juego, ControlHumano control,
                        int arriba, int abajo, int izquierda, int derecha) {
        this.juego = juego;
        this.control = control;
        teclasPresionadas = new HashMap<>();
        esquemaTeclas = new HashMap<>();
        esquemaTeclas.put(arriba,    Direction.ARRIBA);
        esquemaTeclas.put(abajo,     Direction.ABAJO);
        esquemaTeclas.put(izquierda, Direction.IZQUIERDA);
        esquemaTeclas.put(derecha,   Direction.DERECHA);
    }

    public boolean esTeclaPresionada(int codigoTecla) {
        return teclasPresionadas.getOrDefault(codigoTecla, false);
    }

    public void procesarTecla(KeyEvent evento) {
        int code = evento.getKeyCode();
        teclasPresionadas.put(code, true);
        Direction dir = esquemaTeclas.get(code);
        if (dir != null) control.registrarDireccion(dir);
    }

    public void procesarTeclaLiberada(KeyEvent evento) {
        teclasPresionadas.put(evento.getKeyCode(), false);
        boolean hayDireccion = esquemaTeclas.keySet().stream()
                .anyMatch(k -> teclasPresionadas.getOrDefault(k, false));
        if (!hayDireccion) control.registrarDireccion(Direction.QUIETO);
    }

    public Direction obtenerDireccion(Jugador jugador) {
        for (Map.Entry<Integer, Direction> e : esquemaTeclas.entrySet()) {
            if (esTeclaPresionada(e.getKey())) return e.getValue();
        }
        return Direction.QUIETO;
    }
}
