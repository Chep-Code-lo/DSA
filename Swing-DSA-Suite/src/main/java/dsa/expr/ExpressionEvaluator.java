package dsa.expr;
import dsa.common.MyStack;
import java.util.List;

public class ExpressionEvaluator {
    private static double apply(String op, double b, double a){
        return switch(op){
            case "+" -> a+b;
            case "-" -> a-b;
            case "*" -> a*b;
            case "/" -> a/b;
            case "^" -> Math.pow(a,b);
            default -> throw new IllegalArgumentException("op: "+op);
        };
    }
    private static double func(String f, double x){
        return switch(f){
            case "sin"->Math.sin(x);
            case "cos"->Math.cos(x);
            case "tan"->Math.tan(x);
            case "sqrt"->Math.sqrt(x);
            case "ln"->Math.log(x);
            default->throw new IllegalArgumentException("func: "+f);
        };
    }
    public static double evalPostfix(List<Token> post){
        MyStack<Double> st=new MyStack<>();
        for(Token t: post){
            switch(t.type){
                case NUM -> st.push(Double.parseDouble(t.text));
                case OP -> st.push(apply(t.text, st.pop(), st.pop()));
                case FUNC -> st.push(func(t.text, st.pop()));
                default -> throw new IllegalArgumentException("Unexpected token: "+t);
            }
        }
        if(st.size()!=1) throw new IllegalStateException("Bad expression");
        return st.pop();
    }
}
