package com.robut.markov.logging;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class DataLogger {
    private ConcurrentHashMap<WordCombo, Integer> itemsToSave = new ConcurrentHashMap<>();
    private SQLConnection sqlConn;
    private Thread sqlThread;

    public DataLogger(String dbPath) throws SQLException, IOException {
        this.sqlConn = new SQLConnection(dbPath, itemsToSave);
    }

    public void addItem(String pre, String post, int count) {
        addItem(new WordCombo(pre, post), count);
    }

    public void addItem(WordCombo words, int count) {
        if (itemsToSave.containsKey(words)){
            // word combo exists already
            itemsToSave.put(words, itemsToSave.get(words) + count);
        }
        else {
            itemsToSave.put(words, count);
        }
    }

    public void saveToDisk() {
        sqlThread = new Thread(this.sqlConn);
        sqlThread.setDaemon(false);
        sqlThread.start();
        joinSqlThread();
    }

    public Collection<LogItem> loadFromDisk() throws SQLException {
        return sqlConn.loadItems();
    }

    public String getDBPath() {
        return this.sqlConn.getDBPath();
    }

    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run(){
                if (sqlThread != null && sqlThread.isAlive()){
                    System.out.printf("Waiting for write to finish on database: %s%n", sqlConn.getDBPath());
                    joinSqlThread();
                }
            }
        });
    }

    private void joinSqlThread() {
        try {
            sqlThread.join();
        }
        catch (InterruptedException e){
            System.err.printf("Interrupt exception joining previous DB write thread: %s%n", e);
            return;
        }
    }
}
