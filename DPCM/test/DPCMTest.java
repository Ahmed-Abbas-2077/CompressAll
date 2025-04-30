package DPCM.test;

import DPCM.src.DPCM;
import Util.Util;
import Util.Util;



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
            String imagePath = "../data/mit1.png"; // Replace with a valid image path
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
            String imagePath = "../data/mit1.png"; // Replace with a valid image path
            int[][][] image = Util.readImage(imagePath);
            assert image != null : "Image should not be null";
            
            // Save the image
            String outputPath = "../data/output.png"; // Replace with a valid output path
            Util.saveImage(outputPath, image);
            int[][][] savedImage = Util.readImage(outputPath);
            assert savedImage != null : "Saved image should not be null";
            printImage(savedImage);
            System.out.println("Image saved successfully to " + outputPath);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }



    public static void main(String[] args) {
        // Test the DPCM class
        if (!testReadImage()) {
            System.out.println("testReadImage failed.");
            return;
        }
        else {
            System.out.println("testReadImage passed.");
        }

        if (!testSaveImage()) {
            System.out.println("testSaveImage failed.");
            return;
        }
        else {
            System.out.println("testSaveImage passed.");
        }


        System.out.println("All tests passed.");
    }

}
