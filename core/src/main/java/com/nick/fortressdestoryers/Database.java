package com.nick.fortressdestoryers;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private int levelNumber;
    private int highscore;
    private boolean levelComplete;
    private static Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public static List<String[]> getData(){
        List<String[]> scoreRow = new ArrayList<>();
        String query = "SELECT levelID, highscore, levelCompleted FROM playerStats ORDER BY levelID ASC";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int levelNumber = resultSet.getInt("levelID");
                int highscore = resultSet.getInt("highscore");
                boolean levelComplete = resultSet.getBoolean("levelCompleted");

                String levelString = "Level " + levelNumber;
                String scoreString = "High-score: " + highscore;
                String completeString = levelComplete ? "Completed" : "Incomplete";

                scoreRow.add(new String[]{levelString, scoreString, completeString});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return scoreRow;
    }
}

