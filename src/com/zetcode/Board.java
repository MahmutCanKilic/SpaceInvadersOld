package com.zetcode;

import com.zetcode.sprite.Alien;
import com.zetcode.sprite.Player;
import com.zetcode.sprite.Shot;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Board extends JPanel {

    private Dimension d;
    private List<Alien> aliens;
    private Player player;
    private List<Shot> shots;

    private int numStars = 50;
    private int[][] stars;
    private boolean[] starShine;
    private int direction = -1;
    private int deaths = 0;
    private int level = 1;

    private int bombSpeed = 5;

    private boolean inGame = true;
    private String explImg = "src/images/explosion.png";
    private String message = "Game Over";

    private long lastShotTime = 0;
    long shotDelay = 100;
    private Timer timer;
    private long startTime;

    public Board() {
        initBoard();
        gameInit();
        startTime = System.currentTimeMillis();
        createStars();
    }

    private void createStars() {
        stars = new int[numStars][2];
        starShine = new boolean[numStars];

        for (int i = 0; i < numStars; i++) {
            stars[i][0] = (int) (Math.random() * Commons.BOARD_WIDTH);
            stars[i][1] = (int) (Math.random() * Commons.BOARD_HEIGHT);
            starShine[i] = false;
        }
    }

    private void updateStars() {
        for (int i = 0; i < numStars; i++) {
            stars[i][1] = (stars[i][1] + 1) % Commons.BOARD_HEIGHT;

            if (Math.random() < 0.01) {
                starShine[i] = !starShine[i];
            }
        }
    }

    private void drawStars(Graphics g) {
        g.setColor(Color.white);

        for (int i = 0; i < numStars; i++) {
            int size = 2;
            int x = stars[i][0];
            int y = stars[i][1];

            if (starShine[i]) {
                size = 4;
                g.setColor(Color.lightGray);
            }

            g.fillRect(x, y, size, size);
        }
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        d = new Dimension(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        setBackground(Color.black);

        timer = new Timer(Commons.DELAY, new GameCycle());
        timer.start();

        gameInit();
    }

    private void initAliens() {
        aliens = new ArrayList<>();
        Random random = new Random();

        int numberOfAliens = Commons.NUMBER_OF_ALIENS_TO_DESTROY + level * 5;

        for (int i = 0; i < numberOfAliens; i++) {
            int initialX = random.nextInt(Commons.BOARD_WIDTH - Commons.ALIEN_WIDTH);
            int initialY = random.nextInt(Commons.ALIEN_INIT_Y);
            int delay = random.nextInt(40000) + 1000;

            Timer spawnTimer = new Timer(delay, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Alien alien = new Alien(initialX, initialY);
                    aliens.add(alien);
                }
            });

            spawnTimer.setRepeats(false);
            spawnTimer.start();
        }
    }

    private void gameInit() {
        initAliens();
        shots = new ArrayList<>();
        player = new Player();
    }

    private void drawAliens(Graphics g) {
        for (Alien alien : aliens) {
            if (alien.isVisible()) {
                g.drawImage(alien.getImage(), alien.getX(), alien.getY(), this);
            }
            if (alien.isDying()) {
                alien.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }
        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShots(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Alien a : aliens) {
            Alien.Bomb b = a.getBomb();
            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
        drawStars(g);
        Font font = new Font("Arial", Font.PLAIN, 20);
        g.setFont(font);
        g.setColor(Color.white);

        long elapsedTime = System.currentTimeMillis() - startTime;

        Toolkit.getDefaultToolkit().sync();
        long seconds = elapsedTime / 1000;

        g.drawString("Time: " + seconds, 10, 20);

        Font font2 = new Font("Arial", Font.PLAIN, 20);
        g.setFont(font2);
        g.setColor(Color.white);
        g.drawString("Level: " + level, Commons.BOARD_WIDTH - 100, 20);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        g.setColor(Color.green);

        if (inGame) {
            g.drawLine(0, Commons.GROUND,
                    Commons.BOARD_WIDTH, Commons.GROUND);

            drawAliens(g);
            drawPlayer(g);
            drawShots(g);
            drawBombing(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void update() {
        if (deaths == Commons.NUMBER_OF_ALIENS_TO_DESTROY) {
            levelUp();
        }

        player.act();

        for (Shot shot : shots) {
            int shotX = shot.getX();
            int shotY = shot.getY();
            for (Alien alien : aliens) {
                int alienX = alien.getX();
                int alienY = alien.getY();
                if (alien.isVisible() && shot.isVisible()) {
                    if (shotX >= (alienX)
                            && shotX <= (alienX + Commons.ALIEN_WIDTH)
                            && shotY >= (alienY)
                            && shotY <= (alienY + Commons.ALIEN_HEIGHT)) {
                        var ii = new ImageIcon(explImg);
                        alien.setImage(ii.getImage());
                        alien.setDying(true);
                        deaths++;
                        shot.die();
                    }
                }
            }
            int y = shot.getY();
            y -= 4;
            if (y < 0) {
                shot.die();
            } else {
                shot.setY(y);
            }
        }

        for (Alien alien : aliens) {
            int x = alien.getX();
            if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1) {
                direction = -1;
                Iterator<Alien> i1 = aliens.iterator();
                while (i1.hasNext()) {
                    Alien a2 = i1.next();
                    a2.setY(a2.getY() + Commons.GO_DOWN);
                }
            }
            if (x <= Commons.BORDER_LEFT && direction != 1) {
                direction = 1;
                Iterator<Alien> i2 = aliens.iterator();
                while (i2.hasNext()) {
                    Alien a = i2.next();
                    a.setY(a.getY() + Commons.GO_DOWN);
                }
            }
        }

        Iterator<Alien> it = aliens.iterator();
        while (it.hasNext()) {
            Alien alien = it.next();
            if (alien.isVisible()) {
                int y = alien.getY();
                if (y > Commons.GROUND - Commons.ALIEN_HEIGHT) {
                    inGame = false;
                    message = "Invasion!";
                }
                alien.act(direction, level);
            }
        }

        var generator = new Random();
        for (Alien alien : aliens) {
            if (alien.isVisible()) {
                int y = alien.getY();
                if (y >= Commons.GROUND - Commons.ALIEN_HEIGHT) {
                    alien.setY(0);
                }
            }
            int shot = generator.nextInt(15);
            Alien.Bomb bomb = alien.getBomb();
            if (shot == Commons.CHANCE && alien.isVisible() && bomb.isDestroyed()) {
                bomb.setDestroyed(false);
                bomb.setX(alien.getX());
                bomb.setY(alien.getY());
            }
            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();
            if (player.isVisible() && !bomb.isDestroyed()) {
                if (bombX >= (playerX)
                        && bombX <= (playerX + Commons.PLAYER_WIDTH)
                        && bombY >= (playerY)
                        && bombY <= (playerY + Commons.PLAYER_HEIGHT)) {
                    var ii = new ImageIcon(explImg);
                    player.setImage(ii.getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }
            }
            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + bombSpeed);
                if (bomb.getY() >= Commons.GROUND - Commons.BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
    }

    private void updateShots() {
        Iterator<Shot> it = shots.iterator();
        while (it.hasNext()) {
            Shot shot = it.next();
            if (shot.isVisible()) {
                shot.move();
            } else {
                it.remove();
            }
        }
    }

    private void doGameCycle() {
        update();
        updateShots();
        updateStars();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {
        private Set<Integer> pressedKeys = new HashSet<>();

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            pressedKeys.remove(keyCode);

            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            pressedKeys.add(keyCode);

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            if (pressedKeys.contains(KeyEvent.VK_SPACE) && inGame) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastShotTime > shotDelay) {
                    Shot newShot = new Shot(x, y);
                    shots.add(newShot);
                    lastShotTime = currentTime;
                }
            }
        }
    }

    private void levelUp() {
        level++;
        deaths = 0;
        initAliens();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);
        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, Commons.BOARD_WIDTH / 2 - 30, Commons.BOARD_WIDTH - 100, 50);
        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (Commons.BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                Commons.BOARD_WIDTH / 2);
    }
}