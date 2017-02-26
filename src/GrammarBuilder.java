import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Demok on 2017-02-26.
 */
public class GrammarBuilder {

    TDPredictiveParser parser;
    boolean debug;
    enum MODE {TERMINAL,NON_TERMINAL}
    MODE mode;
    ArrayList<ProductionElement> elements;
    ArrayList<Terminal> terminals;

    public GrammarBuilder( TDPredictiveParser par, boolean deb){
        debug = deb;
        parser = par;
        elements = new ArrayList<>();
        terminals = new ArrayList<>();
    }
    public void makeFromInstructions(String filename){
        try
        {
            int lineNum = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            if(debug)
                System.out.println("GENERATING PARSER FROM: "+filename);
            while ((line = reader.readLine()) != null)
            {
                lineNum++;
                if (debug)
                    System.out.println("READING LINE: " + line);
                if(line.length() == 0)
                    continue;
                if(!setMode(line)) {
                    if(mode == MODE.NON_TERMINAL)
                        createNonTerminal(line,lineNum);
                    else
                        createTerminal(line,lineNum);
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
        if(debug)
            System.out.println("PARSER GENERATED");

        for(ProductionElement e : elements){
            System.out.println("\n\n"+e.getType()+" :"+e.getTag());
            for(Production p : e.productions){
               System.out.println(p.print());
            }

        }
    }
    private boolean setMode(String line){
        Scanner setting = new Scanner(line);
        String firstWord = setting.next();
        if(firstWord.charAt(0)=='#') {
            if (firstWord.equals("##TERMINAL")) {
                mode = MODE.TERMINAL;
                return true;
            }
            else if (firstWord.equals("##NON_TERMINAL")){
                mode = MODE.NON_TERMINAL;
                return true;
            }
            return false;
        }
        return false;
    }

    private void createNonTerminal(String line, int lineNumber){
        Scanner in = new Scanner(line);
        String name = in.next();
        ProductionElement start = new ProductionElement(name);
        boolean tagExists = false;

        for(ProductionElement e : elements){
            if(e.getTag().equals(name)){
                start = e;
                if(debug)
                    System.out.println("Using existing: "+e.getTag());
                tagExists = true;
                break;
            }
        }

        if(!tagExists){
            elements.add(start);
            if(debug)
                System.out.println("Could not find tag matching: "+start.getTag());
        }

        Production p = new Production();
        String nextInput = in.next();
        start.addProduction(p);
        while(in.hasNext()){
            nextInput = in.next();
            if(nextInput.equals("|")){
                p = new Production();
                start.addProduction(p);
                if(debug)
                    System.out.println("\tnew production made " + p.print());
                continue;
            }
            else{
                tagExists = false;
                for(ProductionElement e : elements){
                    if(e.getTag().equals(nextInput)){
                        tagExists = true;
                        p.addElement(e);
                        if(debug)
                            System.out.println("\tusing existing element in production: "+nextInput);
                    }
                }
                if(!tagExists){
                    ProductionElement e = new ProductionElement(nextInput);
                    p.addElement(e);
                    elements.add(e);
                    if(debug)
                        System.out.println("\tcreating new element for production: "+nextInput);
                }
            }
        }
    }

    private void createTerminal(String line, int lineNumber){
        Scanner in = new Scanner(line);
        String name = in.next();
        ProductionElement start = new ProductionElement(name);

        for(ProductionElement e : elements){
            if(e.getTag().equals(name)){
                start = e;
                if(debug)
                    System.out.println("Setting terminals for : "+e.getTag());
                break;
            }
        }

        Production p = new Production();
        String nextInput = in.next();
        start.addProduction(p);
        while(in.hasNext()){
            nextInput = in.next();
            if(nextInput.equals("|")){
                p = new Production();
                start.addProduction(p);
                if(debug)
                    System.out.println("\tnew production made " + p.print());
                continue;
            }
            else{
                Terminal t = new Terminal(nextInput);
                p.addElement(t);
                terminals.add(t);
                if(debug)
                    System.out.println("\tcreating new Terminal for production: "+nextInput);

            }
        }
    }
}
