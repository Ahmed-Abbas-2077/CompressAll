package AHuffman.src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AHuffman.visualization.HTreeVisualizer;
import Util.Util;

public class AdaptiveHuffman {

    public static void main(String[] args) {
        String textFile1 = "test/DNA.txt";
        String textFile2 = "test/FA.txt";
        String textFile3 = "test/WoN.txt";

        String binFile1 = "test/DNA.bin";
        String binFile2 = "test/FA.bin";
        String binFile3 = "test/WoN.bin";

        String decodedFile1 = "test/DNA_decoded.txt";
        String decodedFile2 = "test/FA_decoded.txt";
        String decodedFile3 = "test/WoN_decoded.txt";

        // encode
        Encoder encoder = new Encoder();
        encoder.encode(textFile1, binFile1);
        encoder.encode(textFile2, binFile2);
        encoder.encode(textFile3, binFile3);

        // decode
        Decoder decoder = new Decoder();
        decoder.decode(binFile1, decodedFile1, Util.getVocab(Util.readText(textFile1)));
        decoder.decode(binFile2, decodedFile2, Util.getVocab(Util.readText(textFile2)));
        decoder.decode(binFile3, decodedFile3, Util.getVocab(Util.readText(textFile3)));

        // display compression statistics
        Util.printCompressionStatistics(textFile1, binFile1, decodedFile1);
        Util.printCompressionStatistics(textFile2, binFile2, decodedFile2);
        Util.printCompressionStatistics(textFile3, binFile3, decodedFile3);

        // visualize encoding process
        int delayMs = 1000; // Delay in milliseconds for visualization
        visualizeEncoding(textFile1, delayMs);
    }

    public static void visualizeEncoding(String textPath, int delayMs) {
        String text = Util.readText(textPath);
        if (text == null) {
            System.err.println("Error reading text file.");
            return;
        }
        
        Map<Character, String> vocab = Util.getVocab(text);
        HuffmanTree tree = new HuffmanTree(vocab);
        
        // Initialize visualizer
        HTreeVisualizer visualizer = new HTreeVisualizer();
        visualizer.visualize(tree);
        
        System.out.println("Visualizing encoding process for: " + textPath);
        System.out.println("Initial vocabulary: " + vocab);
        
        // For tracking node changes
        List<Node> oldNodes = tree.getAllNodes();
        Map<Integer, Node> nodeMap = new HashMap<>();
        
        // Process text character by character
        for (char c : text.toCharArray()) {
            try {
                // Store old state
                oldNodes = tree.getAllNodes();
                for (Node n : oldNodes) {
                    nodeMap.put(n.number, n);
                }
                
                // Update tree with current character
                if (tree.encounter.get(c) == null) {
                    System.out.println("New character: " + c + " (NYT + " + vocab.get(c) + ")");
                } else {
                    Node matchNode = tree.search(tree.root, c);
                    System.out.println("Existing character: " + c + " (code: " + matchNode.code + ")");
                    // Highlight frequency change
                    visualizer.highlightFrequencyChange(matchNode, c);
                    Thread.sleep(delayMs / 2);
                }
                
                // Update tree structure
                tree.updateTree(c);
                
                // Find and highlight node swaps by comparing node positions
                List<Node> newNodes = tree.getAllNodes();
                for (Node newNode : newNodes) {
                    Node oldNode = nodeMap.get(newNode.number);
                    if (oldNode != null && !oldNode.equals(newNode) && 
                        (oldNode.left != newNode.left || oldNode.right != newNode.right || oldNode.parent != newNode.parent)) {
                        // Node was moved/swapped
                        visualizer.highlightSwap(oldNode, newNode);
                        break;
                    }
                }
                
                // Update visualization
                visualizer.update();
                
                // Delay to see the changes
                Thread.sleep(delayMs);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
