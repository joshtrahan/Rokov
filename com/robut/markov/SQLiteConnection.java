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
    private Connection conn;

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
        }
    }

    public void insertWord(String word) throws SQLException{
        PreparedStatement prepStatement = this.conn.prepareStatement("INSERT INTO WordIDs (id, word) VALUES (?, ?)");

        this.stringToIdMap.put(word, this.currentWordId);

        prepStatement.setInt(1, this.currentWordId);
        prepStatement.setString(2, word);
        prepStatement.executeUpdate();

        this.currentWordId = this.currentWordId + 1;
    }

    public void saveLogItem(String pre, String post, int count) throws SQLException{
        int preID;
        int postID;

        if (pre == null){
            preID = 0;
        }
        else{
            if (!stringToIdMap.containsKey(pre)){
                insertWord(pre);
            }
            preID = stringToIdMap.get(pre);
        }

        if (post == null){
            postID = 1;
        }
        else{
            if (!stringToIdMap.containsKey(post)){
                insertWord(post);
            }
            postID = stringToIdMap.get(post);
        }


        if (!stringToIdMap.containsKey(post)){
            insertWord(post);
        }

        PreparedStatement prepStatement = this.conn.prepareStatement("INSERT INTO WordRelations (preID, postID, count) VALUES (?, ?, ?)");
        prepStatement.setInt(1, preID);
        prepStatement.setInt(2, postID);
        prepStatement.setInt(3, count);
        prepStatement.execute();
    }

    public void saveLogItem(String pre, String post) throws SQLException{
        saveLogItem(pre, post, 1);
    }

    private void createDB() throws SQLException{
        System.out.printf("Created database at: %s%n", this.url);

        createTables();

        System.out.printf("Created tables.%n");

        createStartEndIds();
    }

    private void createStartEndIds() throws SQLException{
        PreparedStatement prepStatement1 = this.conn.prepareStatement("INSERT INTO WordIDs (id, word) VALUES (?, ?)");
        prepStatement1.setInt(1, 0);
        prepStatement1.setString(2, null);
        prepStatement1.execute();

        PreparedStatement prepStatement2 = this.conn.prepareStatement("INSERT INTO WordIDs (id, word) VALUES (?, ?)");
        prepStatement2.setInt(1, 1);
        prepStatement2.setString(2, null);
        prepStatement2.execute();
    }

    private void createTables() throws SQLException{
        this.conn = DriverManager.getConnection(this.url);

        String createIdMapSql = "CREATE TABLE IF NOT EXISTS WordIDs (\n" +
                "id integer PRIMARY KEY NOT NULL, \n" +
                "word text \n" +
                ");";

        String createWordRelationSql = "CREATE TABLE IF NOT EXISTS WordRelations (\n" +
                "preID integer NOT NULL, \n" +
                "postID integer NOT NULL, \n" +
                "count integer NOT NULL, \n" +
                "PRIMARY KEY(preID, postID) \n" +
                ");";


        Statement statement = conn.createStatement();
        statement.execute(createIdMapSql);
        statement.execute(createWordRelationSql);
    }
}
