package org.example.bst;
import org.example.bst.ui.BSTFrame;

import javax.swing.*;

public class BSTSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BSTFrame().setVisible(true));
    }
}