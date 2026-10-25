package com.nick.fortressdestoryers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        //Tests to see if Java is able to connect to mysql
        try (Connection connection = DatabaseConnection.connect()) {
            System.out.println("Connected successfully");

            try (Statement statement = connection.createStatement();
                 ResultSet results = statement.executeQuery("SHOW TABLES")) {
                System.out.println("Tables in database:");
                while (results.next()) {
                    System.out.println(results.getString(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
