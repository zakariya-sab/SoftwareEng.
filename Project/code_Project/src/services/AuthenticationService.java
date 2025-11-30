package services;

import models.Admin;
import models.Client;
import models.OperationResult;
import models.User;

import java.sql.*;
public class AuthenticationService {
    private User currentUser = null;//same think we do in C hhh to avoid the errors

    public OperationResult login(String email, String password) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        if( conn == null){
            return new OperationResult(false, "Connection failed");
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, " +
                "u.phone_number, u.password, u.user_type, " +
                "c.account_number, c.balance " +
                "FROM users u " +
                "LEFT JOIN clients c ON u.user_id = c.client_id " +
                "WHERE u.email = ? AND u.password = ?";

        stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, password);

        rs = stmt.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("user_id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String userEmail = rs.getString("email");
            String phoneNumber = rs.getString("phone_number");
            String userPassword = rs.getString("password");
            String userType = rs.getString("user_type");

            if ("CLIENT".equals(userType)) {
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");

                currentUser = new Client(
                        userId, firstName, lastName, userEmail,
                        phoneNumber, userPassword, accountNumber, balance
                );

                return OperationResult.success("models.Client login successful");

            } else if ("ADMIN".equals(userType)) {
                currentUser = new Admin(
                        userId, firstName, lastName, userEmail,
                        phoneNumber, userPassword
                );

                return OperationResult.success("models.Admin login successful");
            }
        }

        rs.close();
        stmt.close();
        conn.close();

        return OperationResult.error("Invalid email or password");
    }

    public OperationResult logout() {
        if (currentUser != null) {
            String userName = currentUser.getFirstName() + " " + currentUser.getLastName();
            currentUser = null;
            return OperationResult.success("models.User logged out: " + userName);
        }
        return OperationResult.error("No user was logged in");
    }

    public OperationResult changePassword(String currentPassword, String newPassword) throws SQLException, ClassNotFoundException {
        if (currentUser == null) {
            return OperationResult.error("No user is logged in");
        }

        if (!currentUser.getPassword().equals(currentPassword)) {
            return OperationResult.error("Current password is incorrect");
        }

        Connection conn = DataBaseConnection.getConnection();
        if( conn == null){
            return OperationResult.error("Connection failed to the database no change happened");
        }
        String query = "UPDATE users SET password = ? WHERE user_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, newPassword);
        stmt.setInt(2, currentUser.getUserId());

        int rowsAffected = stmt.executeUpdate();

        stmt.close();
        conn.close();

        if (rowsAffected > 0) {
            currentUser.setPassword(newPassword);
            return OperationResult.success("Password changed successfully");
        } else {
            return OperationResult.error("Failed to change password");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser instanceof Admin;
    } //use instanceof is better you

    public boolean isClient() {
        return currentUser instanceof Client;
    }

    public Client getCurrentClient() {
        if (isClient()) {
            return (Client) currentUser;
        }
        return null;
    }

    public Admin getCurrentAdmin() {
        if (isAdmin()) {
            return (Admin) currentUser;
        }
        return null;
    }
}