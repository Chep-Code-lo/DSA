package dsa.tree;
import javax.swing.tree.*; import java.util.*; import java.util.function.Consumer;

public class FamilyTreeModel extends DefaultTreeModel {
    public FamilyTreeModel(PersonNode root){ super(toTree(root)); }

    private static DefaultMutableTreeNode toTree(PersonNode p){
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(p);
        for(PersonNode c: p.children) n.add(toTree(c));
        return n;
    }

    public void refresh(PersonNode newRoot){ setRoot(toTree(newRoot)); reload(); }

    public static int depth(PersonNode r){
        if(r==null) return 0; int best=1; for(PersonNode c: r.children) best = Math.max(best, 1+depth(c)); return best;
    }
    public static java.util.List<java.util.List<PersonNode>> byLevel(PersonNode root){
        java.util.List<java.util.List<PersonNode>> res=new java.util.ArrayList<>(); if(root==null) return res;
        java.util.Queue<PersonNode> q=new java.util.ArrayDeque<>(); q.add(root);
        while(!q.isEmpty()){
            int sz=q.size(); java.util.List<PersonNode> lvl=new java.util.ArrayList<>();
            for(int i=0;i<sz;i++){ PersonNode x=q.remove(); lvl.add(x); q.addAll(x.children);} res.add(lvl);
        }
        return res;
    }
    public static void dfs(PersonNode r, Consumer<PersonNode> f){ if(r==null) return; f.accept(r); for(PersonNode c: r.children) dfs(c,f); }
}
