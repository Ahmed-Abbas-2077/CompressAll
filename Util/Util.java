package Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

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

    public static double[][][][][] readImage(String filePath) {
        try {
            // Read the image file
            BufferedImage image = ImageIO.read(new File(filePath));
            
            // Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();
            
            // Calculate padded dimensions (next multiple of 8)
            int paddedWidth = (width + 7) / 8 * 8;
            int paddedHeight = (height + 7) / 8 * 8;
            
            // Calculate number of blocks
            int numBlocksX = paddedWidth / 8;
            int numBlocksY = paddedHeight / 8;
            
            // Create 5D array for blocks [numBlocksY][numBlocksX][8][8][3]
            double[][][][][] blocks = new double[numBlocksY][numBlocksX][8][8][3];
            
            // Process each block
            for (int blockY = 0; blockY < numBlocksY; blockY++) {
                for (int blockX = 0; blockX < numBlocksX; blockX++) {
                    // Process each pixel in the block
                    for (int y = 0; y < 8; y++) {
                        for (int x = 0; x < 8; x++) {
                            // Calculate actual image coordinates
                            int imgX = blockX * 8 + x;
                            int imgY = blockY * 8 + y;
                            
                            // Get pixel RGB values (handle padding by using black if out of bounds)
                            int rgb = (imgX < width && imgY < height) ? image.getRGB(imgX, imgY) : 0;
                            
                            // Extract RGB components and normalize to range [0, 1]
                            blocks[blockY][blockX][y][x][0] = ((rgb >> 16) & 0xFF) / 255.0;  // R
                            blocks[blockY][blockX][y][x][1] = ((rgb >> 8) & 0xFF) / 255.0;   // G
                            blocks[blockY][blockX][y][x][2] = (rgb & 0xFF) / 255.0;          // B
                        }
                    }
                }
            }
            
            return blocks;
            
        } catch (IOException e) {
            System.err.println("Error reading image file: " + e.getMessage());
            return null;
        }
    }


    // instead of arranging the 3d image blocks in 2d, we will arrange them in 1d
    // so that we can use the same code for encoding and decoding
    public static double[][][][] serializeImageBlocks(double[][][][][] blocks) 
    {
        int numBlocksY = blocks.length;
        int numBlocksX = blocks[0].length;
        int blockSize = blocks[0][0].length * blocks[0][0][0].length * blocks[0][0][0][0].length; // 8*8*3 = 192

        double[][][][] serializedBlocks = new double[numBlocksY * numBlocksX][8][8][3];

        for (int blockY = 0; blockY < numBlocksY; blockY++) {
            for (int blockX = 0; blockX < numBlocksX; blockX++) {
                int index = blockY * numBlocksX + blockX;
                serializedBlocks[index] = blocks[blockY][blockX];
            }
        }
        return serializedBlocks;
    }

    public static double[][][][][] deserializeImageBlocks(double[][][][] serializedBlocks, int aspectX, int aspectY) {
        int numBlocks = serializedBlocks.length;
        int blockSize = serializedBlocks[0].length * serializedBlocks[0][0].length * serializedBlocks[0][0][0].length; // 8*8*3 = 192

        double[][][][][] blocks = new double[aspectY][aspectX][8][8][3];

        for (int i = 0; i < numBlocks; i++) {
            int blockY = i / aspectX;
            int blockX = i % aspectX;
            blocks[blockY][blockX] = serializedBlocks[i];
        }
        return blocks;
    }

    public static void saveImage(double[][][][][] blocks, String filePath) {
        int numBlocksY = blocks.length;
        int numBlocksX = blocks[0].length;
        int blockSize = blocks[0][0].length * blocks[0][0][0].length * blocks[0][0][0][0].length; // 8*8*3 = 192

        // Calculate image dimensions
        int width = numBlocksX * 8;
        int height = numBlocksY * 8;

        // Create a new BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set pixel values in the image
        for (int blockY = 0; blockY < numBlocksY; blockY++) {
            for (int blockX = 0; blockX < numBlocksX; blockX++) {
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        // Get RGB values from the block
                        double r = blocks[blockY][blockX][y][x][0] * 255.0;
                        double g = blocks[blockY][blockX][y][x][1] * 255.0;
                        double b = blocks[blockY][blockX][y][x][2] * 255.0;

                        // Set pixel color in the image
                        int rgb = ((int) r << 16) | ((int) g << 8) | (int) b;
                        image.setRGB(blockX * 8 + x, blockY * 8 + y, rgb);
                    }
                }
            }
        }

        // Save the image to a file
        try {
            ImageIO.write(image, "png", new File(filePath));
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
