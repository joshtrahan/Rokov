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

public class TokenTree<T> {
    // Complete Binary Tree implemented with ArrayList
    private ArrayList<TokenNode> tokenTree = new ArrayList<>();
    private HashMap<T, Integer> indexMap = new HashMap<>();

    TokenTree(Token token){
        addToken(token);
    }

    TokenTree(){}

    public void addValue(T value){
        Token<T> newToken = new Token<>(value);
    }

    public void addToken(Token token){
        int tokenPosition = tokenTree.size();
        this.tokenTree.add(new TokenNode(token));

        if (tokenPosition > 0){
            int tokenParentIndex = (tokenPosition - 1) / 2;
            TokenNode tokenSibling = this.tokenTree.get(tokenParentIndex);

            // Move previous parent to be new token's sibling
            this.tokenTree.add(tokenSibling);

            // Update new parent to have no value and reflect
            this.tokenTree.set(tokenParentIndex, new TokenNode(tokenSibling.getCount() + 1));
            indexMap.put(tokenSibling.getToken().getValue(), tokenPosition + 1);
        }
    }
}
