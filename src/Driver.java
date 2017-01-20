import java.util.Scanner;

/**
 * Created by Demok on 2017-01-16.
 */
public class Driver {

    public static void main(String[] args){
        DFA dfa = new DFA();
        Scanner in = new Scanner(System.in);
        char input;
        DFABuilder b = new DFABuilder(dfa);
        b.getBuildInstructions("instructions/instructions.demo",true);

        while(true){
            input = in.next().charAt(0);
            if(input == 'r')
                dfa.reset();
            else
                dfa.input(input);
        }
    }
}
