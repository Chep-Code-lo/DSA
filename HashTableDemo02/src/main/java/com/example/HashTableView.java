package com.example;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Vẽ trực quan HashTable (Chaining):
 * - Bucket có collision gần nhất sẽ được tô màu cam nhạt ~1.2 giây (flash)
 */
public class HashTableView extends JPanel {
    private HashTableChaining<Integer, String> table;

    // Tham số vẽ
    private int bucketHeight = 38;
    private int bucketWidth  = 90;
    private int nodeWidth    = 130;
    private int nodeHeight   = 30;
    private int gapX         = 12;
    private int gapY         = 6;
    private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

    // Các bucket đang cần highlight (collision)
    private final Set<Integer> collidedBuckets = new HashSet<>();

    public HashTableView(HashTableChaining<Integer, String> table) {
        this.table = table;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 600));
    }

    public void setTable(HashTableChaining<Integer, String> t) {
        this.table = t;
        revalidate();
        repaint();
    }

    /** Gọi để flash highlight các bucket có collision ~5 giây */
    public void flashCollisions(Set<Integer> bucketIdxs) {
        collidedBuckets.clear();
        collidedBuckets.addAll(bucketIdxs);
        repaint();

        Timer timer = new Timer(5000, e -> {
            collidedBuckets.clear();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (table == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<List<Map.Entry<Integer, String>>> snap = table.bucketsSnapshot();
        int m = snap.size();

        // Kích thước cần thiết (để scroll)
        int neededHeight = m * (bucketHeight + gapY) + 50;
        int maxNodes = 0;
        for (var row : snap) maxNodes = Math.max(maxNodes, row.size());
        int neededWidth = bucketWidth + 40 + (nodeWidth + gapX) * Math.max(1, maxNodes) + 40;
        setPreferredSize(new Dimension(Math.max(neededWidth, getWidth()), Math.max(neededHeight, getHeight())));

        // Tiêu đề
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Hash Table (Chaining) — capacity=" + table.capacity() +
                ", size=" + table.size() +
                ", loadFactor=" + String.format("%.2f", table.loadFactor()), 12, 18);

        int y = 40;
        for (int i = 0; i < m; i++) {
            int xBucket = 12;

            // Bucket màu tuỳ theo có collision cần highlight hay không
            boolean highlight = collidedBuckets.contains(i);
            Color fill = highlight ? new Color(255, 220, 180) : new Color(230, 230, 230);
            Color border = highlight ? new Color(200, 120, 60) : Color.GRAY;

            g2.setColor(fill);
            g2.fillRoundRect(xBucket, y, bucketWidth, bucketHeight, 10, 10);
            g2.setColor(border);
            g2.drawRoundRect(xBucket, y, bucketWidth, bucketHeight, 10, 10);

            g2.setColor(Color.BLACK);
            g2.drawString("bucket " + i, xBucket + 10, y + 24);

            // Vẽ danh sách entry
            int xNode = xBucket + bucketWidth + 30;
            var row = snap.get(i);
            for (int j = 0; j < row.size(); j++) {
                var e = row.get(j);

                // Node
                g2.setColor(new Color(200, 230, 255));
                g2.fillRoundRect(xNode, y + 4, nodeWidth, nodeHeight, 10, 10);
                g2.setColor(new Color(80, 120, 180));
                g2.drawRoundRect(xNode, y + 4, nodeWidth, nodeHeight, 10, 10);

                // Text
                g2.setColor(Color.BLACK);
                String txt = e.getKey() + " : " + e.getValue();
                g2.drawString(txt, xNode + 10, y + 22);

                // Mũi tên sang node kế
                if (j < row.size() - 1) {
                    int ax = xNode + nodeWidth;
                    int ay = y + nodeHeight / 2 + 4;
                    int bx = xNode + nodeWidth + gapX - 4;
                    int by = ay;
                    g2.setColor(new Color(100, 100, 100));
                    g2.drawLine(ax, ay, bx, by);
                    g2.drawLine(bx, by, bx - 6, by - 4);
                    g2.drawLine(bx, by, bx - 6, by + 4);
                }

                xNode += nodeWidth + gapX;
            }

            y += bucketHeight + gapY;
        }

        g2.dispose();
    }
}
