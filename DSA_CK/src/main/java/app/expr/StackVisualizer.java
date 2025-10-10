package app.expr;

import javax.swing.*;
import java.awt.*;
import java.awt.BasicStroke;
import java.util.List;
import java.util.ArrayList;

public class StackVisualizer extends JPanel {
    // Simple frame model used for animation publishing
    public enum FrameAction { PUSH_TOKEN, PUSH_STACK, POP_STACK, TO_OUTPUT, FINAL }

    public class Frame {
        public final String token; // incoming token text
        public final List<String> stack; // snapshot (top at index 0)
        public final FrameAction action;
        public final String currentOutput; // current output string

        public Frame(String token, List<String> stack, FrameAction action, String currentOutput) {
            this.token = token;
            this.stack = new ArrayList<>(stack);
            this.action = action;
            this.currentOutput = currentOutput != null ? currentOutput : "";
        }
        
        // Backward compatibility constructor
        public Frame(String token, List<String> stack, FrameAction action) {
            this(token, stack, action, "");
        }
    }

    private List<String> stack = new ArrayList<>();
    private String currentToken = "";
    private FrameAction currentAction = null;
    private String displayOutput = "";

    public StackVisualizer() {
        setPreferredSize(new Dimension(520, 260));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
    }

    public void showFrame(Frame f) {
        if (f == null) return;
        this.currentToken = f.token == null ? "" : f.token;
        this.stack = new ArrayList<>(f.stack);
        this.currentAction = f.action;
        this.displayOutput = f.currentOutput;
        repaint();
    }

    public void clear() {
        stack.clear();
        currentToken = "";
        currentAction = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Title
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.setColor(Color.BLACK);
        g2.drawString("Stack Visualization", 10, 16);

        // Action hint in red
        if (currentAction != null && currentToken != null && !currentToken.isEmpty()) {
            g2.setColor(Color.RED);
            g2.drawString("→ " + actionText(currentAction) + (currentToken.isEmpty() ? "" : " '" + currentToken + "'"), 10, 34);
        }

        // Token box (yellow) at top center with rounded corners
        int tokenW = 80, tokenH = 30;
        int tokenX = w/2 - tokenW/2;
        int tokenY = 50;
        int tokenCornerRadius = 8;
        if (currentToken != null && !currentToken.isEmpty()) {
            // Fill with light yellow
            g2.setColor(new Color(255, 245, 157)); // Softer yellow
            g2.fillRoundRect(tokenX, tokenY, tokenW, tokenH, tokenCornerRadius, tokenCornerRadius);
            
            // Draw border
            g2.setColor(new Color(255, 193, 7)); // Golden yellow border
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(tokenX, tokenY, tokenW, tokenH, tokenCornerRadius, tokenCornerRadius);
            
            // Draw text
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            int tx = tokenX + (tokenW - fm.stringWidth(currentToken))/2;
            int ty = tokenY + (tokenH + fm.getAscent())/2 - 2;
            g2.drawString(currentToken, tx, ty);
            g2.setStroke(new BasicStroke(1.0f));
        }

        // Di chuyển các ô stack xuống thấp hơn
        int stackLabelY = h - 70; // Vị trí label Stack gần phía dưới panel
        int stackStartY = stackLabelY - 35;
        int ew = 80, eh = 24;
        int cx = 30;
        int cornerRadius = 8;

        for (int i = 0; i < stack.size(); i++) {
            int y = stackStartY - i*(eh + 3);
            Color topColor = new Color(220, 240, 255);
            Color bottomColor = new Color(180, 220, 255);
            Color borderColor = new Color(100, 150, 200);
            g2.setColor(i == stack.size() - 1 ? topColor : bottomColor);
            g2.fillRoundRect(cx, y, ew, eh, cornerRadius, cornerRadius);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2.0f));
            g2.drawRoundRect(cx, y, ew, eh, cornerRadius, cornerRadius);
            String s = stack.get(i);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int tx = cx + (ew - fm.stringWidth(s))/2;
            int ty = y + (eh + fm.getAscent())/2 - 2;
            g2.drawString(s, tx, ty);
            g2.setStroke(new BasicStroke(1.0f));
        }

        // Label "Stack:" bên dưới các box stack
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.BLACK);
        g2.drawString("Stack:", cx, stackLabelY + 15);

        // Output box nằm ngang hàng với stack, bên phải stack
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String outputText = displayOutput.isEmpty() ? "(empty)" : displayOutput;
        FontMetrics outputFm = g2.getFontMetrics();
        int minOutputBoxW = 100;
        int outputLabelW = outputFm.stringWidth("Output:") + 20;
        int maxOutputBoxW = w - (cx + ew + 60);
        // Tách dòng nếu có ký tự xuống dòng hoặc tự động xuống dòng nếu quá dài
        java.util.List<String> lines = new java.util.ArrayList<>();
        int availableTextW = maxOutputBoxW - outputLabelW - 30;
        String[] rawLines = outputText.split("\\n");
        for (String raw : rawLines) {
            String line = raw;
            while (outputFm.stringWidth(line) > availableTextW && line.length() > 0) {
                // Tìm vị trí cắt
                int cut = line.length();
                while (cut > 0 && outputFm.stringWidth(line.substring(0, cut)) > availableTextW) cut--;
                if (cut == 0) break;
                lines.add(line.substring(0, cut));
                line = line.substring(cut);
            }
            if (!line.isEmpty()) lines.add(line);
        }
        int outputBoxW = Math.max(minOutputBoxW, Math.min(outputLabelW + availableTextW + 30, maxOutputBoxW));
        int lineHeight = outputFm.getHeight();
        int outputBoxH = Math.max(35, lineHeight * lines.size() + 12);
        int outputBoxX = cx + ew + 40;
        int outputBoxY = stackStartY - (eh + 3)*(stack.size()-1)/2;
        int outputCornerRadius = 8;

        g2.setColor(new Color(220, 255, 220));
        g2.fillRoundRect(outputBoxX, outputBoxY, outputBoxW, outputBoxH, outputCornerRadius, outputCornerRadius);
        g2.setColor(new Color(60, 179, 113));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRoundRect(outputBoxX, outputBoxY, outputBoxW, outputBoxH, outputCornerRadius, outputCornerRadius);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Output:", outputBoxX + 10, outputBoxY + 16);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        // Vẽ từng dòng nội dung
        for (int i = 0; i < lines.size(); i++) {
            g2.drawString(lines.get(i), outputBoxX + outputLabelW + 10, outputBoxY + 16 + i * lineHeight);
        }
        g2.setStroke(new BasicStroke(1.0f));

        g2.dispose();
    }

    private String actionText(FrameAction a) {
        return switch (a) {
            case PUSH_TOKEN -> "Push token to stack";
            case PUSH_STACK -> "Push to stack";
            case POP_STACK -> "Pop from stack";
            case TO_OUTPUT -> "Send to output";
            case FINAL -> "Final";
        };
    }
}
