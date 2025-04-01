package ecs.ui;

import ecs.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class CheckoutScreen extends JFrame {
    private JComboBox<String> itemComboBox;
    private JLabel statusLabel;
    private String username;

    public CheckoutScreen(String username) {
        this.username = username;

        setTitle("Check Out Item");
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        itemComboBox = new JComboBox<>(loadAvailableItems());
        JButton checkoutBtn = new JButton("Check Out");
        statusLabel = new JLabel("", SwingConstants.CENTER);

        add(new JLabel("Select an item:", SwingConstants.CENTER));
        add(itemComboBox);
        add(checkoutBtn);
        add(statusLabel);

        checkoutBtn.addActionListener(e -> {
            String itemName = (String) itemComboBox.getSelectedItem();
            if (itemName != null) {
                processCheckout(itemName);
            } else {
                statusLabel.setText("❌ No item selected");
            }
        });
    }

    private String[] loadAvailableItems() {
        Vector<String> items = new Vector<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name FROM Item WHERE status = 'Available'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        return items.toArray(new String[0]);
    }

    private void processCheckout(String itemName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get userID
            int userID = -1;
            PreparedStatement userStmt = conn.prepareStatement("SELECT userID FROM User WHERE username = ?");
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                userID = userRs.getInt("userID");
            } else {
                statusLabel.setText("❌ User not found");
                return;
            }

            // Get itemID
            int itemID = -1;
            PreparedStatement itemStmt = conn.prepareStatement("SELECT itemID FROM Item WHERE name = ?");
            itemStmt.setString(1, itemName);
            ResultSet itemRs = itemStmt.executeQuery();
            if (itemRs.next()) {
                itemID = itemRs.getInt("itemID");
            } else {
                statusLabel.setText("❌ Item not found");
                return;
            }

            // Insert into CheckoutTransaction
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO CheckoutTransaction (userID, itemID, checkoutDate) VALUES (?, ?, NOW())");
            insertStmt.setInt(1, userID);
            insertStmt.setInt(2, itemID);
            insertStmt.executeUpdate();

            // Update item status
            PreparedStatement updateStmt = conn.prepareStatement(
                "UPDATE Item SET status = 'Checked Out' WHERE itemID = ?");
            updateStmt.setInt(1, itemID);
            updateStmt.executeUpdate();

            statusLabel.setText("✅ Item checked out successfully!");
            itemComboBox.removeItem(itemName);
        } catch (SQLException e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
        }
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckoutScreen("admin").setVisible(true));
    }
}
