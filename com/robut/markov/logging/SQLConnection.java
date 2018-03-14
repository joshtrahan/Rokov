package com.robut.markov.logging;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLConnection implements Runnable {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";

    private String url;
    private Connection conn;

    private ConcurrentHashMap<WordCombo, Integer> itemsToSave;
    private HashMap<WordCombo, Integer> relationMap = new HashMap<>();

    public SQLConnection(String filepath, ConcurrentHashMap<WordCombo, Integer> itemMap) throws SQLException, IOException {
        this(new File(filepath), itemMap);
    }

    public SQLConnection(File filepath, ConcurrentHashMap<WordCombo, Integer> itemMap) throws SQLException, IOException {
        this.itemsToSave = itemMap;
        this.url = this.SQLITE_PREFIX + filepath.getCanonicalPath();
        boolean fileExists = filepath.exists();
        this.conn = DriverManager.getConnection(this.url);

        if (!fileExists){
            createDB();
        }
    }

    public void run() {
        try {
            saveItems();
        }
        catch (SQLException e){
            System.err.printf("Error saving database: %s%n", e);
        }
    }

    public Collection<LogItem> loadItems() throws SQLException {
        Statement statement = this.conn.createStatement();

        ResultSet results = statement.executeQuery("SELECT pre, post, count FROM Relations");

        ArrayList<LogItem> loadedItems = new ArrayList<>();
        while(results.next()){
            String pre = results.getString("pre");
            String post = results.getString("post");
            int count = results.getInt("count");

            loadedItems.add(new LogItem(pre, post, count));

            WordCombo words = new WordCombo(pre, post);
            this.relationMap.put(words, count);
        }

        return loadedItems;
    }

    private void saveItems() throws SQLException {
        this.conn.setAutoCommit(false);

        PreparedStatement insertStatement = this.conn.prepareStatement("INSERT INTO Relations (pre, post, count) " +
                "VALUES (?, ?, ?)");
        PreparedStatement updateStatement = this.conn.prepareStatement("UPDATE Relations \n" +
                "SET count = ? \n" +
                "WHERE pre = ? AND post = ?");

        for (Map.Entry<WordCombo, Integer> entry : itemsToSave.entrySet()){
            WordCombo words = entry.getKey();
            int count = entry.getValue();

            if (!this.relationMap.containsKey(words)){
                // New word combination
                this.relationMap.put(words, count);
                insertStatement.setString(1, words.getPre());
                insertStatement.setString(2, words.getPost());
                insertStatement.setInt(3, count);
                insertStatement.addBatch();
            }
            else{
                // Existing word combination
                int newCount = count + this.relationMap.get(words);
                this.relationMap.put(words, newCount);
                updateStatement.setInt(1, newCount);
                updateStatement.setString(2, words.getPre());
                updateStatement.setString(3, words.getPost());
                updateStatement.addBatch();
            }
            itemsToSave.remove(words);
        }

        insertStatement.executeBatch();
        updateStatement.executeBatch();
        this.conn.commit();
        this.conn.setAutoCommit(true);
    }

    private void createDB() throws SQLException {
        System.out.printf("Created database file at: %s%n", this.url);

        createTables();

        System.out.printf("Created tables.%n");
    }

    private void createTables() throws SQLException {
        String createRelationTableSql = "CREATE TABLE IF NOT EXISTS Relations (\n" +
                "pre text, \n" +
                "post text, \n" +
                "count integer NOT NULL, \n" +
                "PRIMARY KEY(pre, post) \n" +
                ");";

        Statement statement = conn.createStatement();
        statement.execute(createRelationTableSql);
        statement.closeOnCompletion();
    }

    public String getDBPath() {
        return this.url;
    }
}
