public class BPlusTree<K extends Comparable<K>, V> {

    private BPlusTreeNode<K, V> root;
    private final int t;
    private long comparisons;
    private BPlusTreeNode<K, V> firstLeaf;

    public BPlusTree(int t) {
        if (t < 2) {
            throw new IllegalArgumentException("Grau minimo t deve ser >= 2");
        }
        this.t = t;
        this.root = new BPlusTreeNode<>(t, true, null);
        this.firstLeaf = root;
        this.comparisons = 0;
    }

    public void resetComparisons() {
        this.comparisons = 0;
    }

    public long getComparisons() {
        return this.comparisons;
    }

    public int getHeight() {
        int h = 1;
        BPlusTreeNode<K, V> node = root;
        while (!node.leaf) {
            node = node.children.get(0);
            h++;
        }
        return h;
    }

    public V search(K key) {
        BPlusTreeNode<K, V> leaf = findLeaf(key);
        int idx = leaf.findKeyIndex(key);

        comparisons++;
        if (idx < leaf.n && leaf.keys.get(idx).compareTo(key) == 0) {
            return leaf.values.get(idx);
        }
        return null;
    }

    private BPlusTreeNode<K, V> findLeaf(K key) {
        BPlusTreeNode<K, V> node = root;
        while (!node.leaf) {
            int idx = 0;
            while (idx < node.n) {
                comparisons++;
                if (key.compareTo(node.keys.get(idx)) >= 0) {
                    idx++;
                } else {
                    break;
                }
            }
            node = node.children.get(idx);
        }
        return node;
    }

    public void insert(K key, V value) {
        BPlusTreeNode<K, V> leaf = findLeaf(key);
        insertIntoLeaf(leaf, key, value);

        if (leaf.n == 2 * t - 1) {
            splitLeaf(leaf);
        }
    }

    private void insertIntoLeaf(BPlusTreeNode<K, V> leaf, K key, V value) {
        int idx = leaf.findKeyIndex(key);

        leaf.keys.add(idx, key);
        leaf.values.add(idx, value);
        leaf.n++;
    }

