package dev.pener.obfuscator.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import dev.pener.obfuscator.Main;
import dev.pener.obfuscator.core.LoginHandler;
import dev.pener.obfuscator.gui.util.RoundBorderField;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class LoginGui extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JFrame wrongWindow = generateWrongWindow();

    Font balooBhaijaan2;
    public LoginGui() {
        try {
            // Set the selected look and feel
            UIManager.setLookAndFeel(new FlatDarkLaf());

            // Update the UI components
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // no printing because update component causes nullpointer when setting before initalizing jframe objects
            // Handle any exceptions that may occur
        }
        setTitle("AromaShield");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("AromaShield", 10, 30);
            }
        };
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel bannerLabel = new JLabel(" ");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(bannerLabel, constraints);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(255,255,255));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        usernameField.setBackground(GuiVars.foregroundColor);
        usernameField.setForeground(new Color(255,255,255));
        usernameField.setBorder(new RoundBorderField());
        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(255,255,255));
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        passwordField.setBackground(GuiVars.foregroundColor);
        passwordField.setForeground(new Color(255,255,255));
        passwordField.setBorder(new RoundBorderField());
        constraints.gridx = 1;
        constraints.gridy = 2; // Adjust the row for passwordField
        panel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");

        loginButton.setBackground(GuiVars.backgroundColor);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loginButton.setForeground(new Color(255,255,255));
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
            if(LoginHandler.login(username, password)) { // TODO: Add more protection stuff because this is easily spoofable with POP -> ICONST_1
                this.setVisible(false);
                SwingUtilities.invokeLater(() -> {
                    Gui gui = new Gui();
                    gui.setVisible(true);
                    gui.update(gui.getGraphics());
                });
            } else {
                wrongWindow.setVisible(true);
            }
        });
        panel.add(loginButton, constraints);

        setBackground(GuiVars.backgroundColor);
        panel.setBackground(GuiVars.backgroundColor);

        add(panel);
    }

    private JFrame generateWrongWindow() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        frame.setSize(200, 150);
        frame.setLocationRelativeTo(null);
        panel.setBackground(GuiVars.backgroundColor);
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Wrong username or password!");
        label.setForeground(new Color(255,255,255));
        panel.add(label, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.setBackground(GuiVars.foregroundColor);
        okButton.setFont(new Font("Arial", Font.PLAIN, 16));
        okButton.setForeground(new Color(255,255,255));
        okButton.addActionListener(actionEvent -> {
            frame.setVisible(false);
        });
        panel.add(okButton, BorderLayout.SOUTH);
        frame.add(panel);
        return frame;
    }
}
