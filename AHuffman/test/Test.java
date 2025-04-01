package AHuffman.test;

import java.util.Map;


public class Test {
    public static void main(String[] args) {
        System.out.println("Testing...");
        
        // Test HuffmanTree class
        Map<Character, String> vocabulary = Map.of(
            'a', "00",
            'b', "01",
            'c', "10",
            'd', "11"
        );
        AHuffman.src.HuffmanTree huffmanTree = new AHuffman.src.HuffmanTree(vocabulary);
        System.out.println("HuffmanTree created with vocabulary: ");
        for (Map.Entry<Character, String> entry : vocabulary.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // add some characters to the tree
        huffmanTree.updateTree('a');
        huffmanTree.updateTree('a');
        huffmanTree.updateTree('b');
        huffmanTree.updateTree('b');
        huffmanTree.updateTree('b');
        huffmanTree.updateTree('c');
        huffmanTree.updateTree('c');
        huffmanTree.updateTree('c');
        huffmanTree.updateTree('c');
        huffmanTree.updateTree('d');
        huffmanTree.updateTree('d');
        huffmanTree.updateTree('d');
        huffmanTree.updateTree('d');
        huffmanTree.updateTree('d');
        huffmanTree.printTree(huffmanTree.root);
    }
}
