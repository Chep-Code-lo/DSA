package LinkedList;

public class Main {
    public static void main(String[] args){
        LinkedList list = new LinkedList();
        list.addFirst(10);
        list.addFirst(20);
        list.addLast(5);
        list.addLast(15);
        System.out.print("Ban đầu:  "); list.printList();
        System.out.println("Size = " + list.size());
        //addAt
        list.addAt(0, 99);   // chèn đầu: [99, 20, 10, 5, 15] 
        list.addAt(3, 77);   // chèn giữa: [99, 20, 10, 77, 5, 15] 
        list.addAt(list.size(), 33); // chèn cuối: [..., 33] 
        System.out.print("Sau addAt: "); list.printList(); 
        System.out.println("size = " + list.size());   
        //removeAt
        list.removeAt(0);//Xóa đầu
        list.removeAt(2);//Xóa giữa
        list.removeAt(list.size() - 1);//Xóa cuối
        System.out.print("Sau removeAt "); list.printList();
        System.out.println("size = " + list.size());
        //reverse
        list.reverse();
        System.out.print("Sau reverse: "); list.printList();
        //Thử get và kiểm tra biên
        System.out.println("Phần tử index 1 = " + list.get(1));
        //size
        System.out.println("Kích thước cuối: " + list.size());
    }
}
