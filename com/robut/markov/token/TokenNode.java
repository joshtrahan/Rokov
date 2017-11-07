package com.robut.markov.token;

public class TokenNode {
    private TokenNode leftChild;
    private TokenNode rightChild;
    private Token value;
    private int count;

    public Token getToken(int randomValue){
        if (value != null){
            return value;
        }

        if (randomValue <= leftChild.getCount()){
            return leftChild.getToken(randomValue - leftChild.getCount());
        }
        else{
            return rightChild.getToken(randomValue - rightChild.getCount());
        }
    }

    public int getCount(){
        return count;
    }
}
