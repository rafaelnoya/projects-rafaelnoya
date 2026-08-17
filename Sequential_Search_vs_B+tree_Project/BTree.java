import java.util.ArrayList;
import java.util.List;

public class BTree<K extends Comparable<K>, V> {

    private BTreeNode<K, V> root;
    private final int t;
    private long comparisons;

    public BTree(int t) {
        if (t < 2) {
            throw new IllegalArgumentException("Grau minimo t deve ser >= 2");
        }
        this.t = t;
        this.root = new BTreeNode<>(t, true);
        this.comparisons = 0;
    }

    public void resetComparisons() {
        this.comparisons = 0;
    }

    public long getComparisons() {
        return this.comparisons;
    }

    public int getHeight() {
        return calculateHeight(this.root);
    }

    private int calculateHeight(BTreeNode<K, V> node) {
        if (node.leaf) {
            return 1;
        }
        return 1 + calculateHeight(node.children.get(0));
    }

    public V search(K key) {
        return search(root, key);
    }

    private V search(BTreeNode<K, V> node, K key) {
        int i = 0;
        while (i < node.n && key.compareTo(node.keys.get(i)) > 0) {
            comparisons++;
            i++;
        }

        if (i < node.n) {
            comparisons++;
            if (key.compareTo(node.keys.get(i)) == 0) {
                return node.values.get(i);
            }
        }

        if (node.leaf) {
            return null;
        }

        return search(node.children.get(i), key);
    }

