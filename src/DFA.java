import java.util.ArrayList;

/**
 * Created by Demok on 2017-01-19.
 *
 * This Deterministic Finite Automata will be used as the core of the Lexical Analyzer.
 * We will traverse the constructed DFA and return a token depending on where that input string brought us
 *
 */
public class DFA {
    //store all states in dfa in list for easy individual access, access each with id
    private ArrayList<State> states = new ArrayList<>();
    // increment each time we add new state
    int lastID = 0;
    // list of transforms
    ArrayList<CharTransform> transforms;

    //error state is the state we go to when we make invalid transition, default id is 0
    private State errorState;//id 0
    // the state we start in, default id is 1
    private State startState;//id 1
    // this state keeps track of our position in the DFA
    private State currentState;
    private State previousState;

    private boolean debug;
    // the state class
    private class State{
        class Transition{
            private State state;
            private char symbol;
            private char symbol2;
            private boolean hasDouble;

            Transition(State s, char c){
                state = s;
                symbol = c;
                hasDouble = false;
            }
            Transition(State s, char c1, char c2){
                state = s;
                symbol = c1;
                symbol2 = c2;
                hasDouble = true;
            }

            State doTransition(char c){

                if(matchSymbol(c))
                    return state;
                else
                    return errorState;
            }
            boolean matchSymbol(char c){
                if(symbol == c &&!hasDouble)
                    return true;
                else
                    return false;
            }
            State doTransition(char c1, char c2){

                if(matchSymbol(c1,c2))
                    return state;
                else
                    return errorState;
            }
            boolean matchSymbol(char c1,char c2){
                if(hasDouble && symbol == c1 && symbol2 == c2)
                    return true;
                else
                    return false;
            }
            char getSymbol(){
                return symbol;
            }
            char getSecondSymbol(){
                return symbol2;
            }
        }
        int stateID;
        private Token.TOKEN token;
        ArrayList<Transition> transitions = new ArrayList<>();
        private boolean isFinalState;

        State(boolean f){
            stateID = lastID++;
            isFinalState = f;
        };
        State(boolean f,Token.TOKEN t){
            stateID = lastID++;
            isFinalState = f;
            token = t;
        };

        void setToken(Token.TOKEN t){
            token = t;
        }
        void setToken(String t){
            token = Token.TOKEN.valueOf(t);
        }
        Token.TOKEN getToken(){
            return token;
        }
        boolean hasTransition(char c){
            for(Transition t : transitions)
                if(t.matchSymbol(c)){
                    return true;
                }
            return false;
        }
        boolean hasTransition(char c1,char c2){
            for(Transition t : transitions)
                if(t.matchSymbol(c1,c2)){
                    return true;
                }
            return false;
        }
        void addTransition(State s, char c){
            if(!hasTransition(c))
                transitions.add((new Transition(s,c)));
            else
                System.err.println("State "+s.stateID+" - "+s.getToken()+" already has transition "+c);
        }

        void addTransition(State s, char c1,char c2){
            if(!hasTransition(c1,c2))
                transitions.add((new Transition(s,c1,c2)));
            else
                System.err.println("State "+s.stateID+" - "+s.getToken()+" already has transition "+c1+""+c2);
        }

        void removeTransition(char c){
            for(Transition t : transitions){
                if(t.matchSymbol(c))
                    transitions.remove(t);
            }
        }
        void removeTransition(char c1,char c2){
            for(Transition t : transitions){
                if(t.matchSymbol(c1,c2))
                    transitions.remove(t);
            }
        }

        State makeTransition(char c){
            for(Transition t : transitions){
                if(t.matchSymbol(c))
                    return t.doTransition(c);
            }
            return errorState;
        }
        State makeTransition(char c1,char c2){
            for(Transition t : transitions){
                if(t.matchSymbol(c1,c2))
                    return t.doTransition(c1,c2);
            }
            return errorState;
        }
        boolean isFinal(){
            return isFinalState;
        }
        void setFinal(boolean f){
            isFinalState = f;
        }

    }
    private String path;


    public DFA(){
        transforms = new ArrayList<CharTransform>();
        errorState = new State(false); //id 0
        startState = new State(false); //id 1
        currentState = startState;
        previousState = currentState;
        states.add(errorState);
        states.add(startState);
        errorState.setToken(Token.TOKEN.ERROR);
        startState.setToken(Token.TOKEN.ERROR);
        path = "";
    }

