package com.zetcode.sprite;

import javax.swing.ImageIcon;

public class Item extends Sprite {

    private int speed = 2;

    public Item(int x, int y) {
        initItem(x, y);
    }

    private void initItem(int x, int y) {
        var ii = new ImageIcon("src/images/burstshot.png");
        setImage(ii.getImage());

        setX(x);
        setY(y);

        setWidth(getImage().getWidth(null));
        setHeight(getImage().getHeight(null));
    }

    public void move() {

        setY(getY() + speed);


        if (getY() > com.zetcode.Commons.BOARD_HEIGHT) {
            die();
        }
    }
}