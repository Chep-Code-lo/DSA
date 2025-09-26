package org.example.sortviz;

import org.example.sortviz.ui.SortingVisualizerFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new SortingVisualizerFrame().setVisible(true);
        });
    }
}
