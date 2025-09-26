
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

/**
 * StraightSelectionVisualizer
 * Minh họa Selection Sort (lựa chọn trực tiếp) bằng Java Swing.
 * - Bars có hiển thị value.
 * - Tô nổi: vùng đã sắp xếp [0..i-1], j đang quét, minIdx đang giữ nhỏ nhất tạm thời.
 * - Animation khi hoán đổi a[i] <-> a[minIdx].
 * - Bảng info + log gọn trong cửa sổ.
 */
public class StraightSelectionVisualizer extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StraightSelectionVisualizer::new);
    }

    // Window size
    static final int WIDTH = 1100, HEIGHT = 680;

    private final VisualPanel canvas;
    private final JTextArea logArea = new JTextArea(12, 26);

    private final JLabel lblState = new JLabel("Trạng thái: ");
    private final JLabel lblIdx = new JLabel("i=?, j=?, minIdx=?");
    private final JLabel lblStats = new JLabel("So sánh: 0 | Đổi chỗ: 0");

    private final JButton btnStart = new JButton("Start");
    private final JButton btnPause = new JButton("Pause");
    private final JButton btnReset = new JButton("Reset");
    private final JSlider speedSlider = new JSlider(1, 200, 60); // FPS
    private final JComboBox<Integer> sizeBox = new JComboBox<>(new Integer[]{10, 16, 24, 32, 40, 100});

    public StraightSelectionVisualizer() {
        super("Straight Selection Sort Visualizer (bars + values + compact info/log)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        // Top controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Size:"));
        controls.add(sizeBox);
        controls.add(new JLabel("Speed (FPS):"));
        speedSlider.setPreferredSize(new Dimension(160, 24));
        controls.add(speedSlider);
        controls.add(btnStart);
        controls.add(btnPause);
        controls.add(btnReset);

        // Right info/log
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(300, HEIGHT));
        JPanel info = new JPanel(new GridLayout(0,1,4,4));
        info.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        info.add(lblState);
        info.add(lblIdx);
        info.add(lblStats);

        logArea.setEditable(false);
        logArea.setBackground(new Color(24, 26, 33));
        logArea.setForeground(new Color(222, 226, 235));
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Thông báo"));

        right.add(info, BorderLayout.NORTH);
        right.add(scroll, BorderLayout.CENTER);

        // Center canvas
        canvas = new VisualPanel(this::log, this::updateInfo);

        add(controls, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        // Actions
        btnStart.addActionListener(e -> canvas.start());
        btnPause.addActionListener(e -> canvas.pause());
        btnReset.addActionListener(e -> canvas.reset((Integer) sizeBox.getSelectedItem()));
        speedSlider.addChangeListener(e -> canvas.setFps(speedSlider.getValue()));

        // Init
        canvas.reset((Integer) sizeBox.getSelectedItem());
        setVisible(true);
    }

    private void log(String s) {
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        // giữ khoảng 300 dòng
        int maxLines = 300;
        if (logArea.getLineCount() > maxLines) {
            try {
                int end = logArea.getLineEndOffset(logArea.getLineCount() - maxLines);
                logArea.replaceRange("", 0, end);
            } catch (Exception ignored) {}
        }
    }

    private void updateInfo(boolean sorted, boolean swapping, int i, int j, int minIdx, long comps, long swaps) {
        String st = sorted ? "ĐÃ SẮP XẾP ✓" : (swapping ? "ĐANG ĐỔI CHỖ" : "ĐANG QUÉT TÌM MIN");
        lblState.setText("Trạng thái: " + st);
        lblIdx.setText("i=" + i + ", j=" + (j >= 0 ? j : "—") + ", minIdx=" + (minIdx >= 0 ? minIdx : "—"));
        lblStats.setText("So sánh: " + comps + " | Đổi chỗ: " + swaps);
    }

    // ================= Canvas & algorithm =================
    static class VisualPanel extends JPanel {
        interface Logger { void log(String msg); }
        interface UiUpdater { void update(boolean sorted, boolean swapping, int i, int j, int minIdx, long comps, long swaps); }

        private final Logger logger;
        private final UiUpdater uiUpdater;

        // Data
        private int[] a;
        private Bar[] bars;
        private int n;
        private final Random rnd = new Random();

        // Layout
        private int marginLeft = 40, marginBottom = 64, topPad = 56;
        private int barW, gapX = 4, maxValue = 300;

        // Selection sort state
        private int i;         // biên trái vùng chưa sắp xếp
        private int j;         // đang quét [i+1..n-1]
        private int minIdx;    // vị trí nhỏ nhất tạm thời trên lượt i
        private boolean sorted;

        // Stats
        private long comparisons = 0, swaps = 0;

        // Animation (swap a[i] <-> a[minIdx])
        private boolean swapping = false;
        private int idxA = -1, idxB = -1;
        private double t = 0.0;
        private final double SWAP_DURATION = 0.30; // seconds

        // Timer
        private Timer timer;
        private int fps = 60;
        private double dt = 1.0 / fps;

        VisualPanel(Logger logger, UiUpdater updater) {
            this.logger = logger;
            this.uiUpdater = updater;
            setBackground(new Color(18, 20, 26));
            setOpaque(true);
            setPreferredSize(new Dimension(WIDTH - 300, HEIGHT));
            setFont(getFont().deriveFont(Font.BOLD, 14f));
            setFps(fps);
        }

        void setFps(int newFps) {
            fps = Math.max(1, newFps);
            dt = 1.0 / fps;
            if (timer != null) {
                timer.stop();
                timer = new Timer((int)(1000.0 / fps), this::onTick);
                timer.start();
            } else {
                timer = new Timer((int)(1000.0 / fps), this::onTick);
            }
        }

        void start() {
            if (sorted) return;
            if (timer == null) setFps(fps);
            timer.start();
            logger.log("▶️ Start");
        }

        void pause() {
            if (timer != null) timer.stop();
            logger.log("⏸️ Pause");
        }

        void reset(int size) {
            pause();
            n = size;
            a = new int[n];
            for (int k = 0; k < n; k++) {
                a[k] = 20 + rnd.nextInt(maxValue - 20);
            }
            bars = new Bar[n];
            computeBarW();
            for (int k = 0; k < n; k++) {
                bars[k] = new Bar(ix(k), vh(a[k]));
            }
            // init selection sort
            i = 0;
            j = (n > 0 ? 1 : -1);
            minIdx = (n > 0 ? 0 : -1);
            sorted = (n <= 1);
            comparisons = swaps = 0;
            swapping = false; idxA = idxB = -1; t = 0.0;

            logger.log("Reset mảng " + n + " phần tử.");
            updateUi();
            repaint();
        }

        private void computeBarW() {
            int drawableW = getWidth() == 0 ? WIDTH - 300 : getWidth();
            int usable = drawableW - marginLeft * 2;
            barW = Math.max(6, (usable - (n - 1) * gapX) / Math.max(1, n));
        }

        private int ix(int idx) {
            return marginLeft + idx * (barW + gapX);
        }

        private int vh(int val) {
            int drawableH = getHeight() == 0 ? HEIGHT : getHeight();
            int h = drawableH - marginBottom - topPad;
            return (int) Math.round((val / (double) maxValue) * h);
        }

        private void onTick(ActionEvent e) {
            if (sorted) {
                timer.stop();
                updateUi();
                repaint();
                return;
            }

            if (swapping) {
                t += dt / SWAP_DURATION;
                if (t >= 1.0) {
                    t = 1.0;
                    swapping = false;

                    // commit swap in data
                    int tmp = a[idxA]; a[idxA] = a[idxB]; a[idxB] = tmp;

                    // reset bars geometry to canonical
                    bars[idxA].x = ix(idxA);
                    bars[idxA].h = vh(a[idxA]);
                    bars[idxB].x = ix(idxB);
                    bars[idxB].h = vh(a[idxB]);

                    swaps++;
                    updateUi();

                    // kết thúc lượt i -> sang lượt mới
                    i++;
                    if (i >= n - 1) {
                        sorted = true;
                        updateUi();
                    } else {
                        minIdx = i;
                        j = i + 1;
                    }
                } else {
                    animateSwap();
                }
                repaint();
                return;
            }

            // ===== Selection sort step (scan j, update min, then swap when j > n-1) =====
            if (i < n - 1) {
                if (j <= n - 1) {
                    if (j == -1) j = i + 1;
                    if (j <= n - 1) {
                        comparisons++;
                        if (a[j] < a[minIdx]) {
                            minIdx = j;
                            logger.log(String.format("  cập nhật minIdx=%d (val=%d)", minIdx, a[minIdx]));
                        }
                        j++;
                        updateUi();
                    }
                } else {
                    // hết lượt quét -> nếu minIdx != i thì swap, ngược lại chuyển vòng luôn
                    if (minIdx != i) {
                        idxA = i; idxB = minIdx;
                        logger.log(String.format("Đổi chỗ i=%d(val=%d) ↔ minIdx=%d(val=%d)", i, a[i], minIdx, a[minIdx]));
                        startSwap();
                    } else {
                        logger.log(String.format("i=%d đã đúng vị trí (val=%d)", i, a[i]));
                        i++;
                        if (i >= n - 1) {
                            sorted = true;
                        } else {
                            minIdx = i;
                            j = i + 1;
                        }
                        updateUi();
                    }
                }
            } else {
                sorted = true;
                updateUi();
            }

            repaint();
        }

        private void startSwap() {
            swapping = true;
            t = 0.0;
        }

        private void animateSwap() {
            int xA0 = ix(idxA), xB0 = ix(idxB), xA1 = ix(idxB), xB1 = ix(idxA);
            int baseY = getHeight() - marginBottom;
            int hA = vh(a[idxA]), hB = vh(a[idxB]);
            int ctrlA = (xA0 + xA1) / 2, ctrlB = (xB0 + xB1) / 2;
            int lift = 24;
            double u = 1.0 - t;

            int xA = (int)(u*u*xA0 + 2*u*t*ctrlA + t*t*xA1);
            int xB = (int)(u*u*xB0 + 2*u*t*ctrlB + t*t*xB1);
            int topA = baseY - hA - (int)(lift * Math.sin(Math.PI * t));
            int topB = baseY - hB + (int)(lift * Math.sin(Math.PI * t));

            bars[idxA].x = xA;
            bars[idxA].h = baseY - topA;
            bars[idxB].x = xB;
            bars[idxB].h = baseY - topB;
        }

        private void updateUi() {
            uiUpdater.update(sorted, swapping, i, j, minIdx, comparisons, swaps);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            computeBarW();

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Title
            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD, 18f));
            g2.drawString("Straight Selection Sort • bars có value • highlight i/j/minIdx", 16, 30);

            int baseY = getHeight() - marginBottom;
            // background strip
            g2.setColor(new Color(45, 49, 60));
            g2.fillRoundRect(marginLeft - 12, topPad, getWidth() - 2 * (marginLeft - 12),
                    baseY - topPad + 12, 16, 16);

            // Draw bars
            for (int k = 0; k < n; k++) {
                boolean inSortedPrefix = (k < i);              // đã cố định ở đầu
                boolean isJ = (k == j - 1 && j >= 1);          // cặp so sánh vừa xong (j-1)
                boolean isMin = (k == minIdx);

                Color fill;
                if (isMin) fill = new Color(76, 175, 80);                      // minIdx (xanh lá)
                else if (isJ) fill = new Color(255, 193, 7);                   // vừa so sánh (vàng)
                else if (inSortedPrefix) fill = new Color(97, 218, 251, 220);  // đã cố định (xanh dương)
                else fill = new Color(157, 170, 185, 170);                     // còn lại

                drawBarWithValue(g2, bars[k], fill, baseY, a[k]);
            }

            // Legend
            int yLeg = getHeight() - 26;
            drawLegend(g2, 16,  yLeg, new Color(97, 218, 251, 220), "Đã cố định [0..i-1]");
            drawLegend(g2, 240, yLeg, new Color(255, 193, 7), "Vừa so sánh (j-1)");
            drawLegend(g2, 430, yLeg, new Color(76, 175, 80), "minIdx hiện tại");

            g2.dispose();
        }

        private void drawBarWithValue(Graphics2D g2, Bar b, Color fill, int baseY, int value) {
            // shadow
            g2.setColor(new Color(0,0,0,70));
            g2.fillRoundRect(b.x + 3, baseY - b.h + 6, barW, b.h, 8, 8);

            // bar
            g2.setColor(fill);
            g2.fillRoundRect(b.x, baseY - b.h, barW, b.h, 10, 10);

            // border
            g2.setColor(new Color(30,32,38));
            g2.setStroke(new BasicStroke(1.3f));
            g2.drawRoundRect(b.x, baseY - b.h, barW, b.h, 10, 10);

            // value text (tự chọn màu dễ đọc)
            String s = String.valueOf(value);
            Font font = getFont().deriveFont(Font.BOLD, Math.max(11f, Math.min(16f, barW * 0.6f)));
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(s), th = fm.getAscent();

            int cx = b.x + barW/2 - tw/2;
            int cy = baseY - b.h + b.h/2 + th/2 - 2;

            // text color dựa trên độ sáng fill
            double lum = fill.getRed()*0.299 + fill.getGreen()*0.587 + fill.getBlue()*0.114;
            Color textColor = lum > 160 ? new Color(20,22,28) : Color.WHITE;

            // outline nhẹ
            g2.setColor(new Color(0,0,0,120));
            g2.drawString(s, cx+1, cy+1);
            g2.setColor(textColor);
            g2.drawString(s, cx, cy);
        }

        private void drawLegend(Graphics2D g2, int x, int y, Color c, String label) {
            g2.setColor(c);
            g2.fillRect(x, y - 12, 18, 12);
            g2.setColor(Color.WHITE);
            g2.drawString(label, x + 26, y - 2);
        }

        static class Bar {
            int x, h;
            Bar(int x, int h) { this.x = x; this.h = h; }
        }
    }
}
