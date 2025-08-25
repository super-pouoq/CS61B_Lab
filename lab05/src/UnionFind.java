import java.util.Arrays;

public class UnionFind {
    // TODO: Instance variables
    private final int [] weights ;
    private final int size;
    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        weights = new int[N];
        size = N;
        Arrays.fill(weights, -1);
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        return -weights[find(v)];
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        return weights[v];
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        return find(v1) == find(v2);
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if(v<0||v>=size){
            throw new IllegalArgumentException("invalid items");
        }
        if(weights[v]<0){
            return v;
        }
        int root=find(parent(v));
        weights[v]=root;
        return root;
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        if(v1 == v2)return;
        int v1_root=find(v1);
        int v2_root=find(v2);
        if(sizeOf(v1_root)>sizeOf(v2_root)){
            weights[v1_root]+=weights[v2_root];
            weights[v2_root]=v1_root;
        }
        else {
            weights[v2_root]+=weights[v1_root];
            weights[v1_root]=v2_root;
        }
    }

}
