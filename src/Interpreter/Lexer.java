package Interpreter;

import java.util.*;

public class Lexer {

    private static ResourceBundle em = ResourceBundle.getBundle("Interpreter.keywords", Main.locale);

    /**
     * A list of all token-types in the languge.
     */
    enum TokenType {
        //Single-character tokens.
        LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
        COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
        LEFT_SHIFT, RIGHT_SHIFT,

        BANG, BANG_EQUAL,
        EQUAL, EQUAL_EQUAL,
        GREATER, GREATER_EQUAL,
        LESS, LESS_EQUAL,

        //Literals
        IDENTIFIER, STRING, INTEGER, DOUBLE,

        //Keywords.
        AND, OR,
        B_OR, B_AND, B_NOT, B_XOR, B_NAND,
        CLASS, IF, ELSE,
        TRUE, FALSE,
        NIL,
        FUNCTION, RETURN,
        WHILE, FOR,
        PRINT,
        SUPER, THIS,
        VAR,

        EOF

    }

    private static final Map<String, TokenType> keywords;

    /**
     * Puts all the keywords and their token types into a Hash Map.
     */
    static {
        keywords = new HashMap<>();
        keywords.put(em.getString("and"), TokenType.AND);
        keywords.put(em.getString("or"), TokenType.OR);
        keywords.put(em.getString("not"), TokenType.BANG);
        keywords.put(em.getString("class"),TokenType.CLASS);
        keywords.put(em.getString("if"),TokenType.IF);
        keywords.put(em.getString("else"),TokenType.ELSE);
        keywords.put(em.getString("true"), TokenType.TRUE);
        keywords.put(em.getString("false"),TokenType.FALSE);
        keywords.put(em.getString("nil"), TokenType.NIL);
        keywords.put(em.getString("function"),TokenType.FUNCTION);
        keywords.put(em.getString("return"), TokenType.RETURN);
        keywords.put(em.getString("while"), TokenType.WHILE);
        keywords.put(em.getString("for"), TokenType.FOR);
        keywords.put(em.getString("print"), TokenType.PRINT);
        keywords.put(em.getString("super"),TokenType.SUPER);
        keywords.put(em.getString("this"), TokenType.THIS);
        keywords.put(em.getString("b_or"),TokenType.B_OR);
        keywords.put(em.getString("b_and"), TokenType.B_AND);
        keywords.put(em.getString("b_not"),TokenType.B_NOT);
        keywords.put(em.getString("b_xor"),TokenType.B_XOR);
        keywords.put(em.getString("b_nand"),TokenType.B_NAND);
        keywords.put(em.getString("var"),TokenType.VAR);
    }

    private final String code;
    private final List<Token> tokens = new ArrayList<>();

    private final String[] lines; //each line, for debug purposes.

    private int start = 0;
    private int current = 0;
    private int line = 0;
    private int pos = 0;

    /**
     * A Lexer constructed with an unchangable code sequence, which it consumes.
     * Produces a series of Lexed tokens, with types identified.
     * Adds to the CompileError error list as encountered.
     * @param code The source code file.
     */
    public Lexer(String code){
        this.code = code;
        lines = code.split("\n");
    }

    /**
     * Loops through all tokens in the source code, scanning and lexing each one, adding them to a list.
     * @return A List of all parsed tokens.
     */
    List<Token> lexTokens(){
        while(isMore()){
            start = current;
            lexToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line, 0));

