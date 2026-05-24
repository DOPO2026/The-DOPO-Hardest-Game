package presentation;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.KeyEvent;
import java.util.function.BooleanSupplier;

/**
 * Barra de menú superior del juego. Expone el menú "Archivo" con las acciones
 * de persistencia: abrir una partida guardada, guardarla en un archivo elegido
 * por el usuario y cerrar la aplicación.
 *
 * Construida con el patrón prepareElements (crea y dispone los componentes) /
 * prepareActions (conecta los listeners).
 */
public class BarraMenu extends JMenuBar {

    private JMenu menuArchivo;
    private JMenuItem itemAbrir;
    private JMenuItem itemGuardarComo;
    private JMenuItem itemCerrar;

    private final Runnable accionAbrir;
    private final Runnable accionGuardarComo;
    private final Runnable accionCerrar;
    private final BooleanSupplier puedeGuardar;

    public BarraMenu(Runnable accionAbrir, Runnable accionGuardarComo,
                     Runnable accionCerrar, BooleanSupplier puedeGuardar) {
        this.accionAbrir       = accionAbrir;
        this.accionGuardarComo = accionGuardarComo;
        this.accionCerrar      = accionCerrar;
        this.puedeGuardar      = puedeGuardar;
        prepareElements();
        prepareActions();
    }

    private void prepareElements() {
        menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic(KeyEvent.VK_A);

        itemAbrir       = new JMenuItem("Abrir...");
        itemGuardarComo = new JMenuItem("Guardar como...");
        itemCerrar      = new JMenuItem("Cerrar");

        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardarComo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemCerrar);

        add(menuArchivo);
    }

    private void prepareActions() {
        itemAbrir.addActionListener(e -> accionAbrir.run());
        itemGuardarComo.addActionListener(e -> accionGuardarComo.run());
        itemCerrar.addActionListener(e -> accionCerrar.run());

        // "Guardar como" solo tiene sentido si hay una partida activa: se evalúa
        // cada vez que el usuario abre el menú.
        menuArchivo.addMenuListener(new MenuListener() {
            @Override public void menuSelected(MenuEvent e) {
                itemGuardarComo.setEnabled(puedeGuardar.getAsBoolean());
            }
            @Override public void menuDeselected(MenuEvent e) {}
            @Override public void menuCanceled(MenuEvent e) {}
        });
    }
}
