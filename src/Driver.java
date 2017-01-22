import java.util.Scanner;

/**
 * Created by Demok on 2017-01-16.
 */
public class Driver {
    public static void main(String[] args) {

        //change code path to the file you want to analyze
        String codePath = "src/LABuilder.java";



        Scanner in = new Scanner(System.in);
        String input;
        LexicalAnalyzer analyzer = new LexicalAnalyzer("instructions/instruct.demo",codePath,false);

        while(true){
            input = in.next();
            if(input.equals("reset"))
                analyzer.dfa.reset();
            else if(input.equals("show"))
                analyzer.printTokens();
            else
               analyzer.passLexeme(input,0,0);

        }
    }
}
