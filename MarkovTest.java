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

        long startTime = System.nanoTime();
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
        long endTime = System.nanoTime();
        System.out.printf("Read time: %f%n", (endTime - startTime) / 10e9);

        startTime = System.nanoTime();
        for (String input : testString.toString().split("\r\n\r\n")) {
            markov.parseString(input);
        }
        endTime = System.nanoTime();
        System.out.printf("Load time: %f%n", (endTime - startTime) / 10e9);

        startTime = System.nanoTime();
        int repetitions = 10;
        for (int i = 0; i < repetitions; i++){
            markov.generateString();
        }
        endTime = System.nanoTime();
        System.out.printf("Time to gen %d messages: %f%n", repetitions, (endTime - startTime) / 10e9);
    }
}
