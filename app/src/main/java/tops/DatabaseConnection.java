package tops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection conn;
    private static String url = "jdbc:mysql://localhost:3306/textileorderprocessing";
    private static String user = "root"; // Add your username here
    private static String password = ""; // Add your password here

    public static Connection getConnection() throws SQLException{
        if (conn == null || conn.isClosed()) {
            try {
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e){
                conn = DriverManager.getConnection(url);
            }
        }
        return conn;
    }
}
