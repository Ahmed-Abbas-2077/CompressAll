package Multi_VQ.src;

import java.util.Vector;
import Util.Util;

public class LBGVecQuant {

    // calculate the number of code blocks needed in the codebook, based on the compression ratio and the dimension of the image blocks
    // based on this formula: $$\therefore \; \psi = 2^{24\Pi\mathbb{d}^2}$$
    // where $\mathbb{d}$ is the dimension of the image blocks and $\Pi$ is the compression ratio
    public static int ComputeNumCodeBlocks(double compressionRatio, int blockDimension) {
        if (compressionRatio <= 0 || blockDimension <= 0) {
            throw new IllegalArgumentException("Compression ratio and block dimension must be positive.\n");
        }
        return (int) Math.ceil(Math.pow(2, 24 * compressionRatio * Math.pow(blockDimension, 2)));
    }

    // calculate the average vector of a 3D array of 2D vectors
    public static double[][] ComputeAverageVector(double[][][] data) {
        double[][] avgBlock = new double[data[0].length][data[0][0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                for (int k = 0; k < data[i][j].length; k++) {
                    avgBlock[j][k] += data[i][j][k];
                }
            }
        }
        for (int i = 0; i < avgBlock.length; i++) {
            for (int j = 0; j < avgBlock[i].length; j++) {
                avgBlock[i][j] /= data.length;
            }
        }
        return avgBlock;
    }

