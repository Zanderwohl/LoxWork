package Interpreter;

import java.util.List;

import static Interpreter.Lexer.*;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    public Expression parse(){
        try{
            return expression();
        } catch (ParseError error){
            return null;
        }
    }

    private Expression expression(){
        //return equality();
        return conditional();
    }

    private Expression conditional(){
        Expression expression = equality();

        while(match(TokenType.QUESTION)){
            Expression center = equality();
            consume(TokenType.COLON, CompileError.Error.UnterminatedTernary);
            Expression right = equality();
            return new Expression.Ternary(Expression.Ternary.Type.CONDITIONAL, expression, center, right);
        }

        return expression;
    }

    private Expression equality(){
        Expression expression = comparison();

        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression comparison(){
        Expression expression = logic();

        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expression right = logic();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression logic(){
        Expression expression = addition();

        while(match(TokenType.AND, TokenType.OR, TokenType.B_OR, TokenType.B_AND, TokenType.B_XOR, TokenType.B_NAND)){
            Token operator = previous();
            Expression right = addition();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression addition(){
        Expression expression = multiplication();

        while(match(TokenType.PLUS, TokenType.MINUS)){
            Token operator = previous();
            Expression right = multiplication();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression multiplication(){
        Expression expression = exponent();

        while(match(TokenType.SLASH, TokenType.STAR)){
            Token operator = previous();
            Expression right = exponent();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression exponent(){
        Expression expression = unary();

        while(match(TokenType.STAR_STAR)){
            Token operator = previous();
            Expression right = unary();
            expression = new Expression.Binary(expression, operator, right);
        }

        return expression;
    }

    private Expression unary(){
        if(match(TokenType.BANG, TokenType.MINUS, TokenType.B_NOT)){
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }

        return primary();
    }

    private Expression primary(){
        if(match(TokenType.FALSE)){
            return new Expression.Literal(false);
        }
        if(match(TokenType.TRUE)){
            return new Expression.Literal(true);
        }
        if(match(TokenType.NIL)){
            return new Expression.Literal(null);
        }
        if(match(TokenType.INTEGER, TokenType.DOUBLE, TokenType.STRING, TokenType.NUMBER)){
            return new Expression.Literal(previous().literal);
        }

        if(match(TokenType.LEFT_PAREN)){
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN, CompileError.Error.OpenLeftParen);
            return new Expression.Grouping(expression);
        }

        throw error(true, peek(), CompileError.Error.ExpectedExpression);
    }

    private Token consume(TokenType type, CompileError.Error error){
        if(check(type)){
            return advance();
        }

        throw error(true, peek(), error);
    }

    private ParseError error(boolean fatal, Token token, CompileError.Error error){
        int line = token.line;
        int pos = token.pos;
        String[] details = {token.originalLine};
        CompileError.enqueue(error, line, tokens.get(current).pos, details, fatal); //TODO: pass actual code line
        return new ParseError();
    }

    private void synchronize(){
        advance();

        while(!isAtEnd()){
            if(previous().type == TokenType.SEMICOLON){
                return;
            }

            switch (peek().type){
                case CLASS:
                case FUNCTION:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }

    private boolean match(TokenType... types){
        for(TokenType type: types){
            if(check(type)){
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type){
        if(isAtEnd()){
            return false;
        }
        return peek().type == type;
    }

    private Token advance(){
        if(!isAtEnd()){
            current++;
        }
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == TokenType.EOF;
    }

    private Token peek(){
        return tokens.get(current);
    }

    private Token previous(){
        return tokens.get(current - 1);
    }
}
