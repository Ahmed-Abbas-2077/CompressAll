package SHuffman;

public class Node implements Comparable<Node> {
    char symbol; 
    int frequency; 
    Node left; 
    Node right; 

    public Node(char symbol, int frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    @Override
    public int compareTo(Node other) {
        return this.frequency - other.frequency;
    }
}
