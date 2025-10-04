package app.tree;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

public class FamilyTreePanel extends JPanel {
  private NaryNode root = sampleTree();
  private final DefaultMutableTreeNode swingRoot = new DefaultMutableTreeNode(root.name);
  private final JTree tree = new JTree(swingRoot);
  private final JTextArea stats = new JTextArea(8, 40);
  private final JTextField nameField = new JTextField(14);
  private final GenogramPanel genogram = new GenogramPanel();

  public FamilyTreePanel() {
    setLayout(new BorderLayout(8, 8));
    rebuildSwingTree();
    
    // Tạo font lớn hơn cho tiêu đề
    Font titleFont = new Font("SansSerif", Font.BOLD, 16);
    
    TitledBorder treeBorder = new TitledBorder("Cây gia phả (JTree)");
    treeBorder.setTitleFont(titleFont);
    tree.setBorder(treeBorder);
    
    // Thêm custom renderer với icon
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
    renderer.setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
    renderer.setOpenIcon(UIManager.getIcon("Tree.openIcon"));
    tree.setCellRenderer(renderer);

    // Panel trái: cây gia phả, form, và số liệu
    JPanel left = new JPanel(new BorderLayout(8, 8));
    
    // Cây gia phả ở trên
    JPanel treePanel = new JPanel(new BorderLayout());
    treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
    treePanel.setPreferredSize(new Dimension(600, 200));
    
    // Form ở giữa
    JPanel formPanel = new JPanel(new BorderLayout());
    formPanel.add(buildForm(titleFont), BorderLayout.CENTER);
    formPanel.setPreferredSize(new Dimension(600, 100));
    
    // Số liệu ở dưới
    JPanel statsPanel = new JPanel(new BorderLayout());
    TitledBorder statsBorder = new TitledBorder("Số liệu");
    statsBorder.setTitleFont(titleFont);
    stats.setBorder(statsBorder);
    stats.setEditable(false);
    statsPanel.add(new JScrollPane(stats), BorderLayout.CENTER);
    statsPanel.setPreferredSize(new Dimension(600, 200));
    
    left.add(treePanel, BorderLayout.NORTH);
    left.add(formPanel, BorderLayout.CENTER);
    left.add(statsPanel, BorderLayout.SOUTH);

    // Panel phải: sơ đồ
    JPanel right = new JPanel(new BorderLayout());
    TitledBorder genogramBorder = new TitledBorder("Sơ đồ (JGraphX)");
    genogramBorder.setTitleFont(titleFont);
    genogram.setBorder(genogramBorder);
    genogram.setRoot(root);
    right.add(genogram, BorderLayout.CENTER);

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
    split.setResizeWeight(0.5);
    split.setDividerLocation(0.5);

    add(split, BorderLayout.CENTER);
  }

  private JPanel buildForm(Font titleFont) {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    TitledBorder formBorder = new TitledBorder("Thao tác");
    formBorder.setTitleFont(titleFont);
    p.setBorder(formBorder);
    
    // Tạo các button
    JButton addBtn = new JButton("Thêm con");
    JButton editBtn = new JButton("Sửa tên");
    JButton delBtn = new JButton("Xóa nút");
    JButton calcBtn = new JButton("Tính số liệu");
    
    // Thêm icon từ resources hoặc Unicode symbols
    addBtn.setText("➕ Thêm con");
    editBtn.setText("✏️ Sửa tên");
    delBtn.setText("❌ Xóa nút");
    calcBtn.setText("📊 Tính số liệu");
    
    // Style buttons with modern colors
    Color btnAddColor = new Color(76, 175, 80);      // Green
    Color btnEditColor = new Color(33, 150, 243);    // Blue
    Color btnDelColor = new Color(244, 67, 54);      // Red
    Color btnCalcColor = new Color(156, 39, 176);    // Purple
    
    addBtn.setBackground(btnAddColor);
    editBtn.setBackground(btnEditColor);
    delBtn.setBackground(btnDelColor);
    calcBtn.setBackground(btnCalcColor);
    
    for (JButton btn : new JButton[]{addBtn, editBtn, delBtn, calcBtn}) {
      btn.setForeground(Color.WHITE);
      btn.setFocusPainted(false);
      btn.setBorderPainted(false);
      btn.setOpaque(true);
      btn.setFont(new Font("SansSerif", Font.BOLD, 12));
      btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    JLabel nameLabel = new JLabel("Tên:");
    nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    nameField.setPreferredSize(new Dimension(150, 30));
    nameField.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(new Color(189, 189, 189), 1, true),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));
    
