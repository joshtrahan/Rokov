import com.robut.markov.MarkovChain;

import java.io.EOFException;
import java.util.Scanner;

public class InputReader {
    public static void Main(String[] args){
        String filePath;

        if (args.length == 1){
            filePath = args[0];
        }

        Scanner input = new Scanner(System.in);
        MarkovChain markov = new MarkovChain();

        String inputLine;
        while(input.hasNextLine()){
            inputLine = input.nextLine();

            markov.parseString(inputLine);
        }
    }
}
