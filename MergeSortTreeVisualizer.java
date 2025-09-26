
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MergeSortTreeVisualizer
 * ---------------------------------------
 * Visualizes Merge Sort as a split/merge tree (like the reference image).
 * - Each node shows its subarray in a rounded box.
 * - Arrows show the flow of split and merge.
 * - Controls: Back, Next, Play/Pause, Shuffle, Reset, Speed.
 *
 * Build & Run (JDK 17+):
 *   javac MergeSortTreeVisualizer.java
 *   java MergeSortTreeVisualizer
 */
public class MergeSortTreeVisualizer extends JFrame {
    private final TreePanel treePanel;
    private final JTextArea stepArea;
    private final JButton backBtn;
    private final JButton nextBtn;
    private final JButton playBtn;
    private final JButton shuffleBtn;
    private final JButton resetBtn;
    private final JSlider speedSlider;
    private final JSpinner sizeSpinner;

    public MergeSortTreeVisualizer() {
        super("Merge Sort – Tree Visualizer");

        treePanel = new TreePanel();

        stepArea = new JTextArea(18, 28);
        stepArea.setEditable(false);
        stepArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane stepScroll = new JScrollPane(stepArea);
        stepScroll.setBorder(BorderFactory.createTitledBorder("Step Details"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, stepScroll);
        split.setResizeWeight(0.74);
        split.setContinuousLayout(true);

        backBtn = new JButton("Back");
        nextBtn = new JButton("Next");
        playBtn = new JButton("Play");
        shuffleBtn = new JButton("Shuffle");
        resetBtn = new JButton("Reset");
        speedSlider = new JSlider(0, 160, 60);
        sizeSpinner = new JSpinner(new SpinnerNumberModel(8, 4, 16, 2));

        JPanel control = new JPanel(new GridBagLayout());
        control.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 8, 0, 8);
        c.gridy = 0; c.gridx = 0; control.add(new JLabel("N (power of 2):"), c);
        c.gridx++; control.add(sizeSpinner, c);
        c.gridx++; control.add(new JLabel("Speed (ms):"), c);
        c.gridx++; speedSlider.setMajorTickSpacing(40); speedSlider.setPaintTicks(true); control.add(speedSlider, c);
        c.gridx++; control.add(shuffleBtn, c);
        c.gridx++; control.add(resetBtn, c);
        c.gridx++; control.add(backBtn, c);
        c.gridx++; control.add(nextBtn, c);
        c.gridx++; control.add(playBtn, c);

        setLayout(new BorderLayout(0,6));
        add(control, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Wiring
        treePanel.setLogger(this::appendStepEDT);
        shuffleBtn.addActionListener(e -> {
            int n = (Integer) sizeSpinner.getValue();
            treePanel.initArray(n);
            treePanel.shuffle();
            treePanel.rebuildTimeline();
            clearStepsEDT();
            appendStepEDT("Shuffled array of size " + n);
        });
        resetBtn.addActionListener(e -> {
            int n = (Integer) sizeSpinner.getValue();
            treePanel.initArray(n);
            treePanel.resetSortedOrder();
            treePanel.rebuildTimeline();
            clearStepsEDT();
            appendStepEDT("Reset to sorted order 1..n");
        });
        backBtn.addActionListener(e -> treePanel.prevStep());
        nextBtn.addActionListener(e -> treePanel.nextStep());
        playBtn.addActionListener((ActionEvent e) -> {
            if (!treePanel.isPlaying()) {
                playBtn.setText("Pause");
                treePanel.setDelay(speedSlider.getValue());
                treePanel.play();
            } else {
                playBtn.setText("Play");
                treePanel.pause();
            }
        });
        speedSlider.addChangeListener(e -> treePanel.setDelay(speedSlider.getValue()));

        // Defaults
        treePanel.initArray((Integer) sizeSpinner.getValue());
        treePanel.shuffle();
        treePanel.rebuildTimeline();
        appendStepEDT("Ready. Press Play or step with Next/Back.");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void appendStepEDT(String s) {
        SwingUtilities.invokeLater(() -> {
            stepArea.append(s + "\n");
            stepArea.setCaretPosition(stepArea.getDocument().getLength());
        });
    }
    private void clearStepsEDT() {
        SwingUtilities.invokeLater(() -> stepArea.setText(""));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MergeSortTreeVisualizer::new);
    }

    // ---------------------------- Tree Panel ----------------------------
    static class TreePanel extends JPanel implements Runnable {
        private int[] a = new int[0];
        private final Random rnd = new Random();

        private List<State> timeline = new ArrayList<>();
        private int stepIndex = 0;

        private Thread player;
        private volatile boolean playing = false;
        private volatile int delay = 60;

        private java.util.function.Consumer<String> logger = s->{};

        TreePanel() { setBackground(new Color(245,245,245)); }

        void setLogger(java.util.function.Consumer<String> logger) { this.logger = logger; }
        boolean isPlaying() { return playing; }
        void setDelay(int ms) { delay = Math.max(0, ms); }

        void initArray(int n) {
            a = new int[n];
            for (int i = 0; i < n; i++) a[i] = i + 1;
        }
        void shuffle() {
            for (int k = a.length - 1; k > 0; k--) {
                int j = rnd.nextInt(k + 1);
                int t = a[k]; a[k] = a[j]; a[j] = t;
            }
        }
        void resetSortedOrder() {
            for (int i = 0; i < a.length; i++) a[i] = i + 1;
        }

        void rebuildTimeline() {
            timeline.clear();
            // Build split states (top-down)
            buildSplitStates(0, a.length - 1, 0);
            // Build merge states (bottom-up with actual merging)
            int[] work = a.clone();
            buildMergeStates(work, 0, a.length - 1, 0);
            stepIndex = 0;
            repaint();
        }

        private void buildSplitStates(int lo, int hi, int depth) {
            int[] slice = java.util.Arrays.copyOfRange(a, lo, hi + 1);
            timeline.add(State.split(slice, lo, hi, depth));
            if (lo >= hi) return;
            int mid = (lo + hi) >>> 1;
            buildSplitStates(lo, mid, depth + 1);
            buildSplitStates(mid + 1, hi, depth + 1);
        }

        private void buildMergeStates(int[] arr, int lo, int hi, int depth) {
            if (lo >= hi) return;
            int mid = (lo + hi) >>> 1;
            buildMergeStates(arr, lo, mid, depth + 1);
            buildMergeStates(arr, mid + 1, hi, depth + 1);
            // merge while emitting states
            int[] aux = java.util.Arrays.copyOfRange(arr, lo, hi + 1);
            int i = 0, j = mid - lo + 1, k = lo;
            while (i <= mid - lo && j <= hi - lo) {
                if (aux[i] <= aux[j]) arr[k++] = aux[i++];
                else arr[k++] = aux[j++];
                timeline.add(State.merge(java.util.Arrays.copyOfRange(arr, lo, hi + 1), lo, hi, depth));
            }
            while (i <= mid - lo) {
                arr[k++] = aux[i++];
                timeline.add(State.merge(java.util.Arrays.copyOfRange(arr, lo, hi + 1), lo, hi, depth));
            }
            while (j <= hi - lo) {
                arr[k++] = aux[j++];
                timeline.add(State.merge(java.util.Arrays.copyOfRange(arr, lo, hi + 1), lo, hi, depth));
            }
        }

        void nextStep() {
            if (timeline.isEmpty()) return;
            stepIndex = Math.min(stepIndex + 1, timeline.size() - 1);
            State s = timeline.get(stepIndex);
            logger.accept(s.describe());
            repaint();
        }
        void prevStep() {
            if (timeline.isEmpty()) return;
            stepIndex = Math.max(stepIndex - 1, 0);
            State s = timeline.get(stepIndex);
            logger.accept(s.describe());
            repaint();
        }
        void play() {
            if (playing) return;
            playing = true;
            player = new Thread(this, "Player");
            player.start();
        }
        void pause() { playing = false; }

        @Override
        public void run() {
            logger.accept("Playing...");
            while (playing) {
                nextStep();
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {}
                if (stepIndex == timeline.size() - 1) playing = false;
            }
            logger.accept("Stopped.");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (timeline.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            State s = timeline.get(stepIndex);

            // compute layout
            int h = getHeight();
            int w = getWidth();
            int maxDepth = (int) Math.ceil(Math.log(a.length) / Math.log(2));
            int levelHeight = Math.max(80, h / (maxDepth + 1));

            // background label (giống bố cục hình mẫu)
            g2.setColor(new Color(30, 30, 30));
            g2.setFont(getFont().deriveFont(Font.BOLD, 28f));
            g2.drawString("Merge Sort", 20, 40);
            g2.setFont(getFont().deriveFont(Font.PLAIN, 14f));
            g2.setColor(new Color(34, 139, 34));
            g2.fillRoundRect(20, 52, 105, 22, 8, 8);
            g2.setColor(Color.white);
            g2.drawString("Algorithm", 30, 68);

            // vẽ snapshot cây đến thời điểm step hiện tại
            drawTreeSnapshot(g2, stepIndex, levelHeight);

            g2.dispose();
        }

        private void drawTreeSnapshot(Graphics2D g2, int idx, int levelHeight) {
            State s = timeline.get(idx);
            // lấy tất cả các state SPLIT để tạo nodes của cây
            List<Node> nodes = new ArrayList<>();
            for (State st : timeline) {
                if (st.type == State.Type.SPLIT) {
                    nodes.add(new Node(st.lo, st.hi, st.depth, st.values));
                }
            }
            nodes = dedupe(nodes);

            int maxDepth = 0;
            for (Node n : nodes) maxDepth = Math.max(maxDepth, n.depth);
            int w = getWidth();

            // sắp theo depth rồi theo vị trí lo để layout ngang
            List<Node> ordered = orderByDepth(nodes, maxDepth);
            for (Node n : ordered) {
                Point p = positionFor(n, w, levelHeight, maxDepth);
                n.cx = p.x; n.cy = p.y;
            }

            // vẽ mũi tên cha-con
            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(Color.darkGray);
            for (Node n : ordered) {
                if (n.lo < n.hi) {
                    int mid = (n.lo + n.hi) >>> 1;
                    Node left = findNode(ordered, n.lo, mid, n.depth + 1);
                    Node right = findNode(ordered, mid + 1, n.hi, n.depth + 1);
                    if (left != null) drawArrow(g2, n.cx, n.cy + 18, left.cx, left.cy - 18);
                    if (right != null) drawArrow(g2, n.cx, n.cy + 18, right.cx, right.cy - 18);
                }
            }

            // vẽ các hộp mảng con; đoạn hiện hành được highlight
            for (Node n : ordered) {
                boolean highlight = (s.lo == n.lo && s.hi == n.hi);
                drawArrayBox(g2, n, highlight ? new Color(230, 250, 255) : Color.white, highlight);
            }

            // nếu state hiện tại là MERGE: vẽ hộp kết quả (nhấn mạnh) tại chính cấp của nó
            if (s.type == State.Type.MERGE) {
                Node seg = new Node(s.lo, s.hi, s.depth, s.values);
                Point p = positionFor(seg, w, levelHeight, maxDepth);
                seg.cx = p.x; seg.cy = p.y;
                drawArrayBox(g2, seg, new Color(240, 255, 240), true);
            }
        }

        private static class State {
            enum Type { SPLIT, MERGE }
            final Type type;
            final int[] values; // snapshot of this segment
            final int lo, hi;
            final int depth;
            private State(Type t, int[] v, int lo, int hi, int depth) {
                this.type = t; this.values = v; this.lo = lo; this.hi = hi; this.depth = depth;
            }
            static State split(int[] v, int lo, int hi, int depth) { return new State(Type.SPLIT, v, lo, hi, depth); }
            static State merge(int[] v, int lo, int hi, int depth) { return new State(Type.MERGE, v, lo, hi, depth); }
            String describe() {
                StringBuilder sb = new StringBuilder();
                sb.append(type==Type.SPLIT? "Split" : "Merge");
                sb.append(" [").append(lo).append("..").append(hi).append("] -> ");
                sb.append(java.util.Arrays.toString(values));
                return sb.toString();
            }
        }
        private static class Node {
            final int lo, hi, depth;
            final int[] values;
            int cx, cy;
            Node(int lo, int hi, int depth, int[] values) { this.lo=lo; this.hi=hi; this.depth=depth; this.values=values; }
        }

        private List<Node> dedupe(List<Node> nodes) {
            List<Node> out = new ArrayList<>();
            for (Node n : nodes) {
                boolean exists = false;
                for (Node m : out) {
                    if (m.lo==n.lo && m.hi==n.hi && m.depth==n.depth) { exists = true; break; }
                }
                if (!exists) out.add(n);
            }
            return out;
        }
        private List<Node> orderByDepth(List<Node> nodes, int maxDepth) {
            List<Node> out = new ArrayList<>();
            for (int d=0; d<=maxDepth; d++) {
                List<Node> level = new ArrayList<>();
                for (Node n : nodes) if (n.depth==d) level.add(n);
                level.sort((x,y)->Integer.compare(x.lo,y.lo));
                out.addAll(level);
            }
            return out;
        }
        private Node findNode(List<Node> nodes, int lo, int hi, int depth) {
            for (Node n : nodes) if (n.lo==lo && n.hi==hi && n.depth==depth) return n;
            return null;
        }
        private Point positionFor(Node n, int w, int levelH, int maxDepth) {
            int nodesOnLevel = Math.max(1, 1 << n.depth);
            int slotW = Math.max(140, w / nodesOnLevel);
            int x = (n.lo % nodesOnLevel) * slotW + slotW/2;
            int y = 100 + n.depth * levelH;
            return new Point(x, y);
        }
        private void drawArrayBox(Graphics2D g2, Node n, Color fill, boolean bold) {
            int cellW = 28, cellH = 28, gap = 2;
            int totalW = n.values.length * (cellW + gap) - gap + 16;
            int x = n.cx - totalW/2;
            int y = n.cy - (cellH + 12)/2;
            // container
            g2.setColor(fill);
            g2.fillRoundRect(x-8, y-10, totalW, cellH+20, 10, 10);
            g2.setColor(new Color(120,120,120));
            g2.setStroke(new BasicStroke(bold ? 2.2f : 1.2f));
            g2.drawRoundRect(x-8, y-10, totalW, cellH+20, 10, 10);
            // cells
            Font f = g2.getFont().deriveFont(Font.BOLD, 13f);
            g2.setFont(f);
            for (int i=0;i<n.values.length;i++) {
                int cx = x + i*(cellW+gap);
                g2.setColor(Color.white);
                g2.fillRect(cx, y, cellW, cellH);
                g2.setColor(new Color(80,80,80));
                g2.drawRect(cx, y, cellW, cellH);
                String s = String.valueOf(n.values[i]);
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(s);
                int th = fm.getAscent();
                g2.drawString(s, cx + (cellW - tw)/2, y + (cellH + th)/2 - 2);
            }
        }
        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.drawLine(x1, y1, x2, y2);
            double dx = x2 - x1, dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            int len = 8;
            AffineTransform at = g2.getTransform();
            g2.translate(x2, y2);
            g2.rotate(angle);
            Polygon head = new Polygon(new int[]{0, -len, -len}, new int[]{0, -len/2, len/2}, 3);
            g2.fillPolygon(head);
            g2.setTransform(at);
        }
    }
}
