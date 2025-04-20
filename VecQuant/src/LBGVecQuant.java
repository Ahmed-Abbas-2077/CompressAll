package VecQuant.src;

import java.util.Vector;

public class LBGVecQuant {
    
    private double[][][][] codebook;
    private double threshold;
    private string inputFilePath;
    private string outputFilePath;
    
    // utility methods
    private double[][][] ComputeAverageBlock(double[][][][] data); // done
    private boolean isEmpty(double[][][][] data); // done
    private double ComputeDistance(double[][][] block1, double[][][] block2); // done
    private Vector<Vector<double[][][]>> ComputeQuantRegions(double[][][][] data, double[][][][] codebook); // done
    private double[][][][] UpdateCentroids(Vector<Vector<double[][][]>> quantizationRegions, double[][][][] codebook); // done
    private double[][][][] SplitCodebook(double[][][][] data, double[][][][] codebook, double threshold); // done
    private double ComputeDistortion(code);
    private boolean CheckConvergence(prevDistortion, currDistortion, threshold);
    
    // Constructor
    public LBGVecQuant(double[][][][] trainingData, int numCodeblocks, double threshold); 

    // encode/decode methods
    public int[] Encode(double[][][][] data);
    public double[][][][] Decode(int[] encodedData);
}



// utility methods

// calculate the average block of a 4D array of doubles
private double[][][] ComputeAverageBlock(double[][][][] data) {
    if (isEmpty(data)) {
        raise new IllegalArgumentException("Data is empty.\n");
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


private boolean isEmpty(double[][][][] data) {
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
            }
        }
    }
    return false;
}


// calculate euclidean distance between two blocks of data
private double ComputeDistance(double[][][] block1, double[][][] block2) {
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
private Vector<Vector<double[][][]>> ComputeQuantRegions(double[][][][] data, double[][][][] codebook) {
    if (isEmpty(data)) {
        raise new IllegalArgumentException("Data is empty.\n");
    }
    if (isEmpty(codebook)) {
        raise new IllegalArgumentException("codebook is empty.\n");
    }


    Vector<Vector<double[][][]>> quantizationRegions = new Vector<Vector<double[][][]>>();
    for (int i = 0; i < codebook.length; i++) {
        quantizationRegions.add(new Vector<double[][][]>());
    }

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
        quantizationRegions.get(minIndex).add(data[i]);
    }
    return quantizationRegions;
}


private double[][][][] UpdateCentroids(Vector<Vector<double[][][]>> quantizationRegions, double[][][][] codebook) {
    if (isEmpty(quantizationRegions)) {
        raise new IllegalArgumentException("Quantization regions are empty.\n");
    }
    if (isEmpty(codebook)) {
        raise new IllegalArgumentException("codebook is empty.\n");
    }

    double[][][][] newCodebook = new double[codebook.length][][][];
    for (int i = 0; i < codebook.length; i++) {
        Vector<double[][][]> region = quantizationRegions.get(i);
        if (region.size() == 0) {
            continue;
        }
        double[][][] centroid = ComputeAverageBlock(region.toArray(new double[region.size()][][][]));
        newCodebook[i] = centroid;
    }
    return newCodebook;
}


// iterate through the codebook and split each code block into 2 blocks
// , then add each new couple of blocks to the new codebook
// return the new codebook

private Vector<double[][][]> SplitCodebook(double[][][][] codebook, double threshold) {
    if (isEmpty(data)) {
        raise new IllegalArgumentException("Data is empty.\n");
    }
    if (isEmpty(codebook)) {
        raise new IllegalArgumentException("codebook is empty.\n");
    }

    Vector<double[][][]> newCodebook = new double[codebook.length * 2][][][];
    for (int i = 0; i < codebook.length; i++) {
        double[][][] block1 = new double[codebook[i].length][codebook[i][0].length][codebook[i][0][0].length];
        double[][][] block2 = new double[codebook[i].length][codebook[i][0].length][codebook[i][0][0].length];
        for (int j = 0; j < codebook[i].length; j++) {
            for (int k = 0; k < codebook[i][j].length; k++) {
                for (int l = 0; l < codebook[i][j][k].length; l++) {
                    double distance2Max = Math.abs(255 - codebook[i][j][k][l]); // distance to 255 (max value)
                    double distance2Min = Math.abs(codebook[i][j][k][l] - 0); // distance to 0 (min value)
                    block1[j][k][l] = Math.ceil(codebook[i][j][k][l] + distance2Max / 2);
                    block2[j][k][l] = Math.floor(codebook[i][j][k][l] - distance2Min / 2);
                }
            }
        }
        newCodebook.add(block1);
        newCodebook.add(block2);
    }
    return newCodebook;
}


private double ComputeDistortion(double[][][][] data, double[][][][] codebook) {
    if (isEmpty(data)) {
        raise new IllegalArgumentException("Data is empty.\n");
    }
    if (isEmpty(codebook)) {
        raise new IllegalArgumentException("codebook is empty.\n");
    }

    double distortion = 0.0;
    quantizationRegions = ComputeQuantRegions(data, codebook);
    
    for (int i = 0; i < codebook.length; i++) {
        Vector<double[][][]> region = quantizationRegions.get(i);
        for (int j = 0; j < region.size(); j++) {
            distortion += ComputeDistance(codebook[i], region.get(j));
        }
    }

    distortion /= data.length;
    return distortion;
}


private boolean CheckConvergence(double prevDistortion, double currDistortion, double threshold) {
    if (Math.abs((prevDistortion - currDistortion) / currDistortion) < threshold) {
        return true;
    }
    return false;
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
        trainingCodeBlocks = SplitCodebook(trainingData, trainingCodeBlocks, threshold);
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
