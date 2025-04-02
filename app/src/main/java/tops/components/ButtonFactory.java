package tops.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ButtonFactory {
    public static JButton createButton(String tooltipText, String imagePath) {
        URL imageUrl = ButtonFactory.class.getClassLoader().getResource(imagePath);
        if (imageUrl == null) {
            throw new RuntimeException("Image not found:" + imagePath);
        }
        ImageIcon icon = new ImageIcon(imageUrl);

        JButton button = new JButton("", icon);
        button.setToolTipText(tooltipText);
        button.setVisible(false);
        return button;
    }
}
