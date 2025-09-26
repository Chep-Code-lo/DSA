package org.example.sortviz.ui;
import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private final JTextArea ta = new JTextArea();
    public LogPanel(){
        super(new BorderLayout());
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(new JScrollPane(ta), BorderLayout.CENTER);
    }
    public void clear(){ ta.setText(""); }
    public void append(String s){
        ta.append(s + "\n");
        ta.setCaretPosition(ta.getDocument().getLength());
    }
}
