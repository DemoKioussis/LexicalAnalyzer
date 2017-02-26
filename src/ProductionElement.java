import java.util.ArrayList;

/**
 * Created by Demok on 2017-02-26.
 */
public class ProductionElement {
    private static int globalElementId = 0;
    int elementId;
    String tag;
    enum TYPE {TERMINAL,NON_TERMINAL}
    TYPE type;
    ArrayList<Production> productions;
    ArrayList<Terminal> firstSet;
    ArrayList<Terminal> followSet;

    public ProductionElement(){

    }

    public ProductionElement(String name){

        elementId = globalElementId++;
        tag = name;
        productions = new ArrayList<>();
        type = TYPE.NON_TERMINAL;
    }
    public String getTag(){
        return tag;
    }
    public void addProduction(Production p){
        productions.add(p);
        p.start = this;
    }
    public TYPE getType(){
        return type;
    }

}
