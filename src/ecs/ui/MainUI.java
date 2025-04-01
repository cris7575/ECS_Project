package ecs.ui;

import javax.swing.*;

public class MainUI extends JFrame {
    private String username;
    private String role;

    public MainUI(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("ECS Dashboard - " + role);
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " (" + role + ")", SwingConstants.CENTER);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton checkoutBtn = new JButton("Check Out Item");
        JButton returnBtn = new JButton("Return Item");
        JButton reportBtn = new JButton("View Transactions");
        JButton userMgmtBtn = new JButton("Manage Users");
        JButton logoutBtn = new JButton("Logout");

        checkoutBtn.setAlignmentX(CENTER_ALIGNMENT);
        returnBtn.setAlignmentX(CENTER_ALIGNMENT);
        reportBtn.setAlignmentX(CENTER_ALIGNMENT);
        userMgmtBtn.setAlignmentX(CENTER_ALIGNMENT);
        logoutBtn.setAlignmentX(CENTER_ALIGNMENT);

        // Add base components
        add(welcomeLabel);
        add(checkoutBtn);
        add(returnBtn);

        // Only show if Admin or Supervisor
        if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Supervisor")) {
            add(reportBtn);
        }

        // Only show if Admin
        if (role.equalsIgnoreCase("Admin")) {
            add(userMgmtBtn);
        }

        add(logoutBtn);

        // Actions
        checkoutBtn.addActionListener(e -> new CheckoutScreen(username).setVisible(true));
        returnBtn.addActionListener(e -> new ReturnScreen(username).setVisible(true));
        reportBtn.addActionListener(e -> new ReportScreen().setVisible(true));
        userMgmtBtn.addActionListener(e -> new UserManagementScreen().setVisible(true));
        logoutBtn.addActionListener(e -> {
            dispose(); // close dashboard
            new LoginScreen().setVisible(true); // show login again
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUI("admin", "Admin").setVisible(true));
    }
}
