package app.ui;

import javax.swing.*;

public class AppFrame extends JFrame {
  public AppFrame() {
    super("Biểu thức | Cây gia phả | Băm chia dư");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 600);
    setLocationRelativeTo(null);
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Biểu thức", new app.expr.ExprPanel());
    tabs.addTab("Cây gia phả", new app.tree.FamilyTreePanel());
    tabs.addTab("Băm chia dư", new app.hash.HashPanel());
    setContentPane(tabs);
  }
}
