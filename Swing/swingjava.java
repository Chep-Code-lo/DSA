import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
class MyLabel extends JLabel{
    public MyLabel(String text, int x, int y, int w, int h){
        super(text);
        setBounds(x, y, w, h);
        setForeground(Color.RED); 
    }
    public static MyLabel withImage(String path, int x, int y, int w, int h) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        MyLabel label = new MyLabel("", x, y, w, h);
        label.setIcon(new ImageIcon(img));
        return label;
    }
}
class MyButton extends JButton{
    public MyButton(String text, int x, int y, int w, int h){
        super(text);
        setBounds(x, y, w, h);
        setBackground(new Color(0, 128, 255));
        setForeground(Color.BLACK);            
    }
}
class MyTextField extends JTextField{
    public MyTextField(int x, int y, int w, int h){
        super();
        setBounds(x, y, w, h);
    }
}
class AppFrame extends JFrame{
    private final MyLabel statusLabel, label1;
    private final MyLabel imageLabel;
    private final MyTextField inputField;
    private final MyButton actionButton;
    private final Random rand = new Random();
    public AppFrame() {
        setTitle("Demo Swing");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        statusLabel = new MyLabel("Chưa bấm nút kìa", 200, 90, 400, 30);
        label1 = new MyLabel("", 10, 120, 400, 100);
        label1.setFont(new Font("Tahoma", Font.BOLD, 14)); // chữ to, đậm
        imageLabel = MyLabel.withImage("Swing/iconn.jpg", 150, 200, 200, 200);
        inputField = new MyTextField(30, 50, 200, 30);
        actionButton = new MyButton("Nhấn vô đây nè!!!", 150, 120, 200, 30);
        actionButton.addActionListener(e ->{
            JOptionPane.showMessageDialog(this, "Vừa mới nhấn kìa!!!");
            getContentPane().setBackground(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
            String time = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date());
            String input = inputField.getText();
            label1.setText("Bạn nhập: " + input + " | Bấm nút lúc " + time);
        });
        add(statusLabel);
        add(imageLabel);
        add(inputField);
        add(actionButton);
        add(label1);
    }
}
public class swingjava {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppFrame().setVisible(true));
    }
}
