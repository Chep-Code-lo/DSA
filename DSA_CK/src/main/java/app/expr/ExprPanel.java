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
    private final StackVisualizer stackViz = new StackVisualizer();
    private List<String> lastTokens = new ArrayList<>();
    private int animationDelay = 300; // ms per step
    private volatile boolean isPaused = false;
    private SwingWorker<Void, StackVisualizer.Frame> animationWorker = null;
    private JButton btnPause;
    private JButton btnStop;
    private JLabel statusLabel;
    // Theme colors
    private static final Color BG_COLOR = new Color(240, 242, 245);
    private static final Color ACCENT_COLOR = new Color(79, 70, 229);
    private static final Color INPUT_BG = new Color(255, 255, 255);
    private static final Color OUTPUT_BG = new Color(249, 250, 251);
    private static final Color LOG_BG = new Color(254, 252, 245);
    private static final Color BORDER_COLOR = new Color(209, 213, 219);
    // Font
    private static final String FONT_FAMILY = "SansSerif";

    public ExprPanel() {
    // Khởi tạo leftPanel chứa khung trực quan hóa
    JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
    leftPanel.setOpaque(false);
    JPanel vizContainer = createRoundedPanel();
    vizContainer.setLayout(new BorderLayout());
    vizContainer.setBorder(createTitledBorder("🎬 Trực quan hóa Stack", new Color(147, 51, 234)));
    vizContainer.add(stackViz, BorderLayout.CENTER);
    vizContainer.setPreferredSize(new Dimension(0, 50));
    leftPanel.add(vizContainer, BorderLayout.CENTER);
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input section
        JPanel inputPanel = createRoundedPanel();
        inputPanel.setLayout(new BorderLayout(10, 10));
        inputPanel.setBorder(createTitledBorder("📝 Biểu thức trung tố (Infix)", ACCENT_COLOR));
        configureTextArea(input, INPUT_BG, true);
        inputPanel.add(new JScrollPane(input), BorderLayout.CENTER);

        // 3 khung bên cạnh khung trực quan hóa với tiêu đề rõ ràng
        JPanel infixPanel = createRoundedPanel();
        infixPanel.setLayout(new BorderLayout());
        infixPanel.setBorder(createTitledBorder("📝 Biểu thức trung tố (Infix)", ACCENT_COLOR));
        configureTextArea(input, INPUT_BG, true);
        infixPanel.add(new JScrollPane(input), BorderLayout.CENTER);

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

        JPanel topRowRight = new JPanel(new GridLayout(1, 3, 10, 10));
        topRowRight.setOpaque(false);
        topRowRight.add(infixPanel);
        topRowRight.add(postfixPanel);
        topRowRight.add(prefixPanel);
    // Tăng chiều cao cho 3 khung trên
    infixPanel.setPreferredSize(new Dimension(0, 180));
    postfixPanel.setPreferredSize(new Dimension(0, 180));
    prefixPanel.setPreferredSize(new Dimension(0, 180));
    topRowRight.setPreferredSize(new Dimension(0, 180));
    // Tăng chiều cao cho 3 khung trên
    infixPanel.setPreferredSize(new Dimension(0, 180));
    postfixPanel.setPreferredSize(new Dimension(0, 180));
    prefixPanel.setPreferredSize(new Dimension(0, 180));
    topRowRight.setPreferredSize(new Dimension(0, 180));

        // Khung dưới chia làm 2 phần: Các bước chuyển đổi và Lịch sử
        JPanel stepsPanel = createRoundedPanel();
        stepsPanel.setLayout(new BorderLayout());
        stepsPanel.setBorder(createTitledBorder("🔄 Các bước chuyển đổi", new Color(59, 130, 246)));
        JTextArea stepsArea = new JTextArea(5, 30);
        stepsArea.setEditable(false);
        stepsArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        stepsArea.setBackground(new Color(235, 245, 255));
        stepsPanel.add(new JScrollPane(stepsArea), BorderLayout.CENTER);

        JPanel historyPanel = createRoundedPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setBorder(createTitledBorder("📋 Lịch sử", new Color(239, 68, 68)));
        configureTextArea(log, LOG_BG, false);
        historyPanel.add(new JScrollPane(log), BorderLayout.CENTER);

        JPanel bottomRowRight = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomRowRight.setOpaque(false);
        bottomRowRight.add(stepsPanel);
        bottomRowRight.add(historyPanel);
    // Giảm chiều cao cho 2 khung dưới
    stepsPanel.setPreferredSize(new Dimension(0, 70));
    historyPanel.setPreferredSize(new Dimension(0, 70));
    bottomRowRight.setPreferredSize(new Dimension(0, 70));
    // Giảm chiều cao cho 2 khung dưới
    stepsPanel.setPreferredSize(new Dimension(0, 70));
    historyPanel.setPreferredSize(new Dimension(0, 70));
    bottomRowRight.setPreferredSize(new Dimension(0, 70));

        JPanel rightPanel = new JPanel(new BorderLayout(10,10));
        rightPanel.setOpaque(false);
        rightPanel.add(topRowRight, BorderLayout.NORTH);
        rightPanel.add(bottomRowRight, BorderLayout.CENTER);

        // Action panel
        JPanel actionPanel = createRoundedPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
        actionPanel.setBackground(Color.WHITE);

        JLabel varsLabel = new JLabel("🔤 Biến (a = 1, b = 2, ...):");
        varsLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        varsLabel.setForeground(new Color(55, 65, 81));

    vars.setColumns(12); // Giảm chiều rộng khung điền biến
    vars.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
    vars.setBorder(BorderFactory.createCompoundBorder(
        new RoundBorder(BORDER_COLOR, 8),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));
    vars.setToolTipText("Ví dụ: a=1; b=2; c=3");

    JButton btnConvert = createStyledButton("🔄 Chuyển đổi", ACCENT_COLOR);
    JButton btnEval = createStyledButton("📊 Tính giá trị", new Color(16, 185, 129));
    btnPause = createStyledButton("⏸️ Tạm dừng", new Color(245, 158, 11));
    btnPause.setEnabled(false);
    btnStop = createStyledButton("⏹️ Dừng", new Color(220, 38, 38));
    btnStop.setEnabled(false);
    JButton btnReset = createStyledButton("🔁 Reset", new Color(59, 130, 246));
    statusLabel = new JLabel("Trạng thái: Idle");
    statusLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
    statusLabel.setForeground(new Color(55, 65, 81));

        // Ô điền tốc độ
        JTextField speedField = new JTextField(String.valueOf(animationDelay), 5);
        speedField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        speedField.setToolTipText("Nhập tốc độ (ms, ví dụ: 300)");
        speedField.setHorizontalAlignment(JTextField.CENTER);
        speedField.addActionListener(e -> {
            try {
                int val = Integer.parseInt(speedField.getText().trim());
                animationDelay = Math.max(50, Math.min(2000, val));
                speedField.setText(String.valueOf(animationDelay));
            } catch (NumberFormatException ex) {
                speedField.setText(String.valueOf(animationDelay));
            }
        });

        // Sắp xếp lại các nút thao tác cho gọn
        actionPanel.add(varsLabel);
        actionPanel.add(vars);
        actionPanel.add(btnConvert);
        actionPanel.add(btnEval);
        actionPanel.add(btnPause);
        actionPanel.add(btnStop);
        actionPanel.add(btnReset);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(new JLabel("Tốc độ (ms):"));
        actionPanel.add(speedField);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(statusLabel);

    // Xử lý nút reset
    btnReset.addActionListener(e -> {
        input.setText("");
        postfix.setText("");
        prefix.setText("");
        log.setText("");
        vars.setText("");
        stackViz.clear();
        statusLabel.setText("Trạng thái: Idle");
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
    });

    // Log panel (main right component) - reduce its height so top fields have room
    JPanel logPanel = createRoundedPanel();
    logPanel.setLayout(new BorderLayout());
    logPanel.setBorder(createTitledBorder("📋 Nhật ký", new Color(239, 68, 68)));
    logPanel.setPreferredSize(new Dimension(0, 120));
    configureTextArea(log, LOG_BG, false);
    log.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
    logPanel.add(new JScrollPane(log), BorderLayout.CENTER);

    // Put left (inputs + visualizer) and right (top fields + log) into a horizontal split
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    split.setLeftComponent(leftPanel);
    split.setRightComponent(rightPanel);
    split.setResizeWeight(0.35);
    split.setDividerSize(6);
    split.setContinuousLayout(true);

    add(actionPanel, BorderLayout.NORTH);
    add(split, BorderLayout.CENTER);

        btnConvert.addActionListener(e -> doConvert());
        btnEval.addActionListener(e -> doEval());
        btnPause.addActionListener(e -> {
            if (animationWorker == null) return;
            isPaused = !isPaused;
            btnPause.setText(isPaused ? "▶️ Tiếp tục" : "⏸️ Tạm dừng");
            SwingUtilities.invokeLater(() -> statusLabel.setText(isPaused ? "Trạng thái: Paused" : "Trạng thái: Running"));
            if (!isPaused) {
                synchronized (animationWorker) { animationWorker.notifyAll(); }
            }
        });

        btnStop.addActionListener(e -> {
            if (animationWorker != null && !animationWorker.isDone()) {
                animationWorker.cancel(true);
                stackViz.clear();
                postfix.setText("");
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Trạng thái: Idle");
                    btnPause.setEnabled(false);
                    btnStop.setEnabled(false);
                });
            }
        });
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
                new Font(FONT_FAMILY, Font.BOLD, 16),
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
        area.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
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
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
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
            // Khi bắt đầu trực quan hóa, xóa nội dung hậu tố và tiền tố
            postfix.setText("");
            prefix.setText("");
            log.append("✅ Đã chuyển đổi thành công.\n");
            lastTokens = tokens;
            animatePostfix(tokens, post, pre);
        } catch (Exception ex) {
            log.append("❌ Lỗi: " + ex.getMessage() + "\n");
        }
    }

    // Animate postfix conversion steps on stack visualizer
    private void animatePostfix(List<String> tokens, List<String> post, List<String> pre) {
        // If an animation is already running, cancel it
        if (animationWorker != null && !animationWorker.isDone()) {
            animationWorker.cancel(true);
        }
        // reset paused state and UI
        isPaused = false;
        if (btnPause != null) {
            btnPause.setText("⏸️ Tạm dừng");
            btnPause.setEnabled(false);
        }
        stackViz.clear();

        // Use ExprUtils to get detailed steps then publish frames
        String originalInfix = vars.getText().trim();
        ExprUtils.ConversionResult cr = ExprUtils.toPostfixWithSteps(tokens);
        String finalPostfix = String.join(" ", cr.result);
        
        animationWorker = new SwingWorker<Void, StackVisualizer.Frame>() {
            @Override
            protected Void doInBackground() throws Exception {
                SwingUtilities.invokeLater(() -> {
                    btnPause.setEnabled(true);
                    btnStop.setEnabled(true);
                    statusLabel.setText("Trạng thái: Running");
                });
                try {
                    for (ExprUtils.ConversionStep step : cr.steps) {
                        if (isCancelled()) break;
                        List<String> stackState = step.stackState;
                        String token = step.token == null ? "" : step.token;
                        String action = step.action == null ? "" : step.action.toLowerCase();

                        StackVisualizer.FrameAction fa;
                        if (action.contains("push")) fa = StackVisualizer.FrameAction.PUSH_STACK;
                        else if (action.contains("pop")) fa = StackVisualizer.FrameAction.POP_STACK;
                        else if (action.contains("emit") || action.contains("output")) fa = StackVisualizer.FrameAction.TO_OUTPUT;
                        else fa = StackVisualizer.FrameAction.PUSH_TOKEN;

                        String currentOutput = String.join(" ", step.outputState);
                        publish(stackViz.new Frame(token, stackState, fa, currentOutput));

                        // update postfix display progressively
                        SwingUtilities.invokeLater(() -> postfix.setText(currentOutput));

                        // sleep in small chunks and respect pause
                        int delay = Math.max(80, animationDelay);
                        int slept = 0;
                        while (slept < delay) {
                            if (isCancelled()) break;
                            if (isPaused) {
                                synchronized (animationWorker) {
                                    animationWorker.wait(200);
                                }
                                continue;
                            }
                            int chunk = Math.min(120, delay - slept);
                            Thread.sleep(chunk);
                            slept += chunk;
                        }
                    }

                    // show final snapshot
                    List<String> finalStack = cr.steps.isEmpty() ? List.of() : cr.steps.get(cr.steps.size() - 1).stackState;
                    String finalOutput = String.join(" ", cr.result);
                    publish(stackViz.new Frame("", finalStack, StackVisualizer.FrameAction.FINAL, finalOutput));
                    // Sau animation, hiển thị kết quả hậu tố và tiền tố
                    SwingUtilities.invokeLater(() -> {
                        postfix.setText(String.join(" ", post));
                        prefix.setText(String.join(" ", pre));
                    });
                } catch (InterruptedException ignored) {
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        btnPause.setEnabled(false);
                        btnStop.setEnabled(false);
                        statusLabel.setText("Trạng thái: Idle");
                    });
                }
                return null;
            }

            @Override
            protected void process(List<StackVisualizer.Frame> chunks) {
                for (StackVisualizer.Frame f : chunks) stackViz.showFrame(f);
            }

            @Override
            protected void done() {
                animationWorker = null;
                isPaused = false;
                SwingUtilities.invokeLater(() -> {
                    btnPause.setEnabled(false);
                    btnStop.setEnabled(false);
                    statusLabel.setText("Trạng thái: Idle");
                });
            }
        };
        animationWorker.execute();
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