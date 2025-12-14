package ui.design;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

public class CustomPanel extends JPanel {
    private Image backgroundImage;

    /**
     * Initializes the panel with a background image from the specified path.
     */
    public CustomPanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            if (backgroundImage == null) {
                System.out.println("Image non chargée. Vérifiez le chemin : " + imagePath);
            } else {
                System.out.println("Image chargée avec succès.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(new GridBagLayout());
    }

    /**
     * Paints the component with the background image stretched to fill the panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.out.println("L'image de fond est null.");
        }
    }
}