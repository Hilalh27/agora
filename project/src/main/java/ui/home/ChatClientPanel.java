package ui.home;

import assets.AudioPlayer;
import assets.AvatarGenerator;
import com.sun.tools.javac.Main;
import contacts.ActiveUserList;
import contacts.User;
import controller.ContactController;
import controller.MainController;
import controller.TextingController;
import database.Message;
import network.TCPObserver;
import ui.design.FontUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class ChatClientPanel extends JPanel implements TCPObserver {

    public User userContact;
    public List<Message> messages_history;
    private JPanel chatArea;
    private String userIP;
    private String contactIP;
    private JLabel chatTitle;

    /**
     * Initializes the chat panel, loads the contact's data, connects to the server, and sets up the UI.
     */
    public ChatClientPanel(String ipAddress, int index) throws UnknownHostException {
        FontUtil.registerFont("fonts/NokiaPureHeadline_Regular.ttf");
        FontUtil.registerFont("fonts/NokiaPureHeadline_Ultralight.ttf");

        // Initial setup
        String contactNickname = ActiveUserList.getNicknameByIp(ipAddress);
        MainController.client.stopConnection();
        userContact = ContactController.getUserFromNickname(contactNickname);
        assert userContact != null;
        userIP = MainController.getMainUser().getIp_address().getHostAddress();
        contactIP = userContact.getIp_address().getHostAddress();
        MainController.client.startConnection(contactIP, 4500);
        messages_history = TextingController.getMessagesWith(contactIP);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Chat Title Panel setup
        BufferedImage avatar = AvatarGenerator.generateAvatarForIndex(index);
        ImageIcon avatarIcon = new ImageIcon(avatar.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel avatarLabel = new JLabel(avatarIcon);

        if (!Objects.equals(userIP, contactIP))
        {
            chatTitle = new JLabel("Chat with " + contactNickname, JLabel.LEFT);
        }
        else
        {
            chatTitle = new JLabel("Personal notes", JLabel.LEFT);
        }
        chatTitle.setFont(new Font("Nokia Pure Headline", Font.BOLD, 24));
        chatTitle.setForeground(new Color(0, 0, 0));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(avatarLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(chatTitle);
        titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))); // Bottom border
        titlePanel.setPreferredSize(new Dimension(titlePanel.getPreferredSize().width, 80)); // Height

        add(titlePanel, BorderLayout.NORTH);

        // Chat Area (scrollable)
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        // Message Input Panel setup
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230))); // Top border
        messagePanel.setBackground(Color.WHITE);

        JTextField messageField = new JTextField();
        messageField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Padding
        messageField.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));

        JButton sendButton = new JButton();
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/icons/paperplane@2x.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);
        sendButton.setIcon(resizedIcon);
        sendButton.setBorderPainted(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        add(messagePanel, BorderLayout.SOUTH);

        // Event Handlers
        sendButton.addActionListener(e -> {
            try {
                sendButtonHandler(messageField, contactIP);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        });

        messageField.addActionListener(e -> {
            try {
                sendButtonHandler(messageField, userContact.getIp_address().getHostAddress());
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Load existing messages
        loadMessages();
    }

    /**
     * Loads and displays the message history in the chat area.
     */
    public void loadMessages() {
        chatArea.removeAll();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String lastDisplayedDate = ""; // Track the last displayed date

        for (Message msg : messages_history) {
            String messageDate = dateFormat.format(msg.getDate());

            // Add a date separator if the date has changed
            if (!messageDate.equals(lastDisplayedDate)) {
                JLabel dateLabel = new JLabel(messageDate, JLabel.CENTER);
                dateLabel.setFont(new Font("Nokia Pure Headline", Font.BOLD, 14));
                dateLabel.setForeground(new Color(150, 150, 150)); // Gray text
                dateLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0)); // Padding
                chatArea.add(dateLabel);
                lastDisplayedDate = messageDate;
            }

            addMessageBubble(msg, timeFormat);
        }
        revalidate();
        repaint();
    }

    /**
     * Adds a message bubble to the chat area for the given message.
     */
    private void addMessageBubble(Message msg, SimpleDateFormat timeFormat) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bubblePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (msg.getIpSource().equals(userIP)) {
                    g2.setColor(new Color(0, 122, 255)); // Blue bubble for user
                } else {
                    g2.setColor(new Color(230, 230, 230)); // Gray bubble for contact
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        bubblePanel.setLayout(new BorderLayout());
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel messageLabel = new JLabel("<html><p style='width: 300px; word-wrap: break-word;'>" + msg.getData() + "</p></html>");
        messageLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 16));
        messageLabel.setForeground(msg.getIpSource().equals(userIP) ? Color.WHITE : Color.BLACK);

        bubblePanel.add(messageLabel, BorderLayout.CENTER);

        JLabel timeLabel = new JLabel(timeFormat.format(msg.getDate()));
        timeLabel.setFont(new Font("Nokia Pure Headline", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(150, 150, 150));

        if (msg.getIpSource().equals(userIP)) {
            messagePanel.add(bubblePanel, BorderLayout.EAST);
            timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            messagePanel.add(bubblePanel, BorderLayout.WEST);
            timeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }

        messagePanel.add(timeLabel, BorderLayout.SOUTH);
        chatArea.add(messagePanel);
    }

    /**
     * Handles the action when the send button is clicked or enter is pressed in the message input field.
     */
    private void sendButtonHandler(JTextField messageField, String contactIP) throws UnknownHostException {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            AudioPlayer.playMP3("sounds/send.mp3");
            TextingController.sendMessage(message);
            messageField.setText("");
            messages_history = TextingController.getMessagesWith(contactIP);
            loadMessages();

            JScrollBar verticalScrollBar = ((JScrollPane) chatArea.getParent().getParent()).getVerticalScrollBar();
            SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(verticalScrollBar.getMaximum()));
        }
    }

    @Override
    public void update(Message message) {
        if (Objects.equals(message.getIpSource(), contactIP) && Objects.equals(HomeFrame.currentContact, ActiveUserList.getNicknameByIp(message.getIpSource()))) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar verticalScrollBar = ((JScrollPane) chatArea.getParent().getParent()).getVerticalScrollBar();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

                AudioPlayer.playMP3("sounds/receive.mp3");
                addMessageBubble(message, dateFormat);
                revalidate();
                repaint();
                SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(verticalScrollBar.getMaximum()));
            });
        }
    }

    public void refresh() throws UnknownHostException {
        MainController.client.stopConnection();
        assert userContact != null;
        contactIP = userContact.getIp_address().getHostAddress();
        MainController.client.startConnection(contactIP, 4500);
        messages_history = TextingController.getMessagesWith(contactIP);
        loadMessages();
        if (!Objects.equals(userIP, contactIP))
        {
            chatTitle.setText("Chat with " + ActiveUserList.getNicknameByIp(contactIP));
        }
    }
}