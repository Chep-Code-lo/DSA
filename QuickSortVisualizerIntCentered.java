
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

/**
 * QuickSortVisualizerIntCentered
 * -------------------------------------------------
 * - Main algorithm: QuickSortInt (Hoare + median-of-three + insertion cut-off)
 * - Live bar chart with values CENTERED inside each bar
 * - Legend explaining colors
 * - Right-side "Step Details" panel (logs important steps)
 *
 * Build & Run (JDK 17+):
 *   javac QuickSortVisualizerIntCentered.java
 *   java QuickSortVisualizerIntCentered
 */
public class QuickSortVisualizerIntCentered extends JFrame {
    private final VisualPanel visualPanel;
    private final JTextArea stepArea;
    private final JButton startBtn;
    private final JButton shuffleBtn;
    private final JButton resetBtn;
    private final JSlider speedSlider;
    private final JSpinner sizeSpinner;

    public QuickSortVisualizerIntCentered() {
        super("QuickSort Visualizer – QuickSortInt (Centered Values)");

        // ------- left: drawing panel -------
        visualPanel = new VisualPanel();

        // ------- right: step log panel -------
        stepArea = new JTextArea(20, 28);
        stepArea.setEditable(false);
        stepArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane stepScroll = new JScrollPane(stepArea);
        stepScroll.setBorder(BorderFactory.createTitledBorder("Step Details"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, visualPanel, stepScroll);
        split.setResizeWeight(0.78); // bigger canvas
        split.setContinuousLayout(true);

        // ------- top controls -------
        startBtn = new JButton("Start");
        shuffleBtn = new JButton("Shuffle");
        resetBtn = new JButton("Reset");
        speedSlider = new JSlider(0, 120, 25);
        sizeSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 1000, 10));

