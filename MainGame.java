import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;

public class MainGame {
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {


        JFrame fr = new JFrame("Flappy Bird");
        fr.setSize(360, 640);
        fr.setDefaultCloseOperation(3);
        fr.setLocationRelativeTo(null);
        fr.setResizable(false);

        flappyBird fb = new flappyBird();
        fr.add(fb);
        fr.pack();

        fr.setVisible(true);

        fb.requestFocusInWindow();
    }
}