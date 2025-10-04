import dsa.expr.ExprFrame;
import dsa.tree.FamilyFrame;
import dsa.hash.HashFrame;
import javax.swing.*; import java.awt.*;

public class MainLauncher extends JFrame {
    public MainLauncher(){
        super("Java Swing DSA Mini‑Suite"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(500,200); setLocationRelativeTo(null);
        JButton b1=new JButton("1) Infix → Postfix/Prefix + Evaluate");
        JButton b2=new JButton("2) Cây gia phả (n-ary tree)");
        JButton b3=new JButton("3) Hashing (Division): LP/Chaining/DH");
        b1.addActionListener(e->new ExprFrame().setVisible(true));
        b2.addActionListener(e->new FamilyFrame().setVisible(true));
        b3.addActionListener(e->new HashFrame().setVisible(true));
        setLayout(new GridLayout(0,1,8,8)); add(b1); add(b2); add(b3);
    }
    public static void main(String[] args)
    { 
        SwingUtilities.invokeLater(()-> new MainLauncher().setVisible(true)); 
    }
}
