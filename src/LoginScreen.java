
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginScreen() {
        setTitle("Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        // Username Field
        JPanel usernamePanel = new JPanel();
        usernamePanel.add(new JLabel("Username: "));
        usernameField = new JTextField(15);
        usernamePanel.add(usernameField);

        // Password Field
        JPanel passwordPanel = new JPanel();
        passwordPanel.add(new JLabel("Password: "));
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordField);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginAction());

        // Status Label
        statusLabel = new JLabel("", SwingConstants.CENTER);

        add(usernamePanel);
        add(passwordPanel);
        add(loginButton);
        add(statusLabel);
    }

    class LoginAction implements ActionListener {
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
                statusLabel.setText("Invalid username or password!");
                statusLabel.setForeground(Color.RED);
            }
        }
    }
}
