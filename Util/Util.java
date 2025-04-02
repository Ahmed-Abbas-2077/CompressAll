package Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Util {
    // Read text file
    public static String readText(String filePath) {
        try {
            return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error reading text file: " + e.getMessage());
            return null;
        }
    }

    // Write to text file
    public static void writeText(String filePath, String content) {
        try {
            Files.writeString(Paths.get(filePath), content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error writing to text file: " + e.getMessage());
        }
    }

    // Read binary file
    public static byte[] readBinary(String filePath) {
        try {
            return Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading binary file: " + e.getMessage());
            return null;
        }
    }

    // Write to binary file
    public static void writeBinary(String filePath, byte[] data) {
        try {
            Files.write(Paths.get(filePath), data);
        } catch (IOException e) {
            System.err.println("Error writing to binary file: " + e.getMessage());
        }
    }

    // Convert binary string to byte array
    public static byte[] binarize(String binaryString) {
        int length = binaryString.length();
        int byteLength = (length + 7) / 8;
        byte[] byteArray = new byte[byteLength];

        for (int i = 0; i < length; i++) {
            if (binaryString.charAt(i) == '1') {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                byteArray[byteIndex] |= (1 << bitIndex);
            }
        }

        return byteArray;
    }

    public static String textify(byte[] bytes){
        StringBuilder bin_str = new StringBuilder();
        for (byte b : bytes){
            for (int i = 7; i>=0; i--){
                bin_str.append((b >> i) & 1);
            }
        }
        return bin_str.toString();
    }

    public static Character getKeyByValue(Map<Character, String> map, String value) {
        for (var entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Map<Character, String> getVocab(String text) {
        Map<Character, String> vocab = new HashMap<>();
        Set<Character> chars = new HashSet<>();

        for (char c : text.toCharArray()) {
            chars.add(c);
        }

        int length = (int) Math.ceil(Math.log(chars.size()) / Math.log(2));
        int index = 0;

        for (char c : chars) {
            StringBuilder code = new StringBuilder(Integer.toBinaryString(index++));
            while (code.length() < length) {
                code.insert(0, '0');
            }
            vocab.put(c, code.toString());
        }

        return vocab;
    }

    public static int getFileSize(String filePath) {
        try {
            return (int) Files.size(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error getting file size: " + e.getMessage());
            return -1;
        }
    }

    public static double getCompressionRatio(String originalFilePath, String compressedFilePath) {
        int originalSize = getFileSize(originalFilePath);
        int compressedSize = getFileSize(compressedFilePath);

        if (originalSize <= 0 || compressedSize <= 0) {
            return -1;
        }

        return ((double) compressedSize / originalSize) * 100;
    }

    public static void printCompressionStatistics(String originalFilePath, String compressedFilePath, String decodedFilePath) {
        int originalSize = getFileSize(originalFilePath);
        int compressedSize = getFileSize(compressedFilePath);
        int decodedSize = getFileSize(decodedFilePath);

        if (originalSize <= 0 || compressedSize <= 0 || decodedSize <= 0) {
            System.out.println("Error: Invalid file sizes.");
            return;
        }

        double compressionRatio = getCompressionRatio(originalFilePath, compressedFilePath);
        System.out.println("\nOriginal size: " + originalSize + " bytes");
        System.out.println("Compressed size: " + compressedSize + " bytes");
        System.out.println("Decoded size: " + decodedSize + " bytes");
        System.out.println("Compression ratio: " + compressionRatio + "%\n");
    }   

}
