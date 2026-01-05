public class DNode {
    private char letter;
    private AVLTree<Word> tree;
    private DNode next;
    private DNode prev;

    public DNode(char letter) {
        this.letter = Character.toUpperCase(letter);
        this.tree = new AVLTree<>();
        this.next = null;
        this.prev = null;
    }

    public char getLetter() { return letter; }
    public AVLTree<Word> getTree() { return tree; }
    public DNode getNext() { return next; }
    public DNode getPrev() { return prev; }

    public void setNext(DNode next) { this.next = next; }
    public void setPrev(DNode prev) { this.prev = prev; }
}