    public void insert(K key, V value) {
        BTreeNode<K, V> r = root;
        if (r.n == 2 * t - 1) {
            BTreeNode<K, V> s = new BTreeNode<>(t, false);
            s.children.add(r);
            splitChild(s, 0);
            root = s;
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    private void insertNonFull(BTreeNode<K, V> node, K key, V value) {
        int i = node.n - 1;
        if (node.leaf) {
            node.keys.add(null);
            node.values.add(null);
            while (i >= 0) {
                comparisons++;
                if (key.compareTo(node.keys.get(i)) < 0) {
                    node.keys.set(i + 1, node.keys.get(i));
                    node.values.set(i + 1, node.values.get(i));
                    i--;
                } else {
                    break;
                }
            }
            node.keys.set(i + 1, key);
            node.values.set(i + 1, value);
            node.n++;
        } else {
            while (i >= 0) {
                comparisons++;
                if (key.compareTo(node.keys.get(i)) < 0) {
                    i--;
                } else {
                    break;
                }
            }
            i++;
            if (node.children.get(i).n == 2 * t - 1) {
                splitChild(node, i);
                comparisons++;
                if (key.compareTo(node.keys.get(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children.get(i), key, value);
        }
    }

    private void splitChild(BTreeNode<K, V> parent, int childIndex) {
        BTreeNode<K, V> childToSplit = parent.children.get(childIndex);
        BTreeNode<K, V> newNode = new BTreeNode<>(t, childToSplit.leaf);
        newNode.n = t - 1;

        for (int j = 0; j < t - 1; j++) {
            newNode.keys.add(childToSplit.keys.get(j + t));
            newNode.values.add(childToSplit.values.get(j + t));
        }

        if (!childToSplit.leaf) {
            for (int j = 0; j < t; j++) {
                newNode.children.add(childToSplit.children.get(j + t));
            }
        }

        childToSplit.n = t - 1;
        parent.children.add(childIndex + 1, newNode);
        parent.keys.add(childIndex, childToSplit.keys.get(t - 1));
        parent.values.add(childIndex, childToSplit.values.get(t - 1));
        parent.n++;

        childToSplit.keys.subList(t - 1, childToSplit.keys.size()).clear();
        childToSplit.values.subList(t - 1, childToSplit.values.size()).clear();
        if (!childToSplit.leaf) {
            childToSplit.children.subList(t, childToSplit.children.size()).clear();
        }
    }

    public void delete(K key) {
        delete(root, key);
        if (root.n == 0 && !root.leaf) {
            root = root.children.get(0);
        }
    }

    private void delete(BTreeNode<K, V> node, K key) {
        int idx = node.findKey(key);

        comparisons += (idx > 0 ? idx : 1);

        if (idx < node.n && node.keys.get(idx).compareTo(key) == 0) {
            comparisons++;
            if (node.leaf) {
                removeFromLeaf(node, idx);
            } else {
                removeFromInternal(node, idx);
            }
        } else {
            if (node.leaf) {
                return;
            }

            boolean isLastChild = (idx == node.n);
            BTreeNode<K, V> child = node.children.get(idx);

            if (child.n < t) {
                fill(node, idx);
            }

            if (isLastChild && idx > node.n) {
                delete(node.children.get(idx - 1), key);
            } else {
                delete(node.children.get(idx), key);
            }
        }
    }

    private void removeFromLeaf(BTreeNode<K, V> node, int idx) {
        node.removeKey(idx);
    }

    private void removeFromInternal(BTreeNode<K, V> node, int idx) {
        K key = node.keys.get(idx);
        BTreeNode<K, V> prevChild = node.children.get(idx);
        BTreeNode<K, V> nextChild = node.children.get(idx + 1);

        if (prevChild.n >= t) {
            K prevKey = getPredecessorKey(prevChild);
            V prevVal = getPredecessorValue(prevChild);
            node.keys.set(idx, prevKey);
            node.values.set(idx, prevVal);
            delete(prevChild, prevKey);
        } else if (nextChild.n >= t) {
            K nextKey = getSuccessorKey(nextChild);
            V nextVal = getSuccessorValue(nextChild);
            node.keys.set(idx, nextKey);
            node.values.set(idx, nextVal);
            delete(nextChild, nextKey);
        } else {
            merge(node, idx);
            delete(prevChild, key);
        }
    }

    private K getPredecessorKey(BTreeNode<K, V> node) {
        while (!node.leaf) {
            node = node.children.get(node.n);
        }
        return node.keys.get(node.n - 1);
    }

    private V getPredecessorValue(BTreeNode<K, V> node) {
        while (!node.leaf) {
            node = node.children.get(node.n);
        }
        return node.values.get(node.n - 1);
    }

    private K getSuccessorKey(BTreeNode<K, V> node) {
        while (!node.leaf) {
            node = node.children.get(0);
        }
        return node.keys.get(0);
    }

    private V getSuccessorValue(BTreeNode<K, V> node) {
        while (!node.leaf) {
            node = node.children.get(0);
        }
        return node.values.get(0);
    }

    private void fill(BTreeNode<K, V> node, int idx) {
        if (idx != 0 && node.children.get(idx - 1).n >= t) {
            borrowFromPrev(node, idx);
        } else if (idx != node.n && node.children.get(idx + 1).n >= t) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.n) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(BTreeNode<K, V> node, int idx) {
        BTreeNode<K, V> child = node.children.get(idx);
        BTreeNode<K, V> sibling = node.children.get(idx - 1);

        child.keys.add(0, node.keys.get(idx - 1));
        child.values.add(0, node.values.get(idx - 1));

        if (!child.leaf) {
            child.children.add(0, sibling.children.get(sibling.n));
        }

        node.keys.set(idx - 1, sibling.keys.get(sibling.n - 1));
        node.values.set(idx - 1, sibling.values.get(sibling.n - 1));

        sibling.removeKey(sibling.n - 1);
        if (!child.leaf) {
            sibling.removeChild(sibling.n + 1);
        }
        child.n++;
    }

    private void borrowFromNext(BTreeNode<K, V> node, int idx) {
        BTreeNode<K, V> child = node.children.get(idx);
        BTreeNode<K, V> sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));
        child.values.add(node.values.get(idx));

        if (!child.leaf) {
            child.children.add(sibling.children.get(0));
        }

        node.keys.set(idx, sibling.keys.get(0));
        node.values.set(idx, sibling.values.get(0));

        sibling.removeKey(0);
        if (!child.leaf) {
            sibling.removeChild(0);
        }
        child.n++;
    }

    private void merge(BTreeNode<K, V> node, int idx) {
        BTreeNode<K, V> child = node.children.get(idx);
        BTreeNode<K, V> sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));
        child.values.add(node.values.get(idx));

        for (int i = 0; i < sibling.n; ++i) {
            child.keys.add(sibling.keys.get(i));
            child.values.add(sibling.values.get(i));
        }

        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; ++i) {
                child.children.add(sibling.children.get(i));
            }
        }

        node.removeKey(idx);
        node.removeChild(idx + 1);

        child.n += sibling.n + 1;
    }
}