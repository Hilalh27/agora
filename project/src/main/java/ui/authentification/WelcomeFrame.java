package ui.authentification;

import ui.View;
import ui.design.FontUtil;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class WelcomeFrame extends JPanel {

    /**
     * Initializes the welcome frame with the necessary components.
     */
    public WelcomeFrame() {
        initializeWelcome();
    }

    /**
     * Sets up the welcome UI components, including images and buttons.
     */
    private void initializeWelcome() {
        FontUtil.registerFont("fonts/NokiaPureHeadline_Regular.ttf");
        FontUtil.registerFont("fonts/NokiaPureHeadline_Ultralight.ttf");

        setLayout(new GridBagLayout());
        setBackground(new Color(255, 255, 255)); // White background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 20, 20, 20); // Add padding around components

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Load and resize the "hello" image
        try {
            BufferedImage helloImage = ImageIO.read(getClass().getResource("/icons/hello.png"));
            int helloWidth = helloImage.getWidth() / 4; // Adjust the size as needed
            int helloHeight = helloImage.getHeight() / 4;
            Image resizedHelloImage = helloImage.getScaledInstance(helloWidth, helloHeight, Image.SCALE_SMOOTH);
            JLabel helloLabel = new JLabel(new ImageIcon(resizedHelloImage));
            helloLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(helloLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load and resize the logo
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/icons/logo.png"));
            int newWidth = originalImage.getWidth() / 5;
            int newHeight = originalImage.getHeight() / 5;
            Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(resizedImage));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some space between components
            centerPanel.add(logoLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the login button
        JButton loginButton = new JButton("Log In");
        loginButton.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 20));
        loginButton.setBackground(new Color(0, 122, 255)); // Apple's blue color
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50)); // More padding
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginButton.getMinimumSize().height));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 122, 255), 1, true),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 255), 1, true));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 122, 255), 1, true),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        // Add space and button to the center panel
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Add some space between components
        centerPanel.add(loginButton);

        // Add the center panel to the frame
        add(centerPanel, gbc);

        // Add action listener to the button
        loginButton.addActionListener(e -> View.getInstance().showCard("Login", new LoginPanel()));
    }
}