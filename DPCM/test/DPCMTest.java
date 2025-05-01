package DPCM.test;

import DPCM.src.DPCM;
import DPCM.src.QuantInterval;
import Util.Util;
import java.util.ArrayList;
import java.util.List;


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
            
            // Print the difference tensor
            Util.saveImage("data/difference_tensor.png", differenceTensor);
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
            String differencePath = "data/difference_tensor.png"; // Replace with a valid image path
            int[][][] differenceImage = Util.readImage(differencePath);
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
            printImage(savedImage);
            
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }



    public static void main(String[] args) {
        // Test the readImage method
        if (!testReadImage()) {
            System.out.println("testReadImage failed.");
            return;
        }
        else {
            System.out.println("testReadImage passed.");
        }

        // Test the saveImage method
        if (!testSaveImage()) {
            System.out.println("testSaveImage failed.");
            return;
        }
        else {
            System.out.println("testSaveImage passed.");
        }


        // Test the order-1 predictor
        if (!testOrder1Predictor()) {
            System.out.println("testOrder1Predictor failed.");
            return;
        }
        else {
            System.out.println("testOrder1Predictor passed.");
        }


        // Test the order-2 predictor
        if (!testOrder2Predictor()) {
            System.out.println("testOrder2Predictor failed.");
            return;
        }
        else {
            System.out.println("testOrder2Predictor passed.");
        }


        // Test the adaptive predictor
        if (!testAdaptivePredictor()) {
            System.out.println("testAdaptivePredictor failed.");
            return;
        }
        else {
            System.out.println("testAdaptivePredictor passed.");
        }


        // Test the difference tensor
        if (!testDifferenceTensor()) {
            System.out.println("testDifferenceTensor failed.");
            return;
        }
        else {
            System.out.println("testDifferenceTensor passed.");
        }

        // Test the quantization method
        if (!testQuantization()) {
            System.out.println("testQuantization failed.");
            return;
        }
        else {
            System.out.println("testQuantization passed.");
        }
        

        // Test the quantized difference tensor
        if (!testQuantizedDifferenceTensor()) {
            System.out.println("testQuantizedDifferenceTensor failed.");
            return;
        }
        else {
            System.out.println("testQuantizedDifferenceTensor passed.");
        }



        System.out.println("All tests passed.");
    }

}
