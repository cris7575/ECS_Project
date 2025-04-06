package ecs.controllers;

import ecs.database.DatabaseConnection;
import java.sql.*;

public class UserController {

    public boolean validateLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM User WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // true if found
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }
    
    public String getUserRole(String username) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "SELECT role FROM User WHERE username=?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getString("role");
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving role: " + e.getMessage());
    }
    return null;
}

}
