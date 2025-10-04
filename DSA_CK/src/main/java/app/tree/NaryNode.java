package app.tree;

import java.util.*;

public class NaryNode {
  public String id;
  public String name;
  public java.util.List<NaryNode> children = new ArrayList<>();
  public transient NaryNode parent;

  public NaryNode() {}

  public NaryNode(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
  }

  public NaryNode addChild(String name) {
    NaryNode c = new NaryNode(name);
    c.parent = this;
    children.add(c);
    return c;
  }

  public void removeFromParent() {
    if (parent != null) parent.children.remove(this);
  }

  public int depth() {
    int d = 0;
    NaryNode p = parent;
    while (p != null) {
      d++;
      p = p.parent;
    }
    return d;
  }

  public int descendants() {
    int sum = children.size();
    for (NaryNode c : children) sum += c.descendants();
    return sum;
  }
}
