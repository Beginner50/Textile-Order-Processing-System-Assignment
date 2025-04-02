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
        ImageIcon originalIcon = new ImageIcon(imageUrl);

        // Resize the icon to 16x16 pixels
        Image resizedImage = originalIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JButton button = new JButton("", resizedIcon);
        button.setToolTipText(tooltipText);
        button.setVisible(false);
        return button;
    }
}