        return tokens;
    }

    /**
     * Checks if there are characters left in the source code.
     * @return true if there are more characters, false otherwise.
     */
    private boolean isMore(){
        return current < code.length();
    }

    /**
     * Lexes a single token, a keyword, symbol, identifier, or illegal symbol.
     */
    private void lexToken() {
        char c = advance();
        switch(c){
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.':
                if(isDigit(peek())){
                    //advance();
                    number();
                } else {
                    addToken(TokenType.DOT);
                }
                break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=':addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '<': //addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                if(match('=')){
                    addToken(TokenType.LESS_EQUAL);
                } else if(match('<')){
                    addToken(TokenType.LEFT_SHIFT);
                } else {
                    addToken(TokenType.LESS);
                }
                break;
            case '>': //addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                if(match('=')){
                    addToken(TokenType.GREATER_EQUAL);
                } else if(match('>')){
                    addToken(TokenType.RIGHT_SHIFT);
                } else {
                    addToken(TokenType.GREATER);
                }
                break;
            case '"': string(); break;
            case '/':
                if(match('/')){
                    while(peek() != '\n' && isMore()){
                        advance();
                    }
                } else if(match('*')){
                    blockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case '\n': line++; pos = 0; break;  //Advance the line, and reset the character position to 0.
            case ' ': case '\r': case '\t': break;
            default:
                if(isDigit(c)) {
                    number();
                } else if(isAlpha(c)){
                    identifier();
                } else {
                    String[] details = {c + "", lines[line - 1]};
                    CompileError.enqueue(CompileError.Error.UnexpectedCharacter, line, pos, details, false);
                }
                break;
        }
    }

    /**
     * Advance to the next character, increment the position marker, and return the new character.
     * @return The next character.
     */
    private char advance(){
        current++;
        pos++;
        char thisChar = code.charAt(current - 1);
        return code.charAt(current - 1);
    }

    /**
     * Looks at character current + n without advancing the current counter.
     * @param n How many characters to look ahead or behind.
     * @return The character at current + n.
     */
    private char peek(int n){
        if(current + n >= code.length()){
            return '\0';
        }
        return code.charAt(current + n);
    }

    /**
     * Looks at the next character without advancing the current character.
     * @return The next character.
     */
    private char peek(){
        return peek(0);
    }

    /**
     * Checks if the current character is equal to a provided character.
     * @param expected Character to check against.
     * @return false if no EOF, false if the character is not correct. true if neither, and advances the current character.
     */
    private boolean match(char expected){
        if(!isMore()){
            return false;
        }
        if(code.charAt(current) != expected){
            return false;
        }

        current++;
        return true;
    }

    /**
     * Checks if a character is a digit 0 through 9.
     * @param c The character to check.
     * @return c >= '0' && c <= '9'
     */
    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    /**
     * Checks if a character is a letter or underscore.
     * @param c The character to check.
     * @return Matches regex [A-Za-z_]
     */
    private boolean isAlpha(char c){
        return (c + "").matches("[A-Za-z_]");
    }

    /**
     * Checks if a character is a letter, digit, or underscore.
     * @param c The character to check.
     * @return isDigit(c) || isAlpha(c)
     */
    private boolean isAlphaNumeric(char c){
        return isDigit(c) || isAlpha(c);
    }

    /**
     * Adds a token to the list, with only a given type.
     * Puts null in the literal slot.
     * @param type The type of the token.
     */
    private void addToken(TokenType type){
        addToken(type, null);
    }

    /**
     * Adds a token to the list, given a type, and an object that contains any literal information.
     *
     * @param type The type of the token.
     * @param literal Any specific information about a user-defined value or identifier, etc.
     */
    private void addToken(TokenType type, Object literal){
        String text = code.substring(start, current);
        tokens.add(new Token(type, text, literal, line, pos));
    }

    /**
     * Consumes a block comment and discards its contents.
     * Can handle nested block quotes.
     * Adds a compile error if the block comment is never closed.
     */
    private void blockComment(){
        int startLine = line;
        int startPos = pos;

        String[] details = {lines[startLine - 1], ""};//TODO: Add details about openings and closings.

        boolean continueFlag = !(peek(-2) == '*' && peek(-1) == '/') && isMore();
        int level = 1;
        while(continueFlag && isMore()){
            advance();
            //System.err.println(peek(-2) + "" + peek(-1));
            if((peek(-2) == '*' && peek(-1) == '/')){
                level--;
                details[1] += "\t" + lines[line - 1] + "\n";
            }
            if ((peek(-2) == '/' && peek(-1) == '*')) {
                level++;
                details[1] += "\t" + lines[line - 1] + "\n";
            }
            //System.err.println("Level: " + level);
            continueFlag = level > 0;
        }

        if(!isMore()){
            CompileError.enqueue(CompileError.Error.UnterminatedComment, startLine, startPos, details, false);
        }
    }

    /**
     * Consumes a string marked by double-quotes,
     * and places its token and value in the list.
     * It can handle newlines.
     */
    private void string(){
        int startLine = line;
        int startPos = pos;
        while(peek() != '"' && isMore()){
            if(peek() == '\n'){
                line++;
                pos = 0;
            }
            advance();
        }

        if(!isMore()){
            String[] details = {lines[startLine - 1]};
            CompileError.enqueue(CompileError.Error.UnterminatedString, startLine, startPos, details, false);
            return;
        }

        advance();

        String value = code.substring(start + 1, current -1);
        value = StringUtils.unescape(value);
        addToken(TokenType.STRING, value);
    }

    /**
     * Consumes a number, either and integer or a double (even with only the fraction part),
     * and places its token in the list.
     */
    private void number(){//TODO: let leading decimals notate a double.
        while(isDigit(peek())){
            advance();
        }

        if(peek() == '.' && (isDigit(peek(1)))){
            advance();

            while(isDigit(peek())){
                advance();
            }

            addToken(TokenType.DOUBLE, Double.parseDouble(code.substring(start, current))); //TODO: parse
        } else {
            try{
                addToken(TokenType.INTEGER, Integer.parseInt(code.substring(start, current))); //TODO: parse
            } catch (NumberFormatException e){
                addToken(TokenType.DOUBLE, Double.parseDouble(code.substring(start, current))); //TODO: parse
            }

        }
    }

    /**
     * Consumes an identifier,
     * and places its token in the list.
     */
    private void identifier(){
        while(isAlphaNumeric(peek())){
            advance();
        }

        String text = code.substring(start, current);

        TokenType type = keywords.get(text);
        if(type == null){
            type = TokenType.IDENTIFIER;
            addToken(type, text);
        } else {
            addToken(type);
        }
    }


}
