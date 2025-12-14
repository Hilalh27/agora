import controller.MainController;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import ui.View;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    /**
     * Main entry point for the ChatSystem application.
     */
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Le programme est en train de s'arrêter...");
        }));

        Configurator.setRootLevel(Level.INFO);
        LOGGER.info("Starting ChatSystem Application");

        MainController.initSession(4445, 4500);
        View.runGUI();
    }
}
