package VecQuant.testing;

import Util.Util;
import VecQuant.src.LBGVecQuant;
import java.util.Vector;

public class LBGTest {
    
    public static double[][][][][] testReadImage(String filePath) {
        double[][][][][] blocks = Util.readImage(filePath, 8);
        assert blocks != null : "Failed to read image from file";
        return blocks;
    }

    public static double[][][][] testSerializeImageBlocks(double[][][][][] blocks) {
        double[][][][] serializedBlocks = Util.serializeImageBlocks(blocks);
        assert serializedBlocks != null : "Failed to serialize image blocks";
        return serializedBlocks;
    }

    public static double[][][][][] testDeserializeImageBlocks(double[][][][] serializedBlocks, int aspectX, int aspectY) {
        double[][][][][] blocks = Util.deserializeImageBlocks(serializedBlocks, aspectX, aspectY);
        assert blocks != null : "Failed to deserialize image blocks";
        return blocks;
    }

    public static void testSaveImage(double[][][][][] blocks, String filePath) {
        Util.saveImage(blocks, filePath, 8);
        // check if the image is saved correctly
        double[][][][][] loadedBlocks = Util.readImage(filePath, 8);
        assert loadedBlocks != null : "Failed to load image from file after saving";
        assert blocks.length == loadedBlocks.length : "Loaded image blocks do not match original blocks";
    }

    // Test ComputeAverageBlock method
    public static double[][][] testComputeAverageBlock(double[][][][] data) {
        double[][][] averageBlock = LBGVecQuant.ComputeAverageBlock(data);
        assert averageBlock != null : "Failed to compute average block";
        // print average block for debugging
        // System.out.println("Average Block: \n");
        // for (int i = 0; i < averageBlock.length; i++) {
        //     for (int j = 0; j < averageBlock[i].length; j++) {
        //         for (int k = 0; k < averageBlock[i][j].length; k++) {
        //             System.out.print(averageBlock[i][j][k] + " ");
        //         }
        //         System.out.print(", ");
        //     }
        //     System.out.println("\n");
        // }
        return averageBlock;
    }

    public static boolean testIsEmptyBlock(double[][][][] block) {
        boolean isEmpty = LBGVecQuant.isEmpty(block);
        assert isEmpty : "Block is not empty as expected";
        return isEmpty;
    }
    
    public static boolean testIsEmptyBlock(Vector<double[][][]> block) {
        boolean isEmpty = LBGVecQuant.isEmpty(block);
        assert isEmpty : "Block is not empty as expected";
        return isEmpty;
    }

    public static double TestComputeDistance(double[][][] block1, double[][][] block2) {
        double distance = LBGVecQuant.ComputeDistance(block1, block2);
        assert distance >= 0 : "Distance cannot be negative";
        return distance;
    }

    public static Vector<Vector<double[][][]>> testComputeQuantRegions(double[][][][] data, Vector<double[][][]> averageBlocks) {
        Vector<Vector<double[][][]>> quantRegions = LBGVecQuant.ComputeQuantRegions(data, averageBlocks);
        assert quantRegions != null : "Failed to compute quantization regions";

        // print quantization regions for debugging
        // System.out.println("\n\nQuantization Regions: \n");
        // for (int i = 0; i < quantRegions.size(); i++) {
        //     Vector<double[][][]> region = quantRegions.get(i);
        //     System.out.print("----------------------------------------------------------\n");
        //     System.out.print("\n\nRegion " + i + ": \n\n");
        //     System.out.print("----------------------------------------------------------\n");
        //     for (int j = 0; j < region.size(); j++) {
        //         double[][][] block = region.get(j);
        //         System.out.print("\n\t\t[ ");
        //         for (int k = 0; k < block.length; k++) {
        //             for (int l = 0; l < block[k].length; l++) {
        //                 for (int m = 0; m < block[k][l].length; m++) {
        //                     System.out.print(block[k][l][m] + " ");
        //                 }
        //                 System.out.print(", ");
        //             }
        //             System.out.println("\n");
        //         }
        //         System.out.print("\t] \n");
        //     }
        //     System.out.println("\n");
        // }
        return quantRegions;
    }

