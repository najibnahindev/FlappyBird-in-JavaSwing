import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.Timer;
import javax.swing.*;

public class flappyBird extends JPanel implements ActionListener, KeyListener {

    Image bg, bird, top, bottom;
    Bird mainBird;
    Timer loop;
    Timer pipeTimer;
    ArrayList<pipe> pipes;
    Clip clip;

    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    boolean gameOver = false;
    int score = 0;

    JButton restartButton;

    public flappyBird() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        setPreferredSize(new Dimension(360, 640));
        setFocusable(true);
        addKeyListener(this);

        bg = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/bg.png"))).getImage();
        bird = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/bird.png"))).getImage();
        top = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/top.png"))).getImage();
        bottom = new ImageIcon(Objects.requireNonNull(getClass().getResource("./resources/bottom.png"))).getImage();

        mainBird = new Bird(bird);
        pipes = new ArrayList<>();

        pipeTimer = new Timer(1500, _ -> pipeWhere());
        pipeTimer.start();

        loop = new Timer(1000 / 60, this);
        loop.start();

        playBackgroundMusic();

        restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        restartButton.addActionListener(e -> {
            try {
                restartGame();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        this.add(restartButton);
    }

    public void pipeWhere() {
        int minY = -300;
        int maxY = -100;
        int randomYpipe = minY + (int) (Math.random() * (maxY - minY));
        int freeSpace = 160;

        pipe topPipe = new pipe(top);
        topPipe.y = randomYpipe;
        pipes.add(topPipe);

        pipe bottomPipe = new pipe(bottom);
        bottomPipe.y = randomYpipe + topPipe.height + freeSpace;
        pipes.add(bottomPipe);
    }

    public void playBackgroundMusic() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource("./resources/bg_music.wav"))
            );
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(bg, 0, 0, 360, 640, null);
        g.drawImage(mainBird.birdImg, mainBird.x, mainBird.y, mainBird.bWidth, mainBird.bHeight, null);

        for (pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 40);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over", 90, 300);
        }
    }

    public void move() {
        velocityY += gravity;
        mainBird.y += velocityY;
        mainBird.y = Math.max(mainBird.y, 0);

        for (pipe pipe : pipes) {
            pipe.x += velocityX;
        }

        pipes.removeIf(pipe -> pipe.x + pipe.width < 0);

        Rectangle birdRect = new Rectangle(mainBird.x, mainBird.y, mainBird.bWidth, mainBird.bHeight);
        for (pipe pipe : pipes) {
            Rectangle pipeRect = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
            if (birdRect.intersects(pipeRect)) {
                gameOver = true;
            }
        }

        if (mainBird.y > 640 - mainBird.bHeight) {
            gameOver = true;
        }

        for (pipe pipe : pipes) {
            if (!pipe.scored && pipe.img == top && pipe.x + pipe.width < mainBird.x) {
                score++;
                pipe.scored = true;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        } else {
            pipeTimer.stop();
            loop.stop();
            stopBackgroundMusic();
            restartButton.setVisible(true);
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public void restartGame() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        mainBird.y = 320;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        playBackgroundMusic();

        restartButton.setVisible(false);

        pipeTimer.start();
        loop.start();
    }
}



