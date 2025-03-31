package tops.components;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ButtonFactory {
    // Standard icon size for all buttons
    private static final int ICON_WIDTH = 24;
    private static final int ICON_HEIGHT = 24;
    
    public static JButton createButton(String tooltipText, String imagePath) {
        URL imageUrl = ButtonFactory.class.getClassLoader().getResource(imagePath);
        if (imageUrl == null) {
            throw new RuntimeException("Image not found:" + imagePath);
        }
        ImageIcon icon = new ImageIcon(imageUrl);
        
        // Resize the icon to standard dimensions
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImg);
        
        JButton button = new JButton("", resizedIcon);
        button.setToolTipText(tooltipText);
        button.setVisible(false);
        return button;
    }
}
