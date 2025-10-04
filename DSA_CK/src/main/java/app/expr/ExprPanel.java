package app.expr;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class ExprPanel extends JPanel {
  private final JTextArea input = new JTextArea(3, 60);
  private final JTextArea postfix = new JTextArea(3, 30);
  private final JTextArea prefix = new JTextArea(3, 30);
  private final JTextArea log = new JTextArea(5, 60);
  private final JTextField vars = new JTextField("");

  // Theme colors
  private static final Color BG_COLOR = new Color(240, 242, 245);
  private static final Color ACCENT_COLOR = new Color(79, 70, 229);
  private static final Color INPUT_BG = new Color(255, 255, 255);
  private static final Color OUTPUT_BG = new Color(249, 250, 251);
  private static final Color LOG_BG = new Color(254, 252, 245);
  private static final Color BORDER_COLOR = new Color(209, 213, 219);

  public ExprPanel() {
    setLayout(new BorderLayout(15, 15));
    setBackground(BG_COLOR);
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Input section
    JPanel inputPanel = createRoundedPanel();
    inputPanel.setLayout(new BorderLayout(10, 10));
    inputPanel.setBorder(createTitledBorder("📝 Biểu thức trung tố (Infix)", ACCENT_COLOR));
    configureTextArea(input, INPUT_BG, true);
    inputPanel.add(new JScrollPane(input), BorderLayout.CENTER);

    // Output section - vertical layout (top/bottom)
    JPanel outputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    outputPanel.setOpaque(false);

    JPanel postfixPanel = createRoundedPanel();
    postfixPanel.setLayout(new BorderLayout());
    postfixPanel.setBorder(createTitledBorder("⬅️ Hậu tố (Postfix)", new Color(16, 185, 129)));
    configureTextArea(postfix, OUTPUT_BG, false);
    postfixPanel.add(new JScrollPane(postfix), BorderLayout.CENTER);

    JPanel prefixPanel = createRoundedPanel();
    prefixPanel.setLayout(new BorderLayout());
    prefixPanel.setBorder(createTitledBorder("➡️ Tiền tố (Prefix)", new Color(245, 158, 11)));
    configureTextArea(prefix, OUTPUT_BG, false);
    prefixPanel.add(new JScrollPane(prefix), BorderLayout.CENTER);

    outputPanel.add(postfixPanel);
    outputPanel.add(prefixPanel);

    // Center content - input on left, output on right
    JPanel centerContent = new JPanel(new BorderLayout(10, 0));
    centerContent.setOpaque(false);
    centerContent.add(inputPanel, BorderLayout.CENTER);
    centerContent.add(outputPanel, BorderLayout.EAST);

    // Action panel
    JPanel actionPanel = createRoundedPanel();
    actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
    actionPanel.setBackground(Color.WHITE);
    
    JLabel varsLabel = new JLabel("🔤 Biến:");
    varsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    varsLabel.setForeground(new Color(55, 65, 81));
    
    vars.setColumns(25);
    vars.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    vars.setBorder(BorderFactory.createCompoundBorder(
        new RoundBorder(BORDER_COLOR, 8),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));
    vars.setToolTipText("Ví dụ: a=1; b=2; c=3");

    JButton btnConvert = createStyledButton("🔄 Chuyển đổi", ACCENT_COLOR);
    JButton btnEval = createStyledButton("🧮 Tính giá trị", new Color(16, 185, 129));

    actionPanel.add(varsLabel);
    actionPanel.add(vars);
    actionPanel.add(btnConvert);
    actionPanel.add(btnEval);

    // Log panel
    JPanel logPanel = createRoundedPanel();
    logPanel.setLayout(new BorderLayout());
    logPanel.setBorder(createTitledBorder("📋 Nhật ký", new Color(239, 68, 68)));
    configureTextArea(log, LOG_BG, false);
    log.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    logPanel.add(new JScrollPane(log), BorderLayout.CENTER);

    add(actionPanel, BorderLayout.NORTH);
    add(centerContent, BorderLayout.CENTER);
    add(logPanel, BorderLayout.SOUTH);

    btnConvert.addActionListener(e -> doConvert());
    btnEval.addActionListener(e -> doEval());
  }

  private JPanel createRoundedPanel() {
    JPanel panel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
      }
    };
    panel.setOpaque(false);
    return panel;
  }

  private TitledBorder createTitledBorder(String title, Color color) {
    TitledBorder border = BorderFactory.createTitledBorder(
        new RoundBorder(color, 10),
        title,
        TitledBorder.LEFT,
        TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 16),
        color
    );
    border.setTitlePosition(TitledBorder.ABOVE_TOP);
    return border;
  }

  private void configureTextArea(JTextArea area, Color bg, boolean editable) {
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setMargin(new Insets(10, 12, 10, 12));
    area.setBackground(bg);
    area.setEditable(editable);
    area.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    area.setCaretColor(ACCENT_COLOR);
    area.setSelectionColor(new Color(219, 234, 254));
    area.setSelectedTextColor(Color.DARK_GRAY);
    area.setBorder(null);
  }

  private JButton createStyledButton(String text, Color color) {
    JButton button = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isPressed()) {
          g2.setColor(color.darker());
        } else if (getModel().isRollover()) {
          g2.setColor(color.brighter());
        } else {
          g2.setColor(color);
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2.dispose();
        super.paintComponent(g);
      }
    };
    button.setForeground(Color.WHITE);
    button.setFont(new Font("Segoe UI", Font.BOLD, 16));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  private static class RoundBorder extends AbstractBorder {
    private final Color color;
    private final int radius;

    public RoundBorder(Color color, int radius) {
      this.color = color;
      this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(color);
      g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
      g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
      return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
    }
  }

  private Map<String, Double> parseVars() {
    HashMap<String, Double> map = new HashMap<>();
    String s = vars.getText().trim();
    if (s.isEmpty()) return map;
    for (String pair : s.split("[; ,]+")) {
      if (pair.isEmpty()) continue;
      String[] kv = pair.split("=");
      if (kv.length == 2) {
        try {
          map.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
        } catch (NumberFormatException ex) {
          log.append("⚠️ Cảnh báo: Biến không phải số: " + pair + "\n");
        }
      }
    }
    return map;
  }

  private void doConvert() {
    try {
      List<String> tokens = ExprUtils.tokenize(input.getText());
      List<String> post = ExprUtils.toPostfix(tokens);
      List<String> pre = ExprUtils.toPrefix(tokens);
      postfix.setText(String.join(" ", post));
      prefix.setText(String.join(" ", pre));
      log.append("✅ Đã chuyển đổi thành công.\n");
    } catch (Exception ex) {
      log.append("❌ Lỗi: " + ex.getMessage() + "\n");
    }
  }

  private void doEval() {
    try {
      List<String> tokens = ExprUtils.tokenize(input.getText());
      List<String> post = ExprUtils.toPostfix(tokens);
      Map<String, Double> env = parseVars();
      double val = ExprUtils.evalPostfix(post, v -> env.getOrDefault(v, 0.0));
      log.append("➤ Giá trị = " + val + "\n");
    } catch (Exception ex) {
      log.append("❌ Lỗi: " + ex.getMessage() + "\n");
    }
  }
}
