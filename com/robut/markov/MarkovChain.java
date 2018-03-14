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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.lang.StringBuilder;

import com.robut.markov.logging.DataLogger;
import com.robut.markov.logging.LogItem;
import com.robut.markov.token.Token;
import com.robut.markov.token.TokenTree;

public class MarkovChain {
    private TokenTree startTree = new TokenTree();
    private HashMap<String, TokenTree> tokenTreeMap = new HashMap<>();

    private DataLogger logger;

    private String lastValue;

    public MarkovChain(){

    }

    public MarkovChain(String dbPath) throws IOException, SQLException{
        logger = new DataLogger(dbPath);
        loadFromDisk();
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
        this.logger.addItem(this.lastValue, word, 1);
        this.lastValue = word;
    }

    private void addWord(String word, String last, int count){
        if (word != null) {
            word = word.intern();
        }
        Token newToken = new Token(word);
        this.addToken(newToken, last, count);
    }

    private void addToken(Token token, String lastWord, int count) {
        if (!this.tokenTreeMap.containsKey(token.getValue())) {
            this.tokenTreeMap.put(token.getValue(), new TokenTree());
        }

        if (lastWord == null) {
            this.startTree.addToken(token, count);
        }
        else{
            try {
                this.tokenTreeMap.get(lastWord).addToken(token, count);
            }
            catch (NullPointerException e){
                System.err.printf("NPE: %s%n", e);
            }
        }
    }

    private void addToken(Token token){
        addToken(token, this.lastValue, 1);
    }

    public synchronized void endString(){
        Token newToken = new Token(null);
        this.addToken(newToken);
        this.logger.addItem(this.lastValue, null, 1);
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

    private void loadFromDisk() throws SQLException {
        if (logger != null) {
            for (LogItem item : logger.loadFromDisk()) {
                if (!this.tokenTreeMap.containsKey(item.getPre())){
                    this.tokenTreeMap.put(item.getPre(), new TokenTree());
                }
                addWord(item.getPost(), item.getPre(), item.getCount());
            }
        }
        else{
            System.err.printf("Error: Can't load from disk; no database path specified.%n");
        }
    }

    public synchronized void saveToDisk(){
        if (logger != null) {
            logger.saveToDisk();
        }
        else{
            System.err.printf("Error: Can't save to disk; no database path specified.%n");
        }
    }

    public String getDBPath(){
        return this.logger.getDBPath();
    }
}
