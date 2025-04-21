package VecQuant;

import Util.Util;
import VecQuant.src.LBGVecQuant;;

// take the input file and output file as command line arguments
// import the required libraries
// ask the user for their desired number of codeblocks
// and dimensions, providing them with 3 options for each one.
// use the options to create a codebook of the desired size and dimensions
// Use the encoder to encode the input file using the codebook
// Use the decoder to decode the encoded data using the codebook and the encoded array from the encoder
// Deserialize the decoded data and write it to the output file
// then print the image statistics to the user


public class main {

    public static void main(String[] args) {
        // check if the user has provided the correct number of arguments
        if (args.length != 2) {
            System.out.println("Please provide the input file and output file as command line arguments.");
            return;
        }

        // get the input and output file names from the command line arguments
        String inputFile = args[0];
        String outputFile = args[1];

        // ask the user for their desired number of codeblocks and dimensions
        int numCodeblocks = 0;
        int dimension = 0;
        // create a scanner object to read user input
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        // ask the user for the power of 2 for the number of codeblocks
        System.out.print("Enter the power of 2 for the number of codeblocks (1, 2, 3, 4, ...): ");
        int powerOf2 = scanner.nextInt();
        // calculate the number of codeblocks from the power of 2
        numCodeblocks = (int) Math.pow(2, powerOf2);
        System.out.println("Number of codeblocks: " + numCodeblocks);

        // ask the user for the power of 8 for the dimensions
        System.out.print("Enter the power of 8 for the dimension (1, 2, 3, 4, ...): ");
        int powerOf8 = scanner.nextInt();
        // calculate the dimensions from the power of 8
        dimension = (int) Math.pow(8, powerOf8);
        System.out.println("Dimension: " + dimension);


        // start timer
        long startTime = System.currentTimeMillis();



        // read the image data from the input file
        double[][][][][] imageData = Util.readImage(inputFile, dimension);
        // serialize the image data to a byte array
        double[][][][] serializedImageData = Util.serializeImageBlocks(imageData);

        // create a new LBGVecQuant object with the specified number of codeblocks and dimensions
        LBGVecQuant lbg = new LBGVecQuant(serializedImageData, numCodeblocks, 0.01); // 0.01 is the threshold for the LBG algorithm

        // encode the image data using the codebook
        int[] encodedData = lbg.Encode(serializedImageData, lbg.getCodebook());

        // decode the encoded data using the codebook
        double[][][][] decodedData = lbg.Decode(encodedData);

        // deserialize the decoded data to get the original image data
        int aspectX = imageData[0].length;
        int aspectY = imageData.length;
        double[][][][][] deserializedData = Util.deserializeImageBlocks(decodedData, aspectX, aspectY);

        // write the deserialized data to the output file
        Util.saveImage(deserializedData, outputFile, dimension);
        System.out.println("Image data written to " + outputFile);

        // stop timer
        long endTime = System.currentTimeMillis();

        // print time in seconds
        System.out.println("Time taken: " + (endTime - startTime) / 1000.0 + " seconds");
    }
}
