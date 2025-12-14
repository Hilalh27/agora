package assets;

import javazoom.jl.player.Player;
import java.io.InputStream;

public class AudioPlayer {

    /**
     * Plays an MP3 file from the specified file path.
     */
    public static void playMP3(String filePath) {
        try {
            InputStream inputStream = AudioPlayer.class.getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                System.out.println("File not found: " + filePath);
                return;
            }
            Player player = new Player(inputStream);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}