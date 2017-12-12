package com.robut.markov;

public class LogItem {
    private String predecessor;
    private String successor;
    private int count;

    LogItem(String newPredecessor, String newSuccessor, int newCount){
        this.predecessor = newPredecessor;
        this.successor = newSuccessor;
        this.count = newCount;
    }

    LogItem(String newPredecessor, String newSuccessor){
        this.predecessor = newPredecessor;
        this.successor = newSuccessor;
        this.count = 1;
    }

    public String getPredecessor(){
        return this.predecessor;
    }
    public String getSuccessor(){
        return this.successor;
    }
    public int getCount(){
        return this.count;
    }
}
