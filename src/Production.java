import java.util.ArrayList;

/**
 * Created by Demok on 2017-02-26.
 */
public class Production {
    static int productionNumberGlobal = 0;
    private int productionNumber;
    ProductionElement start;
    ArrayList<ProductionElement> production;

    public Production(){
        productionNumber = productionNumberGlobal++;
        production = new ArrayList<>();
    }

    public void setProduction(ArrayList<ProductionElement> prod){
        production = prod;
    }
    public void setStartingElement(ProductionElement e){
        start = e;
    }
    public void addElement(ProductionElement e){
        production.add(e);
    }
    public String print(){
        String p = "PRODUCTION:   "+start.getTag()+" -->  ";
        for(ProductionElement e : production){
            p+= " "+e.getTag();
        }

        return p;
    }
}
