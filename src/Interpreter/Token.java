package Interpreter;

public class Token {
    public final Lexer.TokenType type;
    protected final String lexeme;
    public final Object literal;
    public final int line;
    public final int pos;

    Token(Lexer.TokenType type, String lexeme, Object literal, int line, int pos){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.pos = pos;
    }

    public String toString(){
        return type + " " + lexeme + " " + literal;
    }
}
