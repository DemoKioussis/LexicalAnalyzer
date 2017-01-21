/**
 * Created by Demok on 2017-01-20.
 */
public class Token {
    public enum TOKEN{EL_ID, EL_ALPHANUM,EL_NUM,EL_INTEGER,EL_FLOAT,EL_FRACTION,EL_LETTER,EL_DIGIT,EL_NONZERO,
        W_IF,W_THEN,W_ELSE,W_FOR,W_CLASS,W_INT,W_FLOAT,W_GET,W_PUT,W_RETURN,W_PROGRAM,
        OP_EQUAL,OP_NOT_EQUAL,OP_L_THAN,OP_G_THAN,OP_L_THAN_OR_EQUAL,OP_G_THAN_OR_EQUAL,
        OP_PLUS,OP_MINUS,OP_MULTIPLY,OP_DIVIDE,OP_ASSIGN,OP_AND,OP_NOT,OP_OR,PUNCT_SEMICOL,PUNCT_COMMA,PUNCT_DOT,
        ORG_OPEN_PAREN,ORG_CLOSE_PAREN,ORG_OPEN_CURLY,ORG_CLOSE_CURLY,ORG_OPEN_BRACKET,ORG_CLOSE_BRACKET,
        ORG_COMMENT_LINE,ORG_COMMENT_OPEN,ORG_COMMENT_CLOSE,ERROR,NOTHING,START};

    private String lexeme;
    private TOKEN token;

    public Token(TOKEN t, String lex){
        token = t;
        lexeme = lex;
    }

    public TOKEN getToken(){
        return token;
    }
    public String getLexeme(){
        return lexeme;
    }
    public void print(){
        System.out.println(lexeme+" : "+token);
    }
}
