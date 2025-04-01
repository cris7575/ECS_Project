package ecs.ui;

import ecs.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportScreen extends JFrame {

    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ReportScreen() {
        setTitle("Transaction Report");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = { "Username", "Item Name", "Checkout Date", "Return Date" };
        tableModel = new DefaultTableModel(columnNames, 0);
        reportTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        loadTransactionData();
    }

    private void loadTransactionData() {
        String sql = """
            SELECT u.username, i.name, ct.checkoutDate, ct.returnDate
            FROM CheckoutTransaction ct
            JOIN User u ON ct.userID = u.userID
            JOIN Item i ON ct.itemID = i.itemID
            ORDER BY ct.checkoutDate DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String itemName = rs.getString("name");
                String checkoutDate = rs.getString("checkoutDate");
                String returnDate = rs.getString("returnDate");

                tableModel.addRow(new Object[]{ username, itemName, checkoutDate, returnDate });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading report: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportScreen().setVisible(true));
    }
}
