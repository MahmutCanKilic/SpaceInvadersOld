package com.zetcode.sprite;

import com.zetcode.Commons;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;

public class Player extends Sprite {

    private int width;
    private boolean isMovingLeft, isMovingRight;
    public Player() {

        initPlayer();
    }

    private void initPlayer() {

        var playerImg = "src/images/player.png";
        var ii = new ImageIcon(playerImg);

        width = ii.getImage().getWidth(null);
        setImage(ii.getImage());

        int START_X = 500;
        setX(START_X);

        int START_Y = 850;
        setY(START_Y);

        isMovingLeft = false;
        isMovingRight = false;
    }

    public void act() {
        if (isMovingLeft) {
            x -= 15;
        } else if (isMovingRight) {
            x += 15;
        }

        if (x <= 2) {
            x = 2;
        }

        if (x >= Commons.BOARD_WIDTH - 2 * width) {
            x = Commons.BOARD_WIDTH - 2 * width;
        }
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = true;
            isMovingRight = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = true;
            isMovingLeft = false;
        }
    }


    public void keyReleased(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            isMovingLeft = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            isMovingRight = false;
        }
    }

}
