import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

/**
 * Created by Demok on 2017-01-19.
 * we use this class to build the DFA
 * We build DFA by loading a set of instructions from a file (xxx.demo)
 * To construct DFA, we pass a dfa reference to the constructor
 */
public class LABuilder {
    DFA dfa;
    LexicalAnalyzer analyzer;
    String instructions;
    boolean debug;
    private enum MODES {SINGLE, TRANSFORM,LINK,NONE}
    private MODES mode = MODES.NONE;

    public LABuilder(LexicalAnalyzer l,boolean deb) {
        analyzer = l;
        dfa = analyzer.dfa;
        debug = deb;
    }
    public void setDebug(boolean deb){
        debug = deb;
    }
    public void makeFromInstructions(String filename){
        try
        {
            int lineNum = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            if(debug)
                System.out.println("GENERATING LEXICAL ANALYZER FROM: "+filename);
            while ((line = reader.readLine()) != null)
            {
                lineNum++;
                if (debug)
                    System.out.println("READING LINE: " + line);
                if(line.length() == 0)
                    continue;
                if(!setMode(line)) {

                    if (mode == MODES.SINGLE) {
                        addSingle(line,lineNum);
                    } else if (mode == MODES.TRANSFORM)
                        addTransforms(line,lineNum);
                    else if (mode == MODES.LINK)
                        addLinks(line,lineNum);
                }
                else if(debug)
                    System.out.println("MODE CHANGED TO "+mode);

            }
            reader.close();
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
        }
        dfa.reset();
        if(debug)
            System.out.println("LEXICAL ANALYZER GENERATED");
    }
    private boolean setMode(String line){
        Scanner setting = new Scanner(line);
        String firstWord = setting.next();
        if(firstWord.charAt(0)=='#') {
            if (firstWord.equals("##single")) {
                mode = MODES.SINGLE;
                return true;
            }
            else if (firstWord.equals("##transform")){
                mode = MODES.TRANSFORM;
                return true;
            }
            else if(firstWord.equals("##link")){
                mode = MODES.LINK;
                return true;
            }
            else if (firstWord.equals("##end") && (mode == MODES.SINGLE || mode == MODES.TRANSFORM || mode == MODES.LINK)) {
                mode = MODES.NONE;
                return true;
            }
            return false;
        }
        return false;
    }

    private void addSingle(String line,int lineNum){
        Scanner scan = new Scanner(line);
        String lexeme = scan.next();
        String token = scan.next();
        String path;
        boolean hasPath = false;
        Token.TOKEN tokenValue = Token.TOKEN.NOTHING;
        Token.TOKEN pathValue = Token.TOKEN.NOTHING;
        char input;

        try{
            tokenValue = Token.TOKEN.valueOf(token);
        }
        catch(Exception e){
            System.out.println("Could not match token value: "+token+" at line "+lineNum);
        }
        if(scan.hasNext()) {
            path = scan.next();
            try {
                pathValue = Token.TOKEN.valueOf(path);
                hasPath = true;
            } catch (Exception e) {
                System.out.println("Could not match token value: " + path + " at line " + lineNum);
            }
        }
        dfa.reset();
        for(int i = 0; i<lexeme.length()-1;i++){
            input = lexeme.charAt(i);
            stateMaker(input,hasPath,pathValue);
        }
        input = lexeme.charAt(lexeme.length()-1);
        stateMaker(input,true,tokenValue);
    }
    private void stateMaker(char input,boolean isFinal,Token.TOKEN value){
        int newState;
        int prevState;
        if(!dfa.testInputAtCurrent(input)){ // if transition 'input' does not exsit

            prevState = dfa.getCurrentID();
            newState = dfa.addState(isFinal,value);
            dfa.addTransition(prevState,newState,input);
            dfa.input(input);

            if(debug){
                System.out.println("Made new state with token value: "+value+ " with " +
                        "transition from state "+prevState + " to state "+ newState+
                " for input "+input+"\n");
            }
        }
        else{

            dfa.input(input);
            dfa.setToken(dfa.getCurrentID(), value);

            if(debug){
                System.out.println("Changing token value of "+dfa.getCurrentID()
                        +" from "+dfa.getToken()+" to "+value+"\n");
                }
            }
        }



    private void addTransforms(String line, int lineNum){
        Scanner scan = new Scanner(line);
        char min = 'x', max = 'x';
        String res = null;
        CharTransform t;
        if(scan.hasNext())
            min = scan.next().charAt(0);
        else
            System.err.println("Cannot make transform at line "+lineNum+": "+line+"( did not read first char)");
        if(scan.hasNext())
            max = scan.next().charAt(0);
        else
            System.err.println("Cannot make transform at line "+lineNum+": "+line+"( did not read first char)");
        if(scan.hasNext())
            res = scan.next();
        else
            System.err.println("Cannot make transform at line "+lineNum+": "+line+"(invalid transform)");
        if(res!=null && res.length()>1) {
            t = new CharTransform(min, max, res.charAt(0), res.charAt(1));
            analyzer.dfa.transforms.add(t);
            if(debug)
                System.out.println("Transform added: from "+min+" - "+max+" to "+res);
        }
        else
            System.err.println("Invalid line at line "+lineNum+": "+line);


    }

