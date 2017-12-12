package com.robut.markov;

import java.sql.SQLException;
import java.util.ArrayList;

public class DataLogger {
    private ArrayList<LogItem> newItems = new ArrayList<>();
    private ArrayList<String> newWords = new ArrayList<>();

    private SQLiteConnection sqlConn;

    public DataLogger(){
        try{
            this.sqlConn = new SQLiteConnection("./resources/chain.db");
        }
        catch (SQLException e){
            System.err.printf("Error creating database: %s%n", e);
        }
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

    public void addItem(String predecessor, String successor){
        addItem(predecessor, successor, 1);
    }

    public void addWord(String word){
        newWords.add(word);
    }

    public void addItem(String predecessor, String successor, int count){
        newItems.add(new LogItem(predecessor, successor, count));
    }
}
