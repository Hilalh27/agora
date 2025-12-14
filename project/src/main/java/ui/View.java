package ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import assets.AvatarGenerator;
import ui.authentification.WelcomeFrame;
import ui.home.ChatClientPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class View {

    private static final Logger LOGGER = LogManager.getLogger(View.class);
    private static View instance;

    public static Map<String, ChatClientPanel> activePanels = new HashMap<>();

    public static JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    /**
     * Initializes the main view for the application.
     */
    public View() {
        mainFrame = new JFrame("Agora");
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        mainFrame.add(cardPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);
    }

    /**
     * Returns the singleton instance of the view.
     */
    public static View getInstance() {
        if (instance == null) {
            instance = new View();
        }
        return instance;
    }

    /**
     * Switches to a specified card in the layout and shows it.
     */
    public void showCard(String cardName, JPanel panel) {
        cardPanel.add(panel, cardName);
        cardLayout.show(cardPanel, cardName);
        mainFrame.setVisible(true);
    }

    /**
     * Switches to an already added card without adding it again.
     */
    public void showCardWithoutAdd(String cardName) {
        cardLayout.show(cardPanel, cardName);
        mainFrame.setVisible(true);
    }

    /**
     * Initializes and runs the GUI, including the welcome screen.
     */
    public static void runGUI()
    {
        AvatarGenerator.iniAvatarAlea();
        SwingUtilities.invokeLater(() -> {
            JPanel welcomePanel = new WelcomeFrame();
            View.getInstance().showCard("Welcome", welcomePanel);
        });
    }
}
