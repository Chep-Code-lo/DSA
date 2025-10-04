package app.tree;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public class FamilyTreePanel extends JPanel {
  private NaryNode root = createSampleTree();
  private final DefaultMutableTreeNode swingRoot = new DefaultMutableTreeNode(root.name);
  private final JTree tree = new JTree(swingRoot);
  private final JTextArea stats = new JTextArea(8, 40);
  private final JTextField nameField = new JTextField(14);
  private final GenogramPanel genogram = new GenogramPanel();

  public FamilyTreePanel() {
    super(new BorderLayout(8, 8));
    initUI();
    rebuildSwingTree();
  }

  // --- UI Initialization ---

  private void initUI() {
    JPanel leftPanel = createLeftPanel();
    JPanel rightPanel = createRightPanel();

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
    split.setResizeWeight(0.5);
    add(split, BorderLayout.CENTER);
  }

  private JPanel createLeftPanel() {
    JPanel left = new JPanel(new BorderLayout(8, 8));

    // Tree Panel
    TitledBorder treeBorder = createTitledBorder("Cây gia phả (JTree)");
    tree.setBorder(treeBorder);
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
    renderer.setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
    renderer.setOpenIcon(UIManager.getIcon("Tree.openIcon"));
    tree.setCellRenderer(renderer);
    JPanel treePanel = new JPanel(new BorderLayout());
    treePanel.add(new JScrollPane(tree), BorderLayout.CENTER);
    treePanel.setPreferredSize(new Dimension(600, 200));

    // Form Panel
    JPanel formPanel = createFormPanel();
    formPanel.setPreferredSize(new Dimension(600, 100));

    // Stats Panel
    TitledBorder statsBorder = createTitledBorder("Số liệu");
    stats.setBorder(statsBorder);
    stats.setEditable(false);
    JPanel statsPanel = new JPanel(new BorderLayout());
    statsPanel.add(new JScrollPane(stats), BorderLayout.CENTER);
    statsPanel.setPreferredSize(new Dimension(600, 200));

    left.add(treePanel, BorderLayout.NORTH);
    left.add(formPanel, BorderLayout.CENTER);
    left.add(statsPanel, BorderLayout.SOUTH);
    return left;
  }

  private JPanel createRightPanel() {
    JPanel right = new JPanel(new BorderLayout());
    TitledBorder genogramBorder = createTitledBorder("Sơ đồ (JGraphX)");
    genogram.setBorder(genogramBorder);
    genogram.setRoot(root);
    right.add(genogram, BorderLayout.CENTER);
    return right;
  }

  private JPanel createFormPanel() {
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    p.setBorder(createTitledBorder("Thao tác"));

    JLabel nameLabel = new JLabel("Tên:");
    nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
    nameField.setPreferredSize(new Dimension(150, 30));
    nameField.setBorder(BorderFactory.createCompoundBorder(
      new LineBorder(new Color(189, 189, 189), 1, true),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    ));

    p.add(nameLabel);
    p.add(nameField);
    p.add(createStyledButton("➕ Thêm con", new Color(76, 175, 80), e -> onAdd()));
    p.add(createStyledButton("✏️ Sửa tên", new Color(33, 150, 243), e -> onEdit()));
    p.add(createStyledButton("❌ Xóa nút", new Color(244, 67, 54), e -> onDelete()));
    p.add(createStyledButton("📊 Tính số liệu", new Color(156, 39, 176), e -> onCalc()));
    return p;
  }

  private JButton createStyledButton(String text, Color bgColor, java.awt.event.ActionListener listener) {
    JButton btn = new JButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setBorderPainted(false);
    btn.setOpaque(true);
    btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.addActionListener(listener);
    return btn;
  }

  private TitledBorder createTitledBorder(String title) {
    TitledBorder border = new TitledBorder(title);
    border.setTitleFont(new Font("SansSerif", Font.BOLD, 16));
    return border;
  }

  // --- Event Handlers ---

  private void onAdd() {
    getSelectedNode().ifPresent(node -> {
      String name = nameField.getText().trim();
      if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập tên.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        return;
      }
      node.addChild(name);
      nameField.setText("");
      rebuildSwingTree();
    });
  }

  private void onEdit() {
    getSelectedNode().ifPresent(node -> {
      String name = nameField.getText().trim();
      if (name.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập tên.", "Lỗi", JOptionPane.WARNING_MESSAGE);
        return;
      }
      node.name = name;
      rebuildSwingTree();
    });
  }

  private void onDelete() {
    getSelectedNode().ifPresent(node -> {
      if (node == root) {
        JOptionPane.showMessageDialog(this, "Không thể xóa nút gốc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
      }
      node.removeFromParent();
      rebuildSwingTree();
    });
  }

  private void onCalc() {
    Map<Integer, List<NaryNode>> byGeneration = new TreeMap<>();
    bfs(root, (node, depth) -> byGeneration.computeIfAbsent(depth, k -> new ArrayList<>()).add(node));

    StringBuilder sb = new StringBuilder();
    byGeneration.forEach((depth, nodes) ->
      sb.append(String.format("Thế hệ %d: %d thành viên\n", depth, nodes.size()))
    );

    int k = 1; // Thế hệ cần tính số con cháu
    List<NaryNode> genK = byGeneration.getOrDefault(k, List.of());
    if (!genK.isEmpty()) {
      sb.append(String.format("\nThế hệ %d (con-cháu mỗi người):\n", k));
      genK.forEach(node ->
        sb.append(String.format("- %s: %d\n", node.name, node.descendants()))
      );
    }
    stats.setText(sb.toString());
  }

  // --- Tree & Model Manipulation ---

  private Optional<NaryNode> getSelectedNode() {
    TreePath selPath = tree.getSelectionPath();
    if (selPath == null) {
      JOptionPane.showMessageDialog(this, "Vui lòng chọn một nút trên cây.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
      return Optional.empty();
    }
    return findByPath(selPath);
  }

  private void rebuildSwingTree() {
    swingRoot.removeAllChildren();
    swingRoot.setUserObject(root.name);
    buildChildren(swingRoot, root);
    ((DefaultTreeModel) tree.getModel()).reload();
    for (int i = 0; i < tree.getRowCount(); i++) {
      tree.expandRow(i);
    }
    genogram.setRoot(root);
  }

  private void buildChildren(DefaultMutableTreeNode parentSwing, NaryNode parentModel) {
    for (NaryNode childModel : parentModel.children) {
      DefaultMutableTreeNode childSwing = new DefaultMutableTreeNode(childModel.name);
      parentSwing.add(childSwing);
      buildChildren(childSwing, childModel);
    }
  }

  private Optional<NaryNode> findByPath(TreePath path) {
    NaryNode current = root;
    // Bỏ qua gốc (phần tử đầu tiên trong path)
    for (int i = 1; i < path.getPathCount(); i++) {
      String name = path.getPathComponent(i).toString();
      Optional<NaryNode> next = current.children.stream()
        .filter(n -> n.name.equals(name))
        .findFirst();
      if (next.isEmpty()) return Optional.empty();
      current = next.get();
    }
    return Optional.of(current);
  }

  private void bfs(NaryNode start, BiConsumer<NaryNode, Integer> visitor) {
    Queue<NaryNode> queue = new ArrayDeque<>();
    Map<NaryNode, Integer> depthMap = new HashMap<>();

    queue.add(start);
    depthMap.put(start, 0);

    while (!queue.isEmpty()) {
      NaryNode current = queue.poll();
      int depth = depthMap.get(current);
      visitor.accept(current, depth);

      for (NaryNode child : current.children) {
        if (!depthMap.containsKey(child)) {
          depthMap.put(child, depth + 1);
          queue.add(child);
        }
      }
    }
  }

  private static NaryNode createSampleTree() {
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
}