    // test UpdateCentroids
    public static Vector<double[][][]> testUpdateCentroids(Vector<Vector<double[][][]>> quantRegions, Vector<double[][][]> averageBlocks) {
        Vector<double[][][]> updatedCentroids = LBGVecQuant.UpdateCentroids(quantRegions, averageBlocks);
        assert updatedCentroids != null : "Failed to update centroids";
        // print updated centroids for debugging
        // System.out.println("\nUpdated Centroids: \n");
        // for (int i = 0; i < updatedCentroids.size(); i++) {
        //     double[][][] centroid = updatedCentroids.get(i);
        //     System.out.print("Centroid " + i + ": \n");
        //     for (int j = 0; j < centroid.length; j++) {
        //         for (int k = 0; k < centroid[j].length; k++) {
        //             for (int l = 0; l < centroid[j][k].length; l++) {
        //                 System.out.print(centroid[j][k][l] + " ");
        //             }
        //             System.out.print(", ");
        //         }
        //         System.out.println("\n");
        //     }
        // }
        return updatedCentroids;
    }

    // test SplitCodebook
    public static Vector<double[][][]> testSplitCodebook(Vector<double[][][]> codebook) {
        Vector<double[][][]> splitCodebook = LBGVecQuant.SplitCodebook(codebook);
        assert splitCodebook != null : "Failed to split codebook";
        // print split codebook for debugging
        // System.out.println("\nSplit Codebook: \n");
        // for (int i = 0; i < splitCodebook.size(); i++) {
        //     double[][][] centroid = splitCodebook.get(i);
        //     System.out.print("Centroid " + i + ": \n");
        //     for (int j = 0; j < centroid.length; j++) {
        //         for (int k = 0; k < centroid[j].length; k++) {
        //             for (int l = 0; l < centroid[j][k].length; l++) {
        //                 System.out.print(centroid[j][k][l] + " ");
        //             }
        //             System.out.print(", ");
        //         }
        //         System.out.println("\n");
        //     }
        // }
        return splitCodebook;
    }

    // test ComputeDistortion
    public static double testComputeDistortion(double[][][][] data, Vector<double[][][]> averageBlocks) {
        double distortion = LBGVecQuant.ComputeDistortion(data, averageBlocks);
        assert distortion >= 0 : "Distortion cannot be negative";
        return distortion;
    }


    // test CheckConvergence
    public static boolean testCheckConvergence(double prevDistortion, double currDistortion, double threshold) {
        boolean converged = LBGVecQuant.CheckConvergence(prevDistortion, currDistortion, threshold);
        assert converged : "Failed to check convergence";
        return converged;
    }


    // test ComputeNumCodeBlocks
    public static int testComputeNumCodeBlocks(double compressionRatio, int dimension) {
        int numCodeBlocks = LBGVecQuant.ComputeNumCodeBlocks(compressionRatio, dimension);
        assert numCodeBlocks > 0 : "Number of code blocks must be greater than 0";
        return numCodeBlocks;
    }

    // test LBGVecQuant constructor
    public static LBGVecQuant testLBGVecQuantConstructor(double[][][][] data, int numCodeBlocks, double threshold) {
        LBGVecQuant lbg = new LBGVecQuant(data, numCodeBlocks, threshold);
        assert lbg != null : "Failed to create LBGVecQuant instance";
        return lbg;
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java VecQuant.testing.LBGTest <input_file_path> <output_file_path>");
            return;
        }
        
