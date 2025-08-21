import java.util.ArrayList;
import java.util.List;

public class LinkedListDeque61B<T> implements Deque61B<T>{

    private class Node{
        public T item;
        public Node next;
        public Node before;
        public T getItem(int index){
            if(index == 0)return item;
            return this.next.getItem(index-1);
        }
    }
    private final Node sentinel;
    private int size;
    public LinkedListDeque61B() {
        sentinel = new Node();
        sentinel.next=sentinel;
        sentinel.before=sentinel;
        size=0;
    }
    @Override
    public void addFirst(T x) {
        Node new_node=new Node();
        new_node.item=x;
        new_node.before=sentinel;
        new_node.next=sentinel.next;
        sentinel.next=new_node;
        new_node.next.before=new_node;
        size++;
    }

    @Override
    public void addLast(T x) {
        Node new_node=new Node();
        new_node.item=x;
        new_node.before=sentinel.before;
        new_node.next=sentinel;
        sentinel.before=new_node;
        new_node.before.next=new_node;
        size++;
    }

    @Override
    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        Node pt=sentinel.next;
        while(pt != sentinel){
            returnList.add(pt.item);
            pt=pt.next;
        }
        return returnList;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        Node temp = sentinel.next;
        sentinel.next=temp.next;
        temp.next.before=sentinel;
        size--;
        return null;
    }

    @Override
    public T removeLast() {
        Node temp=sentinel.before;
        sentinel.before=temp.before;
        temp.before.next=sentinel;
        size--;
        return null;
    }

    @Override
    public T get(int index) {
        if(index > size || index <= 0){
            System.out.println("wrong index to get item from List");
            return null;
        }
        int count=index;
        Node pt=sentinel;

        while(count != 0){
            count--;
            pt=pt.next;
        }
        return pt.item;
    }

    @Override
    public T getRecursive(int index) {
        if(index > size || index <= 0){
            System.out.println("wrong index to get item from List");
            return null;
        }
        return sentinel.getItem(index);
    }
}
