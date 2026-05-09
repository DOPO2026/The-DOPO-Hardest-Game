package presentation;

import domain.*;
import java.awt.*;
import java.util.List;

public class GameRender {
    private int escala;

    public GameRender(int escala) { this.escala = escala; }

    public void dibujarJugador(Graphics g, Jugador j, int escala) {
        int x = j.obtenerPosX() * escala, y = j.obtenerPosY() * escala;
        int w = j.obtenerAncho() * escala, h = j.obtenerAlto() * escala;
        g.setColor(new Color(30, 100, 220));
        g.fillRect(x, y, w, h);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, w, h);
    }

    public void dibujarEnemigo(Graphics g, Enemigo e, int escala) {
        int x = e.obtenerPosX() * escala, y = e.obtenerPosY() * escala;
        int w = e.obtenerAncho() * escala, h = e.obtenerAlto() * escala;
        g.setColor(new Color(220, 40, 40));
        g.fillOval(x, y, w, h);
        g.setColor(new Color(160, 0, 0));
        g.drawOval(x, y, w, h);
    }

    public void dibujarMoneda(Graphics g, Moneda m, int escala) {
        if (!m.estaRecolectada()) {
            g.setColor(Color.YELLOW);
            g.fillOval(m.obtenerPosX() * escala, m.obtenerPosY() * escala,
                       m.obtenerAncho() * escala, m.obtenerAlto() * escala);
        }
    }

    public void dibujarElementoEspecial(Graphics g, Elemento el, int escala) {
        g.setColor(Color.CYAN);
        g.fillRect(el.obtenerPosX() * escala, el.obtenerPosY() * escala,
                   el.obtenerAncho() * escala, el.obtenerAlto() * escala);
    }

    public void dibujarPared(Graphics g, Pared p, int escala) {
        int x = p.obtenerPosX() * escala, y = p.obtenerPosY() * escala;
        int w = p.obtenerAncho() * escala, h = p.obtenerAlto() * escala;
        g.setColor(new Color(40, 40, 70));
        g.fillRect(x, y, w, h);
        g.setColor(new Color(70, 70, 120));
        g.drawRect(x, y, w, h);
    }

    public void dibujarZonaSegura(Graphics g, Zona z, int escala) {
        boolean esFinal = z instanceof ZonaFinal;
        int x = z.obtenerPosX() * escala, y = z.obtenerPosY() * escala;
        int w = z.obtenerAncho() * escala, h = z.obtenerAlto() * escala;
        g.setColor(esFinal ? new Color(60, 200, 60, 80) : new Color(60, 60, 200, 80));
        g.fillRect(x, y, w, h);
        g.setColor(esFinal ? new Color(40, 180, 40) : new Color(40, 40, 180));
        g.drawRect(x, y, w, h);
        // Etiqueta de zona
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString(esFinal ? "META" : "INICIO", x + 5, y + 16);
    }

    public void dibujarTodosJugadores(Graphics g, List<Jugador> jugadores, int escala) {
        for (Jugador j : jugadores) dibujarJugador(g, j, escala);
    }
}
