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

package com.robut.markov.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TokenTree {
    // Complete Binary Tree implemented with ArrayList
    private ArrayList<TokenNode> tokenTree = new ArrayList<>();

    // Keep track of which index each string is at
    private HashMap<String, Integer> indexMap = new HashMap<>();

    private Random random = new Random();

    public TokenTree(){}

    public Token getRandomValue(){
        int randomValue = random.nextInt(getRootCount()) + 1;
        int currentIndex = 0;

        while (randomValue > 0 && tokenTree.get(currentIndex).getToken() == null){
            int leftChildCount = tokenTree.get(calcLeftChild(currentIndex)).getCount();

            if (randomValue <= leftChildCount){
                currentIndex = calcLeftChild(currentIndex);
            }
            else{
                currentIndex = calcRightChild(currentIndex);
                randomValue -= leftChildCount;
            }
        }

        return tokenTree.get(currentIndex).getToken();
    }

    public void addToken(Token token){
        if (this.indexMap.containsKey(token.getValue())) {
            this.updateTreeValues(this.indexMap.get(token.getValue()));
        }
        else {
            this.addTokenToTree(token);
        }
    }

    private void updateTreeValues(int valueIndex, int toAdd){
        while (valueIndex > 0){
            tokenTree.get(valueIndex).addToCount(toAdd);
            valueIndex = (valueIndex - 1) / 2;
        }
        tokenTree.get(valueIndex).addToCount(toAdd);
    }

    private void updateTreeValues(int valueIndex){
        updateTreeValues(valueIndex, 1);
    }

    private void addTokenToTree(Token token){
        int tokenPosition = tokenTree.size();
        this.tokenTree.add(new TokenNode(token));
        indexMap.put(token.getValue(), tokenPosition);

        if (tokenPosition > 0){
            int tokenParentIndex = (tokenPosition - 1) / 2;
            TokenNode tokenSibling = this.tokenTree.get(tokenParentIndex);

            // Move previous parent to be new token's sibling
            this.tokenTree.add(tokenSibling);

            // Replace parent with a blank count tracking node
            this.tokenTree.set(tokenParentIndex, new TokenNode(tokenSibling.getCount()));

            // Update sibling's entry in indexMap
            indexMap.put(tokenSibling.getToken().getValue(), tokenPosition + 1);
        }
        // Add 1 all the way up to the root
        updateTreeValues(tokenPosition);
    }

    private int getRootCount(){
        return tokenTree.get(0).getCount();
    }

    private int calcLeftChild(int index){
        return index * 2 + 1;
    }

    private int calcRightChild(int index){
        return index * 2 + 2;
    }
}
