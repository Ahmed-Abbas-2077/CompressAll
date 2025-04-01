package AHuffman.src;


public class Node {
    char character;
    int frequency;
    int number;
    String code;
    Node left, right;
    Node parent;

    // constructor
    public Node(char character, int frequency, int number, String code, Node left, Node right, Node parent) {
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



    // getters
    public int getFrequency() {
        return frequency;
    }

    public char getCharacter() {
        return character;
    }

    public int getNumber() {
        return number;
    }

    public String getCode() {
        return code;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Node getParent() {
        return parent;
    }

    
        
    // setters
    public void incrementFrequency() {
        this.frequency++;
    }
    
    public void setNumber(int number) {
        this.number = number;
    }    
    
    public void setCode(String code) {
        this.code = code;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    
}