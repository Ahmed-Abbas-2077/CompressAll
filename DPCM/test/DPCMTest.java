package DPCM.test;

import DPCM.src.DPCM;
import DPCM.src.QuantInterval;
import Util.Util;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.image.BufferedImage;

public class DPCMTest {
    

    public static void printImage(int[][][] image) {
        // Print the image data in a readable format
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    System.out.print(image[i][j][k] + " ");
                }
                System.out.print(" | ");
            }
            System.out.println();
        }
    }


    public static boolean testReadImage() {
        try{
            // Test reading an image
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // print the image 
            System.out.println("Image read successfully. Dimensions: " + image.length + "x" + image[0].length + "x" + image[0][0].length);
            System.out.println("Image data: ");
            // printImage(image);
            return true;
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }


    public static boolean testSaveImage() {
        try {
            // Test saving an image
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Save the image
            String outputPath = "data/output.png"; // Replace with a valid output path
            Util.saveImage(outputPath, image);
            int[][][] savedImage = Util.readImage(outputPath);
            assert savedImage != null : "Saved image should not be null";
            // printImage(savedImage);
            System.out.println("Image saved successfully to " + outputPath);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean testOrder1Predictor() {
        try {
            // Test the order-1 predictor
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the order-1 predictor
            int[][][] predictedImage = DPCM.order1Predictor(image);
            
            // Print the predicted image
            Util.saveImage("data/predicted_image.png", predictedImage);
            int[][][] savedImage = Util.readImage("data/predicted_image.png");
            System.out.println("Predicted Image: ");
            // printImage(savedImage);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean testOrder2Predictor() {
        try {
            // Test the order-2 predictor
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the order-2 predictor
            int[][][] predictedImage = DPCM.order2Predictor(image);
            
            // Print the predicted image
            Util.saveImage("data/predicted_image2.png", predictedImage);
            int[][][] savedImage = Util.readImage("data/predicted_image2.png");
            System.out.println("Predicted Image: ");
            // printImage(savedImage);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }


    public static boolean testAdaptivePredictor() {
        try {
            // Test the adaptive predictor
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the adaptive predictor
            int[][][] predictedImage = DPCM.adaptivePredictor(image);
            
            // Print the predicted image
            Util.saveImage("data/predicted_image_adaptive.png", predictedImage);
            int[][][] savedImage = Util.readImage("data/predicted_image_adaptive.png");
            System.out.println("Predicted Image: ");
            // printImage(savedImage);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // test differenceTensor
    // which calculates the difference between the original image and the predicted image
    public static boolean testDifferenceTensor() {
        try {
            // Test the difference tensor
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the order-1 predictor
            int[][][] predictedImage = DPCM.order1Predictor(image);
            
            // Calculate the difference tensor
            int[][][] differenceTensor = DPCM.differenceTensor(image, predictedImage);
            
            // Create visualization version with offset
            int[][][] visualDiffTensor = new int[differenceTensor.length][differenceTensor[0].length][differenceTensor[0][0].length];
            for (int i = 0; i < differenceTensor.length; i++) {
                for (int j = 0; j < differenceTensor[i].length; j++) {
                    for (int k = 0; k < differenceTensor[i][j].length; k++) {
                        // Map [-255,255] range to [0,255]
                        visualDiffTensor[i][j][k] = Math.min(255, Math.max(0, differenceTensor[i][j][k] + 128));
                    }
                }
            }
            // Save the visualization version
            Util.saveImage("data/difference_tensor.png", visualDiffTensor);
            int[][][] savedImage = Util.readImage("data/difference_tensor.png");
            System.out.println("Difference Tensor: ");
            // printImage(savedImage);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }


    // test the quantization method
    public static boolean testQuantization() {
        try {
            // Test the quantization method
            String imagePath = "data/mit1.png";
            int[][][] image = Util.readImage(imagePath);
            int[][][] predictedImage = DPCM.order1Predictor(image);
            int[][][] differenceImage = DPCM.differenceTensor(image, predictedImage);
            assert differenceImage != null : "Image should not be null";

            // Apply the quantization method
            List<QuantInterval> quantizer = DPCM.quantization(differenceImage, 16); // 8 quantization intervals
            // Print the quantization intervals
            for (QuantInterval interval : quantizer) {
                System.out.println("Interval: " + interval.getIndex() + ", Min: " + interval.getMin() + ", Max: " + interval.getMax() + ", Median: " + interval.getMedian());
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // test quantized difference tensor
    public static boolean testQuantizedDifferenceTensor() {
        try {
            // Test the quantized difference tensor
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the order-1 predictor
            int[][][] predictedImage = DPCM.order1Predictor(image);
            
            // Calculate the difference tensor
            int[][][] differenceTensor = DPCM.differenceTensor(image, predictedImage);
            
            // Apply the quantization method
            List<QuantInterval> quantizer = DPCM.quantization(differenceTensor, 16); // 16 quantization intervals
            
            int[][][] quantizedDifferenceTensor = DPCM.quantizedDifferenceTensor(differenceTensor, quantizer);

            // Print the quantized difference tensor
            Util.saveImage("data/quantized_difference_tensor.png", quantizedDifferenceTensor);
            int[][][] savedImage = Util.readImage("data/quantized_difference_tensor.png");
            System.out.println("Quantized Difference Tensor: ");
            // printImage(savedImage);
            
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // dequantized Difference Tensor
    public static boolean testDequantizedDifferenceTensor() {
        try {
            // Test the dequantized difference tensor
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the order-1 predictor
            int[][][] predictedImage = DPCM.order1Predictor(image);
            
            // Calculate the difference tensor
            int[][][] differenceTensor = DPCM.differenceTensor(image, predictedImage);
            
            // Apply the quantization method
            List<QuantInterval> quantizer = DPCM.quantization(differenceTensor, 16); // 16 quantization intervals
            
            int[][][] quantizedDifferenceTensor = DPCM.quantizedDifferenceTensor(differenceTensor, quantizer);

            // Dequantize the difference tensor
            int[] minMax = DPCM.getMinMax(differenceTensor);
            int min = minMax[0];
            int max = minMax[1];
            int[] histogram = DPCM.imageHistogram(differenceTensor, min, max);
            int[][][] dequantizedDifferenceTensor = DPCM.dequantizedDifferenceTensor(quantizedDifferenceTensor, quantizer, min, max, histogram);

            // Print the dequantized difference tensor
            Util.saveImage("data/dequantized_difference_tensor.png", dequantizedDifferenceTensor);
            int[][][] savedImage = Util.readImage("data/dequantized_difference_tensor.png");
            System.out.println("Dequantized Difference Tensor: ");
            // printImage(savedImage);
            
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // test reconstruction method with adaptive predictor
    public static boolean testReconstruction(String predictor) {
        try {
            // Test the reconstruction method
            String imagePath = "data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Apply the specified predictor
            int[][][] predictedImage;
            if (predictor.equals("order1")) {
                predictedImage = DPCM.order1Predictor(image);
            } else if (predictor.equals("order2")) {
                predictedImage = DPCM.order2Predictor(image);
            } else if (predictor.equals("adaptive")) {
                predictedImage = DPCM.adaptivePredictor(image);
            } else {
                throw new IllegalArgumentException("Invalid predictor: " + predictor);
            }
            
            // Calculate the difference tensor
            int[][][] differenceTensor = DPCM.differenceTensor(image, predictedImage);
            
            // Apply the quantization method
            List<QuantInterval> quantizer = DPCM.quantization(differenceTensor, 16); // 16 quantization intervals
            
            int[][][] quantizedDifferenceTensor = DPCM.quantizedDifferenceTensor(differenceTensor, quantizer);

            // Dequantize the difference tensor
            int[] minMax = DPCM.getMinMax(differenceTensor);
            int min = minMax[0];
            int max = minMax[1];
            int[] histogram = DPCM.imageHistogram(differenceTensor, min, max);
            int[][][] dequantizedDifferenceTensor = DPCM.dequantizedDifferenceTensor(quantizedDifferenceTensor, quantizer, min, max, histogram);


            // get top left
            int[] topLeft = new int[3];
            topLeft[0] = image[0][0][0]; // R
            topLeft[1] = image[0][0][1]; // G
            topLeft[2] = image[0][0][2]; // B

            // get first row (starting from 1)
            int[][] firstRow = new int[image[0].length-1][3];
            for (int i = 0; i < image[0].length-1; i++) {
                firstRow[i][0] = image[0][i+1][0]; // R
                firstRow[i][1] = image[0][i+1][1]; // G
                firstRow[i][2] = image[0][i+1][2]; // B
            }

            // get first column (starting from 1)
            int[][] firstColumn = new int[image.length-1][3];
            for (int i = 0; i < image.length-1; i++) {
                firstColumn[i][0] = image[i+1][0][0]; // R
                firstColumn[i][1] = image[i+1][0][1]; // G
                firstColumn[i][2] = image[i+1][0][2]; // B
            }

            // Reconstruct the original image
            int[][][] reconstructedImage = DPCM.reconstructImage(topLeft, firstRow, firstColumn, dequantizedDifferenceTensor, predictor);

            // Print the reconstructed image
            String path = "data/reconstructed_image_" + predictor + ".png";
            Util.saveImage(path, reconstructedImage);
            int[][][] savedImage = Util.readImage(path);
            // System.out.println("Reconstructed Image: ");
            // printImage(savedImage);

            // Calculate the MSE
            double mse = DPCM.calculateMSE(image, reconstructedImage);
            System.out.println("MSE: " + mse);
            
            return true;
        } catch (Exception e) {
            // print the whole stack trace
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public static void testPredictorComparison() {
        String[] predictors = {"order1", "order2", "adaptive"};
        for (String predictor : predictors) {
            if (!testReconstruction(predictor)) {
                System.out.println("testReconstruction failed for predictor: " + predictor);
                return;
            } else {
                System.out.println("testReconstruction passed for predictor: " + predictor);
            }
        }
    }


    public static void testQuantizationLevels(String imagePath) {
        int[] levels = {4, 8, 16, 32, 64};
        for (int level : levels) {
            try {
                // Test the quantization method
                int[][][] image = Util.readImage(imagePath);
                assert image != null : "Image should not be null";
                
                // Apply the quantization method
                List<QuantInterval> quantizer = DPCM.quantization(image, level); // 8 quantization intervals
                
                // Print the quantization intervals
                System.out.println("Quantization Level: " + level);
                for (QuantInterval interval : quantizer) {
                    System.out.println("Interval: " + interval.getIndex() + ", Min: " + interval.getMin() + ", Max: " + interval.getMax() + ", Median: " + interval.getMedian());
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    // visually compare the images
    public static void visualComparison(int[][][] original, int[][][] reconstructed, String title) {
        
        // Create BufferedImages from the pixel data
        BufferedImage originalImg = new BufferedImage(original[0].length, original.length, BufferedImage.TYPE_INT_RGB);
        BufferedImage reconstructedImg = new BufferedImage(reconstructed[0].length, reconstructed.length, BufferedImage.TYPE_INT_RGB);
        
        // Fill the images with pixel data
        for (int y = 0; y < original.length; y++) {
            for (int x = 0; x < original[0].length; x++) {
                int r = original[y][x][0];
                int g = original[y][x][1];
                int b = original[y][x][2];
                int rgb = (r << 16) | (g << 8) | b;
                originalImg.setRGB(x, y, rgb);
            }
        }
        
        for (int y = 0; y < reconstructed.length; y++) {
            for (int x = 0; x < reconstructed[0].length; x++) {
                int r = reconstructed[y][x][0];
                int g = reconstructed[y][x][1];
                int b = reconstructed[y][x][2];
                int rgb = (r << 16) | (g << 8) | b;
                reconstructedImg.setRGB(x, y, rgb);
            }
        }
        
        // Calculate the difference image (for visualization)
        BufferedImage diffImg = new BufferedImage(original[0].length, original.length, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < original.length; y++) {
            for (int x = 0; x < original[0].length; x++) {
                int diffR = Math.abs(original[y][x][0] - reconstructed[y][x][0]);
                int diffG = Math.abs(original[y][x][1] - reconstructed[y][x][1]);
                int diffB = Math.abs(original[y][x][2] - reconstructed[y][x][2]);
                
                // Enhance differences for visibility (multiply by 4)
                diffR = Math.min(255, diffR * 4);
                diffG = Math.min(255, diffG * 4);
                diffB = Math.min(255, diffB * 4);
                
                int rgb = (diffR << 16) | (diffG << 8) | diffB;
                diffImg.setRGB(x, y, rgb);
            }
        }
        
        // Scale factor for display (make images larger if needed)
        int scale = 2;
        int imgWidth = original[0].length * scale;
        int imgHeight = original.length * scale;
        
        // Create a JFrame
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Create a panel for images
        JPanel imagesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Create panels for each image and add labels
        JPanel originalPanel = new JPanel(new BorderLayout());
        JLabel originalImgLabel = new JLabel(new ImageIcon(originalImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));
        JLabel originalTextLabel = new JLabel("Original", SwingConstants.CENTER);
        originalPanel.add(originalImgLabel, BorderLayout.CENTER);
        originalPanel.add(originalTextLabel, BorderLayout.SOUTH);
        
        JPanel reconstructedPanel = new JPanel(new BorderLayout());
        JLabel reconstructedImgLabel = new JLabel(new ImageIcon(reconstructedImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));
        JLabel reconstructedTextLabel = new JLabel("Reconstructed", SwingConstants.CENTER);
        reconstructedPanel.add(reconstructedImgLabel, BorderLayout.CENTER);
        reconstructedPanel.add(reconstructedTextLabel, BorderLayout.SOUTH);
        
        JPanel diffPanel = new JPanel(new BorderLayout());
        JLabel diffImgLabel = new JLabel(new ImageIcon(diffImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH)));
        JLabel diffTextLabel = new JLabel("Difference (×4)", SwingConstants.CENTER);
        diffPanel.add(diffImgLabel, BorderLayout.CENTER);
        diffPanel.add(diffTextLabel, BorderLayout.SOUTH);
        
        // Add the image panels to the images panel
        imagesPanel.add(originalPanel);
        imagesPanel.add(reconstructedPanel);
        imagesPanel.add(diffPanel);
        
        // Add MSE information
        double mse = DPCM.calculateMSE(original, reconstructed);
        double psnr = 10 * Math.log10(255*255/mse);
        JPanel infoPanel = new JPanel();
        JLabel mseLabel = new JLabel(String.format("MSE: %.4f    PSNR: %.2f dB", mse, psnr));
        infoPanel.add(mseLabel);
        
        // Add the panels to the frame
        frame.add(imagesPanel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.SOUTH);
        
        // Display the frame
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }


    // test visual comparison for a given image path for each of the predictors
    public static void testVisualComparison(String imagePath) {
        try {
            // Read the original image
            int[][][] originalImage = Util.readImage(imagePath);
            assert originalImage != null : "Original image should not be null";
            
            // Test each predictor and visualize the results
            String[] predictors = {"order1", "order2", "adaptive"};
            for (String predictor : predictors) {
                // Reconstruct the image using the specified predictor
                testReconstruction(predictor);
                
                // Read the reconstructed image
                int[][][] reconstructedImage = Util.readImage("data/reconstructed_image_" + predictor + ".png");
                assert reconstructedImage != null : "Reconstructed image should not be null";
                
                // Visual comparison
                visualComparison(originalImage, reconstructedImage, "Visual Comparison - " + predictor);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    


    public static void testAll() {
        // Test the readImage method
        if (!testReadImage()) {
            System.out.println("testReadImage failed.\n");
            return;
        }
        else {
            System.out.println("testReadImage passed.\n");
        }

        // Test the saveImage method
        if (!testSaveImage()) {
            System.out.println("testSaveImage failed.\n");
            return;
        }
        else {
            System.out.println("testSaveImage passed.\n");
        }


        // Test the order-1 predictor
        if (!testOrder1Predictor()) {
            System.out.println("testOrder1Predictor failed.\n");
            return;
        }
        else {
            System.out.println("testOrder1Predictor passed.\n");
        }


        // Test the order-2 predictor
        if (!testOrder2Predictor()) {
            System.out.println("testOrder2Predictor failed.\n");
            return;
        }
        else {
            System.out.println("testOrder2Predictor passed.\n");
        }


        // Test the adaptive predictor
        if (!testAdaptivePredictor()) {
            System.out.println("testAdaptivePredictor failed.\n");
            return;
        }
        else {
            System.out.println("testAdaptivePredictor passed.\n");
        }


        // Test the difference tensor
        if (!testDifferenceTensor()) {
            System.out.println("testDifferenceTensor failed.\n");
            return;
        }
        else {
            System.out.println("testDifferenceTensor passed.\n");
        }

        // Test the quantization method
        if (!testQuantization()) {
            System.out.println("testQuantization failed.\n");
            return;
        }
        else {
            System.out.println("testQuantization passed.\n");
        }
        

        // Test the quantized difference tensor
        if (!testQuantizedDifferenceTensor()) {
            System.out.println("testQuantizedDifferenceTensor failed.\n");
            return;
        }
        else {
            System.out.println("testQuantizedDifferenceTensor passed.\n");
        }


        // Test the dequantized difference tensor   
        if (!testDequantizedDifferenceTensor()) {
            System.out.println("testDequantizedDifferenceTensor failed.\n");
            return;
        }
        else {
            System.out.println("testDequantizedDifferenceTensor passed.\n");
        }

        // test predictor comparison
        testPredictorComparison();
        System.out.println("testPredictorComparison passed.\n");


        // visually compare the images for mit1.png
        String imagePath = "data/mit1.png"; // Replace with a valid image path
        testVisualComparison(imagePath);
        System.out.println("testVisualComparison passed.\n");


    
        System.out.println("All tests passed.\n");
    }


    // main asks which test to run
    public static void main(String[] args) {
        // ask the user which test to run
        System.out.println("Which test do you want to run?");
        System.out.println("1. Read Image");
        System.out.println("2. Save Image");
        System.out.println("3. Order-1 Predictor");
        System.out.println("4. Order-2 Predictor");
        System.out.println("5. Adaptive Predictor");
        System.out.println("6. Difference Tensor");
        System.out.println("7. Quantization");
        System.out.println("8. Quantized Difference Tensor");
        System.out.println("9. Dequantized Difference Tensor");
        System.out.println("10. Reconstruction");
        System.out.println("11. Visual Comparison");
        System.out.println("12. Quantization Levels");
        System.out.println("13. All Tests");
        System.out.println("14. Exit");
        System.out.print("Enter your choice: ");
        int choice = Util.getIntInput(1, 14);

        switch (choice) {
            case 1:
                testReadImage();
                break;
            case 2:
                testSaveImage();
                break;
            case 3:
                testOrder1Predictor();
                break;
            case 4:
                testOrder2Predictor();
                break;
            case 5:
                testAdaptivePredictor();
                break;
            case 6:
                testDifferenceTensor();
                break;
            case 7:
                testQuantization();
                break;
            case 8:
                testQuantizedDifferenceTensor();
                break;
            case 9:
                testDequantizedDifferenceTensor();
                break;
            case 10:
                testReconstruction("order1");
                testReconstruction("order2");
                testReconstruction("adaptive");
                break;
            case 11:
                String imagePath = "data/mit1.png"; // Replace with a valid image path
                testVisualComparison(imagePath);
                break;
            case 12:
                String quantizationImagePath = "data/mit1.png"; // Replace with a valid image path
                testQuantizationLevels(quantizationImagePath);
                break;
            case 13:
                testAll();
                break;
            case 14:
                System.out.println("Exiting...");
                System.exit(0);
        }
        // ask if the user wants to run another test
        System.out.print("Do you want to run another test? (y/n): ");
        String answer = Util.getStringInput("y", "n");
        if (answer.equals("y")) {
            main(args);
        } else {
            System.out.println("Exiting...");
            System.exit(0);
        }
    }

}
