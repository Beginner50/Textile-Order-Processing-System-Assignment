package tops;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

//LoginScreen class
public class LoginScreen extends JFrame {

    private final Connection conn;
    private final JLabel statusLabel; // Declare statusLabel at class level

    //constructor for  login screen
    public LoginScreen() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
        setTitle("Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        //JPanel
        // Username Field
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username: "));
        JTextField usernameField = new JTextField(15);
        usernamePanel.add(usernameField);

        // Password Field
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password: "));
        JPasswordField passwordField = new JPasswordField(15);
        passwordPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");

        // Initialize statusLabel
        statusLabel = new JLabel("", SwingConstants.CENTER); //Initially empty ("") because no error message is shown at the start
        statusLabel.setForeground(Color.RED);
        // Adds an action listener to handle button clicks.
        // Pass only usernameField and passwordField
        loginButton.addActionListener(new LoginAction(usernameField, passwordField));

        //Add the components(panel,button,label) to the frame
        add(usernamePanel);
        add(passwordPanel);
        add(loginButton);
        add(statusLabel);
    }
//LoginAction class implements ActionListener so it responds to button clicks.
    class LoginAction implements ActionListener {
        private final JTextField usernameField;
        private final JPasswordField passwordField;
    //Constructor for LoginAction
        public LoginAction(JTextField usernameField, JPasswordField passwordField) {
            this.usernameField = usernameField;
            this.passwordField = passwordField;
        }

        @Override
        // Handle Button Clicks
        public void actionPerformed(ActionEvent event) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.equals("admin") && password.equals("password")) {
                dispose(); // Close login screen
                ISAdminScreen isAdminScreen = null; //Opens the main application window
                try {
                    isAdminScreen = new ISAdminScreen();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                isAdminScreen.setVisible(true);
            } else {
                // Show Error Message
                statusLabel.setText("Invalid username or password!");
                statusLabel.setForeground(Color.RED);

            }
        }
    }
}
