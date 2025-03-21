package tops;

import javax.swing.*;
import java.sql.*;

public class Application {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        // url -> "jdbc:mysql://hostname:dbms_port/database"
        String url = "jdbc:mysql://localhost:3306/textileorderprocessing";
        String user = "root"; // Add your username here
        String password = ""; // Add your password here
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.createStatement().execute("show tables;");

            ToolTipManager.sharedInstance().setInitialDelay(200); // Set tooltip delay
            LoginScreen loginScreen = new LoginScreen(conn);
            loginScreen.setVisible(true);

            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}