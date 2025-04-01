package AHuffman.test;


public class Test {
    public static void main(String[] args) {
        System.out.println("Testing...");
        
        // Test Node class
        // create a node and test all methods with appropriate assertions
        AHuffman.src.Node node = new AHuffman.src.Node('a', 5, 1, "001", null, null, null);
        
        // test methods
        assert node.isExternal() == true : "Node should be external";
        assert node.isRoot() == true : "Node should be root";
        assert node.isLeftChild(null) == true : "Node should not be a left child";
        assert node.isRightChild(null) == true : "Node should not be a right child";

        // test accessors
        assert node.getCharacter() == 'a' : "Character mismatch";
        assert node.getFrequency() == 5 : "Frequency mismatch";
        assert node.getNumber() == 1 : "Number mismatch";
        assert node.getCode().equals("001") : "Code mismatch";
        assert node.getLeft() == null : "Left child should be null";
        assert node.getRight() == null : "Right child should be null";
        assert node.getParent() == null : "Parent should be null";

        // test mutators
        node.incrementFrequency();
        assert node.getFrequency() == 6 : "Frequency should be incremented to 6";
        
        AHuffman.src.Node leftChild = new AHuffman.src.Node('b', 3, 2, "0010", null, null, node);
        node.setLeft(leftChild);
        assert node.getLeft() == leftChild : "Left child should be set correctly";
        
        AHuffman.src.Node rightChild = new AHuffman.src.Node('d', 2, 4, "0011", null, null, node);
        node.setRight(rightChild);
        assert node.getRight() == rightChild : "Right child should be set correctly";
        
        AHuffman.src.Node parent = new AHuffman.src.Node('c', 7, 3, "00", null, null, null);
        node.setParent(parent);
        assert node.getParent() == parent : "Parent should be set correctly";

        assert node.isLeftChild(leftChild) == true : "Node should be a left child of its parent";
        assert node.isRightChild(rightChild) == true : "Node should be a right child of its parent";
        assert node.isParent(parent) == true : "Node should be a parent of its parent";
        assert leftChild.isParent(node) == true : "Left child should be a parent of the node";
        assert rightChild.isParent(node) == true : "Right child should be a parent of the node";

    }
}
