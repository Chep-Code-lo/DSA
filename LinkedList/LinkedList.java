package LinkedList;
class Node{
    int data;
    Node next;
    Node(int data){
        this.data = data;
        this.next = null;
    }
}
public class LinkedList{
    private Node head;
    private int n = 0;
    //Tìm kiếm
    public boolean search(int data){
        Node curr = head;
        while(curr != null){
            if(curr.data == data) return true;
            curr = curr.next;
        }
        return false;
    }
    public void addFirst(int value){
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
        n++;
    }
    public void addLast(int value){
        Node newNode = new Node(value);
        if(head == null){
            head = newNode;
            return;
        }
        Node curr = head;
        while(curr.next != null){
            curr = curr.next;
        }
        curr.next = newNode;
        n++;
    }
    int size(){
        int count = 0;
        Node curr = head;
        while(curr != null){
            count++;
            curr = curr.next;
        }
        return count;
    }
    public void addAt(int index, int data){
        if(index == 0){
            addFirst(data);
            return;
        }
        Node newNode = new Node(data);
        Node curr = head;
        for(int i=0; i<index-1 && curr != null; ++i){
            curr = curr.next;
        }
        if(curr == null) return;
        newNode.next = curr.next;
        curr.next = newNode;
        n++;
    }
    public void removeAt(int index){
        if(head == null) return;
        if(index == 0){
            head = head.next;
            return;
        }
        Node curr = head;
        for(int i=0; i<index-1 && curr != null; ++i){
            curr = curr.next;
        }
        if(curr == null || curr.next == null) return;
        curr.next = curr.next.next;
        n--;
    }
    public void reverse(){
        Node prev = null;
        Node curr = head;
        Node next = null;   
        while(curr != null){
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;
    }
    public int get(int index){
        checkIndex(index);
        Node curr = head;
        for(int i=0; i<index; ++i)  curr = curr.next;
        return curr.data;
    }
    private void checkIndex(int index){
        if(index < 0 || index >= n)
            throw new IndexOutOfBoundsException("index= " + index + ", size = " + n);
    }
    private void checkIndexForAdd(int index){ 
        if(index < 0 || index > n)
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + n);
    }
    public void printList(){
        Node curr = head;
        while(curr != null){
            System.out.print(curr.data + "->");
            curr = curr.next;
        }
        System.out.println("null");
    }
}
