package AHuffman.test;

import java.util.Map;
import Util.Util;

public class Test {
    public static void main(String[] args) {
        System.out.println("Testing...");
        Boolean flag = testEncoder("test/test.txt", "test/test.bin");
        if (flag) {
            System.out.println("Encoder test passed.");
        } else {
            System.out.println("Encoder test failed.");
        }
    }

    static void testHuffmanTree() {
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

    static Boolean testEncoder(String textPath, String binPath) {
        // Test Encoder class
        System.out.println("Testing Encoder...");

        AHuffman.src.Encoder encoder = new AHuffman.src.Encoder();

        String text = Util.readText(textPath);
        if (text == null) {
            System.out.println("Failed to read text file.");
            return false;
        }
        System.out.println("Text read from file: " + text);

        Map<Character, String> vocab = encoder.getVocab(text);
        System.out.println("Vocabulary: ");
        for (Map.Entry<Character, String> entry : vocab.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        Boolean result = encoder.encode(textPath, binPath);
        if (result) {
            System.out.println("Encoding successful. Binary file created at: " + binPath);
            System.out.println("Encoded binary data: ");
            byte[] binaryData = Util.readBinary(binPath);
            String binaryString = Util.textify(binaryData);
            System.out.println(binaryString);
        } else {
            System.out.println("Encoding failed.");
        }
        return result;
    }
}
