package Interpreter;

public class Interpreter implements Expression.Visitor<Object> {

    @Override
    public Object visitLiteralExpression(Expression.Literal expression){
        return expression.value;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression){
        return evaluate(expression.expression);
    }

    public Object evaluate(Expression expression){
        return expression.accept(this);
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression){
        Object right = evaluate(expression.right);

        switch(expression.operator.type){
            case MINUS:
                return -(double)right;
            case BANG:
                return isTruthy(right);
            case B_NOT:
                if(right instanceof Double){
                    double number = (double) right;
                    return (double)~(int)((double) right); //hohoho what an evil thing this would be to do.
                }
        }

        return null;
    }

    private boolean isTruthy(Object object){
        if(object == null){
            return false;
        }
        if(object instanceof Boolean) {
            return (boolean)object;
        }
        return true;
    }

    public Object visitBinaryExpression(Expression.Binary expression){
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch(expression.operator.type){
            case MINUS:
                return (double)left - (double)right;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
            case STAR_STAR:
                return Math.pow((double)left, (double)right);
            case PLUS:
                if(left instanceof Double && right instanceof Double){
                    return (double) left + (double) right;
                }
                if(left instanceof String && right instanceof String){
                    return left.toString() + right.toString();
                }
                if((left instanceof String && right instanceof Double) ||
                        (left instanceof Double && right instanceof String)){
                    return left.toString() + right.toString();
                }
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        return null;
    }

    private boolean isEqual(Object a, Object b){
        if(a == null && b == null){
            return true;
        }
        if(a == null){
            return false;
        }
        return a.equals(b);
    }


    @Override
    public Object visitTernaryExpression(Expression.Ternary expression) {
        Object condition = evaluate(expression.left);
        //System.out.println(condition);
        //System.out.println(isTruthy(condition));
        if(isTruthy(condition)){
            return evaluate(expression.center);
        } else {
            return evaluate(expression.right);
        }
    }
}
