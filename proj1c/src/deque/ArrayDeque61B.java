package deque;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ArrayDeque61B<T> implements Deque61B<T> {
    private T[] array;
    private int head;
    private int tail;
    private int size;
    private int used;

    public ArrayDeque61B() {
        array = (T[]) new Object[8];
        head = 0;
        tail = 0;
        size = 8;
        used = 0;
    }

    public void initialize(T x,int Capacity){
        for(int i=0;i<Capacity;i++){
            addFirst(x);
        }
    }
    public void change(int index,T x){
        if(index >= used){
            System.out.println("wrong index");
            return;
        }
        array[Math.floorMod(head + 1 + index, size)]=x;
    }
    private void resize(int FinalSize) {
        T[] new_array = (T[]) new Object[FinalSize];
        int pt = Math.floorMod(head + 1, size);
        int index = 0;
        while (pt != tail) {
            new_array[index] = array[pt];
            index++;
            pt = Math.floorMod(pt + 1, size);
        }
        new_array[index] = array[tail];
        array = new_array;
        head = FinalSize - 1;
        tail = index;
        size = FinalSize;
    }

    @Override
    public void addFirst(T x) {
        if (Math.floorMod(tail + 1, size) == head) {
            resize(size * 2);
        }
        array[head] = x;
        head = Math.floorMod(head - 1, size);
        used++;
    }

    @Override
    public void addLast(T x) {
        if (Math.floorMod(tail + 1, size) == head) {
            resize(size * 2);
        }
        tail = Math.floorMod(tail + 1, size);
        array[tail] = x;
        used++;
    }

    @Override
    public List<T> toList() {
        List<T> returnList = new ArrayList<>();
        int pt = Math.floorMod(head + 1, size);
        while (pt != tail) {
            returnList.add(array[pt]);
            pt = Math.floorMod(pt + 1, size);
        }
        returnList.add(array[tail]);
        return returnList;
    }

    @Override
    public boolean isEmpty() {
        return head == tail;
    }

    @Override
    public int size() {
        return size;
    }

    public int getUsed(){
        return used;
    }
    @Override
    public T removeFirst() {
        used--;
        head = Math.floorMod(head + 1, size);
        if (used < size / 4 && size >= 32) {
            resize(size / 2);
        }
        return null;
    }

    @Override
    public T removeLast() {
        used--;
        tail = Math.floorMod(tail - 1, size);
        if (used < size / 4 && size >= 32) {
            resize(size / 2);
        }
        return null;
    }

    //start with 0
    @Override
    public T get(int index) {
        return array[Math.floorMod(head + 1 + index, size)];
    }

    @Override
    public T getRecursive(int index) {
        throw new UnsupportedOperationException("No need to implement getRecursive for proj 1b");
    }

    private class ArrayIterator implements Iterator<T>{
        private int wizPos;

        public ArrayIterator(){
            wizPos=0;
        }

        @Override
        public T next() {
            T Item = get(wizPos);
            wizPos+=1;
            return Item;
        }

        @Override
        public boolean hasNext() {
            return wizPos<used;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)  return true;
        if(obj instanceof ArrayDeque61B OtherArray){
            if(this.size!=OtherArray.size||this.used!=OtherArray.used){
                return false;
            }
            int index=0;
            for(T x : this){
                if(OtherArray.get(index)!=x){
                    return false;
                }
                index++;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        List<String> ListOfItems =new ArrayList<>();
        for(T x:this){
            ListOfItems.add(x.toString());
        }
        return "{"+String.join(",",ListOfItems)+"}";
    }
}

