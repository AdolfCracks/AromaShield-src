package dev.pener.obfuscator.gui.custom;

import dev.pener.obfuscator.gui.util.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackThemedButton extends JButton {

    private String text;

    private boolean toggled = false;

    @Override
    public String getText() {
        return text;
    }
    private Timer timer;
    public void setOn() {
        if (!toggled) {
            timer = new Timer(20, new ActionListener() {
                private int alpha = 0;
                private int step = 5;
                private Color targetColor = new Color(66, 108, 145, 100);

                @Override
                public void actionPerformed(ActionEvent e) {
                    alpha += step;

                    if (alpha >= 100) {
                        alpha = 100;
                        timer.stop();
                    }

                    Color bgColor = new Color(
                            targetColor.getRed(),
                            targetColor.getGreen(),
                            targetColor.getBlue(),
                            alpha
                    );

                    setBackground(bgColor);
                    repaint();
                }
            });

            timer.setInitialDelay(0);
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.start();

            toggled = true;
        }
    }

    public void setOff() {
        if (timer != null && timer.isRunning()) timer.stop();
        setBackground(new Color(66, 108, 145,0));
        toggled = false;
    }
    public boolean isToggled() {
        return toggled;
    }

    public BlackThemedButton(String text, Font font) {
        super(text);
        this.text = text;
        setBackground(new Color(66, 108, 145,0));
        setForeground(new Color(255,255,255));
        setBorder(new RoundedBorder(new Color(29,31,40,255),1,20));
        setPreferredSize(new Dimension(110,40));
        setFont(font);
    }
}