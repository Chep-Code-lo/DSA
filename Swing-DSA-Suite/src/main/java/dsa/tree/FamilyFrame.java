package dsa.tree;
import dsa.common.Ui;
import javax.swing.*; import javax.swing.tree.*; import java.awt.*; 

public class FamilyFrame extends JFrame {
    private PersonNode root = sample();
    private final JTree tree = new JTree(new FamilyTreeModel(root));
    private final JTextArea stats = new JTextArea();
    private final JTextField tfName = new JTextField();
    private final JSpinner spYear = new JSpinner(new SpinnerNumberModel(1990, 1800, 2100, 1));
    private final JButton bSave = new JButton("Lưu JSON");
    private final JButton bLoad = new JButton("Mở JSON");

    public FamilyFrame(){
        super("Cây Gia Phả (n-ary)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(900,600); setLocationRelativeTo(null);
        tree.setShowsRootHandles(true);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, Ui.titled("Gia phả", new JScrollPane(tree)), rightPanel()));
        refreshStats();
    }

    private JPanel rightPanel(){
        JPanel p = new JPanel(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridLayout(0,1,6,6));
        form.add(new JLabel("Tên:")); form.add(tfName);
        form.add(new JLabel("Năm sinh:")); form.add(spYear);

        JButton bAddChild = new JButton("Thêm Con");
        JButton bEdit = new JButton("Sửa Node");
        JButton bDelete = new JButton("Xóa Node");
        JButton bStats = new JButton("Làm mới thống kê");

        bAddChild.addActionListener(e->{ var sel = selectedNode(); if(sel==null) return; 
            String name=tfName.getText().trim(); int year=(Integer)spYear.getValue();
            if(!name.isEmpty()){ sel.addChild(new PersonNode(name,year)); rebuild(); }
        });
        bEdit.addActionListener(e->{ var sel = selectedNode(); if(sel==null) return; 
            String name=tfName.getText().trim(); int year=(Integer)spYear.getValue();
            if(!name.isEmpty()){ sel.name=name; sel.birthYear=year; rebuild(); }
        });
        bDelete.addActionListener(e->{
            var path = tree.getSelectionPath(); if(path==null) return; 
            DefaultMutableTreeNode n=(DefaultMutableTreeNode)path.getLastPathComponent();
            if(n.getParent()==null){ JOptionPane.showMessageDialog(this,"Không xóa gốc"); return; }
            ((DefaultTreeModel)tree.getModel()).removeNodeFromParent(n);
            root = fromSwing((DefaultMutableTreeNode)tree.getModel().getRoot());
            refreshStats();
        });
        bStats.addActionListener(e->refreshStats());

        bSave.addActionListener(e->{
            JFileChooser ch = new JFileChooser();
            if(ch.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
                try { FamilyIO.save(root, ch.getSelectedFile()); }
                catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(),"Lỗi lưu", JOptionPane.ERROR_MESSAGE);}    
            }
        });
        bLoad.addActionListener(e->{
            JFileChooser ch = new JFileChooser();
            if(ch.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                try {
                    root = FamilyIO.load(ch.getSelectedFile());
                    ((DefaultTreeModel)tree.getModel()).setRoot(FamilyTreeModelToSwing(root));
                    ((DefaultTreeModel)tree.getModel()).reload();
                    refreshStats();
                } catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(),"Lỗi mở", JOptionPane.ERROR_MESSAGE);}    
            }
        });

        JPanel btn = new JPanel(); btn.add(bAddChild); btn.add(bEdit); btn.add(bDelete); btn.add(bStats); btn.add(bSave); btn.add(bLoad);
        p.add(Ui.titled("Form", form), BorderLayout.NORTH);
        stats.setEditable(false);
        p.add(Ui.titled("Thống kê", new JScrollPane(stats)), BorderLayout.CENTER);
        p.add(btn, BorderLayout.SOUTH);
        return p;
    }

    private PersonNode selectedNode(){
        var path = tree.getSelectionPath(); if(path==null) return null;
        DefaultMutableTreeNode n=(DefaultMutableTreeNode)path.getLastPathComponent();
        return (PersonNode) n.getUserObject();
    }

    private void rebuild(){
        ((DefaultTreeModel)tree.getModel()).setRoot(FamilyTreeModelToSwing(root));
        ((DefaultTreeModel)tree.getModel()).reload();
        refreshStats();
    }

    private static DefaultMutableTreeNode FamilyTreeModelToSwing(PersonNode p){
        DefaultMutableTreeNode n=new DefaultMutableTreeNode(p);
        for(PersonNode c: p.children) n.add(FamilyTreeModelToSwing(c));
        return n;
    }

    private static PersonNode fromSwing(DefaultMutableTreeNode n){
        PersonNode p=(PersonNode)n.getUserObject(); PersonNode copy=new PersonNode(p.name,p.birthYear);
        for(int i=0;i<n.getChildCount();i++) copy.addChild(fromSwing((DefaultMutableTreeNode)n.getChildAt(i)));
        return copy;
    }

    private void refreshStats(){
        StringBuilder sb=new StringBuilder();
        int depth=FamilyTreeModel.depth(root);
        sb.append("Số thế hệ: ").append(depth).append('\n');
        var lvls=FamilyTreeModel.byLevel(root);
        for(int i=0;i<lvls.size();i++){
            sb.append("Thế hệ ").append(i).append(": ");
            for(PersonNode p: lvls.get(i)) sb.append(p.name).append(", ");
            sb.append('\n');
        }
        FamilyTreeModel.dfs(root, x->{
            sb.append("- ").append(x).append(" → con trực tiếp: ").append(x.children.size());
            sb.append(", tổng con-cháu: ").append(totalDescendants(x)).append('\n');
        });
        stats.setText(sb.toString());
    }

    private static int totalDescendants(PersonNode p){
        int sum=p.children.size(); for(PersonNode c: p.children) sum+=totalDescendants(c); return sum;
    }

    private static PersonNode sample(){
        PersonNode a=new PersonNode("Ông A", 1950);
        PersonNode b=new PersonNode("B", 1975); a.addChild(b);
        PersonNode c=new PersonNode("C", 1978); a.addChild(c);
        b.addChild(new PersonNode("D", 2000));
        b.addChild(new PersonNode("E", 2003));
        c.addChild(new PersonNode("F", 2005));
        return a;
    }
}
