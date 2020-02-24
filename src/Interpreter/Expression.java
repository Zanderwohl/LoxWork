package Interpreter;

abstract class Expression {
    static class Binary extends Expression{

        final Expression left;
        final Token operator;
        final Expression right;

        Binary(Expression left, Token operator, Expression right){
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    static class Unary extends Expression{
        final Token operator;
        final Expression operand;

        Unary(Token operator, Expression operand){
            this.operator = operator;
            this.operand = operand;
        }
    }
}
