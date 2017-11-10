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

public class TokenNode {
    private Token token;
    private int count;

    TokenNode(Token token){
        this.token = token;
        this.count = 1;
    }

    TokenNode(int count){
        this.token = null;
        this.count = count;
    }

    public void addToCount(int toAdd){
        this.count += toAdd;
    }

    public Token getToken(){
        return this.token;
    }

    public int getCount(){
        return count;
    }
}
