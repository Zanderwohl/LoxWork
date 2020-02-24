package Interpreter;

public class Token {
    private final Lexer.TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;
    private final int pos;

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
