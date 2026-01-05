import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AVLTree<T extends Comparable<T>> {
    private AVLNode<T> root;

    public AVLTree() {
        this.root = null;
    }

    private int height(AVLNode<T> node) {
        return node == null ? 0 : node.getHeight();
    }

    private int getBalance(AVLNode<T> node) {
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }

    private AVLNode<T> rotateRight(AVLNode<T> y) {
        AVLNode<T> x = y.getLeft();
        AVLNode<T> T2 = x.getRight();

        x.setRight(y);
        y.setLeft(T2);

        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);
        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);

        return x;
    }

    private AVLNode<T> rotateLeft(AVLNode<T> x) {
        AVLNode<T> y = x.getRight();
        AVLNode<T> T2 = y.getLeft();

        y.setLeft(x);
        x.setRight(T2);

        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);
        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);

        return y;
    }

    public void insert(T data) {
        root = insert(root, data);
    }

    private AVLNode<T> insert(AVLNode<T> node, T data) {
        if (node == null) {
            return new AVLNode<>(data);
        }

        if (data.compareTo(node.getData()) < 0) {
            node.setLeft(insert(node.getLeft(), data));
        } else if (data.compareTo(node.getData()) > 0) {
            node.setRight(insert(node.getRight(), data));
        } else {
            return node;
        }

        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));

        int balance = getBalance(node);

        if (balance > 1 && data.compareTo(node.getLeft().getData()) < 0) {
            return rotateRight(node);
        }

        if (balance < -1 && data.compareTo(node.getRight().getData()) > 0) {
            return rotateLeft(node);
        }

        if (balance > 1 && data.compareTo(node.getLeft().getData()) > 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        if (balance < -1 && data.compareTo(node.getRight().getData()) < 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    public T search(T data) {
        return search(root, data);
    }

    private T search(AVLNode<T> node, T data) {
        if (node == null) return null;

        if (data.compareTo(node.getData()) == 0) {
            return node.getData();
        } else if (data.compareTo(node.getData()) < 0) {
            return search(node.getLeft(), data);
        } else {
            return search(node.getRight(), data);
        }
    }

    public void delete(T data) {
        root = delete(root, data);
    }

    private AVLNode<T> delete(AVLNode<T> node, T data) {
        if (node == null) return null;

        if (data.compareTo(node.getData()) < 0) {
            node.setLeft(delete(node.getLeft(), data));
        } else if (data.compareTo(node.getData()) > 0) {
            node.setRight(delete(node.getRight(), data));
        } else {
            if (node.getLeft() == null || node.getRight() == null) {
                AVLNode<T> temp = null;
                if (temp == node.getLeft()) {
                    temp = node.getRight();
                } else {
                    temp = node.getLeft();
                }

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                AVLNode<T> temp = minValueNode(node.getRight());
                node.setData(temp.getData());
                node.setRight(delete(node.getRight(), temp.getData()));
            }
        }

        if (node == null) return null;

        node.setHeight(Math.max(height(node.getLeft()), height(node.getRight())) + 1);

        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.getLeft()) >= 0) {
            return rotateRight(node);
        }

        if (balance > 1 && getBalance(node.getLeft()) < 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.getRight()) <= 0) {
            return rotateLeft(node);
        }

        if (balance < -1 && getBalance(node.getRight()) > 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    private AVLNode<T> minValueNode(AVLNode<T> node) {
        AVLNode<T> current = node;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    public List<T> inOrderTraversal() {
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

    public int getTreeHeight() {
        return getTreeHeight(root);
    }

    private int getTreeHeight(AVLNode<T> node) {
        if (node == null) return 0;
        return node.getHeight();
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int getWordCount() {
        return inOrderTraversal().size();
    }

    // البحث بالعربي بدون Map
    public Word searchByArabic(String arabicMeaning) {
        List<Word> allWords = getAllWords();
        for (Word word : allWords) {
            if (word.getArabicMeaning().equals(arabicMeaning)) {
                return word;
            }
        }
        return null;
    }

    private List<Word> getAllWords() {
        List<T> allData = inOrderTraversal();
        List<Word> words = new ArrayList<>();
        for (T data : allData) {
            words.add((Word) data);
        }
        return words;
    }

    public List<Word> getWordsByType(String type) {
        List<Word> result = new ArrayList<>();
        getWordsByType(root, type, result);
        return result;
    }

    private void getWordsByType(AVLNode<T> node, String type, List<Word> result) {
        if (node != null) {
            getWordsByType(node.getLeft(), type, result);
            Word word = (Word) node.getData();
            if (word.getType().equalsIgnoreCase(type)) {
                result.add(word);
            }
            getWordsByType(node.getRight(), type, result);
        }
    }

    public Word getRandomWordByType(String type) {
        List<Word> wordsOfType = getWordsByType(type);
        if (!wordsOfType.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(wordsOfType.size());
            return wordsOfType.get(randomIndex);
        }
        return null;
    }
}