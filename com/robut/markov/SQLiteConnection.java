package com.robut.markov;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteConnection {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";

    private String url;
    private Connection conn;

    private HashMap<String, Integer> stringToIdMap = new HashMap<>();
    private HashMap<Integer, String> idToStringMap = new HashMap<>();
    private int currentWordId = 2;

    private HashMap<Integer, HashMap<Integer, Integer>> relationMap = new HashMap<>();

    public SQLiteConnection(String dbPath) throws SQLException{
        File filepath = new File(dbPath);
        try {
            this.url = this.SQLITE_PREFIX + filepath.getCanonicalPath();
        }
        catch (IOException e) {
            System.err.printf("Error connecting to file at %s: %s%n", filepath, e);
            return;
        }

        if (filepath.exists()){
            this.conn = DriverManager.getConnection(this.url);
            connectDB();
        }
        else {
            this.conn = DriverManager.getConnection(this.url);
            createDB();
        }

    }

    public void connectDB() throws SQLException{
        Statement statement = this.conn.createStatement();

        ResultSet results = statement.executeQuery("SELECT id, word FROM WordIDs WHERE word IS NOT NULL");

        while (results.next()){
            String word = results.getString("word");
            int id = results.getInt("id");
            this.stringToIdMap.put(word, id);
            this.idToStringMap.put(id, word);
            if (this.currentWordId < id){
                this.currentWordId = id;
            }
        }

        results = statement.executeQuery("SELECT preID, postID, count FROM WordRelations");
    }

    public void saveWords(ArrayList<String> words) throws SQLException {
        this.conn.setAutoCommit(false);
        PreparedStatement prepStatement = this.conn.prepareStatement("INSERT INTO WordIDs (id, word) VALUES (?, ?)");

        for (String word : words){
            addWordToBatch(word, prepStatement);
        }

        prepStatement.executeBatch();
        this.conn.commit();
        this.conn.setAutoCommit(true);
    }

    private void addWordToBatch(String word, PreparedStatement prepStatement) throws SQLException{
        this.stringToIdMap.put(word, this.currentWordId);

        prepStatement.setInt(1, this.currentWordId);
        prepStatement.setString(2, word);
        prepStatement.addBatch();

        this.currentWordId = this.currentWordId + 1;
    }

    public void saveLogItems(ArrayList<LogItem> items) throws SQLException{
        this.conn.setAutoCommit(false);
        PreparedStatement insertStatement = this.conn.prepareStatement("INSERT INTO WordRelations (preID, postID, count) VALUES (?, ?, ?)");
        PreparedStatement updateStatement = this.conn.prepareStatement("UPDATE WordRelations \n" +
                "SET count = ? \n" +
                "WHERE preID = ? AND postID = ?");

        for (LogItem item : items){
            addLogItemToBatch(item.getPredecessor(), item.getSuccessor(), item.getCount(), insertStatement, updateStatement);
        }

        insertStatement.executeBatch();
        updateStatement.executeBatch();
        this.conn.commit();
        this.conn.setAutoCommit(true);
    }

    private void addLogItemToBatch(String pre, String post, int count, PreparedStatement insertStatement, PreparedStatement updateStatement) throws SQLException{
        int preID;
        int postID;

        if (pre == null){
            preID = 0;
        }
        else{
            preID = stringToIdMap.get(pre);
        }

        if (post == null){
            postID = 1;
        }
        else{
            postID = stringToIdMap.get(post);
        }

        if (!(this.relationMap.containsKey(preID))){
            this.relationMap.put(preID, new HashMap<>());
        }
        if (!this.relationMap.get(preID).containsKey(postID)){
            this.relationMap.get(preID).put(postID, count);
            insertStatement.setInt(1, preID);
            insertStatement.setInt(2, postID);
            insertStatement.setInt(3, count);
            insertStatement.addBatch();
        }
        else{
            count = count + this.relationMap.get(preID).get(postID);
            this.relationMap.get(preID).put(postID, count);
            updateStatement.setInt(1, count);
            updateStatement.setInt(2, preID);
            updateStatement.setInt(3, postID);
            updateStatement.addBatch();
        }
    }

    private void createDB() throws SQLException{
        System.out.printf("Created database file at: %s...%n", this.url);

        createTables();

        System.out.printf("Created tables...%n");

        createStartEndIds();

        System.out.printf("Created special word IDs...%n");
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