        JPanel control = new JPanel(new GridBagLayout());
        control.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 8, 0, 8);
        c.gridy = 0; c.gridx = 0; control.add(new JLabel("Size:"), c);
        c.gridx++; control.add(sizeSpinner, c);
        c.gridx++; control.add(new JLabel("Speed (ms):"), c);
        c.gridx++; speedSlider.setMajorTickSpacing(30); speedSlider.setPaintTicks(true); control.add(speedSlider, c);
        c.gridx++; control.add(shuffleBtn, c);
        c.gridx++; control.add(resetBtn, c);
        c.gridx++; control.add(startBtn, c);

        // ------- bottom legend -------
        JPanel legend = buildLegendPanel();

        // ------- layout -------
        setLayout(new BorderLayout(0, 6));
        add(control, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(legend, BorderLayout.SOUTH);

        // ------- event wiring -------
        visualPanel.setLogger(this::appendStep);
        shuffleBtn.addActionListener(e -> {
            if (visualPanel.isRunning()) return;
            int n = (Integer) sizeSpinner.getValue();
            visualPanel.initArray(n);
            visualPanel.shuffle();
            clearSteps();
            appendStep("Shuffled array of size " + n);
        });
        resetBtn.addActionListener(e -> {
            if (visualPanel.isRunning()) return;
            visualPanel.resetSortedOrder();
            clearSteps();
            appendStep("Reset to sorted order 1..n");
        });
        startBtn.addActionListener((ActionEvent e) -> {
            if (!visualPanel.isRunning()) {
                startBtn.setText("Pause");
                visualPanel.setDelay(speedSlider.getValue());
                visualPanel.startSort();
                appendStep("Start QuickSortInt");
            } else {
                startBtn.setText("Start");
                visualPanel.togglePause();
                appendStep(visualPanel.isPaused() ? "Paused" : "Resumed");
            }
        });
        speedSlider.addChangeListener(e -> visualPanel.setDelay(speedSlider.getValue()));

        // defaults
        visualPanel.initArray((Integer) sizeSpinner.getValue());
        visualPanel.shuffle();
        appendStep("Ready. Press Start to visualize QuickSortInt.");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1240, 760);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildLegendPanel() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        legend.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0, new Color(220,220,220)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        legend.add(makeLegendItem(new Color(60, 160, 255), "Normal"));
        legend.add(makeLegendItem(new Color(255, 165, 0), "Pivot"));
        legend.add(makeLegendItem(new Color(51, 153, 255), "i pointer"));
        legend.add(makeLegendItem(new Color(255, 0, 255), "j pointer"));
        legend.add(makeLegendItem(Color.red, "Swapping"));
        legend.add(makeOutlineLegendItem(Color.cyan, "Bounds [lo..hi]"));
        legend.add(new JLabel("Value centered on each bar"));
        return legend;
    }

    private JComponent makeLegendItem(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(14, 14));
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        panel.add(swatch);
        panel.add(new JLabel(text));
        return panel;
    }
    private JComponent makeOutlineLegendItem(Color color, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JPanel swatch = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(color);
                g2.drawRect(1,1,getWidth()-3,getHeight()-3);
                g2.dispose();
            }
        };
        swatch.setPreferredSize(new Dimension(14, 14));
        swatch.setBackground(Color.white);
        swatch.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        panel.add(swatch);
        panel.add(new JLabel(text));
        return panel;
    }

    private void appendStep(String s) {
        stepArea.append(s + "\n");
        stepArea.setCaretPosition(stepArea.getDocument().getLength());
    }
    private void clearSteps() { stepArea.setText(""); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuickSortVisualizerIntCentered::new);
    }

    // --------------------------- Visual Panel ---------------------------
    static class VisualPanel extends JPanel implements Runnable {
        private int[] a = new int[0];
        private final Random rnd = new Random();
        private Thread worker;
        private volatile boolean running = false;
        private volatile boolean paused = false;
        private volatile int delay = 25;

        // Visualization markers
        private volatile int lo = -1, hi = -1;
        private volatile int i = -1, j = -1;
        private volatile int pivotIndex = -1;
        private volatile Integer pivotValue = null;
        private volatile int swappingA = -1, swappingB = -1;

        // Logger
        private java.util.function.Consumer<String> logger = s -> {};

        // Algorithm params
        private static final int INSERTION_CUTOFF = 16;

        VisualPanel() { setBackground(Color.black); }

        void setLogger(java.util.function.Consumer<String> logger) { this.logger = logger; }
        boolean isRunning() { return running; }
        boolean isPaused() { return paused; }
        void setDelay(int ms) { delay = Math.max(0, ms); }

        void initArray(int n) {
            a = new int[n];
            for (int k = 0; k < n; k++) a[k] = k + 1;
            clearMarkers();
            repaint();
        }

        void shuffle() {
            for (int k = a.length - 1; k > 0; k--) {
                int j = rnd.nextInt(k + 1);
                swapRaw(k, j, false);
            }
            clearMarkers();
            repaint();
        }

        void resetSortedOrder() {
            if (a.length == 0) return;
            for (int k = 0; k < a.length; k++) a[k] = k + 1;
            clearMarkers();
            repaint();
        }

        void clearMarkers() {
            lo = hi = i = j = -1;
            pivotIndex = -1;
            pivotValue = null;
            swappingA = swappingB = -1;
        }

        void startSort() {
            if (running) return;
            running = true;
            paused = false;
            worker = new Thread(this, "QuickSortInt-Worker");
            worker.start();
        }

        void togglePause() {
            if (!running) return;
            paused = !paused;
        }

        @Override
        public void run() {
            try {
                quicksort(0, a.length - 1);
            } finally {
                running = false;
                SwingUtilities.invokeLater(() -> {
                    Window w = SwingUtilities.getWindowAncestor(this);
                    if (w instanceof QuickSortVisualizerIntCentered frame) {
                        frame.startBtn.setText("Start");
                    }
                });
                clearMarkers();
                repaint();
                logger.accept("Finished.");
            }
        }

        // ------------------------- QuickSortInt core -------------------------
        private void quicksort(int left, int right) {
            while (left < right) {
                setBounds(left, right);
                if (right - left + 1 <= INSERTION_CUTOFF) {
                    logger.accept("InsertionSort for range [" + left + "," + right + "]");
                    insertionSort(left, right);
                    return;
                }
                int m = medianOfThree(left, (left + right) >>> 1, right);
                logger.accept("median-of-three -> index " + m + " value=" + a[m]);
                swap(left, m, true);
                pivotIndex = left;
                pivotValue = a[left];
                repaintAndSleep();

                int p = hoarePartition(left, right);
                logger.accept("Partition done, pivot=" + pivotValue + " -> split at " + p +
                        " => [" + left + "," + p + "] and [" + (p+1) + "," + right + "]");

                // Recurse smaller side first (tail-call elimination style)
                if (p - left < right - (p + 1)) {
                    quicksort(left, p);
                    left = p + 1;
                } else {
                    quicksort(p + 1, right);
                    right = p;
                }
            }
        }

        // Hoare: returns j s.t. [left..j] <= pivot <= [j+1..right]
        private int hoarePartition(int left, int right) {
            setBounds(left, right);
            pivotIndex = left;
            pivotValue = a[pivotIndex];
            repaintAndSleep();

            int ii = left - 1;
            int jj = right + 1;

            while (true) {
                do { ii++; setPointers(ii, jj); repaintAndSleep(); } while (a[ii] < pivotValue);
                do { jj--; setPointers(ii, jj); repaintAndSleep(); } while (a[jj] > pivotValue);

                setPointers(ii, jj);
                if (ii >= jj) {
                    repaintAndSleep();
                    return jj;
                }
                setSwap(ii, jj);
                logger.accept("swap a[" + ii + "]=" + a[ii] + " <-> a[" + jj + "]=" + a[jj]);
                swap(ii, jj, true);
                clearSwap();
                repaintAndSleep();
            }
        }

        private void insertionSort(int left, int right) {
            for (int x = left + 1; x <= right; x++) {
                int key = a[x];
                int y = x - 1;
                while (y >= left && a[y] > key) {
                    a[y + 1] = a[y];
                    y--;
                    setPointers(y, x);
                    repaintAndSleep();
                }
                a[y + 1] = key;
                repaintAndSleep();
            }
        }

        private int medianOfThree(int i, int j, int k) {
            int ai = a[i], aj = a[j], ak = a[k];
            int idx;
            if (ai < aj) {
                if (aj < ak) idx = j;      // ai < aj < ak
                else if (ai < ak) idx = k; // ai < ak <= aj
                else idx = i;              // ak <= ai < aj
            } else {
                if (ai < ak) idx = i;      // aj <= ai < ak
                else if (aj < ak) idx = k; // aj < ak <= ai
                else idx = j;              // ak <= aj <= ai
            }
            setHighlight(idx);
            repaintAndSleep();
            clearHighlight();
            return idx;
        }

        // ------------------------- Helpers -------------------------
        private void swapRaw(int x, int y, boolean repaint) {
            if (x == y) return;
            int t = a[x]; a[x] = a[y]; a[y] = t;
            if (repaint) repaint();
        }
        private void swap(int x, int y, boolean repaint) { swapRaw(x, y, repaint); }

        private void setHighlight(int idx) { pivotIndex = idx; pivotValue = a[idx]; }
        private void clearHighlight() { pivotIndex = -1; pivotValue = null; }

        private void setBounds(int l, int h) { lo = l; hi = h; repaintAndSleep(); }
        private void setPointers(int ii, int jj) { i = ii; j = jj; }
        private void setSwap(int x, int y) { swappingA = x; swappingB = y; }
        private void clearSwap() { swappingA = swappingB = -1; }

        private void repaintAndSleep() {
            repaint();
            sleep();
        }

        private void sleep() {
            try {
                while (paused) Thread.sleep(30);
                if (delay > 0) Thread.sleep(delay);
            } catch (InterruptedException ignored) {}
        }

        // ------------------------- Painting -------------------------
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (a == null || a.length == 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int n = a.length;
            double barW = Math.max(1.0, (double) w / n);

            // Bounds rectangle
            if (lo >= 0 && hi >= 0) {
                g2.setColor(Color.cyan);
                int x1 = (int) Math.floor(lo * barW);
                int x2 = (int) Math.ceil((hi + 1) * barW);
                g2.drawRect(x1, 0, Math.max(1, x2 - x1), h - 1);
            }

            // Bars
            Font font = getFont().deriveFont(Font.BOLD, Math.max(10f, (float) Math.min(16, barW)));
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();

            for (int idx = 0; idx < n; idx++) {
                int val = a[idx];
                int barH = (int) Math.round(((double) val / n) * (h - 50));
                int x = (int) Math.round(idx * barW);
                int y = h - barH - 8;

                Color color = new Color(60, 160, 255); // normal
                if (idx == pivotIndex) color = new Color(255, 165, 0); // pivot
                if (idx == i) color = new Color(51, 153, 255);         // i
                if (idx == j) color = new Color(255, 0, 255);          // j
                if (idx == swappingA || idx == swappingB) color = Color.red;

                g2.setColor(color);
                int bw = Math.max(1, (int) Math.ceil(barW) - 1);
                g2.fillRect(x, y, bw, barH);

                // Centered numeric label (horizontal + vertical)
                String text = String.valueOf(val);
                int tw = fm.stringWidth(text);
                int th = fm.getAscent();
                int tx = x + (bw - tw) / 2;
                int ty = y + (barH + th) / 2 - 2; // vertical center
                if (barH > th + 6) {
                    g2.setColor(Color.white);
                    g2.drawString(text, tx, ty);
                } else {
                    // if bar too small: put just above bar in light gray
                    g2.setColor(new Color(210, 210, 210));
                    g2.drawString(text, x + (bw - tw) / 2, y - 2);
                }
            }

            // Legend text line
            g2.setColor(Color.white);
            g2.drawString("Pivot: orange | i: blue | j: magenta | swap: red | bounds: cyan", 10, 20);

            g2.dispose();
        }
    }
}
