package SHuffman;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SHuff {
    
    public static String read_txt(String txt_file) {
        try{
            return Files.readString(Paths.get(txt_file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Sorry, Error reding file: " + e.getMessage());
            return "";
        }
    }

    public static byte[] read_binary(String bin_file){
        try {
            return Files.readAllBytes(Paths.get(bin_file));
        } catch (IOException e){
            System.err.println("Error reading bin file: " + e.getMessage());
            return new byte[0];
        }
    }


    public static void write_txt (String txt_file, String content){
        try {
            Files.writeString(Paths.get(txt_file), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error writing to text file: " + e.getMessage());
        }
    }

    public static void write_binary(String bin_file, byte[] data) {
        try {
            Files.write(Paths.get(bin_file), data);
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

}