    p.add(nameLabel);
    p.add(nameField);
    p.add(addBtn);
    p.add(editBtn);
    p.add(delBtn);
    p.add(calcBtn);
    addBtn.addActionListener(e -> onAdd());
    editBtn.addActionListener(e -> onEdit());
    delBtn.addActionListener(e -> onDelete());
    calcBtn.addActionListener(e -> onCalc());
    return p;
  }

  private static NaryNode sampleTree() {
    NaryNode r = new NaryNode("Ông Tổ");
    NaryNode a = r.addChild("A");
    NaryNode b = r.addChild("B");
    a.addChild("A1");
    NaryNode a2 = a.addChild("A2");
    a2.addChild("A2.1");
    a2.addChild("A2.2");
    b.addChild("B1");
    return r;
  }

  private void rebuildSwingTree() {
    swingRoot.removeAllChildren();
    swingRoot.setUserObject(root.name);
    buildChildren(swingRoot, root);
    ((DefaultTreeModel) tree.getModel()).reload();
    for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
    genogram.setRoot(root);
  }

  private void buildChildren(DefaultMutableTreeNode parentSwing, NaryNode parentModel) {
    for (NaryNode c : parentModel.children) {
      DefaultMutableTreeNode child = new DefaultMutableTreeNode(c.name);
      parentSwing.add(child);
      buildChildren(child, c);
    }
  }

  private NaryNode findByPath(TreePath path) {
    if (path == null) return null;
    Object[] arr = path.getPath();
    NaryNode cur = root;
    for (int i = 1; i < arr.length; i++) {
      String label = arr[i].toString();
      Optional<NaryNode> next =
          cur.children.stream().filter(n -> n.name.equals(label)).findFirst();
      if (next.isEmpty()) return null;
      cur = next.get();
    }
    return cur;
  }

  private void onAdd() {
    TreePath sel = tree.getSelectionPath();
    if (sel == null) {
      JOptionPane.showMessageDialog(this, "Chọn một nút.");
      return;
    }
    NaryNode node = findByPath(sel);
    if (node == null) return;
    String nm = nameField.getText().trim();
    if (nm.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Nhập tên.");
      return;
    }
    node.addChild(nm);
    nameField.setText("");
    rebuildSwingTree();
  }

  private void onEdit() {
    TreePath sel = tree.getSelectionPath();
    if (sel == null) {
      JOptionPane.showMessageDialog(this, "Chọn một nút.");
      return;
    }
    NaryNode node = findByPath(sel);
    if (node == null) return;
    String nm = nameField.getText().trim();
    if (nm.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Nhập tên.");
      return;
    }
    node.name = nm;
    rebuildSwingTree();
  }

  private void onDelete() {
    TreePath sel = tree.getSelectionPath();
    if (sel == null) {
      JOptionPane.showMessageDialog(this, "Chọn một nút.");
      return;
    }
    NaryNode node = findByPath(sel);
    if (node == null) return;
    if (node == root) {
      JOptionPane.showMessageDialog(this, "Không xóa được gốc.");
      return;
    }
    node.removeFromParent();
    rebuildSwingTree();
  }

  private void onCalc() {
    java.util.Map<Integer, java.util.List<NaryNode>> byGen = new java.util.TreeMap<>();
    bfs(root, (n, d) -> byGen.computeIfAbsent(d, k -> new java.util.ArrayList<>()).add(n));
    StringBuilder sb = new StringBuilder();
    for (var e : byGen.entrySet()) {
      sb.append("Thế hệ ")
          .append(e.getKey())
          .append(": ")
          .append(e.getValue().size())
          .append(" thành viên\n");
    }
    int k = 1; 
    java.util.List<NaryNode> genK = byGen.getOrDefault(k, java.util.List.of());
    sb.append("\nThế hệ ").append(k).append(" (con-cháu mỗi người):\n");
    for (NaryNode n : genK) {
      sb.append("- ").append(n.name).append(": ").append(n.descendants()).append("\n");
    }
    stats.setText(sb.toString());
  }

  private interface Visitor {
    void visit(NaryNode n, int depth);
  }

  private void bfs(NaryNode start, Visitor v) {
    java.util.ArrayDeque<NaryNode> q = new java.util.ArrayDeque<>();
    java.util.ArrayDeque<Integer> d = new java.util.ArrayDeque<>();
    q.add(start);
    d.add(0);
    while (!q.isEmpty()) {
      NaryNode cur = q.poll();
      int dep = d.poll();
      v.visit(cur, dep);
      for (NaryNode c : cur.children) {
        q.add(c);
        d.add(dep + 1);
      }
    }
  }
}
