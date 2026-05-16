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
        g.setColor(toAwt(j.obtenerSkinActual().obtenerColor()));
        g.fillRect(x, y, w, h);
        Graphics2D g2 = (Graphics2D) g;
        Stroke prev = g2.getStroke();
        g2.setStroke(new BasicStroke(3));
        g2.setColor(toAwt(j.obtenerColorBorde()));
        g2.drawRect(x, y, w, h);
        g2.setStroke(prev);
    }

    public void dibujarEnemigo(Graphics g, Enemigo e, int escala) {
        int x = e.obtenerPosX() * escala, y = e.obtenerPosY() * escala;
        int w = e.obtenerAncho() * escala, h = e.obtenerAlto() * escala;
        g.setColor(new Color(40, 80, 220));
        g.fillOval(x, y, w, h);
        g.setColor(new Color(20, 40, 140));
        g.drawOval(x, y, w, h);
    }

    public void dibujarMoneda(Graphics g, Moneda m, int escala) {
        if (m.estaRecolectada()) return;
        int x = m.obtenerPosX() * escala, y = m.obtenerPosY() * escala;
        int w = m.obtenerAncho() * escala, h = m.obtenerAlto() * escala;
        if (m instanceof MonedaSkin ms) {
            g.setColor(toAwt(ms.obtenerSkinOtorgada().obtenerColor()));
        } else {
            g.setColor(Color.YELLOW);
        }
        g.fillOval(x, y, w, h);
        g.setColor(Color.ORANGE);
        g.drawOval(x, y, w, h);
    }

    public void dibujarBomba(Graphics g, Bomba b, int escala) {
        if (!b.estaActivo()) return;
        int x = b.obtenerPosX() * escala, y = b.obtenerPosY() * escala;
        int w = b.obtenerAncho() * escala, h = b.obtenerAlto() * escala;
        g.setColor(Color.BLACK);
        g.fillOval(x, y, w, h);
        g.setColor(Color.RED);
        g.drawOval(x, y, w, h);
    }

    public void dibujarFuente(Graphics g, FuenteDeVida f, int escala) {
        if (!f.estaActivo()) return;
        int x = f.obtenerPosX() * escala, y = f.obtenerPosY() * escala;
        int w = f.obtenerAncho() * escala, h = f.obtenerAlto() * escala;
        g.setColor(new Color(255, 105, 180));
        g.fillRect(x, y, w, h);
        g.setColor(Color.WHITE);
        g.drawString("+", x + w / 3, y + h - 4);
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
        int x = z.obtenerPosX() * escala, y = z.obtenerPosY() * escala;
        int w = z.obtenerAncho() * escala, h = z.obtenerAlto() * escala;
        Color relleno, borde;
        String etiqueta;
        if (z instanceof ZonaFinal) {
            relleno = new Color(60, 200, 60, 90);
            borde   = new Color(40, 160, 40);
            etiqueta = "META";
        } else if (z instanceof ZonaIntermedia) {
            relleno = new Color(255, 215, 0, 90);
            borde   = new Color(190, 160, 0);
            etiqueta = "CHECK";
        } else {
            relleno = new Color(60, 200, 60, 90);
            borde   = new Color(40, 160, 40);
            etiqueta = "INICIO";
        }
        g.setColor(relleno);
        g.fillRect(x, y, w, h);
        g.setColor(borde);
        g.drawRect(x, y, w, h);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.drawString(etiqueta, x + 5, y + 14);
    }

    public void dibujarTodosJugadores(Graphics g, List<Jugador> jugadores, int escala) {
        for (Jugador j : jugadores) dibujarJugador(g, j, escala);
    }

    private static Color toAwt(ColorJuego c) {
        return new Color(c.r, c.g, c.b);
    }
}
