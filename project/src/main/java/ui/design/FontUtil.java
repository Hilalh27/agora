package ui.design;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontUtil {

    /**
     * Registers a custom font from the specified file path.
     */
    public static void registerFont(String fontPath) {
        try (InputStream is = FontUtil.class.getClassLoader().getResourceAsStream(fontPath)) {
            if (is != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
            } else {
                System.err.println("Font not found: " + fontPath);
            }
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }
}