        try {
            String filePath = args[0];
            System.out.println("\nReading image from " + filePath);
            double[][][][][] blocks = testReadImage(filePath);
            
            System.out.println("\nSerializing image blocks...");
            double[][][][] serializedBlocks = testSerializeImageBlocks(blocks);
            int aspectX = blocks[0].length;
            int aspectY = blocks.length;
            
            System.out.println("\nTesting average block calculation...");
            double[][][] averageBlock1 = testComputeAverageBlock(blocks[0]);
            double[][][] averageBlock2 = testComputeAverageBlock(blocks[1]);
            double[][][] averageBlock3 = testComputeAverageBlock(blocks[2]);
            double[][][] averageBlock4 = testComputeAverageBlock(blocks[3]);
            
            System.out.println("\nDeserializing image blocks...");
            double[][][][][] deserializedBlocks = testDeserializeImageBlocks(serializedBlocks, aspectX, aspectY);

            // print deserialized blocks for debugging
            // System.out.println("\nDeserialized Blocks: \n");
            // for (int i = 0; i < deserializedBlocks.length; i++) {
            //     double[][][][] block = deserializedBlocks[i];
            //     System.out.print("Block " + i + ": \n");
            //     for (int j = 0; j < block.length; j++) {
            //         System.out.print("\t Row " + j + ": \n");
            //         for (int k = 0; k < block[j].length; k++) {
            //             for (int l = 0; l < block[j][k].length; l++) {
            //                 System.out.print(block[j][k][l] + " ");
            //             }
            //             System.out.print(", ");
            //         }
            //         System.out.println("\n");
            //     }
            // }
            
            String outputFilePath = args[1];
            System.out.println("\nSaving image to " + outputFilePath);
            testSaveImage(deserializedBlocks, outputFilePath);
            System.out.println("Image saved successfully!");

            // test non-empty block
            System.out.println("\nTesting empty block...");
            double[][][][] emptyBlock = null;
            boolean isEmpty = testIsEmptyBlock(emptyBlock);
            String result = isEmpty ? "Block is empty as expected" : "Block is not empty";
            System.out.println(result);

            // test vector empty block
            System.out.println("\nTesting empty vector block...");
            Vector<double[][][]> emptyVectorBlock = new Vector<>();
            boolean isEmptyVector = testIsEmptyBlock(emptyVectorBlock);
            String vectorResult = isEmptyVector ? "Vector block is empty as expected" : "Vector block is not empty";
            System.out.println(vectorResult);

            // test distance computation
            System.out.println("\nTesting distance computation...");
            double[][][] block1 = new double[][][] { { { 1.0, 2.0 }, { 3.0, 4.0 } }, { { 5.0, 6.0 }, { 7.0, 8.0 } } };
            double[][][] block2 = new double[][][] { { { 5.0, 6.0 }, { 7.0, 8.0 } }, { { 1.0, 2.0 }, { 3.0, 4.0 } } };
            double distance = TestComputeDistance(block1, block2);
            System.out.println("Distance between blocks: " + distance);

            // Compute quantization regions
            System.out.println("\nTesting quantization regions computation...");
            Vector<double[][][]> averageBlockVector = new Vector<>();
            averageBlockVector.add(averageBlock1);
            averageBlockVector.add(averageBlock2);
            averageBlockVector.add(averageBlock3);
            averageBlockVector.add(averageBlock4);
            Vector<Vector<double[][][]>> quantRegions = testComputeQuantRegions(serializedBlocks, averageBlockVector);
            System.out.println("Quantization regions computed successfully!");

            // Update centroids
            System.out.println("\nTesting centroid update...");
            Vector<double[][][]> updatedCentroids = testUpdateCentroids(quantRegions, averageBlockVector);
            System.out.println("Centroids updated successfully!");

            // Split codebook
            System.out.println("\nTesting codebook splitting...");
            Vector<double[][][]> codebook1 = testSplitCodebook(updatedCentroids); 
            codebook1 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook1), codebook1);
            Vector<double[][][]> codebook2 = testSplitCodebook(codebook1);
            codebook2 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook2), codebook2);
            Vector<double[][][]> codebook3 = testSplitCodebook(codebook2);
            codebook3 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook3), codebook3);
            Vector<double[][][]> codebook4 = testSplitCodebook(codebook3);
            codebook4 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook4), codebook4);
            System.out.println("Codebook split successfully!");


            // computing quantization regions with 32 (2^5) centroids
            System.out.println("\nTesting quantization regions computation with 64 centroids...");
            testComputeQuantRegions(serializedBlocks, codebook3);
            System.out.println("Quantization regions computed successfully with 64 centroids!");

            // computing distortion
            System.out.println("\nTesting distortion computation...");
            double distortion1 = testComputeDistortion(serializedBlocks, codebook4);
            System.out.println("Distortion1: " + distortion1);
            // // update codebook4 centroids
            // codebook4 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook4), codebook4);
            // double distortion2 = testComputeDistortion(serializedBlocks, codebook4);
            // System.out.println("Distortion2: " + distortion2);

            // // update codebook4 centroids again
            // codebook4 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook4), codebook4);
            // double distortion3 = testComputeDistortion(serializedBlocks, codebook4);
            // System.out.println("Distortion3: " + distortion3);

            // // update codebook4 centroids again
            // codebook4 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook4), codebook4);
            // double distortion4 = testComputeDistortion(serializedBlocks, codebook4);
            // System.out.println("Distortion4: " + distortion4);


            // check convergence
            System.out.println("\nTesting convergence check...");
            double threshold = 0.0001;
            double prevDistortion = 0.0;
            double currDistortion = distortion1;
            while (!testCheckConvergence(prevDistortion, currDistortion, threshold)) {
                prevDistortion = currDistortion;
                codebook4 = testUpdateCentroids(testComputeQuantRegions(serializedBlocks, codebook4), codebook4);
                currDistortion = testComputeDistortion(serializedBlocks, codebook4);
                System.out.println("Previous Distortion: " + prevDistortion);
                System.out.println("Current Distortion: " + currDistortion + "\n");
            }
            System.out.println("Convergence achieved!");
            
            
            // compute number of code blocks
            System.out.println("\nTesting number of code blocks computation...");
            int dimension = 8; // example dimension
            double compressionRatio = 1.0/192.0;
            int numCodeBlocks = testComputeNumCodeBlocks(compressionRatio, dimension);
            System.out.println("Number of code blocks: " + numCodeBlocks);


            // test LBGVecQuant constructor
            System.out.println("\nTesting LBGVecQuant constructor...");
            LBGVecQuant lbg = testLBGVecQuantConstructor(serializedBlocks, numCodeBlocks, threshold);
            System.out.println("LBGVecQuant instance created successfully!");

            // print final codebook
            System.out.println("\nFinal Codebook: \n");
            double[][][][] finalCodebook = lbg.getCodebook();
            // for (int i = 0; i < finalCodebook.length; i++) {
            //     double[][][] centroid = finalCodebook[i];
            //     System.out.print("Centroid " + i + ": \n");
            //     for (int j = 0; j < centroid.length; j++) {
            //         for (int k = 0; k < centroid[j].length; k++) {
            //             for (int l = 0; l < centroid[j][k].length; l++) {
            //                 System.out.print(centroid[j][k][l] + " ");
            //             }
            //             System.out.print(", ");
            //         }
            //         System.out.println("\n");
            //     }
            // }
            

            // test encoding
            System.out.println("\nTesting encoding...");
            int[] encodedData = lbg.Encode(serializedBlocks, finalCodebook);
            assert encodedData != null : "Failed to encode data";
            System.out.println("Encoding completed successfully!");

            // test decoding
            System.out.println("\nTesting decoding...");
            double[][][][] decodedData = lbg.Decode(encodedData);
            assert decodedData != null : "Failed to decode data";
            System.out.println("Decoding completed successfully!");

            // print decoded data
            // System.out.println("\nDecoded Data: \n");
            // for (int i = 0; i < decodedData.length; i++) {
            //     double[][][] block = decodedData[i];
            //     System.out.print("Block " + i + ": \n");
            //     for (int j = 0; j < block.length; j++) {
            //         for (int k = 0; k < block[j].length; k++) {
            //             for (int l = 0; l < block[j][k].length; l++) {
            //                 System.out.print(block[j][k][l] + " ");
            //             }
            //             System.out.print(", ");
            //         }
            //         System.out.println("\n");
            //     }
            // }


            // test saving encoded data
            
            // deserialize encoded data
            System.out.println("\nTesting deserialization of encoded data...");
            double[][][][][] deserializedDecodedData = Util.deserializeImageBlocks(decodedData, aspectX, aspectY);
            assert deserializedDecodedData != null : "Failed to deserialize encoded data";
            System.out.println("Deserialization of encoded data completed successfully!");
            // print deserialized decoded data
            System.out.println("\nDeserialized Decoded Data: \n");
            // for (int i = 0; i < deserializedDecodedData.length; i++) {
            //     double[][][][] block = deserializedDecodedData[i];
            //     System.out.print("Block " + i + ": \n");
            //     for (int j = 0; j < block.length; j++) {
            //         System.out.print("\t Row " + j + ": \n");
            //         for (int k = 0; k < block[j].length; k++) {
            //             for (int l = 0; l < block[j][k].length; l++) {
            //                 for (int m = 0; m < block[j][k][l].length; m++) {
            //                     System.out.print(block[j][k][l][m] + " ");
            //                 }
            //                 System.out.print(", ");
            //             }
            //             System.out.println("\n");;
            //         }
            //         System.out.println("\n-------------\n");
            //     }
            // }

            // save encoded data
            String compressedFilePath = "E:\\Zewail\\Sophomore Year\\DSAI 325\\DNA-Compression\\data\\mit2_decompressed.png";
            System.out.println("\nSaving encoded data to " + compressedFilePath);
            Util.saveImage(deserializedDecodedData, compressedFilePath, 8);
            System.out.println("Compressed image saved successfully!");

            // display Image Compression Statistics
            System.out.println("\nImage Compression Statistics:");
            Util.printImageCompressionStatistics(filePath, encodedData, compressedFilePath);
            System.out.println("Image compression statistics displayed successfully!");


            System.out.println("\nAll tests completed successfully!");
        } catch (Exception e) {
            System.err.println("Error during tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


