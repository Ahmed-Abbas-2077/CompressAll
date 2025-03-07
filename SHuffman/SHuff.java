package SHuffman;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class SHuff {
    
    public static String read_txt(String txt_file) {
        try{
            System.out.println("Reading file " + txt_file + " ...");
            return Files.readString(Paths.get(txt_file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Sorry, Error reding file: " + e.getMessage());
            return "";
        }
    }


    public static byte[] read_binary(String bin_file){
        try {
            System.out.println("Reading file " + bin_file + " ...");
            return Files.readAllBytes(Paths.get(bin_file));
        } catch (IOException e){
            System.err.println("Error reading bin file: " + e.getMessage());
            return new byte[0];
        }
    }


    public static void write_txt (String txt_file, String content){
        try {
            Files.writeString(Paths.get(txt_file), content, StandardCharsets.UTF_8);
            System.out.println("Written to file " + txt_file + " successfully");
        } catch (IOException e) {
            System.err.println("Error writing to text file: " + e.getMessage());
        }
    }


    public static void write_binary(String bin_file, byte[] data) {
        try {
            Files.write(Paths.get(bin_file), data);
            System.out.println("Written to file " + bin_file + " successfully");
        } catch (IOException e) {
            System.err.println("Error writing binary file: " + e.getMessage());
        }
    }


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


    public static Map<Character, String> encoder(String sequence){
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
        Map<Character, String> encoding = new HashMap<>();         
        TraverseEncode(root, encoding, "");
        return encoding;
    }
}
