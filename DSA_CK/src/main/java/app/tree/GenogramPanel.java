package app.tree;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.*;
import javax.swing.*;

/** Vẽ sơ đồ genogram bằng JGraphX */
public class GenogramPanel extends JPanel {
  private NaryNode root;

  public GenogramPanel() {
    setLayout(new BorderLayout());
  }

  public void setRoot(NaryNode r) {
    this.root = r;
    rebuild();
  }

  private void rebuild() {
    removeAll();
    if (root == null) {
      add(new JLabel("(Chưa có dữ liệu)"), BorderLayout.CENTER);
      revalidate();
      repaint();
      return;
    }
    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();
    graph.getModel().beginUpdate();
    try {
      java.util.Map<NaryNode, Object> map = new java.util.HashMap<>();
      build(graph, parent, root, map);
      new mxHierarchicalLayout(graph).execute(parent);
    } finally {
      graph.getModel().endUpdate();
    }
    mxGraphComponent comp = new mxGraphComponent(graph);
    add(comp, BorderLayout.CENTER);
    revalidate();
    repaint();
  }

  private void build(
      mxGraph graph, Object parent, NaryNode node, java.util.Map<NaryNode, Object> map) {
    Object v = graph.insertVertex(parent, node.id, node.name, 20, 20, 120, 30);
    map.put(node, v);
    for (NaryNode c : node.children) {
      build(graph, parent, c, map);
      graph.insertEdge(parent, null, "", v, map.get(c));
    }
  }
}
