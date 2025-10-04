package dsa.expr;
import java.util.*;

public class Lexer {
    private final String s; private int i=0; private final int n;
    private static final Set<String> FUNCS = Set.of("sin","cos","tan","sqrt","ln");
    public Lexer(String s){ this.s=s.replaceAll("\\s+"," ").trim(); this.n=this.s.length(); }

    public List<Token> lex(){
        List<Token> out = new ArrayList<>();
        while(i<n){ char c=s.charAt(i);
            if(Character.isWhitespace(c)){ i++; continue; }
            if(Character.isDigit(c) || c=='.'){
                int j=i; boolean dot=(c=='.'); j++;
                while(j<n){ char d=s.charAt(j); if(Character.isDigit(d)) j++; else if(d=='.'&&!dot){dot=true;j++;} else break; }
                out.add(new Token(Token.Type.NUM, s.substring(i,j))); i=j; continue;
            }
            if(Character.isLetter(c)){
                int j=i+1; while(j<n && Character.isLetter(s.charAt(j))) j++;
                String t=s.substring(i,j);
                if(FUNCS.contains(t)) out.add(new Token(Token.Type.FUNC,t));
                else throw new IllegalArgumentException("Unknown ident: "+t);
                i=j; continue;
            }
            switch(c){
                case '+': case '-': case '*': case '/': case '^': out.add(new Token(Token.Type.OP, String.valueOf(c))); i++; break;
                case '(': out.add(new Token(Token.Type.LP, "(")); i++; break;
                case ')': out.add(new Token(Token.Type.RP, ")")); i++; break;
                case ',': out.add(new Token(Token.Type.COMMA, ",")); i++; break;
                default: throw new IllegalArgumentException("Bad char: "+c);
            }
        }
        return out;
    }
}
