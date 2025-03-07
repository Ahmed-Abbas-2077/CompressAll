package Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}
