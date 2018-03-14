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

package com.robut.markov.logging;

import java.util.Objects;

public class LogItem {
    private WordCombo words;
    private int count;

    LogItem(String newPre, String newPost, int newCount){
        this(new WordCombo(newPre, newPost), newCount);
        this.count = newCount;
    }

    LogItem(WordCombo newWords, int newCount){
        words = newWords;
        count = newCount;
    }

    public String getPre(){
        return words.getPre();
    }
    public String getPost(){
        return words.getPost();
    }
    public int getCount(){
        return this.count;
    }
}
