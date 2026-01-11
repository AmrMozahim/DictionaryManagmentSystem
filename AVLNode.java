public class AVLNode<T extends Comparable<T>> {
    private T data;
    private AVLNode<T> left;
    private AVLNode<T> right;

    public AVLNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public AVLNode<T> getLeft() { return left; }
    public void setLeft(AVLNode<T> left) { this.left = left; }

    public AVLNode<T> getRight() { return right; }
    public void setRight(AVLNode<T> right) { this.right = right; }

    public boolean hasLeft() { return left != null; }
    public boolean hasRight() { return right != null; }
    public boolean isLeaf() { return left == null && right == null; }
}