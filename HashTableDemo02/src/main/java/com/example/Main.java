package com.example;
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Demo UI:
 * - Put/Get/Remove
 * - Put random 10: luôn sinh đủ 10 key duy nhất; highlight bucket nào bị collision
 * - Bucket collision cũng được highlight khi bấm Put
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::buildUI);
    }

    private static void buildUI() {
        var table = new HashTableChaining<Integer, String>(7); // bắt đầu 7 bucket
        var view  = new HashTableView(table);

        JFrame f = new JFrame("Hash Table (Chaining) — Division Method + Rehash + Swing Visualization");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(1200, 600);
        f.setLocationRelativeTo(null);

        // Panel điều khiển
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        var tfKey = new JTextField();
        var tfVal = new JTextField();
        var btnPut = new JButton("Put");
        var btnGet = new JButton("Get");
        var btnRemove = new JButton("Remove");
        var btnRandom = new JButton("Put random 10 (unique)");
        var btnClear = new JButton("Clear");
        var lbInfo = new JLabel(" ");

        c.gridx=0; c.gridy=0; controls.add(new JLabel("Key (Integer):"), c);
        c.gridx=1; c.gridy=0; controls.add(tfKey, c);
        c.gridx=0; c.gridy=1; controls.add(new JLabel("Value (String):"), c);
        c.gridx=1; c.gridy=1; controls.add(tfVal, c);

        c.gridx=0; c.gridy=2; controls.add(btnPut, c);
        c.gridx=1; c.gridy=2; controls.add(btnGet, c);
        c.gridx=0; c.gridy=3; controls.add(btnRemove, c);
        c.gridx=1; c.gridy=3; controls.add(btnRandom, c);
        c.gridx=0; c.gridy=4; c.gridwidth=2; controls.add(btnClear, c);
        c.gridx=0; c.gridy=5; c.gridwidth=2; controls.add(lbInfo, c);

        // Sự kiện
        btnPut.addActionListener(e -> {
            try {
                Integer k = Integer.valueOf(tfKey.getText().trim());
                String v = tfVal.getText();
                // dùng putWithInfo để biết collision + bucket
                var info = table.putWithInfo(k, v);
                if (info.updated) {
                    lbInfo.setText("Updated key " + k + " at bucket " + info.bucketIndex);
                } else {
                    lbInfo.setText("Inserted key " + k + " at bucket " + info.bucketIndex +
                            (info.collided ? " (collision)" : ""));
                }
                // nếu có collision -> flash bucket
                if (info.collided) {
                    Set<Integer> col = new HashSet<>();
                    col.add(info.bucketIndex);
                    view.flashCollisions(col);
                } else {
                    view.repaint();
                }
            } catch (Exception ex) {
                lbInfo.setText("Key phải là số nguyên hợp lệ");
            }
        });

        btnGet.addActionListener(e -> {
            try {
                Integer k = Integer.valueOf(tfKey.getText().trim());
                var v = table.get(k);
                lbInfo.setText(v == null ? "Not found" : "Found: " + v + " (bucket " + table.bucketIndexFor(k) + ")");
            } catch (Exception ex) {
                lbInfo.setText("Key phải là số nguyên hợp lệ");
            }
        });

        btnRemove.addActionListener(e -> {
            try {
                Integer k = Integer.valueOf(tfKey.getText().trim());
                var v = table.remove(k);
                lbInfo.setText(v == null ? "Not found" : "Removed value=" + v);
                view.repaint();
            } catch (Exception ex) {
                lbInfo.setText("Key phải là số nguyên hợp lệ");
            }
        });

        // Random 10 KEY DUY NHẤT + highlight mọi bucket có collision
        btnRandom.addActionListener(e -> {
            Random rnd = new Random();
            int added = 0;
            Set<Integer> collided = new HashSet<>();

            while (added < 10) {
                int k = rnd.nextInt(200);           // key [0..199]
                if (table.get(k) == null) {         // chỉ thêm khi chưa tồn tại
                    String v = "S" + k;
                    var info = table.putWithInfo(k, v);
                    if (!info.updated) {            // (sẽ luôn là insert mới)
                        added++;
                        if (info.collided) collided.add(info.bucketIndex);
                    }
                }
            }

            lbInfo.setText("Đã insert ngẫu nhiên 10 phần tử (unique); collisions at " + collided);
            // flash các bucket có collision (nếu có); nếu không, chỉ repaint
            if (collided.isEmpty()) view.repaint(); else view.flashCollisions(collided);
        });

        btnClear.addActionListener(e -> {
            table.clear();
            lbInfo.setText("Cleared");
            view.repaint();
        });

        // Khu vực vẽ có scroll
        JScrollPane scroll = new JScrollPane(view);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.getHorizontalScrollBar().setUnitIncrement(24);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controls, scroll);
        split.setDividerLocation(330);
        split.setResizeWeight(0);

        f.setContentPane(split);
        f.setVisible(true);
    }
}
