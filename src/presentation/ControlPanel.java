package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private TheDOPOHardestGame juego;
    private JLabel lblTiempo;
    private List<JLabel> lblMuertes;
    private JLabel lblMonedas;
    private JLabel lblEstadoJuego;
    private JLabel lblMensaje;

    public ControlPanel(TheDOPOHardestGame juego) {
        this.juego = juego;
        lblMuertes = new ArrayList<>();
        setLayout(new FlowLayout(FlowLayout.LEFT, 18, 6));
        setBackground(new Color(18, 18, 28));
        setPreferredSize(new Dimension(800, 42));

        lblTiempo      = etiqueta("Tiempo: --");
        JLabel lbl1    = etiqueta("Muertes J1: 0");
        lblMonedas     = etiqueta("Monedas: 0/0");
        lblEstadoJuego = etiqueta("Estado: MENU");
        lblMensaje     = etiqueta("WASD: mover  |  ESC: pausa  |  R: reiniciar");
        lblMensaje.setForeground(new Color(160, 160, 160));

        lblMuertes.add(lbl1);
        for (JLabel l : new JLabel[]{lblTiempo, lbl1, lblMonedas, lblEstadoJuego, lblMensaje}) add(l);
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Monospaced", Font.BOLD, 12));
        return l;
    }

    public void actualizarEstado(double tiempo, List<Integer> muertesPorJugador, int monedas, int total) {
        lblTiempo.setText(String.format("T: %.0fs", tiempo));
        for (int i = 0; i < Math.min(muertesPorJugador.size(), lblMuertes.size()); i++) {
            lblMuertes.get(i).setText("Muertes J" + (i + 1) + ": " + muertesPorJugador.get(i));
        }
        lblMonedas.setText("Monedas: " + monedas + "/" + total);
        lblEstadoJuego.setText("Estado: " + juego.getEstado());
    }

    public void mostrarMensaje(String mensaje) { lblMensaje.setText(mensaje); }
}
