package services;

import models.Client;
import models.OperationResult;
import models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {

    public OperationResult deposit(String accountNumber, double amount) throws SQLException, ClassNotFoundException {
        if (amount <= 0) {
            return OperationResult.error("Deposit amount must be positive");
        }

        Connection conn = DataBaseConnection.getConnection();
        if (conn == null) {
            return OperationResult.error("Connection is not available today go back tomorowo");
        }
        conn.setAutoCommit(false);


        String updateQuery = "UPDATE clients SET balance = balance + ? WHERE account_number = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        updateStmt.setDouble(1, amount);
        updateStmt.setString(2, accountNumber);

        int rowsAffected = updateStmt.executeUpdate();

        if (rowsAffected == 0) {
            conn.rollback();
            updateStmt.close();
            conn.close();
            return OperationResult.error("Account not found: " + accountNumber);
        }


        String insertQuery = "INSERT INTO transactions (to_account_number, amount, transaction_type, description) " +
                "VALUES (?, ?, 'DEPOSIT', ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
        insertStmt.setString(1, accountNumber);
        insertStmt.setDouble(2, amount);
        insertStmt.setString(3, "Cash deposit");
        insertStmt.executeUpdate();

        conn.commit();

        updateStmt.close();
        insertStmt.close();
        conn.close();

        return OperationResult.success("Deposit successful: " + amount + " added to account " + accountNumber);
    }

    public OperationResult withdraw(String accountNumber, double amount) throws SQLException, ClassNotFoundException {
        if (amount <= 0) {
            return OperationResult.error("Withdrawal amount must be positive");
        }

        Connection conn = DataBaseConnection.getConnection();
        conn.setAutoCommit(false);


        String checkQuery = "SELECT balance FROM clients WHERE account_number = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, accountNumber);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            conn.rollback();
            checkStmt.close();
            conn.close();
            return OperationResult.error("Account not found: " + accountNumber);
        }

        double currentBalance = rs.getDouble("balance");
        if (currentBalance < amount) {
            conn.rollback();
            rs.close();
            checkStmt.close();
            conn.close();
            return OperationResult.error("Insufficient balance. Current: " + currentBalance + ", Required: " + amount);
        }
        rs.close();
        checkStmt.close();


        String updateQuery = "UPDATE clients SET balance = balance - ? WHERE account_number = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
        updateStmt.setDouble(1, amount);
        updateStmt.setString(2, accountNumber);
        updateStmt.executeUpdate();


        String insertQuery = "INSERT INTO transactions (from_account_number, amount, transaction_type, description) " +
                "VALUES (?, ?, 'WITHDRAWAL', ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
        insertStmt.setString(1, accountNumber);
        insertStmt.setDouble(2, amount);
        insertStmt.setString(3, "Cash withdrawal");
        insertStmt.executeUpdate();

        conn.commit();

        updateStmt.close();
        insertStmt.close();
        conn.close();

        return OperationResult.success("Withdrawal successful: " + amount + " from account " + accountNumber);
    }

    public OperationResult transfer(String fromAccount, String toAccount, double amount) throws SQLException, ClassNotFoundException {
        if (amount <= 0) {
            return OperationResult.error("Transfer amount must be positive");
        }

        if (fromAccount.equals(toAccount)) {
            return OperationResult.error("Cannot transfer to the same account");
        }

        Connection conn = DataBaseConnection.getConnection();
        conn.setAutoCommit(false);


        String checkQuery = "SELECT balance FROM clients WHERE account_number = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, fromAccount);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            conn.rollback();
            checkStmt.close();
            conn.close();
            return OperationResult.error("Sender account not found: " + fromAccount);
        }

        double senderBalance = rs.getDouble("balance");
        if (senderBalance < amount) {
            conn.rollback();
            rs.close();
            checkStmt.close();
            conn.close();
            return OperationResult.error("Insufficient balance. Current: " + senderBalance + ", Required: " + amount);
        }
        rs.close();


        checkStmt.setString(1, toAccount);
        rs = checkStmt.executeQuery();
        if (!rs.next()) {
            conn.rollback();
            rs.close();
            checkStmt.close();
            conn.close();
            return OperationResult.error("Receiver account not found: " + toAccount);
        }
        rs.close();
        checkStmt.close();


        String updateFromQuery = "UPDATE clients SET balance = balance - ? WHERE account_number = ?";
        PreparedStatement updateFromStmt = conn.prepareStatement(updateFromQuery);
        updateFromStmt.setDouble(1, amount);
        updateFromStmt.setString(2, fromAccount);
        updateFromStmt.executeUpdate();


        String updateToQuery = "UPDATE clients SET balance = balance + ? WHERE account_number = ?";
        PreparedStatement updateToStmt = conn.prepareStatement(updateToQuery);
        updateToStmt.setDouble(1, amount);
        updateToStmt.setString(2, toAccount);
        updateToStmt.executeUpdate();


        String insertQuery = "INSERT INTO transactions (from_account_number, to_account_number, amount, transaction_type, description) " +
                "VALUES (?, ?, ?, 'TRANSFER', ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
        insertStmt.setString(1, fromAccount);
        insertStmt.setString(2, toAccount);
        insertStmt.setDouble(3, amount);
        insertStmt.setString(4, "Transfer to account " + toAccount);
        insertStmt.executeUpdate();

        conn.commit();

        updateFromStmt.close();
        updateToStmt.close();
        insertStmt.close();
        conn.close();

        return OperationResult.success("Transfer successful: " + amount + " from " + fromAccount + " to " + toAccount);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();
        String query = "SELECT * FROM transactions " +
                "WHERE from_account_number = ? OR to_account_number = ? " +
                "ORDER BY timestamp DESC";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);
        stmt.setString(2, accountNumber);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getString("from_account_number"),
                    rs.getString("to_account_number"),
                    rs.getDouble("amount"),
                    rs.getString("transaction_type"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    rs.getString("description")
            );
            transactions.add(transaction);
        }

        rs.close();
        stmt.close();
        conn.close();

        return transactions;
    }

    public OperationResult updateClientInfo(Client client) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ? " +
                "WHERE user_id = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, client.getFirstName());
        stmt.setString(2, client.getLastName());
        stmt.setString(3, client.getEmail());
        stmt.setString(4, client.getPhoneNumber());
        stmt.setInt(5, client.getUserId());

        int rowsAffected = stmt.executeUpdate();

        stmt.close();
        conn.close();

        if (rowsAffected > 0) {
            return OperationResult.success("models.Client information updated successfully");
        } else {
            return OperationResult.error("Failed to update client information");
        }
    }

    public Client getClientByAccountNumber(String accountNumber) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone_number, u.password, " +
                "c.account_number, c.balance " +
                "FROM users u JOIN clients c ON u.user_id = c.client_id " +
                "WHERE c.account_number = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);

        ResultSet rs = stmt.executeQuery();

        Client client = null;
        if (rs.next()) {
            client = new Client(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("password"),
                    rs.getString("account_number"),
                    rs.getDouble("balance")
            );
        }

        rs.close();
        stmt.close();
        conn.close();

        return client;
    }

    public double getBalance(String accountNumber) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        String query = "SELECT balance FROM clients WHERE account_number = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);

        ResultSet rs = stmt.executeQuery();

        double balance = -1;
        if (rs.next()) {
            balance = rs.getDouble("balance");
        }

        rs.close();
        stmt.close();
        conn.close();

        return balance;
    }
}