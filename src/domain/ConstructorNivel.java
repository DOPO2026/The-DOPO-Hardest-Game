package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConstructorNivel {

    private static final int TAM_MONEDA   = 14;
    private static final int TAM_ENEMIGO  = 20;
    private static final int TAM_ESPECIAL = 20;
    private static final int GROSOR_PARED = 10;

    private Nivel nivel;

    /**
     * Construye un nivel a partir de un archivo de configuración.
     * @throws TheDopoHardestGameException si la ruta es nula/vacía, el archivo no
     *         existe o no puede leerse, o si una línea tiene formato inválido.
     */
    public Nivel construirDesdeArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new TheDopoHardestGameException("Ruta de nivel vacía o nula");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            String id      = "?";
            int    ancho   = 800;
            int    alto    = 600;
            double tiempo  = 60;

            // Primera pasada: metadata
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] t = linea.split(";");
                try {
                    switch (t[0]) {
                        case "NIVEL"   -> id     = t[1];
                        case "TIEMPO"  -> tiempo = Double.parseDouble(t[1]);
                        case "TABLERO" -> { ancho = Integer.parseInt(t[1]); alto = Integer.parseInt(t[2]); }
                        default        -> {}
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                    throw new TheDopoHardestGameException("Línea inválida en metadata: " + linea);
                }
            }
            nivel = new Nivel(id, ancho, alto, tiempo);
            agregarParedesPerimetrales(nivel, ancho, alto);

            // Segunda pasada: contenido
            try (BufferedReader br2 = new BufferedReader(new FileReader(rutaArchivo))) {
                while ((linea = br2.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty() || linea.startsWith("#")) continue;
                    String[] t = linea.split(";");
                    try {
                        switch (t[0]) {
                            case "ZONA"     -> construirZona(t);
                            case "ENEMIGO"  -> construirEnemigo(t);
                            case "MONEDA"   -> construirMoneda(t);
                            case "ELEMENTO" -> construirElemento(t);
                            case "PARED"    -> construirPared(t);
                            default         -> {}
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
                        throw new TheDopoHardestGameException("Línea con formato inválido: " + linea);
                    }
                }
            }
            return nivel;
        } catch (IOException ex) {
            throw new TheDopoHardestGameException("No se pudo cargar el nivel: " + rutaArchivo);
        }
    }

    private void agregarParedesPerimetrales(Nivel n, int ancho, int alto) {
        int g = GROSOR_PARED;
        n.agregarPared(new Pared(0,         0,        ancho, g));
        n.agregarPared(new Pared(0,         alto - g, ancho, g));
        n.agregarPared(new Pared(0,         0,        g,     alto));
        n.agregarPared(new Pared(ancho - g, 0,        g,     alto));
    }

    private void construirZona(String[] t) {
        String tipo = t[1];
        int x = Integer.parseInt(t[2]);
        int y = Integer.parseInt(t[3]);
        int w = Integer.parseInt(t[4]);
        int h = Integer.parseInt(t[5]);
        switch (tipo) {
            case "INICIO"     -> nivel.agregarZona(new ZonaInicial(x, y, w, h));
            case "FIN"        -> nivel.agregarZona(new ZonaFinal(x, y, w, h));
            case "INTERMEDIA" -> nivel.agregarZona(new ZonaIntermedia(x, y, w, h));
            default           -> throw new TheDopoHardestGameException("Tipo de zona desconocido: " + tipo);
        }
    }

    private void construirEnemigo(String[] t) {
        String tipo = t[1];
        int x = Integer.parseInt(t[2]);
        int y = Integer.parseInt(t[3]);
        EstrategiaMovimiento estrategia = switch (tipo) {
            case "BASICO"      -> new Basico(
                    Integer.parseInt(t[4]), Integer.parseInt(t[5]),
                    Integer.parseInt(t[6]), Integer.parseInt(t[7]),
                    Integer.parseInt(t[8]), Integer.parseInt(t[9]));
            case "ACELERADO"   -> new Acelerado(
                    Integer.parseInt(t[4]), Integer.parseInt(t[5]),
                    Integer.parseInt(t[6]), Integer.parseInt(t[7]),
                    Integer.parseInt(t[8]), Integer.parseInt(t[9]));
            case "DESLIZADORV" -> new DeslizadorVertical(
                    Integer.parseInt(t[4]), Integer.parseInt(t[5]), 3);
            default            -> throw new TheDopoHardestGameException("Tipo de enemigo desconocido: " + tipo);
        };
        nivel.agregarEnemigo(new Enemigo(x, y, TAM_ENEMIGO, TAM_ENEMIGO, estrategia));
    }

    private void construirMoneda(String[] t) {
        String tipo = t[1];
        if (tipo.equals("AMARILLA")) {
            int x = Integer.parseInt(t[2]);
            int y = Integer.parseInt(t[3]);
            nivel.agregarMoneda(new MonedaAmarilla(x, y, TAM_MONEDA, TAM_MONEDA));
        } else if (tipo.equals("SKIN")) {
            String color = t[2];
            int x = Integer.parseInt(t[3]);
            int y = Integer.parseInt(t[4]);
            Skin skin = switch (color) {
                case "ROJO"  -> new Blinky();
                case "VERDE" -> new Clyde();
                case "AZUL"  -> new Inky();
                default      -> throw new TheDopoHardestGameException("Color de skin desconocido: " + color);
            };
            nivel.agregarMoneda(new MonedaSkin(x, y, TAM_MONEDA, TAM_MONEDA, skin));
        } else {
            throw new TheDopoHardestGameException("Tipo de moneda desconocido: " + tipo);
        }
    }

    private void construirPared(String[] t) {
        int x = Integer.parseInt(t[1]);
        int y = Integer.parseInt(t[2]);
        int w = Integer.parseInt(t[3]);
        int h = Integer.parseInt(t[4]);
        nivel.agregarPared(new Pared(x, y, w, h));
    }

    private void construirElemento(String[] t) {
        String tipo = t[1];
        int x = Integer.parseInt(t[2]);
        int y = Integer.parseInt(t[3]);
        switch (tipo) {
            case "BOMBA"  -> nivel.agregarBomba(new Bomba(x, y, TAM_ESPECIAL, TAM_ESPECIAL));
            case "FUENTE" -> nivel.agregarFuenteDeVida(new FuenteDeVida(x, y, TAM_ESPECIAL, TAM_ESPECIAL));
            default       -> throw new TheDopoHardestGameException("Elemento desconocido: " + tipo);
        }
    }

    public Nivel obtenerNivel() { return nivel; }
}
