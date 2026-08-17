public interface Index<K extends Comparable<K>> {
    void insert(K key, DataRecord record);
    DataRecord search(K key);
    void delete(K key);
    long getComparisons();
    void resetComparisons();
    int getHeight();
}
