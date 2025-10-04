package dsa.expr;

public class Token {
    public enum Type { NUM, OP, LP, RP, FUNC, COMMA }
    public final Type type;
    public final String text;
    public Token(Type t, String s){ this.type=t; this.text=s; }
    public String toString(){ return type+":"+text; }
}
