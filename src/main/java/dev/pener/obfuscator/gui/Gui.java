package dev.pener.obfuscator.gui;

import dev.pener.obfuscator.GuiMain;
import dev.pener.obfuscator.gui.custom.BlackThemedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Gui extends JFrame {
    JPanel mcardPanel;
    CardLayout mcl;
    public Gui() {


        setSize(800,600);
        setLocationRelativeTo(null);
        setVisible(true);
        JPanel mainPanel = new JPanel(new GridBagLayout());


        JPanel leftPanel2 = new JPanel();
        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel();

        leftPanel.setBackground(new Color(29,31,40,255));
        leftPanel2.setBackground(new Color(29,31,40,255));
        rightPanel.setBackground(new Color(19,17,28,255));

        mcardPanel = new JPanel(new CardLayout());
        mcardPanel.add(new JPanel(),"Dashboard");
        mcardPanel.add(new JPanel(),"Clients");

        mcl = (CardLayout)(mcardPanel.getLayout());
        mcl.show(mcardPanel,"Dashboard");


        JLabel text = new JLabel("AromaShield");

        Font textfont = new Font("Arial",Font.PLAIN,24);
        text.setFont(textfont);
        Font font =  new Font("Arial",Font.PLAIN,17);
        ArrayList<BlackThemedButton> buttons = new ArrayList<>();
        BlackThemedButton obfuscate = new BlackThemedButton("Obfusate",font);
        buttons.add(obfuscate);
        buttons.add(new BlackThemedButton("Reference",font));
        buttons.add(new BlackThemedButton("String",font));
        buttons.add(new BlackThemedButton("Flow",font));
        buttons.add(new BlackThemedButton("Misc",font));

        for (BlackThemedButton button : buttons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    for (BlackThemedButton buttonz : buttons) {
                        if (buttonz.getText() == button.getText()) {
                            mcl.show(mcardPanel,buttonz.getText());
                            buttonz.setOn();
                        } else {
                            buttonz.setOff();
                        }
                    }
                }
            });
        }
        obfuscate.setOn();



        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        leftPanel.add(text, constraints);


        for (int i = 0;i<buttons.size();i++) {
            constraints.gridy = i + 1;
            constraints.anchor = GridBagConstraints.CENTER;
            leftPanel.add(buttons.get(i), constraints);
        }

        //  System.out.println("RGB : " + frame.getBackground().getRed() + " " + frame.getBackground().getGreen() + " " + frame.getBackground().getBlue() + "\n" + "int : " + frame.getBackground().getRGB());


        // Set preferred size for the left panel to make it smaller in width
        leftPanel.setPreferredSize(new Dimension(150, leftPanel.getPreferredSize().height));


        GridBagConstraints gbc2 = new GridBagConstraints();
        // Add the left panel to the left side
        gbc2.fill = GridBagConstraints.VERTICAL;
        gbc2.weighty = 1.0;
        gbc2.gridx = 0;
        leftPanel2.add(leftPanel);
        mainPanel.add(leftPanel2, gbc2);

        // Add the right panel to the right side, taking up all available width
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 1.0;
        gbc2.gridx = 1;

        rightPanel.add(mcardPanel);
        mainPanel.add(rightPanel, gbc2);

        add(mainPanel);
    }
}
