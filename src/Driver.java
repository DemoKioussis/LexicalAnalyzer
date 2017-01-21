import java.util.Scanner;

/**
 * Created by Demok on 2017-01-16.
 */
public class Driver {
    enum tests{HELLO, WHAT};
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String input;
        LexicalAnalyzer analyzer = new LexicalAnalyzer("instructions/instruct.demo","",true);

        while(true){
            input = in.next();
            if(input.equals("reset"))
                analyzer.dfa.reset();
            else if(input.equals("show"))
                analyzer.printTokens();
            else
               analyzer.passLexeme(input);

        }

    }
}
