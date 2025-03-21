package tops;

import javax.swing.*;
import java.sql.*;

public class Application {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try {
            Connection conn = DatabaseConnection.getConnection();

            ToolTipManager.sharedInstance().setInitialDelay(200); // Set tooltip delay
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);

            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}