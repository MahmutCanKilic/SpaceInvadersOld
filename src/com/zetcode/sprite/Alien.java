package com.zetcode.sprite;

import com.zetcode.Commons;

import javax.swing.ImageIcon;
import java.util.Random;

public class Alien extends Sprite {

    private Bomb bomb;
    private int moveSpeed;
    private int moveDirection;

    public Alien(int x, int y) {
        initAlien(x, y);
        initMovement();
    }

    private void initAlien(int x, int y) {
        this.x = x;
        this.y = y;
        bomb = new Bomb(x, y);
        var alienImg = "src/images/alien.png";
        var ii = new ImageIcon(alienImg);
        setImage(ii.getImage());
    }

    private void initMovement() {
        Random random = new Random();
        moveSpeed = random.nextInt(3) + 1;
        moveDirection = random.nextBoolean() ? 1 : -1;
    }

    public void act(int direction, int level) {
        this.x += moveDirection * (moveSpeed + level);

        Random random = new Random();
        int randomNumber = random.nextInt(100);

        if (randomNumber < 5) {
            moveDirection *= -1;
        }

        if (y < Commons.GROUND - Commons.ALIEN_HEIGHT) {
            y += 1;
        }
    }

    public Bomb getBomb() {
        return bomb;
    }

    public class Bomb extends Sprite {
        private boolean destroyed;

        public Bomb(int x, int y) {
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {
            setDestroyed(true);
            this.x = x;
            this.y = y;
            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            setImage(ii.getImage());
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }
}
