package services;

import java.sql.*;
public class DataBaseConnection  {

        private static Connection _connection = null;


        private static final String _URL = "jdbc:mysql://localhost:3306/SupperBank";
        private static final String _USERNAME = "root";
        private static final String _PASSWORD = "";




       private DataBaseConnection() {}

        public static Connection getConnection()  {

            try{
                if (_connection == null || _connection.isClosed()) {

                    Class.forName("com.mysql.cj.jdbc.Driver");

                    _connection = DriverManager.getConnection(_URL, _USERNAME, _PASSWORD);

                }
                return _connection;

            }catch(SQLException | ClassNotFoundException e){
                System.out.println(e.toString());

            }
            return null;
        }

        public static void closeConnection()  {

              try {
                  if (_connection != null && !_connection.isClosed()) {
                      _connection.close();
                      System.out.println("Database connection closed.");
                  }
              }catch(SQLException e){
                  System.out.println(e.toString());

            }


        }
}

