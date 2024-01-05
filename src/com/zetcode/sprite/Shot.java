package com.zetcode.sprite;

import javax.sound.sampled.*;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;

public class Shot extends Sprite {
    private boolean soundPlayed = false;
    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
    }

    private void initShot(int x, int y) {

        var shotImg = "src/images/shot.png";
        var ii = new ImageIcon(shotImg);
        setImage(ii.getImage());

        int H_SPACE = 6;
        setX(x + H_SPACE);

        int V_SPACE = 1;
        setY(y - V_SPACE);
    }
    private void playShotSound() {
        if (!soundPlayed) {
            try {
                File soundFile = new File("sounds/shot.wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);


                Clip clip = AudioSystem.getClip();


                clip.open(audioInputStream);


                clip.start();

                soundPlayed = true;
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }
    public void move() {

        this.y -= 4;


        if (this.y < 0) {
            this.visible = false;
        }
        playShotSound();
    }
}
