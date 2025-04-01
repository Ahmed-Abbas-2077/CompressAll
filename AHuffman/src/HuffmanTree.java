package AHuffman.src;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class HuffmanTree {

    private Node root;
    private int e;
    private Map<Character, String> vocabulary;
    private Map<Character, Boolean> encounter;
    private Node NYT;
    private int initialNumber;

    public HuffmanTree(Map<Character, String> vocabulary) {
        this.vocabulary = vocabulary;
        this.e = vocabulary.values().iterator().next().length(); // all codes have the same length
        this.encounter = new HashMap<>();
        this.initialNumber = vocabulary.size() * 2 - 1; // number of nodes in the tree
        this.root = new Node(null, 0, initialNumber, "", null, null, null); // root node
        this.NYT = this.root;
    }


    // getters
    public Node getRoot() {
        return root;
    }

    public int getE() {
        return e;
    }

    public Map<Character, String> getVocabulary() {
        return vocabulary;
    }   

    public Map<Character, Boolean> getEncounter() {
        return encounter;
    }

    public Node getNYT() {
        return NYT;
    }

    public int getInitialNumber() {
        return initialNumber;
    }


    // setters
    public void setRoot(Node root) {
        this.root = root;
    }

    public void setE(int e) {
        this.e = e;
    }

    public void setVocabulary(Map<Character, String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void setEncounter(Map<Character, Boolean> encounter) {
        this.encounter = encounter;
    }

    public void setNYT(Node NYT) {
        this.NYT = NYT;
    }

    public void setInitialNumber(int initialNumber) {
        this.initialNumber = initialNumber;
    }



    // methods

    // recursively search for node in the tree, with a flag that signals to stop the recursion
    public Node search(Node node, Character character) {
        if (node == null) {
            return null;
        }
        if (node.getCharacter() != null && node.getCharacter().equals(character)) {
            return node;
        }
        Node leftResult = search(node.left, character);
        if (leftResult != null) {
            return leftResult;
        }
        return search(node.right, character);
    }

    
    Node addNew(Character character) {
        int nodeNumber = NYT.number + 2; // number of the node; right child of NYT 
        String nodeCode = NYT.code + "1"; // code of the new node
        Node newNode = new Node(character, 1, nodeNumber, nodeCode, null, null, NYT); 
        NYT.right = newNode;
        NYT.left = new Node(null, 0, nodeNumber - 1, NYT.code + "0", null, null, NYT); // left child of NYT
        this.NYT = NYT.left;
        this.encounter.put(character, true); // mark character as encountered
        return newNode;
    }

    Node incrementChar(Character character) {
        Node node = search(root, character);
        if (node != null) {
            node.frequency++;
            return node;
        } else {
            throw new IllegalArgumentException("Character not found in the tree.");
        }
    }

    // implement using index instead of queue
    List<Node> getAllNodes() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(root);
        
        for (int i = 0; i < nodes.size(); i++) {
            Node current = nodes.get(i);
            
            if (current.getLeft() != null) {
                nodes.add(current.getLeft());
            }
            if (current.getRight() != null) {
                nodes.add(current.getRight());
            }
        }
        
        return nodes;
    }

}
