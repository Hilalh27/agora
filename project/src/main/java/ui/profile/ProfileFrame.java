package ui.profile;

import contacts.UserAlreadyExists;
import controller.ContactController;
import controller.MainController;
import ui.View;
import ui.design.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;

public class ProfileFrame extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JLabel nicknameLabel;
    private JLabel ipAddressLabel;

    /**
     * Initializes the ProfileFrame, sets up the layout, and registers the fonts.
     */
    public ProfileFrame() throws UnknownHostException {
        FontUtil.registerFont("fonts/NokiaPureHeadline_Regular.ttf");
        FontUtil.registerFont("fonts/NokiaPureHeadline_Ultralight.ttf");

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background
        setMinimumSize(new Dimension(600, 600)); // Adjusted size for better spacing

        // Create a CardLayout and a panel to hold the cards
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Create the profile panel
        JPanel profilePanel = createProfilePanel();
        cardPanel.add(profilePanel, "ProfilePanel");

        // Create the change nickname panel
        JPanel changeNicknamePanel = createChangeNicknamePanel();
        cardPanel.add(changeNicknamePanel, "ChangeNicknamePanel");

        // Add the card panel to the main panel
        add(cardPanel, BorderLayout.CENTER);

        // Show the profile panel by default
        cardLayout.show(cardPanel, "ProfilePanel");
    }

    /**
     * Creates the profile panel where the user can view their current nickname and IP address.
     */
    private JPanel createProfilePanel() throws UnknownHostException {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title Label
        JLabel profileLabel = new JLabel("User Profile", JLabel.CENTER);
        profileLabel.setFont(new Font("Nokia Pure Headline", Font.BOLD, 24));
        profileLabel.setForeground(new Color(0, 0, 0)); // Black text
        profileLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Padding
        profilePanel.add(profileLabel);

        // Nickname Label
        nicknameLabel = new JLabel("Nickname: " + MainController.getMainUser().getNickname());
        nicknameLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 18));
        nicknameLabel.setForeground(new Color(50, 50, 50)); // Dark gray text
        nicknameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nicknameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding
        profilePanel.add(nicknameLabel);

        // IP Address Label
        ipAddressLabel = new JLabel("IP Address: " + MainController.getMainUser().getIp_address());
        ipAddressLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 18));
        ipAddressLabel.setForeground(new Color(50, 50, 50)); // Dark gray text
        ipAddressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipAddressLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding
        profilePanel.add(ipAddressLabel);

        // Change Nickname Button
        JButton changeNicknameButton = new JButton("Change Nickname");
        changeNicknameButton.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        changeNicknameButton.setForeground(Color.BLACK);
        changeNicknameButton.setBackground(new Color(0, 122, 255)); // Apple-style blue
        changeNicknameButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        changeNicknameButton.setFocusPainted(false);
        changeNicknameButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changeNicknameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button Action
        changeNicknameButton.addActionListener(e -> cardLayout.show(cardPanel, "ChangeNicknamePanel"));
        profilePanel.add(Box.createVerticalStrut(20)); // Spacing
        profilePanel.add(changeNicknameButton);

        return profilePanel;
    }

    /**
     * Creates the panel where the user can input a new nickname to change their current one.
     */
    private JPanel createChangeNicknamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing

        // Nickname Label
        JLabel nicknameLabel = new JLabel("New Nickname:");
        nicknameLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        nicknameLabel.setForeground(new Color(50, 50, 50)); // Dark gray text
        panel.add(nicknameLabel, gbc);

        // Nickname Text Field
        gbc.gridy++;
        JTextField nicknameField = new JTextField();
        nicknameField.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        nicknameField.setPreferredSize(new Dimension(250, 30));
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), // Light gray border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
        panel.add(nicknameField, gbc);

        // Change Nickname Button
        gbc.gridy++;
        JButton changeButton = new JButton("Change Nickname");
        changeButton.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        changeButton.setForeground(Color.BLACK);
        changeButton.setBackground(new Color(0, 122, 255)); // Apple-style blue
        changeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        changeButton.setFocusPainted(false);
        changeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        changeButton.addActionListener(e -> {
            try {
                handleChange(nicknameField);
            } catch (UserAlreadyExists ex) {
                JOptionPane.showMessageDialog(this, "Nickname already used. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(changeButton, gbc);

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        backButton.setForeground(new Color(0, 122, 255)); // Apple-style blue
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(e -> cardLayout.show(cardPanel, "ProfilePanel"));
        gbc.gridy++;
        panel.add(backButton, gbc);

        return panel;
    }

    /**
     * Handles the nickname change process by validating and applying the new nickname.
     */
    private void handleChange(JTextField nicknameField) throws UserAlreadyExists {
        String nickname = nicknameField.getText();
        if (!nickname.isEmpty()) {
            if (ContactController.changeNickname(nickname)) {
                nicknameLabel.setText("Nickname: " + nickname); // Update the nickname label
                cardLayout.show(cardPanel, "ProfilePanel"); // Switch back to profile panel
                JOptionPane.showMessageDialog(this, "Nickname changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                throw new UserAlreadyExists(nickname);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Nickname cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}