public class BPlusTreeIndex<K extends Comparable<K>> implements Index<K> {
    private BPlusTree<K, DataRecord> tree;

    public BPlusTreeIndex(int order) {
        this.tree = new BPlusTree<>(order);
    }

    public void insert(K key, DataRecord record) {
        this.tree.insert(key, record);
    }

    public DataRecord search(K key) {
        return this.tree.search(key);
    }

    public void delete(K key) {
        this.tree.delete(key);
    }

    public long getComparisons() {
        return this.tree.getComparisons();
    }

    public void resetComparisons() {
        this.tree.resetComparisons();
    }

    public int getHeight() {
        return this.tree.getHeight();
    }
}
