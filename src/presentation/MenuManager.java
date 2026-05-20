package presentation;

import domain.core.ModoJuego;
import domain.skins.Blinky;
import domain.skins.Clyde;
import domain.skins.ColorJuego;
import domain.skins.Inky;
import domain.skins.Skin;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Menú navegable construido con CardLayout. Las pantallas son:
 *   principal → modo → personaje (J1 [→ J2]) → color (J1 [→ J2]) → nivel → callback
 * Cuando se completa la selección, invoca alListo con el SeleccionMenu.
 */
public class MenuManager extends JPanel {

    private final CardLayout layout = new CardLayout();
    private final JPanel cards      = new JPanel(layout);
    private final SeleccionMenu seleccion = new SeleccionMenu();
    private final Consumer<SeleccionMenu> alListo;
    private final Runnable alCargar;
    private boolean hayPartidaGuardada = false;
    private JButton btnCargar;

    private int jugadorActual = 0; // 0 = J1, 1 = J2

    public MenuManager(Consumer<SeleccionMenu> alListo, Runnable alCargar) {
        this.alListo  = alListo;
        this.alCargar = alCargar;
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 28));
        cards.setOpaque(false);
        add(cards, BorderLayout.CENTER);

        cards.add(crearPanelPrincipal(),  "principal");
        cards.add(crearPanelModo(),       "modo");
        cards.add(crearPanelMaquina(),    "maquina");
        cards.add(crearPanelPersonaje(),  "personaje");
        cards.add(crearPanelColor(),      "color");
        cards.add(crearPanelNivel(),      "nivel");

        layout.show(cards, "principal");
    }

    public void mostrarPrincipal() {
        hayPartidaGuardada = new java.io.File("resources/saves/partida.txt").exists();
        if (btnCargar != null) btnCargar.setVisible(hayPartidaGuardada);
        layout.show(cards, "principal");
    }

    public void marcarPartidaGuardada(boolean existe) {
        hayPartidaGuardada = existe;
        if (btnCargar != null) btnCargar.setVisible(existe);
    }

    // ── Pantalla 1: Principal ────────────────────────────────────────────────
    private JPanel crearPanelPrincipal() {
        JPanel p = panelBase();
        p.add(titulo("THE DOPO HARDEST GAME"));
        p.add(Box.createVerticalStrut(30));
        p.add(subtitulo("Rodriguez & Villamizar"));
        p.add(Box.createVerticalStrut(8));
        p.add(subtitulo("DOPO 2026-1"));
        p.add(Box.createVerticalStrut(60));
        p.add(boton("JUGAR",  () -> layout.show(cards, "modo")));
        p.add(Box.createVerticalStrut(15));
        btnCargar = boton("CARGAR PARTIDA", alCargar::run);
        btnCargar.setBackground(new Color(40, 120, 80));
        btnCargar.setVisible(new java.io.File("resources/saves/partida.txt").exists());
        p.add(btnCargar);
        p.add(Box.createVerticalStrut(15));
        p.add(boton("SALIR",  () -> System.exit(0)));
        return p;
    }

    // ── Pantalla 2: Modo de juego ────────────────────────────────────────────
    private JPanel crearPanelModo() {
        JPanel p = panelBase();
        p.add(titulo("SELECCIONA MODO"));
        p.add(Box.createVerticalStrut(40));

        p.add(botonDescriptivo("1 JUGADOR",        "Un humano contra el nivel",      () -> elegirModo(ModoJuego.PLAYER)));
        p.add(Box.createVerticalStrut(15));
        p.add(botonDescriptivo("JUGADOR vs JUGADOR", "Dos humanos en zonas opuestas",   () -> elegirModo(ModoJuego.PvsP)));
        p.add(Box.createVerticalStrut(15));
        p.add(botonDescriptivo("JUGADOR vs MAQUINA", "Humano contra IA aleatoria",      () -> elegirModo(ModoJuego.PvsM)));
        p.add(Box.createVerticalStrut(30));
        p.add(botonSecundario("← Atrás", () -> layout.show(cards, "principal")));
        return p;
    }

    private void elegirModo(ModoJuego modo) {
        seleccion.modo = modo;
        jugadorActual = 0;
        if (modo == ModoJuego.PvsM) {
            layout.show(cards, "maquina");
        } else {
            actualizarPanelPersonaje();
            layout.show(cards, "personaje");
        }
    }

    // ── Pantalla 2b: Tipo de máquina (solo PvsM) ─────────────────────────────
    private JPanel crearPanelMaquina() {
        JPanel p = panelBase();
        p.add(titulo("TIPO DE MÁQUINA"));
        p.add(Box.createVerticalStrut(20));
        p.add(subtitulo("Elige el perfil de la IA adversaria"));
        p.add(Box.createVerticalStrut(40));
        p.add(botonDescriptivo("ALEATORIA", "Se mueve sin estrategia fija",       () -> elegirMaquina(false)));
        p.add(Box.createVerticalStrut(15));
        p.add(botonDescriptivo("EXPERTA",   "Navega con estrategia óptima (BFS)", () -> elegirMaquina(true)));
        p.add(Box.createVerticalStrut(30));
        p.add(botonSecundario("← Atrás", () -> layout.show(cards, "modo")));
        return p;
    }

    private void elegirMaquina(boolean experta) {
        seleccion.maquinaExperta = experta;
        actualizarPanelPersonaje();
        layout.show(cards, "personaje");
    }

    // ── Pantalla 3: Personaje (skin) ─────────────────────────────────────────
    private JPanel panelPersonaje;
    private JLabel lblPersonaje;
    private JPanel crearPanelPersonaje() {
        panelPersonaje = panelBase();
        lblPersonaje   = titulo("PERSONAJE J1");
        panelPersonaje.add(lblPersonaje);
        panelPersonaje.add(Box.createVerticalStrut(20));
        panelPersonaje.add(subtitulo("Atributos: velocidad / vidas / tamaño"));
        panelPersonaje.add(Box.createVerticalStrut(30));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        fila.setOpaque(false);
        fila.add(tarjetaPersonaje(new Blinky(), "Rápido y frágil"));
        fila.add(tarjetaPersonaje(new Clyde(),  "Más vidas (escudo)"));
        fila.add(tarjetaPersonaje(new Inky(),   "Veloz pero grande"));
        panelPersonaje.add(fila);
        return panelPersonaje;
    }

    private void actualizarPanelPersonaje() {
        lblPersonaje.setText(jugadorActual == 0 ? "PERSONAJE J1" : "PERSONAJE J2");
    }

    private JComponent tarjetaPersonaje(Skin skin, String descripcion) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(36, 36, 56));
        card.setBorder(BorderFactory.createLineBorder(toAwt(skin.obtenerColor()), 3));
        card.setPreferredSize(new Dimension(190, 220));

        JLabel nombre = new JLabel(skin.obtenerNombre());
        nombre.setForeground(toAwt(skin.obtenerColor()));
        nombre.setFont(new Font("Arial", Font.BOLD, 22));
        nombre.setAlignmentX(CENTER_ALIGNMENT);

        JPanel muestra = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int s = 50;
                g.setColor(toAwt(skin.obtenerColor()));
                g.fillRect(getWidth()/2 - s/2, 10, s, s);
            }
        };
        muestra.setOpaque(false);
        muestra.setPreferredSize(new Dimension(190, 70));
        muestra.setMaximumSize(new Dimension(190, 70));

        JLabel stats = new JLabel(String.format(
                "<html><center>Vel: %.1fx<br>Vidas: %d<br>Tam: %.1fx</center></html>",
                skin.obtenerVelocidad(), skin.obtenerVidas(), skin.obtenerTamanio()));
        stats.setForeground(Color.WHITE);
        stats.setAlignmentX(CENTER_ALIGNMENT);
        stats.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JLabel desc = new JLabel(descripcion);
        desc.setForeground(new Color(180, 180, 180));
        desc.setAlignmentX(CENTER_ALIGNMENT);
        desc.setFont(new Font("Arial", Font.ITALIC, 11));

        JButton seleccionar = new JButton("Elegir");
        seleccionar.setAlignmentX(CENTER_ALIGNMENT);
        seleccionar.addActionListener(ev -> elegirSkin(skin));

        card.add(Box.createVerticalStrut(10));
        card.add(nombre);
        card.add(muestra);
        card.add(stats);
        card.add(Box.createVerticalStrut(6));
        card.add(desc);
        card.add(Box.createVerticalStrut(8));
        card.add(seleccionar);
        return card;
    }

    private void elegirSkin(Skin skin) {
        if (jugadorActual == 0) seleccion.skinJ1 = skin;
        else                    seleccion.skinJ2 = skin;
        actualizarPanelColor();
        layout.show(cards, "color");
    }

    // ── Pantalla 4: Color de borde ───────────────────────────────────────────
    private JPanel panelColor;
    private JLabel lblColor;
    private JPanel crearPanelColor() {
        panelColor = panelBase();
        lblColor   = titulo("COLOR DE BORDE J1");
        panelColor.add(lblColor);
        panelColor.add(Box.createVerticalStrut(20));
        panelColor.add(subtitulo("Identifica visualmente a tu jugador"));
        panelColor.add(Box.createVerticalStrut(40));

        JPanel grid = new JPanel(new GridLayout(2, 4, 18, 18));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(640, 200));
        for (ColorJuego c : ColorJuego.values()) grid.add(swatch(c));
        panelColor.add(grid);
        return panelColor;
    }

    private void actualizarPanelColor() {
        lblColor.setText(jugadorActual == 0 ? "COLOR DE BORDE J1" : "COLOR DE BORDE J2");
    }

    private JButton swatch(ColorJuego c) {
        JButton b = new JButton(c.name());
        b.setBackground(toAwt(c));
        b.setForeground(brilloRelativo(c) < 128 ? Color.WHITE : Color.BLACK);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140, 80));
        b.addActionListener(ev -> elegirColor(c));
        return b;
    }

    private void elegirColor(ColorJuego c) {
        if (jugadorActual == 0) seleccion.colorJ1 = c;
        else                    seleccion.colorJ2 = c;

        boolean necesitaJ2 = (seleccion.modo == ModoJuego.PvsP) && jugadorActual == 0;
        if (necesitaJ2) {
            jugadorActual = 1;
            actualizarPanelPersonaje();
            layout.show(cards, "personaje");
        } else {
            layout.show(cards, "nivel");
        }
    }

    // ── Pantalla 5: Selección de nivel ───────────────────────────────────────
    private JPanel crearPanelNivel() {
        JPanel p = panelBase();
        p.add(titulo("SELECCIONA NIVEL"));
        p.add(Box.createVerticalStrut(20));
        p.add(subtitulo("Configuraciones disponibles"));
        p.add(Box.createVerticalStrut(40));

        String[][] niveles = {
                {"NIVEL 1", "El original — 90s",       "resources/configuraciones/nivel1.txt"},
                {"NIVEL 2", "Checkpoint + skin — 75s", "resources/configuraciones/nivel2.txt"},
                {"NIVEL 3", "Acelerados + bomba — 60s","resources/configuraciones/nivel3.txt"},
        };
        for (String[] n : niveles) {
            p.add(botonDescriptivo(n[0], n[1], () -> elegirNivel(n[2])));
            p.add(Box.createVerticalStrut(12));
        }
        return p;
    }

    private void elegirNivel(String ruta) {
        seleccion.rutaNivel = ruta;
        if (seleccion.skinJ1 == null) seleccion.skinJ1 = new Blinky();
        if (seleccion.modo != ModoJuego.PLAYER && seleccion.skinJ2 == null) seleccion.skinJ2 = new Inky();
        alListo.accept(seleccion);
    }

    // ── Helpers visuales ─────────────────────────────────────────────────────
    private JPanel panelBase() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(60, 60, 40, 60));
        return p;
    }

    private JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 36));
        l.setForeground(new Color(230, 230, 240));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.ITALIC, 15));
        l.setForeground(new Color(180, 180, 200));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 18));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(60, 100, 180));
        b.setFocusPainted(false);
        b.setMaximumSize(new Dimension(280, 50));
        b.setAlignmentX(CENTER_ALIGNMENT);
        b.addActionListener(ev -> accion.run());
        return b;
    }

    private JButton botonSecundario(String texto, Runnable accion) {
        JButton b = boton(texto, accion);
        b.setBackground(new Color(80, 80, 100));
        b.setFont(new Font("Arial", Font.PLAIN, 14));
        b.setMaximumSize(new Dimension(160, 36));
        return b;
    }

    private JPanel botonDescriptivo(String titulo, String descripcion, Runnable accion) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(new Color(36, 36, 56));
        p.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 100), 1));
        p.setMaximumSize(new Dimension(520, 70));
        p.setPreferredSize(new Dimension(520, 70));
        p.setAlignmentX(CENTER_ALIGNMENT);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Arial", Font.BOLD, 18));
        t.setForeground(Color.WHITE);
        t.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JLabel d = new JLabel(descripcion);
        d.setFont(new Font("Arial", Font.PLAIN, 13));
        d.setForeground(new Color(180, 180, 200));
        d.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JPanel txt = new JPanel(new GridLayout(2, 1));
        txt.setOpaque(false);
        txt.add(t);
        txt.add(d);

        JButton ir = new JButton("▶");
        ir.setFont(new Font("Arial", Font.BOLD, 22));
        ir.setForeground(Color.WHITE);
        ir.setBackground(new Color(60, 100, 180));
        ir.setFocusPainted(false);
        ir.setPreferredSize(new Dimension(70, 70));
        ir.addActionListener(ev -> accion.run());

        p.add(txt, BorderLayout.CENTER);
        p.add(ir,  BorderLayout.EAST);
        return p;
    }

    private static Color toAwt(ColorJuego c) { return new Color(c.r, c.g, c.b); }
    private static int brilloRelativo(ColorJuego c) { return (c.r * 299 + c.g * 587 + c.b * 114) / 1000; }
}
