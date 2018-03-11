/*
    Rokov - Library for generating Markov chain data
    Copyright (C) 2017  Joshua Trahan

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robut.markov;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SQLiteConnection implements Runnable {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";

    private String url;
    private Connection conn;

    private HashMap<String, Integer> stringToIdMap = new HashMap<>();
    private HashMap<Integer, String> idToStringMap = new HashMap<>();
    private int currentWordId = 2;

    private HashMap<Integer, HashMap<Integer, Integer>> relationMap = new HashMap<>();

    private ConcurrentLinkedQueue<String> wordsToSave;
    private ConcurrentLinkedQueue<LogItem> itemsToSave;

    public SQLiteConnection(String dbPath, ConcurrentLinkedQueue<String> wordQueue,
                            ConcurrentLinkedQueue<LogItem> itemQueue) throws SQLException{
        this(new File(dbPath), wordQueue, itemQueue);
    }

    public SQLiteConnection(File filepath, ConcurrentLinkedQueue<String> wordQueue,
                            ConcurrentLinkedQueue<LogItem> itemQueue) throws SQLException{
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

        this.wordsToSave = wordQueue;
        this.itemsToSave = itemQueue;
    }

    public void run(){
        try {
            saveWords(this.wordsToSave);
        }
        catch(SQLException e){
            System.err.printf("Error saving wordlist: %s%n", e);
        }

        try{
            saveLogItems(this.itemsToSave);
        }
        catch(SQLException e){
            System.err.printf("Error saving log items: %s%n", e);
        }
    }

    public void connectDB() throws SQLException{
        Statement statement = this.conn.createStatement();

        // load up word/id mapping
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
        this.currentWordId += 1;

        // load up word relations
        results = statement.executeQuery("SELECT preID, postID, count FROM WordRelations");
        while (results.next()){
            int preID = results.getInt("preID");
            int postID = results.getInt("postID");
            int count = results.getInt("count");

            if (!this.relationMap.containsKey(preID)){
                this.relationMap.put(preID, new HashMap<>());
            }
            this.relationMap.get(preID).put(postID, count);
        }
    }

    public String getDBPath(){
        return this.url;
    }

    public synchronized ArrayList<LogItem> loadLogItems(){
        ArrayList<LogItem> logItems = new ArrayList<>();
        for (Map.Entry<Integer, HashMap<Integer, Integer>> preMap : this.relationMap.entrySet()){
            String pre = this.idToStringMap.get(preMap.getKey());
            HashMap<Integer, Integer> postMaps = preMap.getValue();
            for (Map.Entry<Integer, Integer> postMap : postMaps.entrySet()){
                String post = this.idToStringMap.get(postMap.getKey());
                int count = postMap.getValue();
                logItems.add(new LogItem(pre, post, count));
            }
        }

        return logItems;
    }

    public synchronized void saveWords(ConcurrentLinkedQueue<String> words) throws SQLException {
        this.conn.setAutoCommit(false);
        PreparedStatement prepStatement = this.conn.prepareStatement("INSERT INTO WordIDs (id, word) VALUES (?, ?)");

        while (!words.isEmpty()){
            String word = words.poll();
            if (word == null){
                break;
            }

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

    public synchronized void saveLogItems(ConcurrentLinkedQueue<LogItem> items) throws SQLException{
        this.conn.setAutoCommit(false);
        PreparedStatement insertStatement = this.conn.prepareStatement("INSERT INTO WordRelations (preID, postID, count) VALUES (?, ?, ?)");
        PreparedStatement updateStatement = this.conn.prepareStatement("UPDATE WordRelations \n" +
                "SET count = ? \n" +
                "WHERE preID = ? AND postID = ?");

        while (!items.isEmpty()){
            LogItem item = items.poll();
            if (item == null){
                break;
            }

            addLogItemToBatch(item.getPredecessor(), item.getSuccessor(), item.getCount(), insertStatement, updateStatement);
        }

        insertStatement.executeBatch();
        updateStatement.executeBatch();
        this.conn.commit();
        this.conn.setAutoCommit(true);
    }

    private void addLogItemToBatch(String pre, String post, int count, PreparedStatement insertStatement,
                                   PreparedStatement updateStatement) throws SQLException{
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

// can probably change this to check for containsKey, add to count, then take the insertStatement calls
// out of the if blocks
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
