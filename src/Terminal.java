/**
 * Created by Demok on 2017-02-26.
 */
public class Terminal extends ProductionElement{
    static int globalTerminalId = 0;

    public Terminal(String name){
        tag = name;
        elementId = globalTerminalId++;
        type = TYPE.TERMINAL;
    }

    public boolean MatchValue(Token t){
        return t.match(tag);
    }

}
