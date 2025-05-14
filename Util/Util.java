package Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.awt.image.BufferedImage;
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

    public static void printImageCompressionStatistics(String originalFilePath, int[] encodedData, String decodedFilePath) {
        int originalSize = getFileSize(originalFilePath);
        int compressedSize = encodedData.length * 4; // Assuming each int is 4 bytes
        int decodedSize = getFileSize(decodedFilePath);

        if (originalSize <= 0 || compressedSize <= 0 || decodedSize <= 0) {
            System.out.println("Error: Invalid file sizes.");
            return;
        }

        double compressionRatio = ((double) compressedSize / originalSize) * 100;
        System.out.println("\nOriginal size: " + originalSize + " bytes");
        System.out.println("Compressed size: " + compressedSize + " bytes");
        System.out.println("Decoded size: " + decodedSize + " bytes");
        System.out.println("Compression ratio: " + String.format("%.2f", compressionRatio) + "%\n");
    }


    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------



    public static double[][][][][] readImage(String filePath, int dimension) {
        try {
            // Read the image file
            BufferedImage image = ImageIO.read(new File(filePath));
            
            // Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();
            
            // Calculate padded dimensions (next multiple of 8)
            int paddedWidth = (width + (dimension-1)) / dimension * dimension;
            int paddedHeight = (height + (dimension-1)) / dimension * dimension;
            
            // Calculate number of blocks
            int numBlocksX = paddedWidth / dimension;
            int numBlocksY = paddedHeight / dimension;
            
            // Create 5D array for blocks [numBlocksY][numBlocksX][8][8][3]
            double[][][][][] blocks = new double[numBlocksY][numBlocksX][dimension][dimension][3];
            
            // Process each block
            for (int blockY = 0; blockY < numBlocksY; blockY++) {
                for (int blockX = 0; blockX < numBlocksX; blockX++) {
                    // Process each pixel in the block
                    for (int y = 0; y < dimension; y++) {
                        for (int x = 0; x < dimension; x++) {
                            // Calculate actual image coordinates
                            int imgX = blockX * dimension + x;
                            int imgY = blockY * dimension + y;
                            
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

        double[][][][][] blocks = new double[aspectY][aspectX][serializedBlocks[0].length][serializedBlocks[0][0].length][serializedBlocks[0][0][0].length]; // 8*8*3 = 192

        for (int i = 0; i < numBlocks; i++) {
            int blockY = i / aspectX;
            int blockX = i % aspectX;
            blocks[blockY][blockX] = serializedBlocks[i];
        }
        return blocks;
    }

    public static void saveImage(double[][][][][] blocks, String filePath, int dimension) {
        int numBlocksY = blocks.length;
        int numBlocksX = blocks[0].length;

        // Calculate image dimensions
        int width = numBlocksX * dimension;
        int height = numBlocksY * dimension;

        // Create a new BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Set pixel values in the image
        for (int blockY = 0; blockY < numBlocksY; blockY++) {
            for (int blockX = 0; blockX < numBlocksX; blockX++) {
                for (int y = 0; y < dimension; y++) {
                    for (int x = 0; x < dimension; x++) {
                        // Get RGB values from the block
                        double r = blocks[blockY][blockX][y][x][0] * 255.0;
                        double g = blocks[blockY][blockX][y][x][1] * 255.0;
                        double b = blocks[blockY][blockX][y][x][2] * 255.0;

                        // Set pixel color in the image
                        int rgb = ((int) r << 16) | ((int) g << 8) | (int) b;
                        image.setRGB(blockX * dimension + x, blockY * dimension + y, rgb);
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


    public static double[][][] readImage(String imagePath){
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();
            double[][][] imageData = new double[height][width][3]; // 3 for RGB channels

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    // normalize to range [0,1]
                    imageData[y][x][0] = ((rgb >> 16) & 0xFF) / 255.0; // Red
                    imageData[y][x][1] = ((rgb >> 8) & 0xFF) / 255.0;  // Green
                    imageData[y][x][2] = (rgb & 0xFF) / 255.0;         // Blue
                }
            }
            return imageData;
        } catch (IOException e) {
            System.err.println("Error reading image: " + e.getMessage());
            return null;
        }
    }

    public static boolean saveImage (String imagePath, double[][][] imageData){
        int height = imageData.length;
        int width = imageData[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = imageData[y][x][0] * 255.0;
                double g = imageData[y][x][1] * 255.0;
                double b = imageData[y][x][2] * 255.0;
                int rgb = ((int) r << 16) | ((int) g << 8) | (int) b;
                image.setRGB(x, y, rgb);
            }
        }

        try {
            ImageIO.write(image, "png", new File(imagePath));
            return true;
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            return false;
        }
    }


    public static Vector<String> getImagePathsFromDir(String dirPath){
        Vector<String> imagePaths = new Vector<String>();
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"))) {
                    imagePaths.add(file.getAbsolutePath());
                }
                else if (file.isDirectory()) {
                    imagePaths.addAll(getImagePathsFromDir(file.getAbsolutePath()));
                }
            }
        } else {
            System.err.println("Error: " + dirPath + " is not a directory.");
        }
        return imagePaths;
    }



    public static int getIntInput(int min, int max) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int input = -1;
        while (input < min || input > max) {
            System.out.print("Enter a number between " + min + " and " + max + ": ");
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
            } else {
                scanner.next(); // Clear invalid input
            }
        }
        scanner.close();
        return input;
    }

    public static String getStringInput(String... validInputs) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String input = "";
        while (!isValidInput(input, validInputs)) {
            System.out.print("Enter a valid input (" + String.join(", ", validInputs) + "): ");
            input = scanner.nextLine().trim();
        }
        scanner.close();
        return input;
    }

    private static boolean isValidInput(String input, String... validInputs) {
        for (String validInput : validInputs) {
            if (input.equalsIgnoreCase(validInput)) {
                return true;
            }
        }
        return false;
    }

    public static Vector<double[][]> getImageRGB(double[][][] image) {
        Vector<double[][]> rgbData = new Vector<double[][]>();
        double[][] redChannel = new double[image.length][image[0].length];
        double[][] greenChannel = new double[image.length][image[0].length];
        double[][] blueChannel = new double[image.length][image[0].length];

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[0].length; j++) {
                redChannel[i][j] = image[i][j][0];
                greenChannel[i][j] = image[i][j][1];
                blueChannel[i][j] = image[i][j][2];
            }
        }

        rgbData.add(redChannel);
        rgbData.add(greenChannel);
        rgbData.add(blueChannel);

        return rgbData;
    }


    public static double[][][] splitImageVectors(double[][] image, int dimension) {
        int height = image.length;
        int width = image[0].length;

        // Calculate the number of blocks
        int numBlocksY = (int) Math.ceil((double) height / dimension);
        int numBlocksX = (int) Math.ceil((double) width / dimension);

        // Create a vector to hold the blocks
        double[][][] blocks = new double[numBlocksY * numBlocksX][dimension][dimension];

        int index = 0;

        // Split the image into blocks
        for (int blockY = 0; blockY < numBlocksY; blockY++) {
            for (int blockX = 0; blockX < numBlocksX; blockX++) {
                double[][] block = new double[dimension][dimension];
                for (int y = 0; y < dimension; y++) {
                    for (int x = 0; x < dimension; x++) {
                        int imgY = blockY * dimension + y; // the actual image coordinates
                        int imgX = blockX * dimension + x;
                        if (imgY < height && imgX < width) {
                            block[y][x] = image[imgY][imgX];
                        } else {
                            block[y][x] = 0.0; // Padding with zeros
                        }
                    }
                }
                blocks[index] = block;
                index++;
            }
        }

        return blocks;
    }


    // splits the vectors of each image and compiles them into a single vector
    public static Vector<double[][]> extractVectors(Vector<double[][]> images, int dimension){
        Vector<double[][]> vectors = new Vector<double[][]>();
        for (int i = 0; i < images.size(); i++){
            double[][] image = images.get(i);
            double[][][] blocks = splitImageVectors(image, dimension);
            for (int j = 0; j < blocks.length; j++){
                vectors.add(blocks[j]);
            }
        }
        return vectors;
    }


    public static Vector<double[][][]> extractRGB(Vector<double[][][]> images, int dimension){
        Vector<double[][][]> vectors = new Vector<double[][][]>();

        // keeping track of the index for each channel
        // so that we can add the blocks to the correct channel
        int redIndex = 0;
        int greenIndex = 0;
        int blueIndex = 0;


        for (int i = 0; i < images.size(); i++){
            double[][][] image = images.get(i);
            
            Vector<double[][]> imageRGB = getImageRGB(image);

            // extract the vectors of each channel
            double[][][] redVectors = splitImageVectors(imageRGB.get(0), dimension);
            double[][][] greenVectors = splitImageVectors(imageRGB.get(1), dimension);
            double[][][] blueVectors = splitImageVectors(imageRGB.get(2), dimension);

            // add to each corresponding channel in vectors while incrementing the index
            for (int j = 0; j < redVectors.length; j++){
                vectors.get(0)[redIndex] = redVectors[j];
                redIndex++;
            }

            for (int j = 0; j < greenVectors.length; j++){
                vectors.get(1)[greenIndex] = greenVectors[j];
                greenIndex++;
            }

            for (int j = 0; j < blueVectors.length; j++){
                vectors.get(2)[blueIndex] = blueVectors[j];
                blueIndex++;
            }

        }

        return vectors;
    }
        

    // reconstructs 3D image from RGB slices
    public static double[][][] reconstructImage(Vector<double[][]> rgbData) {
        int height = rgbData.get(0).length;
        int width = rgbData.get(0)[0].length;
        double[][][] imageData = new double[height][width][3]; // 3 for RGB channels

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                imageData[y][x][0] = rgbData.get(0)[y][x]; // Red
                imageData[y][x][1] = rgbData.get(1)[y][x]; // Green
                imageData[y][x][2] = rgbData.get(2)[y][x]; // Blue
            }
        }
        return imageData;
    }


    // reconstructs 2D image from its blocks, using the blocks' dimensions and the image's height and width
    public static double[][] reconstructImageFromBlocks(double[][][] blocks, int dimension, int height, int width) {
        double[][] image = new double[height][width];

        for (int blockY = 0; blockY < height / dimension; blockY++) {
            for (int blockX = 0; blockX < width / dimension; blockX++) {
                for (int y = 0; y < dimension; y++) {
                    for (int x = 0; x < dimension; x++) {
                        int imgX = blockX * dimension + x;
                        int imgY = blockY * dimension + y;
                        image[imgY][imgX] = blocks[blockY * (width / dimension) + blockX][y][x];
                    }
                }
            }
        }

        return image;
    }


    // source: https://en.wikipedia.org/wiki/Y%E2%80%B2UV#SDTV_with_BT.470
    public static double[][][] RGB2YUV(double[][][] image){
        double[][][] yuv = new double[image.length][image[0].length][3];
        for (int i = 0; i < image.length; i++){
            for (int j = 0; j < image[0].length; j++){
                double r = image[i][j][0];
                double g = image[i][j][1];
                double b = image[i][j][2];

                yuv[i][j][0] = 0.299 * r + 0.587 * g + 0.114 * b; // Y
                yuv[i][j][1] = -0.14713 * r - 0.28886 * g + 0.436 * b; // U
                yuv[i][j][2] = 0.615 * r - 0.51499 * g - 0.10001 * b; // V
            }
        }
        return yuv;
    }

    public static double[][][] YUV2RGB(double[][][] yuv){
        double[][][] rgb = new double[yuv.length][yuv[0].length][3];
        for (int i = 0; i < yuv.length; i++){
            for (int j = 0; j < yuv[0].length; j++){
                double y = yuv[i][j][0];
                double u = yuv[i][j][1];
                double v = yuv[i][j][2];

                rgb[i][j][0] = y + 1.13983 * v; // R
                rgb[i][j][1] = y - 0.39465 * u - 0.58060 * v; // G
                rgb[i][j][2] = y + 2.03211 * u; // B
            }
        }
        return rgb;
    }


    public static double[][] downsample(double[][] image, int factor){
        int newHeight = image.length / factor;
        int newWidth = image[0].length / factor;
        double[][] downsampledImage = new double[newHeight][newWidth];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                downsampledImage[i][j] = image[i * factor][j * factor];
            }
        }
        return downsampledImage;
    }

    public static double[][] upsample(double[][] image, int factor){
        int newHeight = image.length * factor;
        int newWidth = image[0].length * factor;
        double[][] upsampledImage = new double[newHeight][newWidth];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                upsampledImage[i][j] = image[i / factor][j / factor];
            }
        }
        return upsampledImage;
    }


    // Stores a 3d codebook (an array of 2d images) in a compact binary file.
    // The header should have the codebook size and the dimensions of each image (2x2 by default).
    public static void storeCodebook(String binPath, double[][][] codebook){
        try {
            // Create a binary file
            java.io.DataOutputStream out = new java.io.DataOutputStream(new java.io.FileOutputStream(binPath));

            // Write the codebook size and dimensions
            out.writeInt(codebook.length);
            out.writeInt(codebook[0].length);
            out.writeInt(codebook[0][0].length);

            // Write the codebook data
            for (double[][] image : codebook) {
                for (double[] row : image) {
                    for (double value : row) {
                        out.writeDouble(value);
                    }
                }
            }

            out.close();
        } catch (IOException e) {
            System.err.println("Error storing codebook: " + e.getMessage());
        }
    }


    public static double[][][] loadCodebook(String binPath) {
        try {
            // Create a binary file
            java.io.DataInputStream in = new java.io.DataInputStream(new java.io.FileInputStream(binPath));

            // Read the codebook size and dimensions
            int codebookSize = in.readInt();
            int height = in.readInt();
            int width = in.readInt();

            // Create the codebook
            double[][][] codebook = new double[codebookSize][height][width];

            // Read the codebook data
            for (int i = 0; i < codebookSize; i++) {
                for (int j = 0; j < height; j++) {
                    for (int k = 0; k < width; k++) {
                        codebook[i][j][k] = in.readDouble();
                    }
                }
            }

            in.close();
            return codebook;
        } catch (IOException e) {
            System.err.println("Error loading codebook: " + e.getMessage());
            return null;
        }
    }


    public static void saveEncodings(int dimension, int height, int width, Vector<int[]> encodings, Vector<double[][][]> codebooks, String filePath) {
        try {
            java.io.DataOutputStream out = new java.io.DataOutputStream(new java.io.FileOutputStream(filePath));

            // Write the dimensions
            out.writeInt(dimension);
            out.writeInt(height);
            out.writeInt(width);



            // codebook lengths
            out.writeInt(codebooks.get(0).length);
            out.writeInt(codebooks.get(1).length);
            out.writeInt(codebooks.get(2).length); 


            // Write the encodings
            for (int[] encoding : encodings) {
                for (int value : encoding) {
                    out.writeInt(value);
                }
            }

            // Write the codebooks
            for (double[][][] codebook : codebooks) {
                for (double[][] image : codebook) {
                    for (double[] row : image) {
                        for (double value : row) {
                            out.writeDouble(value);
                        }
                    }
                }
            }

            out.close();
        } catch (IOException e) {
            System.err.println("Error saving encodings: " + e.getMessage());
        }
    }


    public static ArrayList<Object> readEncodings(String filePath){
        ArrayList<Object> data = new ArrayList<>(); // where we store loaded data
        
        try {
            java.io.DataInputStream in = new java.io.DataInputStream(new java.io.FileInputStream(filePath));
            
            // Read the dimensions
            int dimension = in.readInt();
            int height = in.readInt();
            int width = in.readInt();
            int codebookSize0 = in.readInt();
            int codebookSize1 = in.readInt();
            int codebookSize2 = in.readInt();

            // Read the encodings (3, one for each channel)
            int encodingSize = (int) Math.ceil((double) height * width / dimension*dimension);
            Vector<int[]> encodings = new Vector<int[]>();

            for (int i = 0; i < 3; i++){
                int[] encoding = new int[encodingSize];
                for (int j = 0; j < encodingSize; j++) {
                    encoding[j] = in.readInt();
                }
                encodings.add(encoding);
            }
            
            // Read the codebooks
            Vector<double[][][]> codebooks = new Vector<double[][][]>();
            for (int i = 0; i < 3; i++){
                int codebookSize = (i == 0) ? codebookSize0 : (i == 1) ? codebookSize1 : codebookSize2;
                double[][][] codebook = new double[codebookSize][dimension][dimension];
                for (int j = 0; j < codebookSize; j++) {
                    for (int k = 0; k < dimension; k++) {
                        for (int l = 0; l < dimension; l++) {
                            codebook[j][k][l] = in.readDouble();
                        }
                    }
                }
                codebooks.add(codebook);
            }

            // Add the data to the ArrayList
            data.add(dimension);
            data.add(height);
            data.add(width);
            data.add(codebooks);
            data.add(encodings);
            in.close();

        } catch (IOException e) {
            System.err.println("Error reading encodings: " + e.getMessage());
        }

        return data;

    }

}

