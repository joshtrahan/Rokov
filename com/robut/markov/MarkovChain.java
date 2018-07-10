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
import java.util.HashMap;
import java.lang.StringBuilder;

import com.robut.markov.token.Token;
import com.robut.markov.token.TokenTree;

public class MarkovChain {
    private TokenTree startTree = new TokenTree();
    private HashMap<String, TokenTree> tokenTreeMap = new HashMap<>();

    private String lastValue;

    public MarkovChain(){

    }

    public synchronized void parseString(String toParse) {
        if (!toParse.matches("\\s+")) {
            for (String word : toParse.split("\\s+")) {
                this.addWord(word.replaceAll("\\s+", ""));
            }
            this.endString();
        }
    }

    public synchronized void addWord(String word){
        addWord(word, this.lastValue, 1);
        this.lastValue = word;
    }

    private synchronized void addWord(String word, String last, int count){
        if (word != null) {
            word = word.intern();
        }
        Token newToken = new Token(word);
        this.addToken(newToken, last, count);
    }

    private synchronized void addToken(Token token, String lastWord, int count) {
        if (!this.tokenTreeMap.containsKey(token.getValue())) {
            this.tokenTreeMap.put(token.getValue(), new TokenTree());
        }

        if (lastWord == null) {
            this.startTree.addToken(token, count);
        }
        else{
            this.tokenTreeMap.get(lastWord).addToken(token, count);
        }
    }

    private synchronized void addToken(Token token){
        addToken(token, this.lastValue, 1);
    }

    public synchronized void endString(){
        Token newToken = new Token(null);
        this.addToken(newToken);
        this.lastValue = null;
    }

    public synchronized String generateString(){
        if (startTree.isEmpty()){
            return "";
        }

        StringBuilder partialString = new StringBuilder();
        Token currentToken = startTree.getRandomValue();

        for (; !currentToken.isEnd(); currentToken = this.tokenTreeMap.get(currentToken.getValue()).getRandomValue()){
            partialString.append(currentToken.getValue());
            partialString.append(' ');
        }

        return partialString.toString();
    }
}
