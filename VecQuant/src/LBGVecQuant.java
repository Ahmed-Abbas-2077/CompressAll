package VecQuant.src;

import java.util.Vector;

public class LBGVecQuant {
    
    private double[][][][] codebook;
    private double threshold;
    private String inputFilePath;
    private String outputFilePath;
    
    // utility methods

    public double[][][][] getCodebook() {
        return codebook;
    }

    // calculate the number of code blocks needed in the codebook, based on the compression ratio and the dimension of the image blocks
    // based on this formula: $$\therefore \; \psi = 2^{24\Pi\mathbb{d}^2}$$
    // where $\mathbb{d}$ is the dimension of the image blocks and $\Pi$ is the compression ratio
    public static int ComputeNumCodeBlocks(double compressionRatio, int blockDimension) {
        if (compressionRatio <= 0 || blockDimension <= 0) {
            throw new IllegalArgumentException("Compression ratio and block dimension must be positive.\n");
        }
        return (int) Math.ceil(Math.pow(2, 24 * compressionRatio * Math.pow(blockDimension, 2)));
    }

    // calculate the average block of a 4D array of doubles
    public static double[][][] ComputeAverageBlock(double[][][][] data) {
        if (isEmpty(data)) {
            throw new IllegalArgumentException("Data is empty.\n");
        }
        double[][][] avgBlock = new double[data[0].length][data[0][0].length][data[0][0][0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                for (int k = 0; k < data[i][j].length; k++) {
                    for (int l = 0; l < data[i][j][k].length; l++) {
                        avgBlock[j][k][l] += data[i][j][k][l];
                    }
                }
            }
        }
        for (int i = 0; i < avgBlock.length; i++) {
            for (int j = 0; j < avgBlock[i].length; j++) {
                for (int k = 0; k < avgBlock[i][j].length; k++) {
                    avgBlock[i][j][k] /= data.length;
                }
            }
        }
        return avgBlock;
    }

