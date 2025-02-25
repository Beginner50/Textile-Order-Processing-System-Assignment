import javax.swing.*;

public class Application{
    public static void main(String[] args) {
        ToolTipManager.sharedInstance().setInitialDelay(200); // Set tooltip delay
        MainScreen mainScreen = new MainScreen();
        mainScreen.setVisible(true);
    }
}