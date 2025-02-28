import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // Username Field
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username: "));
        JTextField usernameField = new JTextField(15);  // Local variable
        usernamePanel.add(usernameField);

        // Password Field
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password: "));
        JPasswordField passwordField = new JPasswordField(15);  // Local variable
        passwordPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");  // Local variable
        loginButton.addActionListener(new LoginAction(usernameField, passwordField));

        // Status Label
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);  // Local variable

        add(usernamePanel);
        add(passwordPanel);
        add(loginButton);
        add(statusLabel);
    }

    class LoginAction implements ActionListener {
        private final JTextField usernameField;
        private final JPasswordField passwordField;

        // Constructor for passing the fields
        public LoginAction(JTextField usernameField, JPasswordField passwordField) {
            this.usernameField = usernameField;
            this.passwordField = passwordField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Hardcoded credentials (Replace with actual authentication)
            if (username.equals("admin") && password.equals("password")) {
                dispose(); // Close login screen
                MainScreen mainScreen = new MainScreen();
                mainScreen.setVisible(true);
            } else {
                // Status label should be updated directly within actionPerformed
                JLabel statusLabel = (JLabel) getComponent(3); // Get the status label component
                statusLabel.setText("Invalid username or password!");
                statusLabel.setForeground(Color.RED);
            }
        }
    }
}
