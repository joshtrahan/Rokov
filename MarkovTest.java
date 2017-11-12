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

import com.robut.markov.MarkovChain;

import java.io.*;
import java.lang.StringBuilder;

class MarkovTest {
    public static void main(String[] args){
        MarkovChain markov = new MarkovChain();

        StringBuilder testString = new StringBuilder();

        try{
            BufferedReader br = new BufferedReader(new FileReader("./resources/test_str.txt"));

            String contentLine = br.readLine();
            while (contentLine != null){
                testString.append(contentLine);
                testString.append("\r\n");
                contentLine = br.readLine();
            }
        }
        catch (Exception e){
            System.out.printf("Exception: %s%n", e);
        }

        for (String input : testString.toString().split("\r\n\r\n")) {
            markov.parseString(input);
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < 10; i++){
            System.out.println(markov.generateString());
        }
        long endTime = System.nanoTime();

        System.out.printf("Time in seconds: %f%n", (endTime - startTime) / 1e9);
    }
}
