package org.example.sortviz.ui;
import javax.swing.*;
import java.awt.*;


public class LegendPanel extends JPanel {
    public LegendPanel(){
        super(new FlowLayout(FlowLayout.CENTER, 12, 4));
        setOpaque(false);
        add(entry(new Color(230,200,80), "So sánh"));
        add(entry(new Color(220,80,80), "Đổi chỗ"));
        add(entry(new Color(90,140,220), "Gán/Chèn"));
        add(entry(new Color(60,180,90), "Cố định"));
        add(entry(new Color(180,180,180), "Bình thường"));
    }
    private JComponent entry(Color c, String text){
        JLabel lb = new JLabel(text);
        lb.setIcon(new ColorIcon(c, 14, 14, 4));
        lb.setIconTextGap(6);
        return lb;
    }
    static class ColorIcon implements Icon {
        final Color color; final int w,h,arc;
        ColorIcon(Color color, int w, int h, int arc){ this.color=color; this.w=w; this.h=h; this.arc=arc; }
        @Override public int getIconWidth(){ return w; }
        @Override public int getIconHeight(){ return h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y){
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.fillRoundRect(x, y, w, h, arc, arc);
            g2.setColor(Color.DARK_GRAY); g2.drawRoundRect(x, y, w, h, arc, arc);
        }
    }
}