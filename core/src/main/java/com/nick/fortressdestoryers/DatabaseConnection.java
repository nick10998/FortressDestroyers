package com.nick.fortressdestoryers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/fddb";
    private static final String user = "root";
    private static final String password = "root";

    //connect to the mysql database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}

