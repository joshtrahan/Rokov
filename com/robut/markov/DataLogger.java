package com.robut.markov;

import java.sql.SQLException;
import java.util.ArrayList;

public class DataLogger {
    private ArrayList<LogItem> logItems = new ArrayList<>();
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
        for (LogItem item : logItems){
            try {
                sqlConn.saveLogItem(item.getPredecessor(), item.getSuccessor(), item.getCount());
            }
            catch (SQLException e){
                System.err.printf("Error saving log item: %s | %s | %d %n%s%n", item.getPredecessor(), item.getSuccessor(),
                        item.getCount(), e);
            }
        }

        logItems.clear();
    }

    public void addItem(String predecessor, String successor){
        addItem(predecessor, successor, 1);
    }

    public void addItemStart(String successor){

    }

    public void addItem(String predecessor, String successor, int count){
        logItems.add(new LogItem(predecessor, successor, count));
    }
}
