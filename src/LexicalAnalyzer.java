import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Demok on 2017-01-19.
 */
public class LexicalAnalyzer {
    ArrayList<Token> tokens;
    ArrayList<CharTransform> transforms;
    DFA dfa;
    LABuilder builder;
    boolean debug;


    public  LexicalAnalyzer(String instructions, String code,boolean debug){
        tokens = new ArrayList<Token>();
        transforms = new ArrayList<>();
        dfa = new DFA();
        dfa.setDebug(debug);
        this.debug = debug;
        builder = new LABuilder(this,debug);
        builder.makeFromInstructions(instructions);
    }

    private void analyzeCode(String code,boolean debug){
        try
        {
            int lineNum = 0;
            BufferedReader reader = new BufferedReader(new FileReader(code));
            String line;
            while ((line = reader.readLine()) != null)
            {
                    if (debug)
                        System.out.println("READING LINE: " + line);
                    evaluateLine(line, lineNum++, debug);
            }
            reader.close();
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", code);
            e.printStackTrace();
        }
    }
    private void evaluateLine(String line, int lineNum,boolean debug){
        Scanner in = new Scanner(line);
            while(in.hasNext()){
                String lexeme = in.next();
                passLexeme(lexeme);
            }
        in.close();
    }
    public void passLexeme(String lexeme){
        boolean tokenMade = false;
        dfa.reset();
        if(debug)
            System.out.println("READING LEXEME: "+lexeme);

        char input;
        for(int i = 0; i<lexeme.length();i++){
            System.out.println("SIZE: "+lexeme.length());

            input = lexeme.charAt(i);
            if(!dfa.testInputAtCurrent(input)&&hasTransform(input)){
                char[] inputs = getTransform(input);
                if(dfa.testInputAtCurrent(inputs[0],inputs[1])) {
                    dfa.input(inputs[0], inputs[1]);
                    if (debug)
                        System.out.println("Using transform: " + input + " to " + inputs[0] + "" + inputs[1]);
                }
                else
                    dfa.input(input);

            }
            else {
                dfa.input(input);
            }

            if(dfa.getToken() == Token.TOKEN.ERROR && dfa.getPreviousToken() != Token.TOKEN.ERROR) {

                tokenMade = true;
                    makeToken(dfa.getPreviousToken(),lexeme.substring(0,i));
                if(debug)
                    System.out.println("Trying lexeme "+lexeme.substring(i));
                if(i!=0) {
                    passLexeme(lexeme.substring(i));
                    break;
                }
            }
        }
        if(!tokenMade) {
            makeToken(dfa.getToken(), lexeme);

        }
    }
    private void makeToken(Token.TOKEN t, String lex){
        tokens.add(new Token(t,lex));
        if(debug)
            System.out.println("Token made: "+lex+" - "+t);
    }

    public boolean hasTransform(char c){

        for(CharTransform t : transforms){
            if(t.hasTransfomr(c))
                return true;
        }
        return false;
    }
    public char[] getTransform(char c){
        char[] temp = {c,c};
        for(CharTransform t : transforms){
            if(t.hasTransfomr(c))
                return t.transform(c);
        }
        return temp;
    }
    public void printTokens(){
        for(Token t : tokens){
            t.print();
        }
    }

}
