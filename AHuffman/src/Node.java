package AHuffman.src;


public class Node {
    public Character character;
    public int frequency;
    public int number;
    public String code;
    public Node left, right;
    public Node parent;

    // constructor
    Node(Character character, int frequency, int number, String code, Node left, Node right, Node parent) {
        this.character = character;
        this.frequency = frequency;
        this.number = number;
        this.code = code;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }



    // util methods
    Boolean isExternal() {
        return (left == null && right == null);
    }

    Boolean isRoot() {
        return (parent == null);
    }

    Boolean isRightChild(Node node) {
        return (this.right == node);
    }

    Boolean isLeftChild(Node node) {
        return (this.left == node);
    }

    Boolean isParent(Node node) {
        return (this.parent == node);
    }

    void incrementFrequency() {
        this.frequency++;
        System.out.println("\nIncrementing frequency of " + this.character + " to " + this.frequency + "\n");
    }
}