package SHuffman;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import Util.Util;

public class SHuff {


    public static Map<Character, Integer> getFrequency(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()){
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
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

    public static void stats(String org_file, String bin_file, String decoded_file){
        String original = Util.readText(org_file);
        String decoded = Util.readText(decoded_file);
        byte[] original_bytes = Util.readBinary(bin_file);
        String bin_str = Util.textify(original_bytes);

        double original_entropy = calculateEntropy(original);
        double compressed_entropy = calculateEntropy(bin_str);
        double compression_ratio = (double) original.length() / bin_str.length();
        boolean match = original.equals(decoded);

        System.out.println("Original Entropy: " + original_entropy);
        System.out.println("Compressed Entropy: " + compressed_entropy);
        System.out.println("Compression Ratio: " + compression_ratio);
        System.out.println("Match: " + match);
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

        int originalLength = bin_str.length();
        String header = originalLength + "|";
        byte[] headerBytes = header.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = Util.binarize(bin_str.toString());
    
        byte[] combinedBytes = new byte[headerBytes.length + dataBytes.length];
        System.arraycopy(headerBytes, 0, combinedBytes, 0, headerBytes.length);
        System.arraycopy(dataBytes, 0, combinedBytes, headerBytes.length, dataBytes.length);
    
        Util.writeBinary(bin_file, combinedBytes);
    }

    public static void decoder(Map<Character, String> encoding, String bin_file, String decoded_file){
        byte[] bytes = Util.readBinary(bin_file);

        String fullContent = new String(bytes, StandardCharsets.UTF_8);
        int separatorIndex = fullContent.indexOf('|');
        int originalLength = Integer.parseInt(fullContent.substring(0, separatorIndex));

        byte[] dataBytes = new byte[bytes.length - (separatorIndex + 1)];
        System.arraycopy(bytes, separatorIndex + 1, dataBytes, 0, dataBytes.length);
    

        String bin_str = Util.textify(dataBytes);
        bin_str = bin_str.substring(0, originalLength);

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

    public static void main(String[] args){
        
        java.util.Scanner scanner = new java.util.Scanner(System.in);
    
        while (true) {
            System.out.println("\n1. Encode\n2. Decode\n3. Stats\n4. Exit");
            
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                System.out.print("Text file: ");
                String textFile = scanner.nextLine();
                System.out.print("Binary file: ");
                String binFile = scanner.nextLine();
                
                Map<Character, String> encoding = new HashMap<>();
                encoder(textFile, binFile, encoding);
                System.out.println("Done.");
                
            } else if (choice == 2) {
                System.out.print("Original file: ");
                String origFile = scanner.nextLine();
                System.out.print("Binary file: ");
                String binFile = scanner.nextLine();
                System.out.print("Output file: ");
                String outFile = scanner.nextLine();
                
                Map<Character, String> encoding = new HashMap<>();
                encoder(origFile, "temp.bin", encoding);
                decoder(encoding, binFile, outFile);
                System.out.println("Done.");
                
            } else if (choice == 3) {
                System.out.print("Original file: ");
                String origFile = scanner.nextLine();
                System.out.print("Binary file: ");
                String binFile = scanner.nextLine();
                System.out.print("Decoded file: ");
                String decodedFile = scanner.nextLine();
                
                stats(origFile, binFile, decodedFile);
                
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        
        scanner.close();
    }

}
