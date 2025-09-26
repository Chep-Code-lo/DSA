import javax.swing.*;
import java.awt.*;

public class PanelLayoutDemo {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Demo JPanel + Layout");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel chứa các nút, căn giữa ngang
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(new JButton("Nút 1"));
        buttonPanel.add(new JButton("Nút 2"));
        buttonPanel.add(new JButton("Nút 3"));

        // Panel chứa text field, căn trái
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        inputPanel.add(new JLabel("Tên:"));
        inputPanel.add(new JTextField(15));

        // Đặt các panel vào frame bằng BorderLayout
        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);   // ô nhập ở trên
        frame.add(buttonPanel, BorderLayout.CENTER); // nút ở giữa

        frame.setLocationRelativeTo(null); // căn giữa màn hình
        frame.setVisible(true);
    }
}