    public static double[][] ComputeAverageVector(Vector<double[][]> data) {
        double[][] avgBlock = new double[data.get(0).length][data.get(0)[0].length];
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).length; j++) {
                for (int k = 0; k < data.get(i)[j].length; k++) {
                    avgBlock[j][k] += data.get(i)[j][k];
                }
            }
        }
        for (int i = 0; i < avgBlock.length; i++) {
            for (int j = 0; j < avgBlock[i].length; j++) {
                avgBlock[i][j] /= data.size();
            }
        }
        return avgBlock;
    }


    // calculate euclidean distance between two blocks of data
    public static double ComputeDistance(double[][] block1, double[][] block2) {
        double distance = 0.0;
        for (int i = 0; i < block1.length; i++) {
            for (int j = 0; j < block1[i].length; j++) {
                distance += Math.pow(block1[i][j] - block2[i][j], 2);
            }
        }
        return Math.sqrt(distance);
    }

    // iterate through the training data blocks and calculate their distance to the code blocks
    // , then assign each training data block to its closest code block, appending it to
    // the index of code block in the quantization regions vector
    // , and return the quantization regions vector
    public static Vector<Vector<double[][]>> ComputeQuantRegions(double[][][] data, Vector<double[][]> codebook) {

        Vector<Vector<double[][]>> quantizationRegions = new Vector<Vector<double[][]>>();
        for (int i = 0; i < codebook.size(); i++) {
            quantizationRegions.add(new Vector<double[][]>());
        }

        for (int i = 0; i < data.length; i++) {
            double minDistance = Double.MAX_VALUE;
            int minIndex = -1;
            for (int j = 0; j < codebook.size(); j++) {
                double distance = ComputeDistance(data[i], codebook.get(j));
                if (distance < minDistance) {
                    minDistance = distance;
                    minIndex = j;
                }
            }
            quantizationRegions.get(minIndex).add(data[i]);
        }
        return quantizationRegions;
    }

    public static Vector<double[][]> UpdateCentroids(Vector<Vector<double[][]>> quantizationRegions, Vector<double[][]> codebook) {

        Vector<double[][]> newCodebook = new Vector<double[][]>();

        for (int i = 0; i < codebook.size(); i++) {
            Vector<double[][]> region = quantizationRegions.get(i);
            if (region.size() == 0) {
                newCodebook.add(codebook.get(i)); // Keep the old codeblock if region is empty
                continue;
            }
            double[][] centroid = ComputeAverageVector(region);
            newCodebook.add(centroid);
        }
        return newCodebook;
    }

    // iterate through the codebook and split each code block into 2 blocks
    // , then add each new couple of blocks to the new codebook
    // return the new codebook
    public static Vector<double[][]> SplitCodebook(Vector<double[][]> codebook) {

        Vector<double[][]> newCodebook = new Vector<double[][]>();

        for (int i = 0; i < codebook.size(); i++) {
            double[][] block1 = new double[codebook.get(i).length][codebook.get(i)[0].length];
            double[][] block2 = new double[codebook.get(i).length][codebook.get(i)[0].length];
            for (int j = 0; j < codebook.get(i).length; j++) {
                for (int k = 0; k < codebook.get(i)[j].length; k++) {
                    double distance2Max = Math.abs(1 - codebook.get(i)[j][k]); // distance to 1 (max value)
                    double distance2Min = Math.abs(codebook.get(i)[j][k] - 0); // distance to 0 (min value)
                    // we add stochasticity by randomizing who would get min and max
                    // we have 4 choices: max-min, min-max, min-min, max-max
                    // we add the distance to the max value to the first block and the distance to the min value to the second block
                    int randomChoice = (int) (Math.random() * 4);
                    if (randomChoice == 0) {
                        block1[j][k] = codebook.get(i)[j][k] + (distance2Max / 2);
                        block2[j][k] = codebook.get(i)[j][k] - (distance2Min / 2);
                    } else if (randomChoice == 1) {
                        block1[j][k] = codebook.get(i)[j][k] - (distance2Min / 2);
                        block2[j][k] = codebook.get(i)[j][k] + (distance2Max / 2);
                    } else if (randomChoice == 2) {
                        block1[j][k] = codebook.get(i)[j][k] - (distance2Min / 2);
                        block2[j][k] = codebook.get(i)[j][k] - (distance2Min / 2);
                    } else {
                        block1[j][k] = codebook.get(i)[j][k] + (distance2Max / 2);
                        block2[j][k] = codebook.get(i)[j][k] + (distance2Max / 2);
                    }
                    
                }
            }
            newCodebook.add(block1);
            newCodebook.add(block2);
        }
        return newCodebook;
    }

    public static double ComputeDistortion(double[][][] data, Vector<double[][]> codebook) {

        double distortion = 0.0;
        Vector<Vector<double[][]>> quantizationRegions = ComputeQuantRegions(data, codebook);
        
        for (int i = 0; i < codebook.size(); i++) {
            Vector<double[][]> region = quantizationRegions.get(i);
            for (int j = 0; j < region.size(); j++) {
                distortion += ComputeDistance(codebook.get(i), region.get(j));
            }
        }

        distortion /= data.length;
        return distortion;
    }

    public static boolean CheckConvergence(double prevDistortion, double currDistortion, double threshold) {
        return Math.abs((prevDistortion - currDistortion) / currDistortion) < threshold;
    }

    // constructor
    // creates a vector of 3d code blocks that represent the initial code blocks
    // , using k-means clustering algorithm. Start with the average block
    // , then split each block into 2 blocks, and repeat until the desired number of blocks is reached.
    // Once the desired number of blocks is reached, the algorithm will stop splitting and return the codebook.
    // The codebook is a 4D array of doubles, where the first dimension is the number of code blocks,
    // the second dimension is the number of rows in each block, the third dimension is the number of columns in each block, and the fourth dimension is the number of channels in each block.
    // The codebook is used to represent the training data in a more compact form, by replacing each block of training data with the closest code block in the codebook.
    public double[][][] codebook(double[][][] trainingData, int numCodeblocks, double threshold) {
        double[][][] codebook = new double[numCodeblocks][][];

        // stores the initial code blocks then assigns them to the codebook at the end
        Vector<double[][]> trainingCodeBlocks = new Vector<double[][]>();

        // the average block is the first code block
        double[][] avgBlock = ComputeAverageVector(trainingData);
        trainingCodeBlocks.add(avgBlock);

        while(trainingCodeBlocks.size() < numCodeblocks) {
            // split the code blocks into 2 blocks
            trainingCodeBlocks = SplitCodebook(trainingCodeBlocks);
            // update the code blocks with the new blocks
            trainingCodeBlocks = UpdateCentroids(ComputeQuantRegions(trainingData, trainingCodeBlocks), trainingCodeBlocks);
        }

        // keep updating the codebook until convergence
        // where convergence is determined by the threshold value
        double prevDistortion = 0.0;
        double currDistortion = ComputeDistortion(trainingData, trainingCodeBlocks);
        while (!CheckConvergence(prevDistortion, currDistortion, threshold)) {
            prevDistortion = currDistortion;
            trainingCodeBlocks = UpdateCentroids(ComputeQuantRegions(trainingData, trainingCodeBlocks), trainingCodeBlocks);
            currDistortion = ComputeDistortion(trainingData, trainingCodeBlocks);
        }

        // assign the final code blocks to the codebook
        for (int i = 0; i < trainingCodeBlocks.size(); i++) {
            codebook[i] = trainingCodeBlocks.get(i);
        }
        return codebook;
    }
    
    // Encode
    // create a new int array where each index represents the index of the image block in the data
    // , and the value at that index is the index of the closest code block in the codebook
    // , then return the int array
    // , this is done by iterating over each block of image data, finding the closest code block in the codebook,
    // and assigning the index of the closest code block to the int array at the index of the image block
    public int[] Encode(double[][][] data, double[][][] codebook) {

        int[] encodedData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            double minDistance = Double.MAX_VALUE;
            int minIndex = -1;
            for (int j = 0; j < codebook.length; j++) {
                double distance = ComputeDistance(data[i], codebook[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                    minIndex = j;
                }
            }
            encodedData[i] = minIndex;
        }
        return encodedData;
    }
    
    // Decode
    // create a new 3D array of doubles where each index represents the index of the image block in the data
    public double[][][] Decode(int[] encodedData, double[][][] codebook) {
        double[][][] decodedData = new double[encodedData.length][][];
        for (int i = 0; i < encodedData.length; i++) {
            decodedData[i] = codebook[encodedData[i]];
        }
        return decodedData;
    }


    public Vector<double[][][]> codebookRGB(Vector<double[][][]> rgbImages, int numCodeVectors, int dimension){
        Vector<double[][][]> rgbValues = Util.extractRGB(rgbImages, dimension);

        // create a codebook for each color channel
        Vector<double[][][]> codebooks = new Vector<double[][][]>();
        for (int i = 0; i < rgbValues.size(); i++) {
            double[][][] codebook = new double[numCodeVectors][][];
            codebook = codebook(rgbValues.get(i), numCodeVectors, 0.01);
            codebooks.add(codebook);
        }

        return codebooks;
    }


    public void multiImageCodebook(Vector<String> imagePaths, int dimension, int numCodeVectors){
        // create a vector of image paths
        Vector<double[][][]> rgbImages = new Vector<double[][][]>();
        for (int i = 0; i < imagePaths.size(); i++) {
            double[][][] image = Util.readImage(imagePaths.get(i));
            rgbImages.add(image);
        }

        // create a codebook for each color channel
        Vector<double[][][]> codebooks = codebookRGB(rgbImages, numCodeVectors, dimension);

        // save the codebooks to files
        for (int i = 0; i < codebooks.size(); i++) {
            Util.storeCodebook("codebook" + i + ".txt", codebooks.get(i));
        }
    }




}