    private void splitLeaf(BPlusTreeNode<K, V> leaf) {
        int mid = (leaf.n + 1) / 2;

        BPlusTreeNode<K, V> newLeaf = new BPlusTreeNode<>(t, true, leaf.parent);

        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.n));
        newLeaf.values.addAll(leaf.values.subList(mid, leaf.n));
        newLeaf.n = newLeaf.keys.size();

        leaf.keys.subList(mid, leaf.n).clear();
        leaf.values.subList(mid, leaf.n).clear();
        leaf.n = mid;

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        K keyToParent = newLeaf.keys.get(0);
        insertIntoParent(leaf, keyToParent, newLeaf);
    }

    private void insertIntoParent(BPlusTreeNode<K, V> leftChild, K key, BPlusTreeNode<K, V> rightChild) {
        BPlusTreeNode<K, V> parent = leftChild.parent;

        if (parent == null) {
            root = new BPlusTreeNode<>(t, false, null);
            root.keys.add(key);
            root.children.add(leftChild);
            root.children.add(rightChild);
            root.n = 1;
            leftChild.parent = root;
            rightChild.parent = root;
            return;
        }

        int idx = parent.children.indexOf(leftChild);
        parent.keys.add(idx, key);
        parent.children.add(idx + 1, rightChild);
        parent.n++;
        rightChild.parent = parent;

        if (parent.n == 2 * t - 1) {
            splitInternal(parent);
        }
    }

    private void splitInternal(BPlusTreeNode<K, V> node) {
        int mid = (node.n + 1) / 2 - 1;
        K keyToParent = node.keys.get(mid);

        BPlusTreeNode<K, V> newNode = new BPlusTreeNode<>(t, false, node.parent);

        newNode.keys.addAll(node.keys.subList(mid + 1, node.n));
        newNode.children.addAll(node.children.subList(mid + 1, node.n + 1));
        newNode.n = newNode.keys.size();

        for (BPlusTreeNode<K, V> child : newNode.children) {
            child.parent = newNode;
        }

        node.keys.subList(mid, node.n).clear();
        node.children.subList(mid + 1, node.n + 1).clear();
        node.n = node.keys.size();

        insertIntoParent(node, keyToParent, newNode);
    }

    public void delete(K key) {
        BPlusTreeNode<K, V> leaf = findLeaf(key);
        deleteEntry(leaf, key);
    }

    private void deleteEntry(BPlusTreeNode<K, V> node, K key) {
        int idx = node.findKeyIndex(key);

        comparisons++;
        if (idx == node.n || node.keys.get(idx).compareTo(key) != 0) {
            return;
        }

        node.keys.remove(idx);
        node.values.remove(idx);
        node.n--;

        if (node == root) {
            return;
        }

        if (node.n < (t - 1)) {
            handleUnderflow(node);
        }
    }

    private void handleUnderflow(BPlusTreeNode<K, V> node) {
        if (node == root) {
            if (node.n == 0 && !node.leaf && !node.children.isEmpty()) {
                root = node.children.get(0);
                root.parent = null;
            }
            return;
        }

        BPlusTreeNode<K, V> parent = node.parent;
        int idxInParent = parent.children.indexOf(node);

        BPlusTreeNode<K, V> prevSibling = (idxInParent > 0) ? parent.children.get(idxInParent - 1) : null;
        BPlusTreeNode<K, V> nextSibling = (idxInParent < parent.n) ? parent.children.get(idxInParent + 1) : null;

        int prevKeyIdx = idxInParent - 1;
        int nextKeyIdx = idxInParent;

        if (prevSibling != null && prevSibling.n >= t) {
            redistributeFromPrev(node, prevSibling, parent, prevKeyIdx);
        } else if (nextSibling != null && nextSibling.n >= t) {
            redistributeFromNext(node, nextSibling, parent, nextKeyIdx);
        } else if (prevSibling != null) {
            mergeWithPrev(node, prevSibling, parent, prevKeyIdx);
        } else if (nextSibling != null) {
            mergeWithNext(node, nextSibling, parent, nextKeyIdx);
        }
    }

    private void redistributeFromPrev(BPlusTreeNode<K, V> node, BPlusTreeNode<K, V> prevSibling, BPlusTreeNode<K, V> parent, int parentKeyIdx) {
        if (node.leaf) {
            node.keys.add(0, prevSibling.keys.get(prevSibling.n - 1));
            node.values.add(0, prevSibling.values.get(prevSibling.n - 1));
            prevSibling.keys.remove(prevSibling.n - 1);
            prevSibling.values.remove(prevSibling.n - 1);
            node.n++;
            prevSibling.n--;
            parent.keys.set(parentKeyIdx, node.keys.get(0));
        } else {
            node.keys.add(0, parent.keys.get(parentKeyIdx));
            node.children.add(0, prevSibling.children.get(prevSibling.n));
            prevSibling.children.get(prevSibling.n).parent = node;

            parent.keys.set(parentKeyIdx, prevSibling.keys.get(prevSibling.n - 1));

            prevSibling.keys.remove(prevSibling.n - 1);
            prevSibling.children.remove(prevSibling.n);
            node.n++;
            prevSibling.n--;
        }
    }

    private void redistributeFromNext(BPlusTreeNode<K, V> node, BPlusTreeNode<K, V> nextSibling, BPlusTreeNode<K, V> parent, int parentKeyIdx) {
        if (node.leaf) {
            node.keys.add(nextSibling.keys.get(0));
            node.values.add(nextSibling.values.get(0));
            nextSibling.keys.remove(0);
            nextSibling.values.remove(0);
            node.n++;
            nextSibling.n--;
            parent.keys.set(parentKeyIdx, nextSibling.keys.get(0));
        } else {
            node.keys.add(parent.keys.get(parentKeyIdx));
            node.children.add(nextSibling.children.get(0));
            nextSibling.children.get(0).parent = node;

            parent.keys.set(parentKeyIdx, nextSibling.keys.get(0));

            nextSibling.keys.remove(0);
            nextSibling.children.remove(0);
            node.n++;
            nextSibling.n--;
        }
    }

    private void mergeWithPrev(BPlusTreeNode<K, V> node, BPlusTreeNode<K, V> prevSibling, BPlusTreeNode<K, V> parent, int parentKeyIdx) {
        if (node.leaf) {
            prevSibling.keys.addAll(node.keys);
            prevSibling.values.addAll(node.values);
            prevSibling.n += node.n;
            prevSibling.next = node.next;
        } else {
            prevSibling.keys.add(parent.keys.get(parentKeyIdx));
            prevSibling.keys.addAll(node.keys);
            prevSibling.children.addAll(node.children);
            for (BPlusTreeNode<K, V> child : node.children) {
                child.parent = prevSibling;
            }
            prevSibling.n += node.n + 1;
        }

        parent.keys.remove(parentKeyIdx);
        parent.children.remove(parentKeyIdx + 1);
        parent.n--;

        if (parent == root && parent.n == 0) {
            root = prevSibling;
            prevSibling.parent = null;
        } else if (parent.n < (t - 1)) {
            handleUnderflow(parent);
        }
    }

    private void mergeWithNext(BPlusTreeNode<K, V> node, BPlusTreeNode<K, V> nextSibling, BPlusTreeNode<K, V> parent, int parentKeyIdx) {
        if (node.leaf) {
            node.keys.addAll(nextSibling.keys);
            node.values.addAll(nextSibling.values);
            node.n += nextSibling.n;
            node.next = nextSibling.next;
        } else {
            node.keys.add(parent.keys.get(parentKeyIdx));
            node.keys.addAll(nextSibling.keys);
            node.children.addAll(nextSibling.children);
            for (BPlusTreeNode<K, V> child : nextSibling.children) {
                child.parent = node;
            }
            node.n += nextSibling.n + 1;
        }

        parent.keys.remove(parentKeyIdx);
        parent.children.remove(parentKeyIdx + 1);
        parent.n--;

        if (parent == root && parent.n == 0) {
            root = node;
            node.parent = null;
        } else if (parent.n < (t - 1)) {
            handleUnderflow(parent);
        }
    }
}