package Interpreter;

public class AstPrinter implements Expression.Visitor<String>{

    String print(Expression expression){
        return expression.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitTernaryExpression(Expression.Ternary expression){
        return parenthesize(expression.type.toString(),
                expression.left, expression.center, expression.right);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if (expression.value == null) {
            return "nil";
        }
        return expression.value.toString();
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    private String parenthesize(String name, Expression... expressions){
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for(Expression expression: expressions){
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    /* public static void main(String[] args){
        Expression expression = new Expression.Binary(
            new Expression.Unary(
                    new Token(Lexer.TokenType.MINUS, "-", null, 1, 1),
                    new Expression.Literal(123)
            ),
            new Token(Lexer.TokenType.STAR, "*", null, 1, 1),
            new Expression.Grouping(
                    new Expression.Literal(45.67)
            )
        );

        System.out.println(new AstPrinter().print(expression));
    }*/
}