    int buildState = -1;
    private void addLinks(String line, int lineNum){
        Scanner scan = new Scanner(line);
        String next = scan.next();
        if(!scan.hasNext()){
            if(debug)
                System.err.println("Invalid link statement at line "+lineNum+" : "+line);
            return;
        }

        if(next.charAt(0) =='&'){
            if(next.equals("&new"))
               buildNewState(scan.next(),lineNum);
            else if(next.equals("&path"))
                linkPath(line.substring("&path".length()),lineNum);
            else if(next.equals("&start"))
                linkStart(line.substring("&start".length()),lineNum);
            else if(next.equals("&self"))
                linkSelf(line.substring("&self".length()),lineNum);
        }
        else
            linkTail(line,lineNum);
    }

    void buildNewState(String token,int lineNum){
        if(debug)
            System.out.println("New State:  "+token);
        try{
           Token.TOKEN t =  Token.TOKEN.valueOf(token);
            buildState = dfa.addState(true,t);
        }
        catch(Exception e){
                System.err.println("Could not match token "+token+" at line "+ lineNum);
        }
    }
    void linkPath(String line, int lineNum){
        dfa.reset();
        if(debug)
            System.out.println("Path Link: "+line);
        Scanner in = new Scanner(line);
        String path;
        if(in.hasNext()){
            path = in.next();
        }
        else{
            System.err.println("Cannot link path on line "+ lineNum +" "+line+"- no path");
            return;
        }
        while(in.hasNext()){
            String input = in.next();
            boolean dub = input.length() == 2 ;
            for(int i = 0; i<path.length();i++){
                if(path.charAt(i)=='$' && path.length()>i+1) {
                    dfa.input('$', path.charAt(++i));
                }
                else
                    dfa.input(path.charAt(i));
                linker(input);
            }
            dfa.reset();
        }
    }
    void linkStart(String line, int lineNum){
        if(debug)
            System.out.println("Start Link: "+line);
        Scanner in = new Scanner(line);
        dfa.reset();
        while(in.hasNext()) {

            String input = in.next();
            linker(input);
        }
    }


    void linkSelf(String line, int lineNum){
        if(debug)
            System.out.println("Link self: "+line);
        Scanner in = new Scanner(line);
        dfa.moveTo(buildState);
        String input;
        while(in.hasNext()){
            input = in.next();
            linker(input);
        }
    }
    void linker(String input){

        if((dfa.getToken()!= Token.TOKEN.ERROR ||dfa.getCurrentID()==1) && buildState !=-1) {
            boolean dub = input.charAt(0)=='$' && input.length()==2;
            if (dub) {
                dfa.addTransition(dfa.getCurrentID(), buildState, input.charAt(0), input.charAt(1));
            } else
                dfa.addTransition(dfa.getCurrentID(), buildState, input.charAt(0));
            if(debug)
                System.out.println("Added transition from "+dfa.getCurrentID()+" to " +
                        buildState+" with input: "+input +" dub: "+dub);
        }
    }
    void clearLink(){
        buildState = -1;
    }

    void linkTail(String line, int lineNum){
        dfa.reset();
        if(debug)
            System.out.println("Path Link: "+line);
        Scanner in = new Scanner(line);
        String path;
        if(in.hasNext()){
            path = in.next();
        }
        else{
            System.err.println("Cannot link tail on line "+ lineNum +" "+line+"- no path");
            return;
        }
        for(int i = 0; i<path.length();i++){
            if(path.charAt(i)=='$' && path.length()>i+1) {
                dfa.input('$', path.charAt(++i));
            }
            else
                dfa.input(path.charAt(i));
        }
        while(in.hasNext()){
            String input = in.next();
            linker(input);
        }
    }
}

/** This is for the test case, as the default input is this file
 *  test23782_238293.+-(hello there_(_232_)-if<(why)then)("THIS_ISrecognised(here):Nor_WAS99_that>
 *  1234.aa111 12.12a 123.123 hello123_abc_123.acb 123_abc_123.1abc
 * =============== == == = === == =
 *><><><><</></></> <><></></>
 * <=>>=<><<=><<<<<<>>>>>>>></>
 * ;;;;,,,,.....,.;,.;,.;,.;
 *+_+_+_+_===+++----___=***---//////// /** /* /* / / /
 * +and-not||or ()()()()(((()))){{{}}}{{()[][][({})}}
 * ifthenelse
 * iiiiiif thennn tthennnn eeelllsseee else for class classses classing
 * getts getget get float int if int put return program
 *
 * ++ -- +-+-+/+/+-==-=
 * == <> < > <= >= ; , . + - * / =  ( ) () { } {} [ ] [] /* //
 *
 * and not or if then else for class int float get put return program
 *
 */


