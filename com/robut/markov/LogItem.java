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
}
