package ui.home;

import assets.AvatarGenerator;
import contacts.ActiveUserList;
import contacts.User;
import controller.ContactController;
import controller.MainController;
import network.UDP.UDPServer;
import network.UDPObserver;
import ui.design.FontUtil;
import ui.View;
import ui.profile.ProfileFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.HashSet;

public class HomeFrame extends JPanel implements UDPObserver {

    private final UDPServer udpServer;
    private final DefaultListModel<String> contacts;
    private final DefaultListModel<String> contactsNicknames;
    public static JPanel mainPanel;
    public static CardLayout cardLayout;
    private JDialog profileDialog;
    public static String currentContact;

    /**
     * Constructor that initializes the HomeFrame UI.
     */
    public HomeFrame(String myNickname) {
        FontUtil.registerFont("fonts/NokiaPureHeadline_Regular.ttf");
        FontUtil.registerFont("fonts/NokiaPureHeadline_Ultralight.ttf");

        this.contacts = new DefaultListModel<>();
        this.contactsNicknames = new DefaultListModel<>();
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        this.udpServer = new UDPServer(MainController.socket);

        udpServer.addObserver(this);

        for (User contact : ActiveUserList.activeUsers) {
            contacts.addElement(contact.getIp_address().getHostName());
        }

        for (int i = 0; i < contacts.size(); i++) {
            String ipAddress = contacts.getElementAt(i);
            String nickname = ActiveUserList.getNicknameByIp(ipAddress);
            contactsNicknames.addElement(nickname);
        }
        contactsNicknames.addElement(myNickname);

        startUDPServer();
        initializeHome();
    }

    /**
     * Initializes the home screen UI components.
     */
    private void initializeHome() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Contacts Panel (left)
        JPanel contactsPanel = new JPanel(new BorderLayout());
        contactsPanel.setPreferredSize(new Dimension(300, 600));
        contactsPanel.setBackground(new Color(255, 255, 255)); // White background
        contactsPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230))); // Right border

        // Contacts Title
        JLabel contactsLabel = new JLabel("Contacts", JLabel.CENTER);
        contactsLabel.setFont(new Font("Nokia Pure Headline", Font.BOLD, 24));
        contactsLabel.setForeground(new Color(0, 0, 0));
        contactsLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Padding

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(contactsLabel);
        contactsPanel.add(titlePanel, BorderLayout.NORTH);

        // Contacts List
        JList<String> contactsList = new JList<>(contactsNicknames);
        contactsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                BufferedImage avatar = AvatarGenerator.generateAvatarForIndex(index);
                ImageIcon avatarIcon = new ImageIcon(avatar);
                label.setIcon(new ImageIcon(avatarIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
                label.setText("    " + value.toString());
                label.setFont(new Font("NokiaPureHeadline_Ultralight.ttf", Font.PLAIN, 18));
                label.setIconTextGap(15);
                label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
                label.setForeground(Color.BLACK);
                label.setBackground(isSelected ? new Color(230, 230, 230) : Color.WHITE); // Highlight selected item
                label.setOpaque(true);
                return label;
            }
        });

        contactsList.setFixedCellHeight(70);
        contactsPanel.add(new JScrollPane(contactsList), BorderLayout.CENTER);
        add(contactsPanel, BorderLayout.WEST);

        // Default Message Panel
        JPanel messagePanel = new JPanel(new GridBagLayout());
        JLabel messageLabel = new JLabel("Select a contact to start chatting");
        messageLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 20));
        messageLabel.setForeground(new Color(150, 150, 150)); // Light gray text
        messagePanel.add(messageLabel);
        messagePanel.setBackground(Color.WHITE);
        mainPanel.add(messagePanel, "default");
        add(mainPanel, BorderLayout.CENTER);

        // Profile Button (top-right)
        add(createProfileButtonPanel(), BorderLayout.NORTH);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Contact Selection Listener
        contactsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedContact = contactsList.getSelectedValue();
                int selectedIndex = contactsList.getSelectedIndex();
                if (selectedContact != null) {
                    try {
                        currentContact = selectedContact;
                        showChatPanel(selectedContact, selectedIndex);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    /**
     * Creates and returns the profile button panel.
     */
    private JPanel createProfileButtonPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        ImageIcon profileIcon = new ImageIcon(getClass().getClassLoader().getResource("icons/person.circle.fill@2x.png"));
        JButton profileButton = new JButton(resizeIcon(profileIcon, 40, 40));
        profileButton.setBorderPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        profileButton.addActionListener(e -> {
            profileButton.setEnabled(false);
            try {
                showProfileWindow();
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
            SwingUtilities.invokeLater(() -> profileButton.setEnabled(true));
        });

        topPanel.add(profileButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        return topPanel;
    }

    /**
     * Resizes the given icon to the specified width and height.
     */
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    /**
     * Displays the profile window.
     */
    private void showProfileWindow() throws UnknownHostException {
        if (profileDialog != null && profileDialog.isShowing()) {
            profileDialog.toFront();
            return;
        }
        ProfileFrame profileFrame = new ProfileFrame();
        profileDialog = new JDialog();
        profileDialog.setTitle("Profile");
        profileDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        profileDialog.getContentPane().add(profileFrame);
        profileDialog.setSize(400, 300);
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setVisible(true);
    }

    /**
     * Displays the chat panel for the selected contact.
     */
    private void showChatPanel(String contact, int index) throws UnknownHostException {
        if (!View.activePanels.containsKey(ContactController.getIpFromNickname(contact))) {
            ChatClientPanel chatPanel = new ChatClientPanel(ContactController.getIpFromNickname(contact), index);
            View.activePanels.put(ContactController.getIpFromNickname(contact), chatPanel);
            MainController.server.addObserver(chatPanel);
            mainPanel.add(chatPanel, "chat_" + ContactController.getIpFromNickname(contact));
        }
        cardLayout.show(mainPanel, "chat_" + ContactController.getIpFromNickname(contact));
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible() && comp instanceof ChatClientPanel) {
                ((ChatClientPanel) comp).refresh();
                break;
            }
        }
    }

    /**
     * Starts the UDP server with two threads for sending and updating users.
     */
    private void startUDPServer() {
        Thread sendNickNameThread = udpServer.new SendNickNameThread();
        sendNickNameThread.start();

        Thread updateUserThread = udpServer.new UpdateUserThread();
        updateUserThread.start();
    }

    /**
     * Updates the list of active users.
     */
    @Override
    public void update(HashSet<User> activeUsers) {
        contacts.clear();
        contactsNicknames.clear();

        for (User user : activeUsers) {
            contacts.addElement(user.getIp_address().toString());
            contactsNicknames.addElement(user.getNickname());
        }
    }
}