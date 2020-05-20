package Interpreter;

public class Token {
    public final Lexer.TokenType type;
    protected final String lexeme;
    public final Object literal;
    public final int line;
    public final int pos;
    public final String originalLine;

    Token(Lexer.TokenType type, String lexeme, Object literal, int line, int pos, String originalLine){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.pos = pos;
        this.originalLine = originalLine;
    }

    public String toString(){
        return type + " " + lexeme + " " + literal;
    }
}