    public static boolean isEmpty(double[][][][] data) {
        if (data == null) {
            return true;
        }
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null || data[i].length == 0) {
                return true;
            }
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] == null || data[i][j].length == 0) {
                    return true;
                }
                for (int k = 0; k < data[i][j].length; k++) {
                    if (data[i][j][k] == null || data[i][j][k].length == 0) {
                        return true;
                    }
                    for (int l = 0; l < data[i][j][k].length; l++) {
                        if (data[i][j][k][l] < 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isEmpty(Vector<double[][][]> data) {
        if (data == null || data.size() == 0) {
            return true;
        }
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) == null || data.get(i).length == 0) {
                return true;
            }
            for (int j = 0; j < data.get(i).length; j++) {
                if (data.get(i)[j] == null || data.get(i)[j].length == 0) {
                    return true;
                }
                for (int k = 0; k < data.get(i)[j].length; k++) {
                    if (data.get(i)[j][k] == null || data.get(i)[j][k].length == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // calculate euclidean distance between two blocks of data
    public static double ComputeDistance(double[][][] block1, double[][][] block2) {
        double distance = 0.0;
        for (int i = 0; i < block1.length; i++) {
            for (int j = 0; j < block1[i].length; j++) {
                for (int k = 0; k < block1[i][j].length; k++) {
                    distance += Math.pow(block1[i][j][k] - block2[i][j][k], 2);
                }
            }
        }
        return Math.sqrt(distance);
    }

    // iterate through the training data blocks and calculate their distance to the code blocks
    // , then assign each training data block to its closest code block, appending it to
    // the index of code block in the quantization regions vector
    // , and return the quantization regions vector
    public static Vector<Vector<double[][][]>> ComputeQuantRegions(double[][][][] data, Vector<double[][][]> codebook) {
        if (isEmpty(data)) {
            throw new IllegalArgumentException("Data is empty.\n");
        }
        if (isEmpty(codebook)) {
            throw new IllegalArgumentException("codebook is empty.\n");
        }

        Vector<Vector<double[][][]>> quantizationRegions = new Vector<Vector<double[][][]>>();
        for (int i = 0; i < codebook.size(); i++) {
            quantizationRegions.add(new Vector<double[][][]>());
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

    public static Vector<double[][][]> UpdateCentroids(Vector<Vector<double[][][]>> quantizationRegions, Vector<double[][][]> codebook) {
        if (isEmpty(codebook)) {
            throw new IllegalArgumentException("codebook is empty.\n");
        }

        Vector<double[][][]> newCodebook = new Vector<double[][][]>();
        for (int i = 0; i < codebook.size(); i++) {
            Vector<double[][][]> region = quantizationRegions.get(i);
            if (region.size() == 0) {
                newCodebook.add(codebook.get(i)); // Keep the old codeblock if region is empty
                continue;
            }
            // Convert Vector<double[][][]> to double[][][][]
            double[][][][] regionArray = new double[region.size()][][][];
            for (int j = 0; j < region.size(); j++) {
                regionArray[j] = region.get(j);
            }
            double[][][] centroid = ComputeAverageBlock(regionArray);
            newCodebook.add(centroid);
        }
        return newCodebook;
    }

    // iterate through the codebook and split each code block into 2 blocks
    // , then add each new couple of blocks to the new codebook
    // return the new codebook
    public static Vector<double[][][]> SplitCodebook(Vector<double[][][]> codebook) {
        if (isEmpty(codebook)) {
            throw new IllegalArgumentException("codebook is empty.\n");
        }

        Vector<double[][][]> newCodebook = new Vector<double[][][]>();
        for (int i = 0; i < codebook.size(); i++) {
            double[][][] block1 = new double[codebook.get(i).length][codebook.get(i)[0].length][codebook.get(i)[0][0].length];
            double[][][] block2 = new double[codebook.get(i).length][codebook.get(i)[0].length][codebook.get(i)[0][0].length];
            for (int j = 0; j < codebook.get(i).length; j++) {
                for (int k = 0; k < codebook.get(i)[j].length; k++) {
                    for (int l = 0; l < codebook.get(i)[j][k].length; l++) {
                        double distance2Max = Math.abs(1 - codebook.get(i)[j][k][l]); // distance to 1 (max value)
                        double distance2Min = Math.abs(codebook.get(i)[j][k][l] - 0); // distance to 0 (min value)
                        // we add stochasticity by randomizing who would get min and max
                        // we have 4 choices: max-min, min-max, min-min, max-max
                        // we add the distance to the max value to the first block and the distance to the min value to the second block
                        int randomChoice = (int) (Math.random() * 4);
                        if (randomChoice == 0) {
                            block1[j][k][l] = codebook.get(i)[j][k][l] + (distance2Max / 2);
                            block2[j][k][l] = codebook.get(i)[j][k][l] - (distance2Min / 2);
                        } else if (randomChoice == 1) {
                            block1[j][k][l] = codebook.get(i)[j][k][l] - (distance2Min / 2);
                            block2[j][k][l] = codebook.get(i)[j][k][l] + (distance2Max / 2);
                        } else if (randomChoice == 2) {
                            block1[j][k][l] = codebook.get(i)[j][k][l] - (distance2Min / 2);
                            block2[j][k][l] = codebook.get(i)[j][k][l] - (distance2Min / 2);
                        } else {
                            block1[j][k][l] = codebook.get(i)[j][k][l] + (distance2Max / 2);
                            block2[j][k][l] = codebook.get(i)[j][k][l] + (distance2Max / 2);
                        }
                    }
                }
            }
            newCodebook.add(block1);
            newCodebook.add(block2);
        }
        return newCodebook;
    }

    public static double ComputeDistortion(double[][][][] data, Vector<double[][][]> codebook) {
        if (isEmpty(data)) {
            throw new IllegalArgumentException("Data is empty.\n");
        }
        if (isEmpty(codebook)) {
            throw new IllegalArgumentException("codebook is empty.\n");
        }

        double distortion = 0.0;
        Vector<Vector<double[][][]>> quantizationRegions = ComputeQuantRegions(data, codebook);
        
        for (int i = 0; i < codebook.size(); i++) {
            Vector<double[][][]> region = quantizationRegions.get(i);
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
    public LBGVecQuant(double[][][][] trainingData, int numCodeblocks, double threshold) {
        this.codebook = new double[numCodeblocks][][][];
        this.threshold = threshold;

        // stores the initial code blocks then assigns them to the codebook at the end
        Vector<double[][][]> trainingCodeBlocks = new Vector<double[][][]>();

        // the average block is the first code block
        double[][][] avgBlock = ComputeAverageBlock(trainingData);
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
            this.codebook[i] = trainingCodeBlocks.get(i);
        }
    }
    
//     // Encode and Decode methods (stubs - to be implemented)
//     public int[] Encode(double[][][][] data) {
//         // TODO: Implementation
//         return new int[0];
//     }
    
//     public double[][][][] Decode(int[] encodedData) {
//         // TODO: Implementation
//         return new double[0][0][0][0];
//     }
}