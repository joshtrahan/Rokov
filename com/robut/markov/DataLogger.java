package com.robut.markov;

import java.sql.SQLException;
import java.util.ArrayList;

public class DataLogger {
    private ArrayList<LogItem> newItems = new ArrayList<>();
    private ArrayList<String> newWords = new ArrayList<>();

    private SQLiteConnection sqlConn;

    public DataLogger(String dbPath){
        try{
            this.sqlConn = new SQLiteConnection(dbPath);
        }
        catch (SQLException e){
            System.err.printf("Error creating database: %s%n", e);
        }
    }

    public void dbExists(){

    }

    public void saveToDisk(){
        try {
            sqlConn.saveWords(newWords);
        }
        catch(SQLException e){
            System.err.printf("Error saving wordlist: %s%n", e);
        }

        try{
            sqlConn.saveLogItems(newItems);
        }
        catch(SQLException e){
            System.err.printf("Error saving log items: %s%n", e);
        }

        newWords.clear();
        newItems.clear();
    }

    public ArrayList<LogItem> loadLogItems(){
        return sqlConn.loadLogItems();
    }

    public void addWord(String word){
        newWords.add(word);
    }

    public void addItem(String predecessor, String successor, int count){
        newItems.add(new LogItem(predecessor, successor, count));
    }
}
