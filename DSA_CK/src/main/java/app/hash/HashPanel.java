package app.hash;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class HashPanel extends JPanel {
  // Components
  private final JComboBox<String> method = new JComboBox<>(new String[] { "Linear Probing", "Chaining" });
  private final JTextField sizeField = new JTextField("20", 5);
  private final JTextField keyField = new JTextField(6);
  private final JTextField valField = new JTextField(10);
  private final JTextArea view = new JTextArea(20, 64);
  private final JTextArea log = new JTextArea(8, 64);
  private HashTable<Integer, String> table;

  // Theme colors
  private static final Color BG = new Color(240, 242, 245);
  private static final Color CARD = Color.WHITE;
  private static final Color BORDER = new Color(209, 213, 219);
  private static final Color PRIMARY = new Color(59, 130, 246);
  private static final Color ACCENT = new Color(16, 185, 129);
  private static final Color DANGER = new Color(239, 68, 68);
  private static final Color PURPLE = new Color(168, 85, 247);

  private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
  private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 10);
  private static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 13);

  public HashPanel() {
    setLayout(new GridLayout(1, 2, 12, 12));
    setBackground(BG);
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    add(createLeftPanel());
    add(createRightPanel());

    initTable();
  }

  private JPanel createLeftPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    panel.add(createSetupPanel());
    panel.add(Box.createVerticalStrut(10));
    panel.add(createOperationPanel());
    panel.add(Box.createVerticalStrut(10));
    panel.add(createLogPanel());

    return panel;
  }

  private JPanel createSetupPanel() {
    JPanel panel = createRoundedPanel("Thiết lập");
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));

    panel.add(createLabel("Phương pháp:"));
    method.setPreferredSize(new Dimension(110, 24));
    method.setFont(LABEL_FONT);
    panel.add(method);

    panel.add(createLabel("Kích thước m:"));
    sizeField.setPreferredSize(new Dimension(50, 24));
    sizeField.setFont(LABEL_FONT);
    panel.add(sizeField);

    JButton btnInit = createButton("Khởi tạo", PRIMARY, e -> initTable());
    btnInit.setPreferredSize(new Dimension(80, 24));
    panel.add(btnInit);

    return panel;
  }

  private JPanel createOperationPanel() {
    JPanel panel = createRoundedPanel("Thao tác");
    panel.setLayout(new GridLayout(2, 1, 4, 4));

    JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
    row1.setOpaque(false);
    row1.add(createLabel("Key:"));
    keyField.setPreferredSize(new Dimension(60, 24));
    keyField.setFont(LABEL_FONT);
    row1.add(keyField);
    row1.add(createLabel("Value:"));
    valField.setPreferredSize(new Dimension(100, 24));
    valField.setFont(LABEL_FONT);
    row1.add(valField);

    JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
    row2.setOpaque(false);
    row2.add(createButton("Thêm/Sửa", ACCENT, e -> doPut()));
    row2.add(createButton("Lấy", PRIMARY, e -> doGet()));
    row2.add(createButton("Xóa", DANGER, e -> doRemove()));
    row2.add(createButton("Trace", PURPLE, e -> doTraceGet()));

    panel.add(row1);
    panel.add(row2);

    return panel;
  }

  private JPanel createLogPanel() {
    JPanel panel = createRoundedPanel("Nhật ký / Trace");
    panel.setLayout(new BorderLayout());
    log.setFont(MONO_FONT);
    log.setEditable(false);
    log.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    panel.add(new JScrollPane(log), BorderLayout.CENTER);
    return panel;
  }

  private JPanel createRightPanel() {
    JPanel panel = createRoundedPanel("Bảng băm");
    panel.setLayout(new BorderLayout());
    view.setFont(MONO_FONT);
    view.setEditable(false);
    view.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    panel.add(new JScrollPane(view), BorderLayout.CENTER);
    return panel;
  }

  private JLabel createLabel(String text) {
    JLabel label = new JLabel(text);
    label.setFont(LABEL_FONT);
    return label;
  }

  private JButton createButton(String text, Color bg, java.awt.event.ActionListener action) {
    JButton button = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        g2.dispose();
        super.paintComponent(g);
      }
    };
    button.setFont(BUTTON_FONT);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setOpaque(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(90, 24));
    button.addActionListener(action);
    return button;
  }

  private JPanel createRoundedPanel(String title) {
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.dispose();
      }
    };
    panel.setBackground(CARD);
    panel.setOpaque(false);
    panel.setBorder(createTitledBorder(title));
    return panel;
  }

  private TitledBorder createTitledBorder(String title) {
    return BorderFactory.createTitledBorder(
      BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(6, 6, 6, 6),
        BorderFactory.createLineBorder(BORDER, 1, true)
      ),
      title,
      TitledBorder.LEFT,
      TitledBorder.TOP,
      new Font("Segoe UI", Font.BOLD, 11),
      new Color(55, 65, 81)
    );
  }

  private void initTable() {
    try {
      int m = Integer.parseInt(sizeField.getText().trim());
      if (m <= 0) throw new NumberFormatException();
      table = "Linear Probing".equals(method.getSelectedItem()) 
        ? new LinearProbingHashTable<>(m) 
        : new ChainingHashTable<>(m);
      log.setText("");
      refresh();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Kích thước m phải là số nguyên dương");
    }
  }

  private void doPut() {
    executeOperation(() -> {
      Integer k = Integer.parseInt(keyField.getText().trim());
      String v = valField.getText();
      table.put(k, v);
      log.append("PUT key=" + k + ", value=" + v + "\n");
    });
  }

  private void doGet() {
    executeOperation(() -> {
      Integer k = Integer.parseInt(keyField.getText().trim());
      String v = table.get(k);
      log.append("GET key=" + k + " -> " + (v == null ? "NOT FOUND" : v) + "\n");
    });
  }

  private void doRemove() {
    executeOperation(() -> {
      Integer k = Integer.parseInt(keyField.getText().trim());
      boolean ok = table.remove(k);
      log.append("REMOVE key=" + k + " -> " + (ok ? "OK" : "NOT FOUND") + "\n");
    });
  }

  private void doTraceGet() {
    try {
      Integer k = Integer.parseInt(keyField.getText().trim());
      String trace = table.traceGet(k);
      JTextArea ta = new JTextArea(trace, 18, 60);
      ta.setEditable(false);
      ta.setFont(MONO_FONT);
      JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Trace tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
      log.append("TRACE key=" + k + " (xem chi tiết ở hộp thoại)\n");
    } catch (Exception ex) {
      showError();
    }
  }

  private void executeOperation(Runnable operation) {
    try {
      operation.run();
      refresh();
    } catch (Exception ex) {
      showError();
    }
  }

  private void showError() {
    JOptionPane.showMessageDialog(this, "Nhập key là số nguyên (int).");
  }

  private void refresh() {
    view.setText("capacity=" + table.capacity() + ", size=" + table.size() + "\n\n" + table.debugView());
  }
}
