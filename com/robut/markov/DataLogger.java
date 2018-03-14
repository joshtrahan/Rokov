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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataLogger {
    private ConcurrentLinkedQueue<LogItem> newItems = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> newWords = new ConcurrentLinkedQueue<>();

    private SQLiteConnection sqlConn;
    private Thread sqlThread;

    public DataLogger(String dbPath) throws SQLException{
        this.sqlConn = new SQLiteConnection(dbPath, newWords, newItems);

        setupShutdownHook();
    }

    public String getDBPath(){
        return this.sqlConn.getDBPath();
    }

    public void saveToDisk(){
        if (sqlThread != null && sqlThread.isAlive()) {
            try {
                sqlThread.join();
            } catch (InterruptedException e) {
                System.err.printf("Interrupt exception joining previous DB write thread: %s%n", e);
                System.err.printf("Aborting write operation. No data has been lost unless something crazy happened.");
                return;
            }
        }
        sqlThread = new Thread(this.sqlConn);
        sqlThread.setDaemon(false);
        sqlThread.start();
        try {
            sqlThread.join();
        } catch (InterruptedException e) {
            System.err.printf("Interrupt exception joining previous DB write thread: %s%n", e);
            System.err.printf("Aborting write operation. No data has been lost unless something crazy happened.");
            return;
        }
    }

    public ArrayList<LogItem> loadLogItems(){
        return sqlConn.loadLogItems();
    }

    public void addWord(String word){
        newWords.offer(word);
    }

    public void addItem(String predecessor, String successor, int count){
        newItems.offer(new LogItem(predecessor, successor, count));
    }

    private void setupShutdownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run(){
                if (sqlThread != null && sqlThread.isAlive()){
                    try {
                        System.out.printf("Waiting for write to finish on database: %s%n", sqlConn.getDBPath());
                        sqlThread.join();
                    }
                    catch (InterruptedException e){
                        System.err.printf("Exception waiting for DB write to finish: %s%n", e);
                        System.err.printf("Check for corruption in database: %s.%n", sqlConn.getDBPath());
                    }
                }
            }
        });
    }
}
