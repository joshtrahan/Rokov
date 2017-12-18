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

public class DataLogger {
    private ArrayList<LogItem> newItems = new ArrayList<>();
    private ArrayList<String> newWords = new ArrayList<>();

    private SQLiteConnection sqlConn;

    public DataLogger(String dbPath) throws SQLException{
        this.sqlConn = new SQLiteConnection(dbPath);
    }

    public String getDBPath(){
        return this.sqlConn.getDBPath();
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
