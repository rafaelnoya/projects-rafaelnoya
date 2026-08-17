import java.util.List;

interface Index<K extends Comparable<K>> {
    void insert(K key, DataRecord record);
    DataRecord search(K key);
    void delete(K key);
    long getComparisons();
    void resetComparisons();
    int getHeight();
}

class BTreeIndex<K extends Comparable<K>> implements Index<K> {
    private BTree<K, DataRecord> tree;

    public BTreeIndex(int order) {
        this.tree = new BTree<>(order);
    }

    @Override
    public void insert(K key, DataRecord record) {
        tree.insert(key, record);
    }

    @Override
    public DataRecord search(K key) {
        return tree.search(key);
    }

    @Override
    public void delete(K key) {
        tree.delete(key);
    }

    @Override
    public long getComparisons() {
        return tree.getComparisons();
    }

    @Override
    public void resetComparisons() {
        tree.resetComparisons();
    }

    @Override
    public int getHeight() {
        return tree.getHeight();
    }
}

class BPlusTreeIndex<K extends Comparable<K>> implements Index<K> {
    private BPlusTree<K, DataRecord> tree;

    public BPlusTreeIndex(int order) {
        this.tree = new BPlusTree<>(order);
    }

    @Override
    public void insert(K key, DataRecord record) {
        tree.insert(key, record);
    }

    @Override
    public DataRecord search(K key) {
        return tree.search(key);
    }

    @Override
    public void delete(K key) {
        tree.delete(key);
    }

    @Override
    public long getComparisons() {
        return tree.getComparisons();
    }

    @Override
    public void resetComparisons() {
        tree.resetComparisons();
    }

    @Override
    public int getHeight() {
        return tree.getHeight();
    }
}

public class IndexManager {
    private StorageModule storage;
    private Index<String> activeIndex;
    private String indexedField;
    private String indexType;
    private int indexOrder;

    public IndexManager(StorageModule storage) {
        this.storage = storage;
        this.activeIndex = null;
        this.indexedField = null;
        this.indexType = "Nenhum";
    }

    public boolean createIndex(String field, String type, int order) {
        List<DataRecord> records = storage.getSequentialView();
        if ("btree".equalsIgnoreCase(type)) {
            this.activeIndex = new BTreeIndex<String>(order);
            this.indexType = "B-Tree";
        } else if ("bplustree".equalsIgnoreCase(type)) {
            this.activeIndex = new BPlusTreeIndex<String>(order);
            this.indexType = "B+ Tree";
        } else {
            return false;
        }

        this.indexedField = field;
        this.indexOrder = order;

        for(DataRecord record : records) {
            Comparable val = record.get(field);
            if (val != null) {
                this.activeIndex.insert(val.toString(), record);
            }
        }
        return true;
    }

    public String getIndexStatus() {
        if (activeIndex == null) {
            return "Nenhum indice criado.";
        }
        return String.format("Indice %s (Ordem=%d, Altura=%d) criado no campo '%s'.",
                this.indexType, this.indexOrder, this.activeIndex.getHeight(), this.indexedField);
    }

    public boolean insert(DataRecord record) {
        boolean success = storage.insert(record);
        if (success && activeIndex != null && indexedField != null) {
            Comparable val = record.get(this.indexedField);
            if (val != null) {
                this.activeIndex.insert(val.toString(), record);
            }
        }
        return success;
    }

    public DataRecord delete(String primaryKey) {
        DataRecord record = storage.findByPrimaryKey(primaryKey);

        if (record != null && activeIndex != null && indexedField != null) {
            Comparable val = record.get(this.indexedField);
            if (val != null) {
                this.activeIndex.delete(val.toString());
            }
        }

        return storage.delete(primaryKey);
    }

    public Index<String> getIndex() {
        return this.activeIndex;
    }

    public String getIndexedField() {
        return this.indexedField;
    }
}