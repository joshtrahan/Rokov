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

    private void addToken(Token token){
        if (!this.tokenTreeMap.containsKey(token.getValue())) {
            this.tokenTreeMap.put(token.getValue(), new TokenTree());
        }

        if (this.lastValue == null) {
            this.startTree.addToken(token);
        }
        else{
            this.tokenTreeMap.get(lastValue).addToken(token);
        }
        this.lastValue = token.getValue();
    }

    public void parseString(String toParse) {
        if (!toParse.matches("\\s+")) {
            for (String word : toParse.split("\\s+")) {
                this.addWord(word);
            }
            this.endString();
        }
    }

    public void addWord(String word){
        Token newToken = new Token(word);
        this.addToken(newToken);
    }

    public void endString(){
        Token newToken = new Token(null);
        this.addToken(newToken);
    }

    public String generateString(){
        StringBuilder partialString = new StringBuilder();
        Token currentToken = startTree.getRandomValue();

        for (; !currentToken.isEnd(); currentToken = this.tokenTreeMap.get(currentToken.getValue()).getRandomValue()){
            partialString.append(currentToken.getValue());
            partialString.append(' ');
        }

        return partialString.toString();
    }
}
