package org.example.bst.ui;

import org.example.bst.BST;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
public class BSTFrame extends JFrame {
    private BST<Integer, String> bst = new BST<>();
    private final BSTPanel canvas = new BSTPanel(bst);

    private final JTextField tfKey = new JTextField();
    private final JTextField tfVal = new JTextField();
    private final JTextArea taOutput = new JTextArea(7, 30);
    private final JLabel lbStatus = new JLabel("Ready");

    public BSTFrame() {
        super("BST Swing Demo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(
                canvas,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        sp.getViewport().setBackground(Color.WHITE); 
        add(sp, BorderLayout.CENTER);

        add(buildRightPanel(), BorderLayout.EAST);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }


    private JComponent buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(new EmptyBorder(6, 8, 6, 8));
        JButton btRandom = new JButton("Sinh ngẫu nhiên");
        btRandom.addActionListener(e -> doRandom());
        JButton btClear = new JButton("Xoá sạch");
        btClear.addActionListener(e -> {
            bst = new BST<>();
            canvas.setBST(bst);
            setStatus("Đã xoá cây");
        });
        JButton btBalance = new JButton("Cân bằng từ inorder");
        btBalance.addActionListener(e -> rebalanceFromInorder());
        JButton btExport = new JButton("Xuất PNG...");
        btExport.addActionListener(e -> exportPng());
        p.add(btRandom);
        p.add(btClear);
        p.add(btBalance);
        p.add(btExport);
        return p;
    }

    private JComponent buildRightPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Khoá (số nguyên):"));
        form.add(tfKey);
        form.add(new JLabel("Giá trị (text):"));
        form.add(tfVal);
        JButton btPut = new JButton("➕ put/insert");
        btPut.addActionListener(e -> doPut());
        JButton btDel = new JButton("🗑 delete");
        btDel.addActionListener(e -> doDelete());
        JButton btFind = new JButton("🔍 search");
        btFind.addActionListener(e -> doSearch());
        JButton btMinMax = new JButton("min/max");
        btMinMax.addActionListener(e -> doMinMax());
        JButton btFloorCeil = new JButton("floor/ceil");
        btFloorCeil.addActionListener(e -> doFloorCeil());
        form.add(btPut);
        form.add(btDel);
        form.add(btFind);
        form.add(btMinMax);
        form.add(btFloorCeil);
        JPanel trav = new JPanel(new GridLayout(0, 1, 4, 4));
        JButton btIn = new JButton("In-order");
        btIn.addActionListener(e -> printTraversal("In-order", bst.keysInOrder()));
        JButton btPre = new JButton("Pre-order");
        btPre.addActionListener(e -> printTraversal("Pre-order", bst.keysPreOrder()));
        JButton btPost = new JButton("Post-order");
        btPost.addActionListener(e -> printTraversal("Post-order", bst.keysPostOrder()));
        JButton btBfs = new JButton("Level-order");
        btBfs.addActionListener(e -> printTraversal("Level-order", bst.keysLevelOrder()));
        trav.add(btIn);
        trav.add(btPre);
        trav.add(btPost);
        trav.add(btBfs);
        taOutput.setEditable(false);
        taOutput.setLineWrap(true);
        taOutput.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(taOutput);
        sp.setPreferredSize(new Dimension(360, 260));

        JButton btValidate = new JButton("Kiểm tra isBST");
        btValidate.addActionListener(e -> setStatus("isBST = " + bst.isBST() + ", size=" + bst.size() + ", height=" + bst.height()));

