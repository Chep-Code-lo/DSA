package dsa.expr;
import dsa.common.Ui;
import javax.swing.*; import java.awt.*; import java.util.List;

public class ExprFrame extends JFrame {
    private final JTextArea in = new JTextArea("3 + 4 * 2 / (1 - 5)^2^3");
    private final JTextField tfPost = new JTextField();
    private final JTextField tfPre = new JTextField();
    private final JTextField tfVal = new JTextField();

    public ExprFrame(){
        super("Infix ↔ Postfix/Prefix + Evaluate");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800,500); setLocationRelativeTo(null);

        JButton bConv = new JButton("Convert");
        JButton bEval = new JButton("Evaluate Postfix");
        tfPost.setEditable(false); tfPre.setEditable(false); tfVal.setEditable(false);

        bConv.addActionListener(e->{
            try{
                List<Token> toks = new Lexer(in.getText()).lex();
                var post = ShuntingYard.infixToPostfix(toks);
                var pre  = ShuntingYard.infixToPrefix(toks);
                tfPost.setText(ShuntingYard.join(post));
                tfPre.setText(ShuntingYard.join(pre));
            }catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} 
        });
        bEval.addActionListener(e->{
            try{
                List<Token> toks = new Lexer(in.getText()).lex();
                var post = ShuntingYard.infixToPostfix(toks);
                tfVal.setText(String.valueOf(ExpressionEvaluator.evalPostfix(post)));
            }catch(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);} 
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JScrollPane(in));
        JPanel mid = new JPanel(new GridLayout(3,1,8,8));
        mid.add(Ui.titled("Postfix", tfPost));
        mid.add(Ui.titled("Prefix", tfPre));
        mid.add(Ui.titled("Giá trị (theo Postfix)", tfVal));

        JPanel btn = new JPanel(); btn.add(bConv); btn.add(bEval);
        setLayout(new BorderLayout(8,8)); add(top,BorderLayout.CENTER); add(mid,BorderLayout.SOUTH); add(btn,BorderLayout.NORTH);
    }
}
