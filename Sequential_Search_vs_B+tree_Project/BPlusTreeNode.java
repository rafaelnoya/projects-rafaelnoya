import java.util.ArrayList;
import java.util.List;

public class BPlusTreeNode<K extends Comparable<K>, V> {
    int t;
    int n;
    boolean leaf;
    ArrayList<K> keys;
    ArrayList<V> values;
    ArrayList<BPlusTreeNode<K, V>> children;
    BPlusTreeNode<K, V> next;
    BPlusTreeNode<K, V> parent;

    public BPlusTreeNode(int t, boolean leaf, BPlusTreeNode<K, V> parent) {
        this.t = t;
        this.leaf = leaf;
        this.parent = parent;
        this.n = 0;
        this.keys = new ArrayList<>();

        if (leaf) {
            this.values = new ArrayList<>();
            this.children = null;
            this.next = null;
        } else {
            this.values = null;
            this.children = new ArrayList<>();
        }
    }

    public int findKeyIndex(K key) {
        int l = 0, r = n - 1;
        int ans = n;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int cmp = keys.get(mid).compareTo(key);
            if (cmp >= 0) {
                ans = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return ans;
    }
}