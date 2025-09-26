package org.example.bst;
public class BSTConsole {
    public static void main(String[] args) {
        BST<Integer, String> bst = new BST<>();
        int[] keys = {50, 30, 70, 20, 40, 60, 80, 65, 62, 75, 85};
        for (int k : keys) bst.put(k, "v"+k);
        System.out.println("BST size="+bst.size()+", height="+bst.height()+", isBST="+bst.isBST());
        bst.printPretty();
    }
}
