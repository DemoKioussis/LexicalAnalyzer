import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by Demok on 2017-01-19.
 * we use this class to build the DFA
 * We build DFA by loading a set of instructions from a file (xxx.demo)
 * To construct DFA, we pass a dfa reference to the constructor
 */
public class DFABuilder {
    DFA dfa;
    String instructions;
    boolean debug;

    public DFABuilder(DFA d) {
        dfa = d;
    }

    public void getBuildInstructions(String filename,boolean debug){
        try
        {
            int lineNum = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null)
            {
                if(line.length() >1 &&!( line.charAt(0) == '/'&& line.charAt(1)=='/')) {
                    if (debug)
                        System.out.println("READING LINE: " + line);
                    evaluateLine(line, lineNum++, debug);
                }

            }
            reader.close();
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
        }
    }

    public void evaluateLine(String line, int lineNum,boolean debug){
        Scanner in = new Scanner(line);

        char type = in.next().charAt(0);
        switch(type){
            case 'M':
                try {
                    int number = in.nextInt();
                    boolean isFinal = (in.next().charAt(0) == 'T');
                    for (int i = 0; i < number; i++) {
                        dfa.addState(isFinal);
                    }
                    if(debug)
                        System.out.println("MADE "+number+" states("+isFinal+")\n");
                }
                catch(Exception e){
                    System.err.println("INVALID FORMAT - line: "+lineNum+" - "+line+"\n");
                }
                    break;
            case 'T':
                try {
                    int state1 = in.nextInt();
                    int state2 = in.nextInt();
                    char c = in.next().charAt(0);
                    dfa.addTransition(state1,state2,c);
                    if(debug)
                        System.out.println("Transition set between: "+state1+" and "+state2+" with symbol "+c+"\n");
                }
                catch(Exception e){
                    System.err.println("INVALID FORMAT - line: "+lineNum+" - "+line+"\n");
                }
                break;
            case 'S':
                try {
                    int state = in.nextInt();
                    String token = in.next();
                    dfa.setToken(state,token);
                    if(debug)
                        System.out.println("Set token of  "+state+" to "+token+"\n");
                }
                catch(Exception e){
                    System.err.println("INVALID FORMAT - line: "+lineNum+" - "+line+"\n");
                }
                break;
            default: System.err.println("INVALID FORMAT - line: "+lineNum+" - "+line+"\n");
        }
        in.close();
    }
}
