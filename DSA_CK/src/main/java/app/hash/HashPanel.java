package app.hash;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class HashPanel extends JPanel {
  private JComboBox<String> method = new JComboBox<>(new String[] {"Linear Probing", "Chaining"});
  private final JTextField sizeField = new JTextField("20", 8);
  private HashTable<Integer, String> table;
  private final JTextField keyField = new JTextField(8);
  private final JTextField valField = new JTextField(10);
  private final JTextArea view = new JTextArea(22, 60);

  public HashPanel() {
    setLayout(new BorderLayout(8, 8));
    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton build = new JButton("Khởi tạo");
    top.add(new JLabel("Kỹ thuật:"));
    top.add(method);
    top.add(new JLabel("m:"));
    top.add(sizeField);
    top.add(build);

    JPanel ops = new JPanel(new FlowLayout(FlowLayout.LEFT));
    ops.setBorder(new TitledBorder("Thao tác"));
    JButton put = new JButton("insert");
    JButton get = new JButton("search");
    JButton del = new JButton("delete");
    ops.add(new JLabel("key (int):"));
    ops.add(keyField);
    ops.add(new JLabel("value:"));
    ops.add(valField);
    ops.add(put);
    ops.add(get);
    ops.add(del);

    view.setBorder(new TitledBorder("Bảng"));
    view.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    view.setEditable(false);
    add(top, BorderLayout.NORTH);
    add(ops, BorderLayout.CENTER);
    add(new JScrollPane(view), BorderLayout.SOUTH);

    build.addActionListener(e -> onBuild());
    put.addActionListener(e -> onPut());
    get.addActionListener(e -> onGet());
    del.addActionListener(e -> onDel());

    onBuild();
  }

  private void onBuild() {
    try {
      int m = Integer.parseInt(sizeField.getText().trim());
      if (m <= 2) throw new NumberFormatException();
      if (method.getSelectedIndex() == 0) table = new LinearProbingHashTable<>(m);
      else table = new ChainingHashTable<>(m);
      refresh();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "m không hợp lệ (>2)");
    }
  }

  private Integer getKey() {
    return Integer.parseInt(keyField.getText().trim());
  }

  private void onPut() {
    try {
      table.put(getKey(), valField.getText());
      refresh();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Nhập key là số nguyên");
    }
  }

  private void onGet() {
    try {
      String v = table.get(getKey());
      JOptionPane.showMessageDialog(this, v == null ? "Không thấy" : ("Giá trị: " + v));
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Nhập key là số nguyên");
    }
  }

  private void onDel() {
    try {
      boolean ok = table.remove(getKey());
      JOptionPane.showMessageDialog(this, ok ? "Đã xóa" : "Không thấy");
      refresh();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(this, "Nhập key là số nguyên");
    }
  }

  private void refresh() {
    view.setText(
        "capacity=" + table.capacity() + ", size=" + table.size() + "\n\n" + table.debugView());
  }
}
