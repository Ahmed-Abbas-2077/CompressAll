package AHuffman.src;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class HuffmanTree {

    public Node root;
    int e;
    int size;
    Map<Character, String> vocabulary;
    Map<Character, Boolean> encounter;
    Node NYT;
    int initialNumber;

    public HuffmanTree(Map<Character, String> vocabulary) {
        this.vocabulary = vocabulary;
        this.e = vocabulary.values().iterator().next().length(); // all codes have the same length
        this.encounter = new HashMap<>();
        this.initialNumber = vocabulary.size() * 2 - 1; // number of nodes in the tree
        this.root = new Node(null, 0, initialNumber, "", null, null, null); // root node
        this.NYT = this.root;
        this.size = 0;
    }

    // methods

    // recursively search for node in the tree, with a flag that signals to stop the recursion
    Node search(Node node, Character character) {
        if (node == null) {
            return null;
        }
        if (node.character != null && node.character.equals(character)) {
            return node;
        }
        Node leftResult = search(node.left, character);
        if (leftResult != null) {
            return leftResult;
        }
        return search(node.right, character);
    }

    Node searchCode(Node node, String code) {
        if (node == null) {
            return null;
        }
        if (code.equals("")) {
            return node; // return the root node if code is empty
        }

        if (code.charAt(0) == '0') {
            return searchCode(node.left, code.substring(1)); // go left
        } else if (code.charAt(0) == '1') {
            return searchCode(node.right, code.substring(1)); // go right
        } else {
            return null; // invalid code
        }
    }

    
    Node addNew(Character character) {
        System.out.println("\nCurrent NYT node: " + NYT.code);
        // frequency incerementing will be done in the updateTree method
        NYT.right = new Node(character, 0, NYT.number - 1, NYT.code + "1", null, null, NYT);
        NYT.left = new Node(null, 0, NYT.number - 2, NYT.code + "0", null, null, NYT);
        this.encounter.put(character, true); // mark character as encountered
        this.size += 2; // increase size of the tree by 2
        System.out.println("Adding new character: " + character + " with code: " + NYT.right.code);
        this.NYT = NYT.left;
        System.out.println("New NYT node: " + NYT.code);
        return NYT.parent.right; // return the new node
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
            
            if (current.left != null) {
                nodes.add(current.left);
            }
            if (current.right != null) {
                nodes.add(current.right);
            }
        }
        
        return nodes;
    }

    List<Node> getBlock(Node node) {
        List<Node> block = new ArrayList<>();
        
        List<Node> allNodes = getAllNodes();
        for (Node n : allNodes) {
            if (n.frequency == node.frequency) {
                block.add(n);
            }
        }
        System.out.println("Block of " + node.character + ": " + block.size() + " nodes with frequency: " + node.frequency);
        System.out.println("Block: ");
        for (Node n : block) {
            System.out.println(n.character + " " + n.frequency + " " + n.code + " " + n.number);
        }
        return block;
    }

    void assignChildrenCodes(Node node) {
        if (node == null) {
            return;
        }
        if (node.left != null) {
            node.left.code = node.code + "0";
            assignChildrenCodes(node.left);
        }
        if (node.right != null) {
            node.right.code = node.code + "1";
            assignChildrenCodes(node.right);
        }
    }

    void assignChildrenNumbers(Node node) {
        if (node == null) {
            return;
        }
        if (node.left != null) {
            node.left.number = node.number - 2;
            if(node.number % 2 == 0) {
                node.left.number--;
            }
            assignChildrenNumbers(node.left);
        }
        if (node.right != null) {
            node.right.number = node.number - 1;
            if(node.number % 2 == 0) {
                node.right.number--;
            }
            assignChildrenNumbers(node.right);
        }
    }

    Node switchNodes(Node node1, Node node2) {

        System.out.println("\nSwitching nodes: " + node1.character + " and " + node2.character);
        System.out.println("                 " + node1.code + " and " + node2.code);
        System.out.println("                 " + node1.number + " and " + node2.number);
        System.out.println("                 " + node1.frequency + " and " + node2.frequency);
        Node parent1 = node1.parent;
        Node parent2 = node2.parent;

        Boolean isLeftChild1 = parent1.isLeftChild(node1);
        Boolean isLeftChild2 = parent2.isLeftChild(node2);

        if(isLeftChild1) {
            parent1.left = node2;
        } else {
            parent1.right = node2;
        }

        if(isLeftChild2) {
            parent2.left = node1;
        } else {
            parent2.right = node1;
        }

        node1.parent = parent2;
        node2.parent = parent1;
        
        assignChildrenCodes(this.root);
        assignChildrenNumbers(this.root);
        

        System.out.println("Switched nodes: " + node1.character + " and " + node2.character);
        System.out.println("                 " + node1.code + " and " + node2.code);
        System.out.println("                 " + node1.number + " and " + node2.number);
        System.out.println("                 " + node1.frequency + " and " + node2.frequency);

        return node1; // return the first node
    }

    public void updateTree(Character character) {
        Node node = new Node(null, 0, 0, "", null, null, null);
        
        if (encounter.get(character) == null) {
            node = addNew(character);
        } else {
            node = search(root, character);
        }

        while (node != null) {
            List<Node> block = getBlock(node);
            Node maxBlockNode = node;
            
            for (Node n : block) {
                if (n.number > maxBlockNode.number && n != node && !node.isParent(n)) {
                    maxBlockNode = n;
                }
            }

            if (maxBlockNode != node) {
                switchNodes(node, maxBlockNode);
            }

            node.incrementFrequency();
            
            if (node.isRoot()) {
                break;
            } else {
                node = node.parent;
            }
        }
    }

    // make it print like a tree, using its size
    public void printTree(Node node) {
        if (node == null) {
            return;
        }
        System.out.println(node.character + " " + node.frequency + " " + node.code);
        printTree(node.left);
        printTree(node.right);
    }
}
