package dsa.expr;
import dsa.common.MyStack;
import java.util.*;

public class ShuntingYard {
    private static int prec(String op){
        return switch(op){ case "^"->4; case "*","/"->3; case "+","-"->2; default->0; };
    }
    private static boolean rightAssoc(String op){ return op.equals("^"); }

    public static List<Token> infixToPostfix(List<Token> in){
        List<Token> out = new ArrayList<>();
        MyStack<Token> ops = new MyStack<>();
        for(Token t: in){
            switch(t.type){
                case NUM -> out.add(t);
                case FUNC -> ops.push(t);
                case COMMA -> { while(!ops.isEmpty() && ops.peek().type!=Token.Type.LP) out.add(ops.pop()); }
                case OP -> {
                    while(!ops.isEmpty() && ops.peek().type==Token.Type.OP){
                        String o1=t.text, o2=ops.peek().text;
                        if((!rightAssoc(o1) && prec(o1)<=prec(o2)) || (rightAssoc(o1) && prec(o1)<prec(o2))) out.add(ops.pop()); else break;
                    }
                    ops.push(t);
                }
                case LP -> ops.push(t);
                case RP -> {
                    while(!ops.isEmpty() && ops.peek().type!=Token.Type.LP) out.add(ops.pop());
                    if(ops.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses");
                    ops.pop(); // '('
                    if(!ops.isEmpty() && ops.peek().type==Token.Type.FUNC) out.add(ops.pop());
                }
            }
        }
        while(!ops.isEmpty()){
            Token x=ops.pop();
            if(x.type==Token.Type.LP||x.type==Token.Type.RP) throw new IllegalArgumentException("Mismatched parentheses");
            out.add(x);
        }
        return out;
    }

    public static List<Token> infixToPrefix(List<Token> in){
        List<Token> rev = new ArrayList<>();
        for(int i=in.size()-1;i>=0;--i){
            Token t=in.get(i);
            if(t.type==Token.Type.LP) rev.add(new Token(Token.Type.RP, ")"));
            else if(t.type==Token.Type.RP) rev.add(new Token(Token.Type.LP, "("));
            else rev.add(t);
        }
        List<Token> post = infixToPostfix(rev);
        Collections.reverse(post);
        return post;
    }

    public static String join(List<Token> ts){
        StringBuilder sb=new StringBuilder();
        for(Token t: ts){ sb.append(t.text).append(' '); }
        return sb.toString().trim();
    }
}
