package presentation;

import domain.core.TheDOPOHardestGame;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private TheDOPOHardestGame juego;
    private JLabel lblTiempo;
    private JLabel lblMonedas;
    private JLabel lblEstadoJuego;
    private JLabel lblMensaje;
    private JPanel panelStatsJugadores;
    private List<JLabel> lblMuertes;
    private List<JLabel> lblVidas;

    public ControlPanel(TheDOPOHardestGame juego) {
        this.juego = juego;
        lblMuertes = new ArrayList<>();
        lblVidas   = new ArrayList<>();
        setLayout(new FlowLayout(FlowLayout.LEFT, 14, 6));
        setBackground(new Color(18, 18, 28));
        setPreferredSize(new Dimension(800, 42));

        lblTiempo           = etiqueta("Tiempo: --");
        panelStatsJugadores = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelStatsJugadores.setOpaque(false);
        lblMonedas          = etiqueta("Monedas: 0/0");
        lblEstadoJuego      = etiqueta("Estado: MENU");
        lblMensaje          = etiqueta("WASD: mover  |  ESC: pausa  |  R: reiniciar  |  M: menú");
        lblMensaje.setForeground(new Color(160, 160, 160));

        add(lblTiempo);
        add(panelStatsJugadores);
        add(lblMonedas);
        add(lblEstadoJuego);
        add(lblMensaje);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Monospaced", Font.BOLD, 12));
        return l;
    }

    private void asegurarFilasJugadores(int n) {
        while (lblMuertes.size() < n) {
            int idx = lblMuertes.size() + 1;
            JLabel lm = etiqueta("M J" + idx + ": 0");
            JLabel lv = etiqueta("♥ J" + idx + ": 1");
            lv.setForeground(new Color(255, 120, 120));
            lblMuertes.add(lm);
            lblVidas.add(lv);
            panelStatsJugadores.add(lm);
            panelStatsJugadores.add(lv);
        }
        panelStatsJugadores.revalidate();
    }

    public void actualizarEstado(double tiempo, List<Integer> muertesPorJugador,
                                 List<Integer> vidasPorJugador, int monedas, int total) {
        lblTiempo.setText(String.format("T: %.0fs", tiempo));
        asegurarFilasJugadores(muertesPorJugador.size());
        for (int i = 0; i < muertesPorJugador.size(); i++) {
            lblMuertes.get(i).setText("M J" + (i + 1) + ": " + muertesPorJugador.get(i));
            lblVidas.get(i).setText("♥ J" + (i + 1) + ": " + vidasPorJugador.get(i));
        }
        lblMonedas.setText("Monedas: " + monedas + "/" + total);
        lblEstadoJuego.setText("Estado: " + juego.getEstado());
    }

    public void mostrarMensaje(String mensaje) { lblMensaje.setText(mensaje); }
}
