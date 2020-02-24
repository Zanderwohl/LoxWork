package Interpreter;

import java.util.List;

abstract class Expression {

    static class Binary extends Expression {
        Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitBinaryExpression(this);
        }

        final Expression left;
        final Token operator;
        final Expression right;
    }

    static class Grouping extends Expression {
        Grouping(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitGroupingExpression(this);
        }

        final Expression expression;
    }

    static class Literal extends Expression {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitLiteralExpression(this);
        }

        final Object value;
    }

    static class Unary extends Expression {
        Unary(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitUnaryExpression(this);
        }

        final Token operator;
        final Expression right;
    }

    abstract <R> R accept(Visitor<R> visitor);

    interface Visitor<R> {
        R visitBinaryExpression(Expression.Binary expression);
        R visitGroupingExpression(Expression.Grouping expression);
        R visitLiteralExpression(Expression.Literal expression);
        R visitUnaryExpression(Expression.Unary expression);
    }
}
