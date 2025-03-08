import javax.swing.*;
import java.sql.*;

public class Application{
    public static void main(String[] args) {
        //     url -> "jdbc:mysql://hostname:dbms_port/database"
        String url = "jdbc:mysql://localhost:3306/order_processing_system";
        Connection conn;
        try{
            conn = DriverManager.getConnection(url);
//            conn.createStatement().execute("show tables;");

            ToolTipManager.sharedInstance().setInitialDelay(200); // Set tooltip delay
            LoginScreen loginScreen = new LoginScreen(conn);
            loginScreen.setVisible(true);

            conn.close();
        } catch (Exception e){
            System.out.println("Error: " + e);
        }
    }
}