package presentation;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class GestorSonido {

    private Clip clip;

    public void reproducir(String ruta) {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }
            File archivo = new File(ruta);
            if (!archivo.exists()) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(archivo);
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[GestorSonido] Error al reproducir " + ruta + ": " + e.getMessage());
        }
    }

    public void detener() {
        if (clip != null && clip.isRunning()) clip.stop();
    }

    public void detenerTodos() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }
}
