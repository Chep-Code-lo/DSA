package org.example.bst.ui;
import org.example.bst.BST;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.*;
public class BSTPanel extends JPanel {
    private BST<Integer, String> bst;
    private java.util.List<Integer> searchPath = Collections.emptyList();
    private Integer foundKey = null; 
    private static class NodeDraw {
        int key;
        int depth;
        int ix;
        Integer left, right;
    }

    private java.util.List<NodeDraw> nodes = new ArrayList<>();
    private Map<Integer, NodeDraw> map = new HashMap<>();
    private int maxDepth = 0;

    public BSTPanel(BST<Integer,String> bst){
        setBackground(Color.WHITE);
        setOpaque(true);
        setDoubleBuffered(true);
        setToolTipText("");

        setBST(bst);        
    }

    public void setBST(BST<Integer, String> b) {
        this.bst = b;
        rebuildLayout();
        repaint();
    }

    public void setSearchResult(java.util.List<Integer> path, Integer found) {
        this.searchPath = (path == null ? Collections.emptyList() : path);
        this.foundKey = found;
        repaint();
    }

    private void rebuildLayout() {
        nodes.clear();
        map.clear();
        maxDepth = 0;
        var root = bst.snapshot();
        if (root == null) return;
        final int[] idx = {0};
        dfs(root, 0, idx);
    }

    private void dfs(BST.ViewNode<Integer> x, int depth, int[] idx) {
        if (x == null) return;
        dfs(x.left, depth + 1, idx);
        NodeDraw nd = new NodeDraw();
        nd.key = x.key;
        nd.depth = depth;
        nd.ix = idx[0]++;
        nd.left = (x.left == null ? null : x.left.key);
        nd.right = (x.right == null ? null : x.right.key);
        nodes.add(nd);
        map.put(nd.key, nd);
        maxDepth = Math.max(maxDepth, depth);
        dfs(x.right, depth + 1, idx);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        int margin = 30;
        float nodeR = 18f;
        int n = Math.max(1, nodes.size());
        float stepX = (n == 1) ? 0 : Math.max(50f, (w - 2f * margin) / (n - 1));
        float stepY = Math.max(70f, (h - 2f * margin) / Math.max(1, maxDepth));

        java.util.function.Function<Integer, Point> pt = k -> {
            NodeDraw nd = map.get(k);
            if (nd == null) return new Point(margin, margin);
            int x = Math.round(margin + nd.ix * stepX);
            int y = Math.round(margin + nd.depth * stepY);
            return new Point(x, y);
        };
        g2.setStroke(new BasicStroke(1.4f));
        g2.setColor(new Color(180, 180, 180));
        for (NodeDraw nd : nodes) {
            Point p = pt.apply(nd.key);
            if (nd.left != null) {
                Point c = pt.apply(nd.left);
                g2.drawLine(p.x, p.y, c.x, c.y);
            }
            if (nd.right != null) {
                Point c = pt.apply(nd.right);
                g2.drawLine(p.x, p.y, c.x, c.y);
            }
        }

        if (searchPath.size() >= 2) {
            g2.setStroke(new BasicStroke(3.2f));
            g2.setColor(new Color(255, 140, 0));
            for (int i = 0; i < searchPath.size() - 1; i++) {
                Point a = pt.apply(searchPath.get(i));
                Point b = pt.apply(searchPath.get(i + 1));
                g2.drawLine(a.x, a.y, b.x, b.y);
            }
        }

        FontMetrics fm = g2.getFontMetrics();
        for (NodeDraw nd : nodes) {
            Point p = pt.apply(nd.key);
            Shape circle = new Ellipse2D.Float(p.x - nodeR, p.y - nodeR, 2 * nodeR, 2 * nodeR);
            boolean isFound = (foundKey != null && nd.key == foundKey);
            g2.setColor(isFound ? new Color(56, 142, 60) : new Color(33, 150, 243));
            g2.fill(circle);
            g2.setColor(Color.WHITE);
            String s = String.valueOf(nd.key);
            int tw = fm.stringWidth(s);
            int th = fm.getAscent();
            g2.drawString(s, p.x - tw / 2, p.y + th / 3);
        }
        g2.dispose();
    }

    @Override
    public String getToolTipText(java.awt.event.MouseEvent event) {
        int w = getWidth(), h = getHeight();
        int margin = 30;
        float nodeR = 18f;
        int n = Math.max(1, nodes.size());
        float stepX = (n == 1) ? 0 : Math.max(50f, (w - 2f * margin) / (n - 1));
        float stepY = Math.max(70f, (h - 2f * margin) / Math.max(1, maxDepth));
        for (NodeDraw nd : nodes) {
            int x = Math.round(margin + nd.ix * stepX);
            int y = Math.round(margin + nd.depth * stepY);
            double dx = event.getX() - x, dy = event.getY() - y;
            if (dx * dx + dy * dy <= nodeR * nodeR) return "key=" + nd.key + ", depth=" + nd.depth;
        }
        return null;
    }
}
