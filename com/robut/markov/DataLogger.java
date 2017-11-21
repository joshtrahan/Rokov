package com.robut.markov;

import java.util.ArrayList;

public class DataLogger {
    private ArrayList<LogItem> logItems = new ArrayList<>();

    public void saveToDisk(){
        return;
    }

    public void addItem(String predecessor, String successor){
        logItems.add(new LogItem(predecessor, successor));
    }
}
