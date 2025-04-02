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
    public LoginScreen(Connection conn){
        this.conn = conn;
        setTitle("Login");
        setSize(400, 300); // Increased height to accommodate role selector
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1)); // Changed to 5 rows to add role selector

        // Role Selection Panel
        JPanel rolePanel = new JPanel();
        rolePanel.add(new JLabel("Role: "));
        String[] roles = {"Admin", "Inventory Officer"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        rolePanel.add(roleComboBox);

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
        // Pass roleComboBox, usernameField and passwordField
        loginButton.addActionListener(new LoginAction(usernameField, passwordField, roleComboBox));

        //Add the components(panel,button,label) to the frame
        add(rolePanel);
        add(usernamePanel);
        add(passwordPanel);
        add(loginButton);
        add(statusLabel);
    }
//LoginAction class implements ActionListener so it responds to button clicks.
    class LoginAction implements ActionListener {
        private final JTextField usernameField;
        private final JPasswordField passwordField;
        private final JComboBox<String> roleComboBox;
    //Constructor for LoginAction
        public LoginAction(JTextField usernameField, JPasswordField passwordField, JComboBox<String> roleComboBox) {
            this.usernameField = usernameField;
            this.passwordField = passwordField;
            this.roleComboBox = roleComboBox;
        }

        @Override
        // Handle Button Clicks
        public void actionPerformed(ActionEvent event) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String selectedRole = (String) roleComboBox.getSelectedItem();

            // Check credentials based on role
            if (selectedRole.equals("Admin")) {
                if (username.equals("") && password.equals("")) {
                    dispose(); // Close login screen
                    ISAdminScreen isAdminScreen = new ISAdminScreen(conn);
                    isAdminScreen.setVisible(true);
                } else {
                    statusLabel.setText("Invalid admin credentials!");
                    statusLabel.setForeground(Color.RED);
                }
            } else if (selectedRole.equals("Inventory Officer")) {
                if (username.equals("officer") && password.equals("password2")) {
                    dispose(); // Close login screen
                    InventoryOfficerScreen officerScreen = new InventoryOfficerScreen(conn);
                    officerScreen.setVisible(true);
                } else {
                    statusLabel.setText("Invalid inventory officer credentials!");
                    statusLabel.setForeground(Color.RED);
                }
            }
        }
    }
}
