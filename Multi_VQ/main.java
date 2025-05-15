package Multi_VQ;

import Util.Util;
import Multi_VQ.src.LBGVecQuant;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class main {

    public static void main(String[] args) {
        
        String trainingDir = args[0];
        String testDir = args[1];
        String outputDir = args[2];

        
        Vector<String> trainingImages = Util.getImagePathsFromDir(trainingDir);
        Vector<String> testImages = Util.getImagePathsFromDir(testDir);

        
        int codebookSize = Integer.parseInt(args[3]);
        
        
        int dimensions = Integer.parseInt(args[4]);

        Boolean YUV = false;

        
        System.out.println("Training images:\n");
        for (int i = 0; i < trainingImages.size(); i++) {
            System.out.println("Training image " + i + ": " + trainingImages.get(i));
        }

        
        System.out.println("\nTest images:\n");
        for (int i = 0; i < testImages.size(); i++) {
            System.out.println("Test image " + i + ": " + testImages.get(i));
        }

        
        System.out.println("\nCodebook size: " + codebookSize);

        
        System.out.println("Dimensions: " + dimensions);

        
        System.out.println("Output directory: " + outputDir);


        // Vector<double[][][]> trainingData = new Vector<>();
        // for (String trainingImage : trainingImages) {
        //     double[][][] image = Util.readImage(trainingImage);
        //     trainingData.add(image);
        //     System.out.println("Read Training image: " + trainingImage);
        // }
        

        // Vector<double[][][]> codebooks = LBGVecQuant.codebookRGB(trainingData, codebookSize, dimensions, YUV);

        // for (int i = 0; i < codebooks.size(); i++) {
        //     String codebookPath = outputDir + "/codebooks/codebook_" + i + ".bin";
        //     Util.storeCodebook(codebookPath, codebooks.get(i));
        //     System.out.println("Stored codebook: " + codebookPath);
        // }



        Vector<double[][][]> codebooksRead = new Vector<>();
        for (int i = 0; i < 3; i++) {
            String codebookPath = outputDir + "\\codebooks\\codebook_" + i + ".bin";
            double[][][] codebook = Util.loadCodebook(codebookPath);
            codebooksRead.add(codebook);
            System.out.println("Read codebook: " + codebookPath);
        }


        
        String encodedDir = outputDir + "\\encoded";
        Map<String, Double> compressionRatios = new HashMap<>();
        Map<String, String> originalPaths = new HashMap<>();
        Vector<String> encodedPaths = new Vector<>();


        for (String testImage : testImages){
            System.out.println("\nCompressing test image: " + testImage);

            String testImageName = testImage.substring(testImage.lastIndexOf("\\") + 1, testImage.lastIndexOf("."));
            String encodedPath = encodedDir + "\\" + testImageName + "_encoded.bin";


            long startTime = System.currentTimeMillis();
            System.out.println("Start time: " + startTime);

            LBGVecQuant.compressRGB(testImage, encodedPath, dimensions, codebooksRead, YUV);
            
            long endTime = System.currentTimeMillis();
            System.out.println("End time: " + endTime);
            
            
            compressionRatios.put(testImageName, Util.getCompressionRatio(testImage, encodedPath));
            originalPaths.put(testImageName, testImage);


            System.out.println("Compression time: " + (endTime - startTime) + " ms");
            System.out.println("Compressed test image: " + testImage + " to " + encodedPath);
            System.out.println("Compression ratio: " + compressionRatios.get(testImageName));
            encodedPaths.add(encodedPath);
        }


        
        String decodedDir = outputDir + "\\decoded";
        Map<String, Double> mseValues = new HashMap<>();

        for (String encodedPath : encodedPaths) {
            String encodedImageName = encodedPath.substring(encodedPath.lastIndexOf("\\") + 1, encodedPath.lastIndexOf("_encoded.bin"));
            String decodedPath = decodedDir + "\\" + encodedImageName + "_decoded.png";
          
            long startTime = System.currentTimeMillis();
            LBGVecQuant.decompressRGB(encodedPath, decodedPath, codebooksRead, YUV);
          
            long endTime = System.currentTimeMillis();
            System.out.println("Decompression time: " + (endTime - startTime) + " ms");
          
            double mse = Util.calculateMSE(originalPaths.get(encodedImageName), decodedPath);
            mseValues.put(encodedImageName, mse);
            System.out.println("\nDecompressed encoded image: " + encodedPath + " to " + decodedPath);
            System.out.println("MSE: " + mse);
        }

        System.out.println("\nCompression Ratios:");
        for (Map.Entry<String, Double> entry : compressionRatios.entrySet()) {
            String imageName = entry.getKey();
            double compressionRatio = entry.getValue();
            System.out.println("Image: " + imageName + ", Compression Ratio: " + compressionRatio);
        }

        System.out.println("\nMSE Values:");
        for (Map.Entry<String, Double> entry : mseValues.entrySet()) {
            String imageName = entry.getKey();
            double mse = entry.getValue();
            System.out.println("Image: " + imageName + ", MSE: " + mse);
        }

        if (YUV) {
            System.out.println("YUV is used");
        } else {
            System.out.println("YUV is not used");
        }

    }

}
