package ecs.ui;

import ecs.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ReturnScreen extends JFrame {
    private JComboBox<String> itemDropdown;
    private JLabel statusLabel;
    private String username;

    public ReturnScreen(String username) {
        this.username = username;

        setTitle("Return Item");
        setSize(400, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1));

        itemDropdown = new JComboBox<>(loadCheckedOutItems());
        JButton returnBtn = new JButton("Return Item");
        statusLabel = new JLabel("", SwingConstants.CENTER);

        add(new JLabel("Select an item to return:", SwingConstants.CENTER));
        add(itemDropdown);
        add(returnBtn);
        add(statusLabel);

        returnBtn.addActionListener(e -> {
            String itemName = (String) itemDropdown.getSelectedItem();
            if (itemName != null) {
                processReturn(itemName);
            } else {
                statusLabel.setText("❌ No item selected");
            }
        });
    }

    private String[] loadCheckedOutItems() {
        Vector<String> items = new Vector<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = """
                SELECT i.name FROM Item i
                JOIN CheckoutTransaction ct ON i.itemID = ct.itemID
                JOIN User u ON ct.userID = u.userID
                WHERE u.username = ? AND i.status = 'Checked Out' AND ct.returnDate IS NULL
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        return items.toArray(new String[0]);
    }

    private void processReturn(String itemName) {
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

            // Update returnDate in CheckoutTransaction
            String updateTransaction = """
                UPDATE CheckoutTransaction
                SET returnDate = NOW()
                WHERE userID = ? AND itemID = ? AND returnDate IS NULL
                """;
            PreparedStatement transStmt = conn.prepareStatement(updateTransaction);
            transStmt.setInt(1, userID);
            transStmt.setInt(2, itemID);
            transStmt.executeUpdate();

            // Update item status
            PreparedStatement updateItem = conn.prepareStatement("UPDATE Item SET status = 'Available' WHERE itemID = ?");
            updateItem.setInt(1, itemID);
            updateItem.executeUpdate();

            statusLabel.setText("✅ Item returned successfully!");
            itemDropdown.removeItem(itemName);
        } catch (SQLException e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReturnScreen("admin").setVisible(true));
    }
}
