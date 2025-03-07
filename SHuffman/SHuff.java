package SHuffman;


import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import Util.Util;

public class SHuff {


    public static Map<Character, Integer> getFrequency(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()){
            frequencyMap.put(c, frequencyMap.getOrDefault(frequencyMap, 0) + 1);
        }
        return frequencyMap;
    }


    public static double calculateEntropy(String text){
        Map<Character, Integer> freqMap = getFrequency(text);
        double H = 0; // Entropy
        int totalLength = text.length();

        // calculating entropy in an iterative fashion
        for (int freq : freqMap.values()){
            double prob = (double) freq/totalLength;
            double log_prob = Math.log(prob) / Math.log(2); // bits, nats, hartleys are all welcome!
            H -= prob * log_prob;
        } 

        return H;
    }


    public static void TraverseEncode(Node root, Map<Character, String> encoding, String code){
        if (root == null){
            return;
        } else if (root.left == null && root.right == null){
            encoding.put(root.symbol, code);
        } else {
            TraverseEncode(root.left, encoding, code + "0");
            TraverseEncode(root.right, encoding, code + "1");
        }
    }



    public static void encoder(String text_file, String bin_file, Map<Character, String> encoding){
        String sequence = Util.readText(text_file);
        Map<Character, Integer> frequencyMap = getFrequency(sequence);
        PriorityQueue<Node> Q = new PriorityQueue<>();
        

        // insert elements in Q
        for (var entry : frequencyMap.entrySet()){
            char character = entry.getKey();
            int frequency = entry.getValue();
            Node node = new Node(character, frequency);
            Q.offer(node);
        }

        while (Q.size() > 1){
            Node left = Q.poll();
            Node right = Q.poll();
            int combinedFreq = left.frequency + right.frequency;
            Node newNode = new Node(' ', combinedFreq);
            newNode.left = left;
            newNode.right = right;
            Q.add(newNode);
        }

        Node root = Q.poll();
        TraverseEncode(root, encoding, "");
        
        StringBuilder bin_str = new StringBuilder();
        for (char c : sequence.toCharArray()){
            String code = encoding.get(c);
            bin_str.append(code);
        }

        byte[] bytes = Util.binarize(bin_str.toString());
        Util.writeBinary(bin_file, bytes);
    }

    public static void decoder(Map<Character, String> encoding, String bin_file, String decoded_file){
        byte[] bytes = Util.readBinary(bin_file);
        String bin_str = Util.textify(bytes);

        StringBuilder decoded = new StringBuilder();
        StringBuilder buffer = new StringBuilder();
        for (char c : bin_str.toCharArray()){
            buffer.append(c);
            Character symbol = Util.getKeyByValue(encoding, buffer.toString());
            if(symbol != null){
                decoded.append(symbol);
                buffer = new StringBuilder();
            }
        }

        Util.writeText(decoded_file, decoded.toString());
    }

}
