package com.robut.markov;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class SQLiteConnection {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";

    private String url;
    private Connection connection;

    private HashMap<String, Integer> stringToIdMap = new HashMap<>();
    private HashMap<Integer, String> idToStringMap = new HashMap<>();
    private int currentWordId = 2;

    public SQLiteConnection(String dbPath) throws SQLException{
        File filepath = new File(dbPath);
        try {
            this.url = this.SQLITE_PREFIX + filepath.getCanonicalPath();
        }
        catch (IOException e) {
            System.err.printf("Error connecting to file at %s: %s%n", filepath, e);
            return;
        }

        if (!filepath.exists()) {
            createDB();
            createTables();
        }
        else{
            connectDB();
        }
    }

    public void insertWord(String word) throws SQLException{
        PreparedStatement prepStatement = this.connection.prepareStatement("INSERT INTO WordIDs (id, word) VALUES (?, ?)");

        prepStatement.setInt(1, this.currentWordId++);
        prepStatement.setString(2, word);
        prepStatement.executeUpdate();
    }

    public void saveLogItem(String pre, String post, int count) throws SQLException{
        if (!stringToIdMap.containsKey(pre)){
            insertWord(pre);
        }
        if (!stringToIdMap.containsKey(post)){
            insertWord(post);
        }

        PreparedStatement prepStatement = this.connection.prepareStatement("INSERT INTO WordRelations (preID, postID) VALUES (?, ?)");
        prepStatement.setInt(1, this.stringToIdMap.get(pre));
        prepStatement.setInt(2, this.stringToIdMap.get(pre));
    }

    public void saveLogItem(String pre, String post) throws SQLException{
        saveLogItem(pre, post, 1);
    }

    private void createDB() throws SQLException{
        this.connection = DriverManager.getConnection(this.url);
        System.out.printf("Created database at: %s%n", this.url);

        createTables();
    }

    private void createTables() throws SQLException{
        String createIdMapSql = "CREATE TABLE IF NOT EXISTS WordIDs (\n" +
                "id integer PRIMARY KEY NOT NULL, \n" +
                "word text \n" +
                ");";

        String createWordRelationSql = "CREATE TABLE IF NOT EXISTS WordRelations (\n" +
                "preID integer NOT NULL, \n" +
                "postID integer NOT NULL, \n" +
                "PRIMARY KEY(preID, postID) \n" +
                ");";


        Statement statement = this.connection.createStatement();
        statement.execute(createIdMapSql);
        statement.execute(createWordRelationSql);
    }

    private void connectDB() throws SQLException{
        try{
            this.connection = DriverManager.getConnection(this.url);
            System.out.printf("Connected to database at: %s%n", this.url);
        }
        catch (SQLException e){
            System.err.printf("Error connecting to database: %s%n", e);
        }
    }
}
