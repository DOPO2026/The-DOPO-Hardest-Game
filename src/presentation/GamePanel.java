package presentation;

import domain.collectibles.Bomba;
import domain.collectibles.FuenteDeVida;
import domain.collectibles.Moneda;
import domain.core.EstadoJuego;
import domain.core.Nivel;
import domain.core.TheDOPOHardestGame;
import domain.enemy.Enemigo;
import domain.world.Pared;
import domain.world.Zona;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private TheDOPOHardestGame juego;
    private int escala;
    private GameRender render;

    public GamePanel(TheDOPOHardestGame juego, int escala) {
        this.juego = juego;
        this.escala = escala;
        this.render = new GameRender(escala);
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);
    }

    public void ajustarTamanio(int ancho, int alto) {
        setPreferredSize(new Dimension(ancho * escala, alto * escala));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Nivel nivel = juego.getNivelActual();
        if (nivel == null) return;

        g.setColor(new Color(185, 185, 215));
        g.fillRect(0, 0, getWidth(), getHeight());

        boolean multi = juego.getModo() != domain.core.ModoJuego.PLAYER;
        for (Zona z          : nivel.getZonas())    render.dibujarZonaSegura(g, z, escala, multi);
        for (Pared p         : nivel.getParedes())  render.dibujarPared(g, p, escala);
        for (FuenteDeVida f  : nivel.getFuentes())  render.dibujarFuente(g, f, escala);
        for (Bomba b         : nivel.getBombas())   render.dibujarBomba(g, b, escala);
        for (Moneda m        : nivel.getMonedas())  render.dibujarMoneda(g, m, escala);
        for (Enemigo e       : nivel.getEnemigos()) render.dibujarEnemigo(g, e, escala);
        render.dibujarTodosJugadores(g, nivel.getJugadores(), escala);

        if (juego.getEstado() == EstadoJuego.PAUSADO) {
            g.setColor(new Color(0, 0, 0, 130));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 52));
            g.drawString("PAUSA", getWidth() / 2 - 75, getHeight() / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("ESC para continuar", getWidth() / 2 - 85, getHeight() / 2 + 35);
        }

        EstadoJuego estado = juego.getEstado();
        if (estado == EstadoJuego.VICTORIA || estado == EstadoJuego.DERROTA) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(), getHeight());

            Font fontGrande  = new Font("Arial", Font.BOLD, 52);
            Font fontMediano = new Font("Arial", Font.BOLD, 34);
            Font fontPequeno = new Font("Arial", Font.PLAIN, 16);
            int cx = getWidth() / 2;

            // Título principal
            g.setColor(estado == EstadoJuego.VICTORIA ? new Color(80, 255, 80) : new Color(255, 80, 80));
            g.setFont(fontGrande);
            String titulo = estado == EstadoJuego.VICTORIA ? "¡VICTORIA!" : "¡TIEMPO AGOTADO!";
            g.drawString(titulo, cx - g.getFontMetrics().stringWidth(titulo) / 2, getHeight() / 2 - 20);

            // Ganador (solo en multijugador)
            String ganador = juego.obtenerMensajeGanador();
            int lineaGanador = getHeight() / 2 + 30;
            if (!ganador.isEmpty()) {
                String winText = ganador.split("\\|")[0].trim();
                g.setFont(fontMediano);
                g.setColor(Color.WHITE);
                g.drawString(winText, cx - g.getFontMetrics().stringWidth(winText) / 2, lineaGanador);
                lineaGanador += 38;
            }

            // Pista de teclas
            g.setFont(fontPequeno);
            g.setColor(new Color(200, 200, 200));
            String hint = estado == EstadoJuego.VICTORIA
                    ? "N: siguiente nivel  |  S: guardar  |  R: reiniciar  |  M: menú"
                    : "R: reiniciar  |  M: menú";
            g.drawString(hint, cx - g.getFontMetrics().stringWidth(hint) / 2, lineaGanador);
        }
    }

    public void actualizarPantalla() { repaint(); }
}
