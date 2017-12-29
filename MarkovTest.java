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

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.StringBuilder;

class MarkovTest {
    public static void main(String[] args){
        if (args.length > 1) {
            String dbPath = args[0];
            String textPath = args[1];
            System.out.printf(".%ndbPath = \"%s\"%ntextPath = \"%s\"%n%n", dbPath, textPath);
            testChainGen(dbPath, textPath);
        }
        else if (args.length > 0){
            String dbPath = args[0];
            System.out.printf("Testing database loading.%ndbpath = \"%s\"%n%n", dbPath);
            testDBLoad(dbPath);
        }
    }

    public static void testDBLoad(String dbPath){
        long startTime = System.nanoTime();
        MarkovChain markov = new MarkovChain(dbPath);
        long endTime = System.nanoTime();
        System.out.printf("Time to load DB info: %f%n", (endTime - startTime) / 10e9);

        startTime = System.nanoTime();
        int repetitions = 10;
        for (int i = 0; i < repetitions; i++){
            System.out.println(markov.generateString());
        }
        endTime = System.nanoTime();
        System.out.printf("Time to gen %d messages: %f%n", repetitions, (endTime - startTime) / 10e9);
    }

    public static void testChainGen(String dbPath, String textPath){
        MarkovChain markov = new MarkovChain(dbPath);

        StringBuilder testString = new StringBuilder();

        long startTime = System.nanoTime();
        try{
            BufferedReader br = new BufferedReader(new FileReader(textPath));

            String contentLine = br.readLine();
            while (contentLine != null){
                if ( !(contentLine.matches("\\s+") || contentLine.matches("")) ) {
                    testString.append(contentLine);
                    testString.append("\n");
                }
                contentLine = br.readLine();
            }
        }
        catch (Exception e){
            System.out.printf("Exception: %s%n", e);
        }
        long endTime = System.nanoTime();
        System.out.printf("Read time: %f%n", (endTime - startTime) / 10e9);

        startTime = System.nanoTime();
        for (String paragraph : testString.toString().split("\n"))
        {
            markov.parseString(paragraph);
        }
        endTime = System.nanoTime();
        System.out.printf("Load time: %f%n", (endTime - startTime) / 10e9);

        startTime = System.nanoTime();
        int repetitions = 10;
        for (int i = 0; i < repetitions; i++){
            System.out.println(markov.generateString());
        }
        endTime = System.nanoTime();
        System.out.printf("Time to gen %d messages: %f%n", repetitions, (endTime - startTime) / 10e9);

        startTime = System.nanoTime();
        markov.saveToDisk();
        endTime = System.nanoTime();
        System.out.printf("Time to save to disk: %f%n", (endTime - startTime) / 10e9);
    }
}
