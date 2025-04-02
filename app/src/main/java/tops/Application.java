package tops;

import javax.swing.*;
import java.sql.*;

public class Application {
    private static String url = "jdbc:mysql://localhost:3306/textileorderprocessing";
    private static String user = "root"; // Add your username here
    private static String password = ""; // Add your password here

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        try {
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e){
                conn = DriverManager.getConnection(url);
            }

            ToolTipManager.sharedInstance().setInitialDelay(200); // Set tooltip delay
            LoginScreen loginScreen = new LoginScreen(conn);
            loginScreen.setVisible(true);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}