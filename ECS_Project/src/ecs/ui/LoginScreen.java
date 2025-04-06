package ecs.ui;

import ecs.controllers.UserController;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("ECS Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        add(new JLabel("Username", SwingConstants.CENTER));
        add(usernameField);
        add(new JLabel("Password", SwingConstants.CENTER));
        add(passwordField);
        add(loginBtn);
        add(statusLabel);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            UserController uc = new UserController();
            if (uc.validateLogin(username, password)) {
                statusLabel.setText("✅ Login successful!");
                // Launch dashboard or next screen here
                String role = uc.getUserRole(username);
                dispose(); // Close login window
                new MainUI(username, role).setVisible(true);
            } else {
                statusLabel.setText("❌ Invalid credentials");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
