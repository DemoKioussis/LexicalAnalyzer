import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Demok on 2017-01-19.
 */
public class LexicalAnalyzer {
    ArrayList<Token> tokens;
    DFA dfa;
    LABuilder builder;
    boolean debug;
    int next = 0;


    public  LexicalAnalyzer(String instructions, String code,boolean debug){
        float time = System.currentTimeMillis();
        tokens = new ArrayList<Token>();
        dfa = new DFA();
        dfa.setDebug(debug);
        this.debug = debug;
        builder = new LABuilder(this,debug);
        builder.makeFromInstructions(instructions);
        analyzeCode(code,debug);
        System.out.println("Done\nTime Take: "+(System.currentTimeMillis()-time)+"milliseconds");
        output();
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
            tokens.add(new Token(Token.TOKEN.END,"",lineNum+1,0));
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", code);
            e.printStackTrace();
        }
    }
    private void evaluateLine(String line, int lineNum,boolean debug){
        Scanner in = new Scanner(line);
        int colNum = 0;
            while(in.hasNext()){
                String lexeme = in.next();
                passLexeme(lexeme,lineNum,colNum);
                colNum+=lexeme.length();
            }
        in.close();
    }
    public void passLexeme(String lexeme,int lineNum,int colNum){

        boolean tokenMade = false;
        dfa.reset();
        if(debug)
            System.out.println("READING LEXEME: "+lexeme);

        char input;
        for(int i = 0; i<lexeme.length();i++){
            input = lexeme.charAt(i);
            dfa.input(input);

            if(dfa.getToken() == Token.TOKEN.ERROR ||
                    (dfa.getToken() == Token.TOKEN.INTERMEDIATE && i == lexeme.length()-1)) {

                    tokenMade = true;
                    makeToken(dfa.getPreviousToken(), lexeme.substring(0, i),lineNum,colNum+i);
                    if (debug)
                        System.out.println("Trying lexeme " + lexeme.substring(i));
                    if (i != 0) {
                        passLexeme(lexeme.substring(i),lineNum,colNum+i+1);
                        break;
                    }
            }
            else if(dfa.getToken() == Token.TOKEN.INTERMEDIATE && dfa.peek(lexeme.charAt(i+1)) == Token.TOKEN.ERROR){
                tokenMade = true;
                makeToken(dfa.getPreviousToken(), lexeme.substring(0, i),lineNum,colNum+i-1);
                passLexeme(lexeme.substring(i),lineNum,colNum+i);

            }

        }
        if(!tokenMade) {
            makeToken(dfa.getToken(), lexeme,lineNum,colNum);

        }
    }
    public Token.TOKEN checkLexemeType(String lexeme){
        dfa.reset();
        char input;
        for(int i = 0; i<lexeme.length();i++) {
            input = lexeme.charAt(i);
            dfa.input(input);
        }
        return dfa.getToken();
    }
    private void makeToken(Token.TOKEN t, String lex,int line,int colNum){
        if(lex.length()>0)
            tokens.add(new Token(t,lex,line,colNum));
        if(debug)
            System.out.println("Token made: "+lex+" - "+t);
    }
    public void printTokens(){
        for(Token t : tokens){
            t.print();
        }
    }

    public void resetIndex(){
        next = 0;
    }
    public boolean hasNext(){
        return (next+1< tokens.size());
    }
    public Token nextToken(){
        if(hasNext())
            return tokens.get(next++);
        else
            return null;
    }

    private void output(){
        try{
            PrintWriter tokenWriter = new PrintWriter("output/tokens.txt", "UTF-8");
            PrintWriter errorWriter = new PrintWriter("output/errors.txt", "UTF-8");
            tokenWriter.printf("%-25s  %-25s     Position", "Lexeme", "Token");
            tokenWriter.println();
            tokenWriter.println("_________________________________________________________________________");

            errorWriter.printf("%-25s  %-25s     Position", "Lexeme", "Token");
            errorWriter.println();
            errorWriter.println("_________________________________________________________________________");

            for(Token t : tokens){
                if(t.getToken()!= Token.TOKEN.ERROR) {
                    tokenWriter.printf("%-25s  %-25s     (" + t.getPosition()[0] + "," + t.getPosition()[1] + ")", t.getLexeme(), t.getToken());
                    tokenWriter.println();
                }
                else {
                    errorWriter.printf("%-25s  %-25s     (" + t.getPosition()[0] + "," + t.getPosition()[1] + ") + - input not recognized", t.getLexeme(), t.getToken());
                    errorWriter.println();
                }
            }
            tokenWriter.close();
            errorWriter.close();

        } catch (Exception e) {
        }
    }
}
