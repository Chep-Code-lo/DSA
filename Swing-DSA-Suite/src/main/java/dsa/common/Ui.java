package dsa.common;
import javax.swing.*;
import java.awt.*;

public class Ui {
    public static JFrame frame(String title){
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        return f;
    }
    public static JPanel titled(String title, JComponent inner){
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(inner);
        return p;
    }
}
