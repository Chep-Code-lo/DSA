package dsa.tree;
import java.util.*;

public class PersonNode {
    public String name; public int birthYear; public final List<PersonNode> children = new ArrayList<>();
    public PersonNode(String name,int birthYear){ this.name=name; this.birthYear=birthYear; }
    public void addChild(PersonNode c){ children.add(c); }
    public String toString(){ return name+" ("+birthYear+")"; }
}
