package domain.core;

public class TheDopoHardestGameException extends RuntimeException {

    private TheDopoHardestGameException(String mensaje) {
        super(mensaje);
    }

    // Jugador

    public static TheDopoHardestGameException skinNull() {
        return new TheDopoHardestGameException("Skin no puede ser null");
    }

    // TheDOPOHardestGame 

    public static TheDopoHardestGameException modoNoEspecificado() {
        return new TheDopoHardestGameException("Modo de juego no especificado");
    }

    public static TheDopoHardestGameException skinRequerida() {
        return new TheDopoHardestGameException("Se requiere al menos una Skin");
    }

    public static TheDopoHardestGameException nivelSinZonaInicial() {
        return new TheDopoHardestGameException("El nivel no tiene ZonaInicial");
    }

    // ConstructorNivel

    public static TheDopoHardestGameException rutaVacia() {
        return new TheDopoHardestGameException("Ruta de nivel vacía o nula");
    }

    public static TheDopoHardestGameException nivelNoCargable(String ruta) {
        return new TheDopoHardestGameException("No se pudo cargar el nivel: " + ruta);
    }

    public static TheDopoHardestGameException lineaInvalidaMetadata(String linea) {
        return new TheDopoHardestGameException("Línea inválida en metadata: " + linea);
    }

    public static TheDopoHardestGameException lineaConFormatoInvalido(String linea) {
        return new TheDopoHardestGameException("Línea con formato inválido: " + linea);
    }

    public static TheDopoHardestGameException tipoZonaDesconocido(String tipo) {
        return new TheDopoHardestGameException("Tipo de zona desconocido: " + tipo);
    }

    public static TheDopoHardestGameException tipoEnemigoDesconocido(String tipo) {
        return new TheDopoHardestGameException("Tipo de enemigo desconocido: " + tipo);
    }

    public static TheDopoHardestGameException colorSkinDesconocido(String color) {
        return new TheDopoHardestGameException("Color de skin desconocido: " + color);
    }

    public static TheDopoHardestGameException tipoMonedaDesconocido(String tipo) {
        return new TheDopoHardestGameException("Tipo de moneda desconocido: " + tipo);
    }

    public static TheDopoHardestGameException elementoDesconocido(String tipo) {
        return new TheDopoHardestGameException("Elemento desconocido: " + tipo);
    }

    // GestorPartida

    public static TheDopoHardestGameException errorGuardando(String ruta) {
        return new TheDopoHardestGameException("No se pudo guardar la partida en: " + ruta);
    }

    public static TheDopoHardestGameException errorCargando(String ruta) {
        return new TheDopoHardestGameException("No se pudo cargar la partida desde: " + ruta);
    }

    public static TheDopoHardestGameException partidaCorrupta(String detalle) {
        return new TheDopoHardestGameException("Archivo de partida corrupto: " + detalle);
    }
}
