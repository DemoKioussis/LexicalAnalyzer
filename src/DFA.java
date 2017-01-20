import java.util.ArrayList;

/**
 * Created by Demok on 2017-01-19.
 *
 * This Deterministic Finite Automata will be used as the core of the Lexical Analyzer.
 * We will traverse the constructed DFA and return a token depending on where that input string brought us
 *
 */
public class DFA {
    // list of tokens --> see paper for details
    enum TOKEN{EL_ID, EL_ALPHANUM,EL_NUM,EL_INTEGER,EL_FLOAT,EL_FRACTION,EL_LETTER,EL_DIGIT,EL_NONZERO,
        W_IF,W_THEN,W_ELSE,W_FOR,W_CLASS,W_INT,W_FLOAT,W_GET,W_PUT,W_RETURN,W_PROGRAM,
        OP_EQUAL,OP_NOT_EQUAL,OP_L_THAN,OP_G_THAN,OP_L_THAN_OR_EQUAL,OP_G_THAN_OR_EQUAL,
        OP_PLUS,OP_MINUS,OP_MULTIPLY,OP_DIVID,OP_ASSING,OP_AND,OP_NOT,OP_OR,PUNCT_SEMICOL,PUNCT_COMMA,PUNCT_DOT,
        ORG_OPEN_PAREN,ORG_CLOSE_PAREN,ORG_OPEN_CURLY,ORG_CLOSE_CURLY,ORG_OPEN_BRACKET,ORG_CLOSE_BRACKET,
        ORG_COMMENT_LINE,ORG_COMMENT_OPEN,ORG_COMMENT_CLOSE,ERROR};

    //store all states in dfa in list for easy individual access, access each with id
    private ArrayList<State> states = new ArrayList<>();
    // increment each time we add new state
    int lastID = 0;

    //error state is the state we go to when we make invalid transition, default id is 0
    State errorState;//id 0
    // the state we start in, default id is 1
    State startState;//id 1
    // this state keeps track of our position in the DFA
    State currentState;

    // the state class
    private class State{
        class Transition{
            private State state;
            private char symbol;

            Transition(State s, char c){
                state = s;
                symbol = c;
            }

            State doTransition(char c){

                if(c == symbol)
                    return state;
                else
                    return errorState;
            }
            boolean matchSymbol(char c){
                if(symbol == c)
                    return true;
                else
                    return false;
            }
            char getSymbol(){
                return symbol;
            }
        }
        int stateID;
        private TOKEN token;
        ArrayList<Transition> transitions = new ArrayList<>();
        private boolean isFinalState;

        State(boolean f){
            stateID = lastID++;
            isFinalState = f;
        };
        State(boolean f,TOKEN t){
            stateID = lastID++;
            isFinalState = f;
            token = t;
        };

        void setToken(TOKEN t){
            token = t;
        }
        void setToken(String t){
            token = TOKEN.valueOf(t);
        }
        TOKEN getToken(){
            return token;
        }
        void addTransition(State s, char c){
            for(Transition t : transitions)
                if(t.matchSymbol(c)){
                    System.out.println("CANNOT ADD TRANSITION "+c+" to state "+stateID+" because that transition exists");
                    return;
                }
            transitions.add((new Transition(s,c)));
        }

        void removeTransition(char c){
            for(Transition t : transitions){
                if(t.matchSymbol(c))
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
        boolean isFinal(){
            return isFinalState;
        }
        void setFinal(boolean f){
            isFinalState = f;
        }

    }


    public DFA(){
        errorState = new State(false); //id 0
        startState = new State(false); //id 1
        currentState = startState;
        states.add(errorState);
        states.add(startState);
    }


    public void input(char c){
        currentState = currentState.makeTransition(c);
        System.out.print("Moving to state "+currentState.stateID);
        if(currentState.isFinal())
            System.out.print(" - Final State\n\n");
        if(currentState.stateID==errorState.stateID)
            System.out.print(" - Error State\n\n");


    }
    public void addState(boolean fin){
        State s = new State(fin);
        states.add(s);

    }
    public void addState(boolean fin,TOKEN t){
        State s = new State(fin,t);
        states.add(s);
    }
    public void setToken(int state,TOKEN t){
        if(state>=0 && state<states.size() )
            states.get(state).setToken(t);
        else
            System.out.println("Could not set token - invalid state: "+state);
    }
    public void setToken(int state,String t){
        if(state>=0 && state<states.size() )
            states.get(state).setToken(t);
        else
            System.out.println("Could not set token - invalid state: "+state);
    }
    public void setFinal(int state, boolean bool){
        if(state>=0 && state<states.size() )
            states.get(state).setFinal(bool);
        else
            System.out.println("Could not set final status - invalid state: "+state);
    }

    public void addTransition(int start, int end, char c){
        if(start>=0 && start<states.size()  && end>=0 && end<states.size())
            states.get(start).addTransition(states.get(end),c);
        else
            System.out.println("Could not add transition - invalid state(s): "+start +" to "+end);
    }

    public TOKEN getToken(){
        if(currentState.isFinal())
            return currentState.getToken();
        else
            return TOKEN.ERROR;

    }
    public void reset(){
        currentState = startState;
        System.out.println("RESET DFA");
    }
    public int size(){
        return states.size();
    }

}