    public void setDebug(boolean deb){
        debug = deb;
    }


    public int getCurrentID(){
        return currentState.stateID;
    }
    public void input(char c){
        previousState = currentState;
        if(currentState.hasTransition(c)) {
            currentState = currentState.makeTransition(c);
        }
        else if(hasValidTransform(c))
        {
            char[] trans = transform(c);
            currentState = currentState.makeTransition(trans[0],trans[1]);
        }
        else
            currentState = currentState.makeTransition(c);

        if(debug) {
            System.out.print("Moving to state " + currentState.stateID+": "+getToken());
            if (currentState.isFinal())
                System.out.print(" - Final State");
            if (currentState.stateID == errorState.stateID)
                System.out.print(" - Error State");
        }

    }

    public void input(char c1,char c2){

        if(peek(c1,c2)!=errorState.getToken()) {
            previousState = currentState;
            currentState = currentState.makeTransition(c1, c2);
        }
        if(debug) {
            System.out.print("Moving to state " + currentState.stateID+": "+getToken());
            if (currentState.isFinal())
                System.out.print(" - Final State");
            if (currentState.stateID == errorState.stateID)
                System.out.print(" - Error State");
        }

    }

    private boolean hasValidTransform(char c){
        for(CharTransform t : transforms){
            if(t.hasTransform(c) && peek(t.result[0],t.result[1])!= errorState.getToken()){
                return true;
            }
        }
        return false;
    }
    private char[] transform(char c){
        for(CharTransform t : transforms){
            if(t.hasTransform(c) && peek(t.result[0],t.result[1])!= errorState.getToken()){
                return t.result;
            }
        }
        return null;
    }

    public int addState(boolean fin){
        State s = new State(fin);
        states.add(s);
        return s.stateID;
    }
    public int addState(boolean fin,Token.TOKEN t){
        State s = new State(fin,t);
        states.add(s);
        return s.stateID;
    }
    public void setToken(int state,Token.TOKEN t){
        if(state>=0 && state<states.size() )
            states.get(state).setToken(t);
        else
            System.err.println("Could not set token - invalid state: "+state);
    }
    public void setToken(int state,String t){
        if(state>=0 && state<states.size() )
            states.get(state).setToken(t);
        else
            System.err.println("Could not set token - invalid state: "+state);
    }
    public void setFinal(int state, boolean bool){
        if(state>=0 && state<states.size() )
            states.get(state).setFinal(bool);
        else
            System.err.println("Could not set final status - invalid state: "+state);
    }

    public void moveTo(int id){
        currentState = states.get(id);
    }

    public boolean testInputAtCurrent(char c){
        return currentState.hasTransition(c);
    }
    public boolean testInputAtCurrent(char c1,char c2){
        return currentState.hasTransition(c1,c2);
    }

    public void addTransition(int start, int end, char c){
        if(start>=0 && start<states.size()  && end>=0 && end<states.size())
            states.get(start).addTransition(states.get(end),c);
        else
            System.err.println("Could not add transition - invalid state(s): "+start +" to "+end);
    }
    public void addTransition(int start, int end, char c1, char c2){
        if(start>=0 && start<states.size()  && end>=0 && end<states.size())
            states.get(start).addTransition(states.get(end),c1,c2);
        else
            System.err.println("Could not add transition - invalid state(s): "+start +" to "+end);
    }

    public Token.TOKEN getToken(){
        return currentState.getToken();
    }
    public Token.TOKEN getPreviousToken(){
        return previousState.getToken();

    }

    public Token.TOKEN peek(char c){
        if(!currentState.hasTransition(c) && hasValidTransform(c))
            return peek(transform(c)[0],transform(c)[1]);
        else
           return currentState.makeTransition(c).getToken();
    }
    public Token.TOKEN peek(char c1,char c2){

        return currentState.makeTransition(c1,c2).getToken();
    }


    public void reset(){
        currentState = startState;
        previousState = startState;
        if(debug) {
            System.out.println("Path was "+path);
            System.out.println("RESET DFA");
        }
        path = "";

    }

    public int size(){
        return states.size();
    }

}
