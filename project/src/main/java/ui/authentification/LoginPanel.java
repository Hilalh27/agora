package ui.authentification;

import contacts.UserAlreadyExists;
import controller.ContactController;
import ui.View;
import ui.design.FontUtil;
import ui.home.HomeFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginPanel extends JPanel {

    /**
     * Initializes the login panel with the necessary components.
     */
    public LoginPanel() {
        initializeLogin();
    }

    /**
     * Sets up the login UI components and layout.
     */
    private void initializeLogin() {
        FontUtil.registerFont("fonts/NokiaPureHeadline_Regular.ttf");
        FontUtil.registerFont("fonts/NokiaPureHeadline_Ultralight.ttf");

        setLayout(new GridBagLayout());
        setBackground(new Color(255, 255, 255)); // Pure white background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing

        ImageIcon loginIcon = new ImageIcon(getClass().getResource("/icons/login_icon.png"));
        int iconWidth = 400; // Desired width
        int iconHeight = (int) (iconWidth * ((double) loginIcon.getIconHeight() / loginIcon.getIconWidth())); // Proportional height
        JLabel iconLabel = new JLabel(new ImageIcon(loginIcon.getImage().getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH)));
        gbc.gridy++;
        add(iconLabel, gbc);

        JLabel titleLabel = new JLabel("Welcome to Agora");
        titleLabel.setFont(new Font("Nokia Pure Headline", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 0, 0)); // Black text
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); // Padding
        gbc.gridy++;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Connect with your partners.");
        subtitleLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(100, 100, 100)); // Gray text
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Padding
        gbc.gridy++;
        add(subtitleLabel, gbc);

        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        nicknamePanel.setOpaque(false);

        JLabel nicknameLabel = new JLabel("Nickname:");
        nicknameLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 18));
        nicknameLabel.setForeground(new Color(50, 50, 50)); // Dark gray text

        JTextField nicknameField = new JTextField();
        nicknameField.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        nicknameField.setPreferredSize(new Dimension(300, 40)); // Larger input field
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), // Rounded border
                BorderFactory.createEmptyBorder(10, 15, 10, 15) // Padding
        ));

        nicknamePanel.add(nicknameLabel);
        nicknamePanel.add(nicknameField);
        gbc.gridy++;
        add(nicknamePanel, gbc);

        JButton loginButton = new JButton("Log In");
        loginButton.setFont(new Font("Nokia Pure Headline", Font.BOLD, 18));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(0, 122, 255)); // Apple-style blue
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50)); // Padding
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);

        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 122, 255), 1, true), // Rounded border
                BorderFactory.createEmptyBorder(10, 25, 10, 25) // Padding
        ));

        loginButton.addActionListener(e -> handleLogin(nicknameField));
        gbc.gridy++;
        add(loginButton, gbc);

        JLabel footerLabel = new JLabel("Agora - Connecting People Since 2024");
        footerLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 14));
        footerLabel.setForeground(new Color(150, 150, 150)); // Light gray text
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Padding
        gbc.gridy++;
        add(footerLabel, gbc);
    }

    /**
     * Handles the login process, validating and initiating user registration.
     */
    private void handleLogin(JTextField nicknameField) {
        String nickname = nicknameField.getText();
        if (!nickname.isEmpty()) {
            JDialog loadingDialog = new JDialog();
            loadingDialog.setUndecorated(true);
            loadingDialog.setSize(200, 100);
            loadingDialog.setLocationRelativeTo(this);
            loadingDialog.add(new JLabel("Loading, please wait...", SwingConstants.CENTER));

            SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));

            new Thread(() -> {
                try {
                    ContactController.inscriptionPanel(nickname, this);
                    SwingUtilities.invokeLater(() -> {
                        View.getInstance().showCard("Home", new HomeFrame(nickname));
                        loadingDialog.dispose();
                    });
                } catch (IOException | UserAlreadyExists ex) {
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose();
                        showMessage("Error: " + ex.getMessage());
                    });
                } catch (ContactController.NicknameAlreadyUsedException ex) {
                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose();
                        showMessage(ex.getMessage());
                    });
                }
            }).start();
        } else {
            showMessage("Nickname cannot be empty.");
        }
    }

    /**
     * Displays a message in a dialog box.
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays a success message for a successful connection.
     */
    public void showConnexionMessage() {
        showMessage("Connected!");
    }
}
