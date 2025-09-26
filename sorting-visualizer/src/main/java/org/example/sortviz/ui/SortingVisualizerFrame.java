package org.example.sortviz.ui;
import org.example.sortviz.core.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SortingVisualizerFrame extends JFrame {
    private final JComboBox<AlgorithmCategory> cbCategory = new JComboBox<>();
    private final JComboBox<SortingAlgorithm<Integer>> cbAlgorithm = new JComboBox<>();
    private final JSpinner spCount = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
    private final JButton btnRandom = new JButton("Tạo DS");
    private final JButton btnReset = new JButton("Reset");
    private final JToggleButton btnPlay = new JToggleButton("Play");
    private final JSlider sliderSpeed = new JSlider(1, 100, 50);

    private final BarPanel barPanel = new BarPanel();
    private final LogPanel logPanel = new LogPanel();
    // ngay cạnh barPanel/logPanel:
    private final LegendPanel legend = new LegendPanel();

    private final Random rnd = new Random();
    private List<Integer> data = new ArrayList<>();
    private boolean[] finalMask = new boolean[0];

    private final ArrayDeque<Step<Integer>> queue = new ArrayDeque<>();
    private Timer timer;

    public SortingVisualizerFrame(){
        super("Sorting Visualizer – Generic (Insertion / Exchange / Selection / Merge)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10,10));
        JPanel top = buildTop();
        //
        JPanel north = new JPanel(new BorderLayout());
        north.add(top, BorderLayout.NORTH);
        north.add(legend, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);
        //
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                wrap(barPanel), wrap(logPanel));
        split.setResizeWeight(0.72);
        add(split, BorderLayout.CENTER);

        // init
        for (AlgorithmCategory c : AlgorithmRegistry.categories()) cbCategory.addItem(c);
        cbCategory.addActionListener(e -> reloadAlgorithms());
        cbAlgorithm.addActionListener(e -> prepare());
        //
        cbAlgorithm.setRenderer(new DefaultListCellRenderer(){
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof org.example.sortviz.core.SortingAlgorithm<?> alg) {
                    setText(alg.getName()); // ← hiện tên giải thuật
                }
                return this;
            }
        });
        //
        btnRandom.addActionListener(this::onRandom);
        btnReset.addActionListener(e -> prepare());
        btnPlay.addActionListener(e -> onPlayToggle());
        sliderSpeed.addChangeListener(e -> applySpeed());

        cbCategory.setSelectedItem(AlgorithmCategory.INSERTION);
        randomizeData((Integer) spCount.getValue());
        prepare();
    }

    private JPanel buildTop(){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(8,8,8,8));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.gridy = 0; g.anchor = GridBagConstraints.WEST;

        g.gridx=0; p.add(new JLabel("Loại:"), g);
        g.gridx=1; cbCategory.setPreferredSize(new Dimension(180, 26)); p.add(cbCategory, g);
        g.gridx=2; p.add(new JLabel("Giải thuật:"), g);
        g.gridx=3; cbAlgorithm.setPreferredSize(new Dimension(220, 26)); p.add(cbAlgorithm, g);

        g.gridx=4; p.add(new JLabel("Số phần tử:"), g);
        g.gridx=5; p.add(spCount, g);

        g.gridx=6; p.add(btnRandom, g);
        g.gridx=7; p.add(btnReset, g);
        g.gridx=8; p.add(btnPlay, g);

        g.gridx=9; p.add(new JLabel("Tốc độ:"), g);
        g.gridx=10; sliderSpeed.setPreferredSize(new Dimension(160, 26)); p.add(sliderSpeed, g);

        return p;
    }

    private JScrollPane wrap(JComponent c){
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(new EmptyBorder(4,4,4,4));
        return sp;
    }

    private void reloadAlgorithms(){
        cbAlgorithm.removeAllItems();
        AlgorithmCategory cat = (AlgorithmCategory) cbCategory.getSelectedItem();
        if (cat != null) {
            for (SortingAlgorithm<Integer> a : AlgorithmRegistry.byCategory(cat)) cbAlgorithm.addItem(a);
            if (cbAlgorithm.getItemCount() > 0) cbAlgorithm.setSelectedIndex(0);
        }
    }

    private void onRandom(ActionEvent e){
        int n = (Integer) spCount.getValue();
        randomizeData(n);
        prepare();
    }

    private void randomizeData(int n){
        data = new ArrayList<>(n);
        for (int i=0;i<n;i++) data.add(rnd.nextInt(101));
        finalMask = new boolean[n];
        barPanel.setData(data, finalMask, null);
    }

    private void prepare(){
        // stop
        if (timer != null) timer.stop();
        btnPlay.setSelected(false); btnPlay.setText("Play");
        queue.clear();
        logPanel.clear();
        // rebuild steps from a copy
        List<Integer> work = new ArrayList<>(data);
        SortingAlgorithm<Integer> algo = (SortingAlgorithm<Integer>) cbAlgorithm.getSelectedItem();
        if (algo==null) return;
        StepEmitter<Integer> emitter = queue::add;
        emitter.emit(Step.note("Chọn: ["+algo.getCategory()+"] " + algo.getName()));
        algo.sort(work, Comparator.naturalOrder(), emitter);
        // reset visual state
        finalMask = new boolean[data.size()];
        barPanel.setData(new ArrayList<>(data), finalMask, null);
        barPanel.repaint();
    }

    private void onPlayToggle(){
        if (btnPlay.isSelected()) startTimer(); else stopTimer();
    }

    private void startTimer(){
        applySpeed();
        btnPlay.setText("Pause");
        if (timer != null) timer.start();
    }

    private void stopTimer(){
        if (timer != null) timer.stop();
        btnPlay.setText("Play");
    }

    private void applySpeed(){
        int v = sliderSpeed.getValue();
        int delay = 505 - v*5; // 5..500 ms
        if (timer == null){
            timer = new Timer(delay, e -> stepOnce());
        } else {
            timer.setDelay(delay);
        }
    }

    private void stepOnce(){
        if (queue.isEmpty()){
            stopTimer();
            return;
        }
        Step<Integer> s = queue.poll();
        if (s.type == StepType.NOTE){
            logPanel.append(s.note);
            return;
        }
        switch (s.type){
            case COMPARE -> {
                logPanel.append(String.format("So sánh a[%d]=%d với a[%d]=%d", s.i, data.get(s.i), s.j, data.get(s.j)));
            }
            case SWAP -> {
                int vi = data.get(s.i);
                int vj = data.get(s.j);
                data.set(s.i, vj);
                data.set(s.j, vi);
                logPanel.append(String.format("Đổi chỗ a[%d]↔a[%d] (%d↔%d)", s.i, s.j, vj, vi));
            }
            case SET -> {
                data.set(s.i, s.value);
                logPanel.append(String.format("Gán a[%d]=%d", s.i, s.value));
            }
            case MARK_FINAL -> {
                if (s.i>=0 && s.i<finalMask.length) finalMask[s.i] = true;
                logPanel.append(String.format("Cố định vị trí %d", s.i));
            }
        }
        barPanel.setData(data, finalMask, s);
        barPanel.repaint();
    }
}
