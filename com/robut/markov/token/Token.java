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

public class Token {
    private boolean isEnd = false;
    private String value;

    public Token(String value){
        this.value = value;
        if (value == null){
            isEnd = true;
        }
    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String newValue){
        this.value = newValue;
    }

    public boolean isEnd() {
        return isEnd;
    }
}
