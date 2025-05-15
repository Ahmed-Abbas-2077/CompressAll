package Multi_VQ.src;

import java.util.Vector;
import Util.Util;
import java.util.ArrayList;

public class LBGVecQuant {


    public static int ComputeNumCodeBlocks(double compressionRatio, int blockDimension) {
        if (compressionRatio <= 0 || blockDimension <= 0) {
            throw new IllegalArgumentException("Compression ratio and block dimension must be positive.\n");
        }
        return (int) Math.ceil(Math.pow(2, 24 * compressionRatio * Math.pow(blockDimension, 2)));
    }

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


    public static double ComputeDistance(double[][] block1, double[][] block2) {
        double distance = 0.0;
        for (int i = 0; i < block1.length; i++) {
            for (int j = 0; j < block1[i].length; j++) {
                distance += Math.pow(block1[i][j] - block2[i][j], 2);
            }
        }
        return Math.sqrt(distance);
    }


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
                newCodebook.add(codebook.get(i)); 
                continue;
            }
            double[][] centroid = ComputeAverageVector(region);
            newCodebook.add(centroid);
        }
        return newCodebook;
    }


    public static Vector<double[][]> SplitCodebook(Vector<double[][]> codebook, Boolean YUV, int channel) {

        Vector<double[][]> newCodebook = new Vector<double[][]>();

        for (int i = 0; i < codebook.size(); i++) {
            double[][] block1 = new double[codebook.get(i).length][codebook.get(i)[0].length];
            double[][] block2 = new double[codebook.get(i).length][codebook.get(i)[0].length];

            for (int j = 0; j < codebook.get(i).length; j++) {
                for (int k = 0; k < codebook.get(i)[j].length; k++) {

                    double maxVal = 1.0; 
                    double minVal = 0.0; 
                    
                    if (YUV && channel > 0) { 
                        maxVal = (channel == 1) ? 0.436 : 0.615;
                        minVal = -maxVal;
                    }
                    
                    double distance2Max = Math.abs(maxVal - codebook.get(i)[j][k]); 
                    double distance2Min = Math.abs(codebook.get(i)[j][k] - minVal); 




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

    public static double[][][] codebook(double[][][] trainingData, int numCodeblocks, double threshold, Boolean YUV, int channel) {
        double[][][] codebook = new double[numCodeblocks][][];

        Vector<double[][]> trainingCodeBlocks = new Vector<double[][]>();

        double[][] avgBlock = ComputeAverageVector(trainingData);
        trainingCodeBlocks.add(avgBlock);

        while(trainingCodeBlocks.size() < numCodeblocks) {
            trainingCodeBlocks = SplitCodebook(trainingCodeBlocks, YUV, channel);
            trainingCodeBlocks = UpdateCentroids(ComputeQuantRegions(trainingData, trainingCodeBlocks), trainingCodeBlocks);
        }

        double prevDistortion = 0.0;
        double currDistortion = ComputeDistortion(trainingData, trainingCodeBlocks);
        while (!CheckConvergence(prevDistortion, currDistortion, threshold)) {
            prevDistortion = currDistortion;
            trainingCodeBlocks = UpdateCentroids(ComputeQuantRegions(trainingData, trainingCodeBlocks), trainingCodeBlocks);
            currDistortion = ComputeDistortion(trainingData, trainingCodeBlocks);
        }

        for (int i = 0; i < trainingCodeBlocks.size(); i++) {
            codebook[i] = trainingCodeBlocks.get(i);
        }
        return codebook;
    }
    
    public static int[] Encode(double[][][] data, double[][][] codebook) {

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
    
    public static double[][][] Decode(int[] encodedData, double[][][] codebook) {
        double[][][] decodedData = new double[encodedData.length][][];
        for (int i = 0; i < encodedData.length; i++) {
            decodedData[i] = codebook[encodedData[i]];
        }
        return decodedData;
    }


    public static Vector<double[][][]> codebookRGB(Vector<double[][][]> rgbImages, int numCodeVectors, int dimension, Boolean YUV) {
        Vector<double[][][]> rgbValues = Util.extractRGB(rgbImages, dimension, YUV);


        Vector<double[][][]> codebooks = new Vector<double[][][]>();
        for (int i = 0; i < rgbValues.size(); i++) {
            double[][][] codebook = new double[numCodeVectors][][];

            long start = System.currentTimeMillis();

            codebook = codebook(rgbValues.get(i), numCodeVectors, 0.01, YUV, i);

            long end = System.currentTimeMillis();
            long duration = end - start;
            System.out.println("Time taken to create codebook " + i + ": " + duration + " ms");
            
            codebooks.add(codebook);

            System.out.println("Codebook " + i + " created with size: " + codebook.length);
        }

        return codebooks;
    }


    public static Vector<double[][][]> codebookRGB(double[][][] rgbImage, int numCodeVectors, int dimension, Boolean YUV){
        Vector<double[][]> rgbChannels = Util.getImageRGB(rgbImage, YUV);
        Vector<double[][][]> codebooks = new Vector<double[][][]>();


        for (int i = 0; i < rgbChannels.size(); i++) {
            double[][][] channelVectors = Util.splitImageVectors(rgbChannels.get(i), dimension);
            int numCV = numCodeVectors;
            if (YUV){
                if (i == 1 || i == 2) {
                    numCV = numCodeVectors / 4;
                }
            }
            double[][][] codebook = codebook(channelVectors, numCV, 0.01, YUV, i);
            codebooks.add(codebook);
        }

        return codebooks;

    }


    public static void multiImageCodebook(Vector<String> imagePaths, int dimension, int numCodeVectors, Boolean YUV) {
        Vector<double[][][]> rgbImages = new Vector<double[][][]>();
        for (int i = 0; i < imagePaths.size(); i++) {
            double[][][] image = Util.readImage(imagePaths.get(i));
            rgbImages.add(image);
        }

        Vector<double[][][]> codebooks = codebookRGB(rgbImages, numCodeVectors, dimension, YUV);

        for (int i = 0; i < codebooks.size(); i++) {
            Util.storeCodebook("codebook" + i + ".txt", codebooks.get(i));
        }
    }


    public static Vector<int[]> multiChannelEncoding(double[][][] image, Vector<double[][][]> codebooks, int dimension, Boolean YUV){
        
        Vector<double[][]> imageRGB = Util.getImageRGB(image, YUV);


        Vector<double[][][]> channelBlocks = new Vector<double[][][]>();
        for (int i = 0; i < 3; i++){
            double[][][] channel = Util.splitImageVectors(imageRGB.get(i), dimension);
            channelBlocks.add(channel);
        }

        Vector<int[]> encodings = new Vector<int[]>();
        for (int i=0; i<channelBlocks.size(); i++){
            int[] encoding = Encode(channelBlocks.get(i), codebooks.get(i));
            encodings.add(encoding);
        }

        return encodings;
    }


    public static Vector<double[][][]> multiChannelDecoding(Vector<int[]> encodings, Vector<double[][][]> codebooks){
        Vector<double[][][]> decodedChannels = new Vector<double[][][]>();
        for (int i=0; i<encodings.size(); i++){
            double[][][] decodedChannel = Decode(encodings.get(i), codebooks.get(i));
            decodedChannels.add(decodedChannel);
        }

        return decodedChannels;
    }


    public static void compressRGB(String imagePath, String outputPath, int dimension, Vector<double[][][]> codebooks, Boolean YUV){
        double[][][] image = Util.readImage(imagePath);
        int height = image.length;
        int width = image[0].length;

        Vector<int[]> encodings = multiChannelEncoding(image, codebooks, dimension, YUV);

        Util.saveEncodings(dimension, height, width, encodings, outputPath);

    }

    public static void decompressRGB(String encodedPath, String decodedPath, Vector<double[][][]> codebooks, Boolean YUV){
        ArrayList<Object> encodedData = Util.readEncodings(encodedPath);

        if (encodedData.isEmpty()) {
            System.err.println("Failed to decompress: No data could be read from " + encodedPath);
            return;
        }

        int dimension = (int) encodedData.get(0);
        int width = (int) encodedData.get(1);
        int height = (int) encodedData.get(2);
        @SuppressWarnings("unchecked")
        Vector<int[]> encodings = (Vector<int[]>) encodedData.get(3);


        Vector<double[][][]> decodedChannels = multiChannelDecoding(encodings, codebooks);


        Vector<double[][]> reconstructedChannels = new Vector<double[][]>();
        for (int i=0; i<decodedChannels.size(); i++){
            double[][][] decodedChannel = decodedChannels.get(i);
            if (i==0){
                reconstructedChannels.add(Util.reconstructImageFromBlocks(decodedChannel, dimension, height, width, false));
            } else {
                if (YUV){
                    reconstructedChannels.add(Util.reconstructImageFromBlocks(decodedChannel, dimension, (int) Math.ceil(height/2), (int) Math.ceil(width/2), YUV));
                } else {
                    reconstructedChannels.add(Util.reconstructImageFromBlocks(decodedChannel, dimension, height, width, YUV));
                }
            }
        }

        for (int i=0; i<reconstructedChannels.size(); i++){
            System.out.println("Reconstructed channel " + i + " dimensions: " + reconstructedChannels.get(i).length + "x" + reconstructedChannels.get(i)[0].length);
        }


        double[][][] decodedImage = Util.reconstructImage(reconstructedChannels, YUV); 

        // System.out.println("Decoded image dimensions: " + decodedImage.length + "x" + decodedImage[0].length + "x" + decodedImage[0][0].length);
        // for (int i=0; i<decodedImage.length; i++){
        //     for (int j=0; j<decodedImage[i].length; j++){
        //         for (int k=0; k<decodedImage[i][j].length; k++){
        //             System.out.print(decodedImage[i][j][k] + " ");
        //         }
        //         System.out.println();
        //     }
        // }


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int c = 0; c < 3; c++) {
                    decodedImage[y][x][c] = Math.max(0, Math.min(1, decodedImage[y][x][c]));
                }
            }
        }


        Util.saveImage(decodedPath, decodedImage);

    }



}