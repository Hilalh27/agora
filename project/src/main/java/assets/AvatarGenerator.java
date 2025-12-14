package assets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class AvatarGenerator {
    private static final String AVATAR_DIRECTORY = "resources/avatars";
    public static final int IMAGE_SIZE = 100;
    private static List<String> avatarPaths = new ArrayList<>();
    private static List<Color> avatarColors = new ArrayList<>();

    /**
     * Creates a combined avatar image with a colored background.
     */
    public static BufferedImage createAvatarImage(String avatarPath, Color color) {
        BufferedImage avatarImage = null;
        try {
            avatarImage = ImageIO.read(new File(avatarPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage combinedImage = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(color);
        g2d.fillOval(0, 0, IMAGE_SIZE, IMAGE_SIZE);

        if (avatarImage != null) {
            g2d.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, IMAGE_SIZE, IMAGE_SIZE));
            g2d.drawImage(avatarImage.getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH), 0, 0, null);
        }

        g2d.dispose();
        return combinedImage;
    }

    /**
     * Initializes the avatar paths and generates random colors for avatars.
     * This method loads the avatars from the resources directory and creates 70 random colors.
     */
    public static void iniAvatarAlea() {
        URL avatarsUrl = AvatarGenerator.class.getClassLoader().getResource("avatars");
        if (avatarsUrl == null) {
            throw new IllegalStateException("Le répertoire avatars est introuvable dans le classpath");
        }

        File avatarDir = new File(avatarsUrl.getPath());
        File[] avatarFiles = avatarDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (avatarFiles == null || avatarFiles.length == 0) {
            throw new IllegalStateException("Aucun avatar trouvé dans " + avatarDir.getPath());
        }

        for (File avatarFile : avatarFiles) {
            avatarPaths.add(avatarFile.getAbsolutePath());
        }

        Random random = new Random();
        for (int i = 0; i < 70; i++) {
            avatarColors.add(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        Collections.shuffle(avatarPaths);
        Collections.shuffle(avatarColors);
    }

    /**
     * Generates an avatar image for a given index, combining an avatar and color.
     */
    public static BufferedImage generateAvatarForIndex(int index) {
        if (index < 0 || index >= avatarPaths.size()) {
            throw new IllegalArgumentException("Index invalide");
        }
        String avatarPath = avatarPaths.get(index);
        Color color = avatarColors.get(index);
        return createAvatarImage(avatarPath, color);
    }
}