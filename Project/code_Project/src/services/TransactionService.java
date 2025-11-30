package services;

import models.OperationResult;
import models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    public OperationResult recordTransaction(Transaction transaction) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        if (conn == null || transaction == null) {
            return OperationResult.error("Connection is not working");
        }

        String query = "INSERT INTO transactions (from_account_number, to_account_number, amount, transaction_type, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, transaction.getFromAccountNumber());
        stmt.setString(2, transaction.getToAccountNumber());
        stmt.setDouble(3, transaction.getAmount());
        stmt.setString(4, transaction.getTransactionType());
        stmt.setString(5, transaction.getDescription());

        int rowsAffected = stmt.executeUpdate();

        stmt.close();
        conn.close();

        if (rowsAffected > 0) {
            return OperationResult.success("models.Transaction recorded successfully");
        } else {
            return OperationResult.error("Failed to record transaction");
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();
        if (conn == null || accountNumber == null) {
            return null;
        }

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

    public List<Transaction> getAllTransactions() throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();
        if (conn == null) {
            return null;
        }

        String query = "SELECT * FROM transactions ORDER BY timestamp DESC";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

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

    public List<Transaction> getTransactionsByType(String transactionType) throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();
        if (conn == null || transactionType == null) {
            return null;
        }

        String query = "SELECT * FROM transactions WHERE transaction_type = ? ORDER BY timestamp DESC";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, transactionType);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getString("from_account_number"),
                    rs.getString("to_account_number"),
                    rs.getDouble("amount"),
                    rs.getString("transaction_type"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),// ask deep seek to know the role of this methode
                    rs.getString("description")
            );
            transactions.add(transaction);
        }

        rs.close();
        stmt.close();
        conn.close();

        return transactions;
    }

    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) throws SQLException, ClassNotFoundException {
        List<Transaction> transactions = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();
        if (conn == null ) {
            return null;
        }
        String query = "SELECT * FROM transactions WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDate(1, startDate);
        stmt.setDate(2, endDate);

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

    public int getTransactionCountForAccount(String accountNumber) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
/**/
       if (conn == null ) {
            return 0;
        }
        String query = "SELECT COUNT(*) as count FROM transactions " +
                "WHERE from_account_number = ? OR to_account_number = ?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, accountNumber);
        stmt.setString(2, accountNumber);

        ResultSet rs = stmt.executeQuery();

        int count = 0;
        if (rs.next()) {
            count = rs.getInt("count");
        }

        rs.close();
        stmt.close();
        conn.close();

        return count;
    }
}