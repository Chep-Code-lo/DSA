package dsa.hash;
import dsa.common.Ui;
import javax.swing.*; import java.awt.*; import java.util.function.ToIntFunction;

public class HashFrame extends JFrame {
    private JTable table; private final JTextField tfKey = new JTextField(); private final JTextField tfVal=new JTextField();
    private final JSpinner spM = new JSpinner(new SpinnerNumberModel(11, 3, 401, 1));
    private final JComboBox<String> mode = new JComboBox<>(new String[]{"Linear Probing","Chaining","Double Hashing"});
    private final JSpinner spLF = new JSpinner(new SpinnerNumberModel(0.70, 0.10, 0.90, 0.05));
    private final JLabel lbStats = new JLabel("…");

    private HashTableLP<Integer,String> lp; private HashTableChaining<Integer,String> ch; private HashTableDH<Integer,String> dh;

    public HashFrame(){
        super("Hashing (Division)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(900,600); setLocationRelativeTo(null);
        rebuild();
        JButton bBuild=new JButton("Tạo bảng mới"); bBuild.addActionListener(e->rebuild());
        JButton bPut=new JButton("Insert/Update"); JButton bGet=new JButton("Search"); JButton bDel=new JButton("Delete");
        bPut.addActionListener(e->{ Integer k=key(); if(k==null) return; String v=tfVal.getText(); if(isLP()) lp.put(k,v); else if(isCH()) ch.put(k,v); else dh.put(k,v); refresh(); });
        bGet.addActionListener(e->{ Integer k=key(); if(k==null) return; String v = isLP()? lp.get(k): isCH()? ch.get(k): dh.get(k); JOptionPane.showMessageDialog(this, v==null?"Không thấy":"Value = "+v); });
        bDel.addActionListener(e->{ Integer k=key(); if(k==null) return; boolean ok = isLP()? lp.remove(k): isCH()? ch.remove(k): dh.remove(k); JOptionPane.showMessageDialog(this, ok?"Đã xóa":"Không thấy"); refresh(); });

        JPanel ctrl=new JPanel(new GridLayout(0,2,6,6));
        ctrl.add(new JLabel("m:")); ctrl.add(spM);
        ctrl.add(new JLabel("Chế độ:")); ctrl.add(mode);
        ctrl.add(new JLabel("Key (int):")); ctrl.add(tfKey); ctrl.add(new JLabel("Value:")); ctrl.add(tfVal);
        ctrl.add(new JLabel("Load factor max:")); ctrl.add(spLF);

        JPanel btn=new JPanel(); btn.add(bBuild); btn.add(bPut); btn.add(bGet); btn.add(bDel);
        JPanel south = new JPanel(new BorderLayout()); south.add(btn, BorderLayout.NORTH); south.add(lbStats, BorderLayout.SOUTH);

        setLayout(new BorderLayout(8,8));
        add(Ui.titled("Điều khiển", ctrl), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private boolean isLP(){ return mode.getSelectedIndex()==0; }
    private boolean isCH(){ return mode.getSelectedIndex()==1; }
    private boolean isDH(){ return mode.getSelectedIndex()==2; }
    private Integer key(){ try{ return Integer.parseInt(tfKey.getText().trim()); }catch(Exception ex){ JOptionPane.showMessageDialog(this,"Key phải là số nguyên"); return null; } }

    private void rebuild(){
        int m=(Integer)spM.getValue(); ToIntFunction<Integer> hasher = k->k;
        lp=new HashTableLP<>(m,hasher); ch=new HashTableChaining<>(m,hasher);
        dh=new HashTableDH<>(m, hasher, k-> 1 + (k % (m-1))); // h2 = 1 + k mod (m-1)
        lp.setLoadFactorThreshold(((Double)spLF.getValue()).doubleValue());
        table = new JTable(m, 2); table.getColumnModel().getColumn(0).setHeaderValue("Index"); table.getColumnModel().getColumn(1).setHeaderValue("Data");
        refresh();
    }

    private void refresh(){
        String[] snap = isLP()? lp.snapshot() : isCH()? ch.snapshot() : dh.snapshot();
        for(int i=0;i<snap.length;i++){ table.setValueAt(i, i, 0); table.setValueAt(snap[i], i, 1); }
        if(isLP()) lbStats.setText(String.format("LP size=%d, m=%d, LF=%.3f, threshold=%.2f", lp.size(), lp.capacity(), lp.loadFactor(), ((Double)spLF.getValue())));
        else if(isCH()) lbStats.setText(String.format("Chaining: buckets=%d", snap.length));
        else lbStats.setText(String.format("DH size=%d, m=%d, LF=%.3f, threshold=%.2f", dh.size(), dh.capacity(), dh.loadFactor(), ((Double)spLF.getValue())));
        revalidate(); repaint();
    }
}
