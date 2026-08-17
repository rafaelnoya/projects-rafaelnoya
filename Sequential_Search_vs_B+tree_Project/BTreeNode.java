import java.util.ArrayList;
import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> {
    int t;
    int n;
    boolean leaf;
    ArrayList<K> keys;
    ArrayList<V> values;
    ArrayList<BTreeNode<K, V>> children;

    public BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.n = 0;
        this.keys = new ArrayList<>(2 * t - 1);
        this.values = new ArrayList<>(2 * t - 1);
        this.children = new ArrayList<>(2 * t);
    }

    public int findKey(K key) {
        int idx = 0;
        while (idx < n && keys.get(idx).compareTo(key) < 0) {
            ++idx;
        }
        return idx;
    }

    public void removeKey(int idx) {
        keys.remove(idx);
        values.remove(idx);
        n--;
    }

    public void removeChild(int idx) {
        children.remove(idx);
    }
}