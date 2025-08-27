import org.checkerframework.checker.units.qual.K;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class BST_Node implements Comparable<BST_Node>{
        public K key;
        public V value;
        BST_Node left;
        BST_Node right;
        BST_Node(K new_key,V new_value){
            key=new_key;
            value=new_value;
        }

        @Override
        public int compareTo(BST_Node other) {
            return this.key.compareTo(other.key);
        }
    }
    private int size;
    private BST_Node root;
    public BSTMap(){
        size=0;
    }
    @Override
    public void put(K key, V value) {
        BST_Node node=new BST_Node(key,value);
        if(size == 0){
            root=node;
        }
        else secret_put(node,root);
        size++;
    }
    private BST_Node secret_put(BST_Node child, BST_Node parent){
        if(parent == null){
            return child;
        }
        if(child.compareTo(parent)==0){
            parent.value=child.value;
            size--;
        }
        if(child.compareTo(parent)>0){
            parent.right=secret_put(child,parent.right);
        }
        else if(child.compareTo(parent)<0){
            parent.left=secret_put(child,parent.left);
        }
        return parent;
    }

    @Override
    public V get(K key) {
        if(size==0)return null;
        return secret_get(root,key);
    }

    private V secret_get(BST_Node parent,K key){
        if(parent == null){
            return null;
        }
        if(parent.key.equals(key)){
            return parent.value;
        }
        if(key.compareTo(parent.key)>=0){
            return secret_get(parent.right,key);
        }
        else{
            return secret_get(parent.left,key);
        }
    }
    @Override
    public boolean containsKey(K key) {
        return secret_containsKey(root,key);
    }

    private boolean secret_containsKey(BST_Node parent,K key){
        if(parent == null){
            return false;
        }
        if(parent.key.equals(key)){
            return true;
        }
        if(key.compareTo(parent.key)>=0){
            return secret_containsKey(parent.right,key);
        }
        else{
            return secret_containsKey(parent.left,key);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root=null;
        size=0;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
