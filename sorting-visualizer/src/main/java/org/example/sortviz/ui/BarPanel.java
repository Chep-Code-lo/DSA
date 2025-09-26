package org.example.sortviz.ui;
import org.example.sortviz.core.Step;
import org.example.sortviz.core.StepType;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BarPanel extends JPanel {
    private List<Integer> data;
    private boolean[] finalMask;
    private Step<Integer> last;

    public void setData(List<Integer> data, boolean[] finalMask, Step<Integer> last){
        this.data = data; this.finalMask = finalMask; this.last = last;
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data==null || data.isEmpty()) return;
        Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(); int h = getHeight();
        int n = data.size();
        int gap = Math.max(2, w/(n*8));
        int barW = Math.max(8, (w - (n+1)*gap)/n);
        int maxV = 100; // values are 0..100
        Font f = getFont().deriveFont(Font.BOLD, Math.max(10f, barW*0.35f));
        g2.setFont(f);
        int x = gap;

        for (int i=0;i<n;i++){
            int v = data.get(i);
            int barH = Math.max(4, (int)((h-30) * (v / (double)maxV)));
            int y = h - barH - 10;

            Color col = new Color(180,180,180);
            if (finalMask!=null && i<finalMask.length && finalMask[i]) col = new Color(60,180,90);
            if (last!=null){
                if (last.type== StepType.COMPARE && (i==last.i || i==last.j)) col = new Color(230,200,80);
                if (last.type== StepType.SWAP && (i==last.i || i==last.j)) col = new Color(220,80,80);
                if (last.type== StepType.SET && i==last.i) col = new Color(90,140,220);
            }

            g2.setColor(col);
            g2.fillRoundRect(x, y, barW, barH, 8, 8);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRoundRect(x, y, barW, barH, 8, 8);

            // value label centered
            String s = String.valueOf(v);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (barW - fm.stringWidth(s))/2;
            int ty = y + (barH + fm.getAscent() - fm.getDescent())/2;
            g2.setColor(Color.BLACK);
            g2.drawString(s, tx, ty);

            x += barW + gap;
        }
    }
}
