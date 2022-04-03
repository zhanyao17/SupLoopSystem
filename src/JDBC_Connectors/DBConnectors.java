package JDBC_Connectors;


import java.sql.*;


public class DBConnectors{  
    public Connection getConnection() throws SQLException
    {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Sup_Loop_Database","newUser","zhanyao12345");

        return connection;
    }
}  