        root.add(new JLabel("Thao tác"));
        root.add(Box.createVerticalStrut(6));
        root.add(form);
        root.add(Box.createVerticalStrut(10));
        root.add(new JSeparator());
        root.add(Box.createVerticalStrut(10));
        root.add(new JLabel("Duyệt cây"));
        root.add(trav);
        root.add(Box.createVerticalStrut(8));
        root.add(sp);
        root.add(Box.createVerticalStrut(8));
        root.add(btValidate);
        return root;
    }

    private JComponent buildStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(6, 8, 6, 8));
        p.add(lbStatus, BorderLayout.WEST);
        return p;
    }

    private void doPut() {
        Integer k = parseKey();
        if (k == null) return;
        String v = tfVal.getText();
        bst.put(k, v);
        canvas.setBST(bst);
        canvas.setSearchResult(Collections.emptyList(), null);
        setStatus("put(" + k + ") OK — size=" + bst.size());
    }

    private void doDelete() {
        Integer k = parseKey();
        if (k == null) return;
        var old = bst.remove(k);
        canvas.setBST(bst);
        setStatus(old == null ? "Không có khoá " + k : "Xoá " + k + " OK");
    }

    private void doSearch() {
        Integer k = parseKey();
        if (k == null) return;
        var path = bst.searchPath(k);
        boolean ok = bst.containsKey(k);
        canvas.setSearchResult(path, ok ? k : null);
        setStatus("search(" + k + ") " + (ok ? "tìm thấy" : "không thấy") + ", bước đi: " + path);
    }

    private void doMinMax() {
        if (bst.isEmpty()) {
            setStatus("Cây rỗng");
            return;
        }
        setStatus("min=" + bst.minKey() + ", max=" + bst.maxKey());
    }

    private void doFloorCeil() {
        Integer k = parseKey();
        if (k == null) return;
        setStatus("floor(" + k + ")=" + bst.floor(k) + ", ceil(" + k + ")=" + bst.ceil(k));
    }

    private void printTraversal(String name, Iterable<Integer> it) {
        String s = join(it);
        taOutput.setText(name + ":\n" + s);
    }

    private Integer parseKey() {
        try {
            String s = tfKey.getText().trim();
            if (s.isEmpty()) {
                setStatus("Nhập khoá trước");
                return null;
            }
            return Integer.parseInt(s);
        } catch (Exception e) {
            setStatus("Khoá phải là số nguyên");
            return null;
        }
    }

    private static String join(Iterable<?> it) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object o : it) {
            if (!first) sb.append(", ");
            sb.append(o);
            first = false;
        }
        return sb.toString();
    }

    private void doRandom() {
        String nStr = JOptionPane.showInputDialog(this, "Số phần tử?", "20");
        if (nStr == null) return;
        int n;
        try {
            n = Integer.parseInt(nStr.trim());
        } catch (Exception e) {
            setStatus("Số không hợp lệ");
            return;
        }
        Random rnd = new Random();
        bst = new BST<>();
        for (int i = 0; i < n; i++) {
            int k = rnd.nextInt(999);
            bst.put(k, "v" + k);
        }
        canvas.setBST(bst);
        canvas.setSearchResult(Collections.emptyList(), null);
        setStatus("Sinh ngẫu nhiên " + n + " nút — height=" + bst.height());
    }

    private void rebalanceFromInorder() {
        java.util.List<Integer> keys = new ArrayList<>();
        for (Integer k : bst.keysInOrder()) keys.add(k);
        java.util.List<String> vals = new ArrayList<>();
        for (Integer k : keys) vals.add(bst.get(k));
        bst = new BST<>();
        buildBalanced(keys, vals, 0, keys.size() - 1);
        canvas.setBST(bst);
        setStatus("Đã cân bằng từ inorder — height=" + bst.height());
    }

    private void buildBalanced(java.util.List<Integer> keys, java.util.List<String> vals, int l, int r) {
        if (l > r) return;
        int m = (l + r) / 2;
        bst.put(keys.get(m), vals.get(m));
        buildBalanced(keys, vals, l, m - 1);
        buildBalanced(keys, vals, m + 1, r);
    }

    private void exportPng() {
        try {
            Rectangle rect = canvas.getBounds();
            BufferedImage img = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            canvas.printAll(g2);
            g2.dispose();
            File f = new File("bst_panel.png");
            ImageIO.write(img, "png", f);
            setStatus("Đã xuất " + f.getAbsolutePath());
        } catch (Exception ex) {
            setStatus("Lỗi xuất PNG: " + ex.getMessage());
        }
    }

    private void setStatus(String s) {
        lbStatus.setText(s);
    }
}
