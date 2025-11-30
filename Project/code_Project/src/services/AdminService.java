package services;

import models.Client;
import models.OperationResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public OperationResult addClient(Client client) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        if(conn == null || client == null){
            return OperationResult.error("Connection not available");
        }
        conn.setAutoCommit(false);
        String userQuery = "INSERT INTO users (first_name, last_name, email, phone_number, password, user_type) " +
                "VALUES (?, ?, ?, ?, ?, 'CLIENT')";

        PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
        userStmt.setString(1, client.getFirstName());
        userStmt.setString(2, client.getLastName());
        userStmt.setString(3, client.getEmail());
        userStmt.setString(4, client.getPhoneNumber());
        userStmt.setString(5, client.getPassword());

        int userRows = userStmt.executeUpdate();

        if (userRows == 0) {
            conn.rollback();
            userStmt.close();
            conn.close();
            return OperationResult.error("Failed to create user account");
        }

        // Get the auto-generated user_id
        ResultSet generatedKeys = userStmt.getGeneratedKeys();
        int userId = -1;
        if (generatedKeys.next()) {
            userId = generatedKeys.getInt(1);
        }
        generatedKeys.close();
        userStmt.close();

        // 2. Then insert into clients table
        String clientQuery = "INSERT INTO clients (client_id, account_number, balance) VALUES (?, ?, ?)";
        PreparedStatement clientStmt = conn.prepareStatement(clientQuery);
        clientStmt.setInt(1, userId);
        clientStmt.setString(2, client.getAccountNumber());
        clientStmt.setDouble(3, client.getBalance());

        int clientRows = clientStmt.executeUpdate();

        if (clientRows == 0) {
            conn.rollback();
            clientStmt.close();
            conn.close();
            return OperationResult.error("Failed to create client account");
        }

        conn.commit();
        clientStmt.close();
        conn.close();

        return OperationResult.success("models.Client added successfully: " + client.getFirstName() + " " + client.getLastName());
    }

    public OperationResult deleteClient(String accountNumber) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        if (conn == null||accountNumber == null) {
            return OperationResult.error("Connection not available");
        }

        // First get the client_id to delete from users table
        String getQuery = "SELECT client_id FROM clients WHERE account_number = ?";
        PreparedStatement getStmt = conn.prepareStatement(getQuery);
        getStmt.setString(1, accountNumber);

        ResultSet rs = getStmt.executeQuery();

        if (!rs.next()) {
            rs.close();
            getStmt.close();
            conn.close();
            return OperationResult.error("models.Client not found with account number: " + accountNumber);
        }

        int clientId = rs.getInt("client_id");
        rs.close();
        getStmt.close();


        String deleteClientQuery = "DELETE FROM clients WHERE client_id = ?";
        PreparedStatement deleteClientStmt = conn.prepareStatement(deleteClientQuery);
        deleteClientStmt.setInt(1, clientId);
        deleteClientStmt.executeUpdate();


        String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery);
        deleteUserStmt.setInt(1, clientId);
        deleteUserStmt.executeUpdate();

        int rowsAffectedUser = deleteClientStmt.executeUpdate();
        int rowsAffectedClient = deleteUserStmt.executeUpdate();

        deleteClientStmt.close();
        conn.close();

        if (rowsAffectedUser > 0 && rowsAffectedClient >0 ) {
            return OperationResult.success("models.Client deleted successfully: " + accountNumber);
        } else {
            return OperationResult.error("Failed to delete client: " + accountNumber);
        }
    }

    public List<Client> getAllClients() throws SQLException, ClassNotFoundException {
        List<Client> clients = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();
        if (conn == null) {
            return clients;
        }

        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone_number, u.password, " +
                "c.account_number, c.balance " +
                "FROM users u JOIN clients c ON u.user_id = c.client_id " +
                "ORDER BY u.first_name, u.last_name";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Client client = new Client(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("password"),
                    rs.getString("account_number"),
                    rs.getDouble("balance")
            );
            clients.add(client);
        }

        rs.close();
        stmt.close();
        conn.close();

        return clients;
    }

    public List<Client> findClient(String searchTerm) throws SQLException, ClassNotFoundException {
        List<Client> clients = new ArrayList<>();

        Connection conn = DataBaseConnection.getConnection();

        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.phone_number, u.password, " +
                "c.account_number, c.balance " +
                "FROM users u JOIN clients c ON u.user_id = c.client_id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR u.email LIKE ? OR c.account_number = ? " +
                "ORDER BY u.first_name, u.last_name";

        PreparedStatement stmt = conn.prepareStatement(query);
        String likePattern = "%" + searchTerm + "%";
        stmt.setString(1, likePattern);
        stmt.setString(2, likePattern);
        stmt.setString(3, likePattern);
        stmt.setString(4, searchTerm);


        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Client client = new Client(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("password"),
                    rs.getString("account_number"),
                    rs.getDouble("balance")
            );
            clients.add(client);
        }

        rs.close();
        stmt.close();
        conn.close();

        return clients;
    }

    public OperationResult updateClient(Client client) throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        if (conn == null ) {
            return OperationResult.error("Connection not available");
        }if (client == null ) {
            return OperationResult.error("you give me a empty client \"fi9 m3ana\"");
        }
        conn.setAutoCommit(false);


        String userQuery = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, password = ? " +
                "WHERE user_id = ?";

        PreparedStatement userStmt = conn.prepareStatement(userQuery);
        userStmt.setString(1, client.getFirstName());
        userStmt.setString(2, client.getLastName());
        userStmt.setString(3, client.getEmail());
        userStmt.setString(4, client.getPhoneNumber());
        userStmt.setString(5, client.getPassword());
        userStmt.setInt(6, client.getUserId());

        int userRows = userStmt.executeUpdate();
        userStmt.close();


        String clientQuery = "UPDATE clients SET account_number = ?, balance = ? WHERE client_id = ?";
        PreparedStatement clientStmt = conn.prepareStatement(clientQuery);
        clientStmt.setString(1, client.getAccountNumber());
        clientStmt.setDouble(2, client.getBalance());
        clientStmt.setInt(3, client.getUserId());

        int clientRows = clientStmt.executeUpdate();
        clientStmt.close();

        if (userRows > 0 && clientRows > 0) {
            conn.commit();
            conn.close();
            return OperationResult.success("models.Client updated successfully: " + client.getFirstName() + " " + client.getLastName());
        } else {
            conn.rollback();
            conn.close();
            return OperationResult.error("Failed to update client");
        }
    }

    public int getTotalClients() throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
//        if(conn == null ) {
//            return 0;
//        }
        String query = "SELECT COUNT(*) as TotalOfClient FROM clients";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        int total = 0;
        if (rs.next()) {
            total = rs.getInt("TotalOfClient");
        }

        rs.close();
        stmt.close();
        conn.close();

        return total;
    }

    public double getTotalBankBalance() throws SQLException, ClassNotFoundException {
        Connection conn = DataBaseConnection.getConnection();
        //if (conn == null ) {return -1;}
        String query = "SELECT SUM(balance) as TotalBalance FROM clients";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        double  TotalBalance = 0;
        if (rs.next()) {
             TotalBalance = rs.getDouble("TotalBalance");
        }

        rs.close();
        stmt.close();
        conn.close();

        return TotalBalance;
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
}