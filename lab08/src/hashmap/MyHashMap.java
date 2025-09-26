package hashmap;

import org.eclipse.jetty.io.ByteBufferPool;

import java.util.*;

/**
 *  A hash table-backed Map implementation.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int Capacity = 16;
    private double loadFactor = 0.75;
    private int size=0;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this.buckets = new Collection[Capacity];
    }

    public MyHashMap(int initialCapacity) {
        this.Capacity=initialCapacity;
        this.buckets = new Collection[Capacity];
    }

    /**
     * MyHashMap constructor that creates a backing array of initialCapacity.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialCapacity initial size of backing array
     * @param loadFactor maximum load factor
     */
    public MyHashMap(int initialCapacity, double loadFactor) {
        this.Capacity=initialCapacity;
        this.loadFactor=loadFactor;
        this.buckets = new Collection[Capacity];
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *  Note that that this is referring to the hash table bucket itself,
     *  not the hash map itself.
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }

    private int hashCodeComputer(K key){
        return Math.floorMod(key.hashCode(),Capacity);
    }

    @Override
    public void put(K key, V value) {
        int index=hashCodeComputer(key);
        if (buckets[index] == null) {
            buckets[index] = createBucket();
        }
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        size++;
        buckets[index].add(new Node(key, value));
        if ((double) size / Capacity > loadFactor) {
            resize();
        }
    }

    private void resize(){
        Collection<Node>[] oldBuckets = buckets;
        int oldCapacity = Capacity;
        Capacity*=2;
        buckets = new Collection[Capacity];
        size=0;
        for(int i=0;i<oldCapacity;i++){
            if (oldBuckets[i] != null) {
                for (Node node:oldBuckets[i]){
                    put(node.key,node.value);
                }
            }
        }
    }
    @Override
    public V get(K key) {
        int index=hashCodeComputer(key);
        if(buckets[index]==null)return null;
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        int index=hashCodeComputer(key);
        if(buckets[index]==null)return false;
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(buckets, null);
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (int i = 0; i < Capacity; i++) {
            if (buckets[i] != null) {
                for (Node node : buckets[i]) {
                    keys.add(node.key);
                }
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        int index=hashCodeComputer(key);
        if(buckets[index]==null)return null;
        Iterator<Node> iterator = buckets[index].iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (Objects.equals(node.key, key)) {
                V value = node.value;
                iterator.remove(); // ✅ 安全删除当前元素
                size--;            // 别忘了更新 size！
                return value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        Collection temp=createBucket();
        return temp.iterator();
    }

}
