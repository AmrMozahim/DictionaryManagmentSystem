import java.util.ArrayList;
import java.util.List;

public class AVLTree<T extends Comparable<T>> {
    private AVLNode<T> root;

    public AVLTree() {
        root = null;
    }

    // Get height of tree
    private int getHeight(AVLNode<T> node) {
        if (node == null) return 0;

        int leftHeight = getHeight(node.getLeft());
        int rightHeight = getHeight(node.getRight());

        return Math.max(leftHeight, rightHeight) + 1;
    }

    // Get balance factor
    private int getBalance(AVLNode<T> node) {
        if (node == null) return 0;
        return getHeight(node.getLeft()) - getHeight(node.getRight());
    }

    // Right rotation
    private AVLNode<T> rotateRight(AVLNode<T> y) {
        AVLNode<T> x = y.getLeft();
        AVLNode<T> T2 = x.getRight();

        // Perform rotation
        x.setRight(y);
        y.setLeft(T2);

        return x;
    }

    // Left rotation
    private AVLNode<T> rotateLeft(AVLNode<T> x) {
        AVLNode<T> y = x.getRight();
        AVLNode<T> T2 = y.getLeft();

        // Perform rotation
        y.setLeft(x);
        x.setRight(T2);

        return y;
    }

    // Public insert method
    public void insert(T data) {
        root = insert(root, data);
    }

    // Private insert method
    private AVLNode<T> insert(AVLNode<T> node, T data) {
        if (node == null) {
            return new AVLNode<>(data);
        }

        // Normal BST insertion
        if (data.compareTo(node.getData()) < 0) {
            node.setLeft(insert(node.getLeft(), data));
        } else if (data.compareTo(node.getData()) > 0) {
            node.setRight(insert(node.getRight(), data));
        } else {
            return node; // Duplicate not allowed
        }

        // Get balance factor
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && data.compareTo(node.getLeft().getData()) < 0) {
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && data.compareTo(node.getRight().getData()) > 0) {
            return rotateLeft(node);
        }

        // Left Right Case
        if (balance > 1 && data.compareTo(node.getLeft().getData()) > 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        // Right Left Case
        if (balance < -1 && data.compareTo(node.getRight().getData()) < 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    // Search for data - FIXED
    public T search(T data) {
        AVLNode<T> current = root;

        while (current != null) {
            int cmp = data.compareTo(current.getData());

            if (cmp == 0) {
                return current.getData(); // Found
            } else if (cmp < 0) {
                current = current.getLeft();
            } else {
                current = current.getRight();
            }
        }

        return null; // Not found
    }

    // Delete data - FIXED
    public void delete(T data) {
        root = delete(root, data);
    }

    private AVLNode<T> delete(AVLNode<T> node, T data) {
        if (node == null) return null;

        // Find the node to delete
        if (data.compareTo(node.getData()) < 0) {
            node.setLeft(delete(node.getLeft(), data));
        } else if (data.compareTo(node.getData()) > 0) {
            node.setRight(delete(node.getRight(), data));
        } else {
            // Node found
            if (node.getLeft() == null && node.getRight() == null) {
                // Case 1: Leaf node
                return null;
            } else if (node.getLeft() == null) {
                // Case 2: Only right child
                return node.getRight();
            } else if (node.getRight() == null) {
                // Case 3: Only left child
                return node.getLeft();
            } else {
                // Case 4: Two children
                AVLNode<T> successor = getMinValueNode(node.getRight());
                node.setData(successor.getData());
                node.setRight(delete(node.getRight(), successor.getData()));
            }
        }

        // Check balance and rotate if needed
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && getBalance(node.getLeft()) >= 0) {
            return rotateRight(node);
        }

        // Left Right Case
        if (balance > 1 && getBalance(node.getLeft()) < 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && getBalance(node.getRight()) <= 0) {
            return rotateLeft(node);
        }

        // Right Left Case
        if (balance < -1 && getBalance(node.getRight()) > 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    // Get minimum value node
    private AVLNode<T> getMinValueNode(AVLNode<T> node) {
        AVLNode<T> current = node;
        while (current != null && current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    // Get all data in sorted order
    public List<T> getInOrder() {
        List<T> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(AVLNode<T> node, List<T> result) {
        if (node != null) {
            inOrderTraversal(node.getLeft(), result);
            result.add(node.getData());
            inOrderTraversal(node.getRight(), result);
        }
    }

    // Get tree height
    public int getTreeHeight() {
        return getHeight(root);
    }

    // Get number of nodes
    public int getSize() {
        return countNodes(root);
    }

    private int countNodes(AVLNode<T> node) {
        if (node == null) return 0;
        return 1 + countNodes(node.getLeft()) + countNodes(node.getRight());
    }

    // Search by Arabic meaning
    public Word searchByArabic(String arabic) {
        List<T> allWords = getInOrder();
        for (T word : allWords) {
            Word w = (Word) word;
            if (w.getArabicMeaning().equals(arabic)) {
                return w;
            }
        }
        return null;
    }

    // Get words by type
    public List<Word> getWordsByType(String type) {
        List<Word> result = new ArrayList<>();
        List<T> allWords = getInOrder();

        for (T word : allWords) {
            Word w = (Word) word;
            if (w.getType().equalsIgnoreCase(type)) {
                result.add(w);
            }
        }
        return result;
    }

    // Check if tree is empty
    public boolean isEmpty() {
        return root == null;
    }
}