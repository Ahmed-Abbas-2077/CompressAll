package DPCM.src;

import java.util.List;
import java.util.ArrayList;
import DPCM.src.QuantInterval;


public class DPCM {

    public int[] topLeft; // top left of the original image
    public int[][] firstRow; // first row of the original image
    public int[][] firstColumn; // first column of the original image
    public List<QuantInterval> quantizer; // quantization intervals
    public int[][][] quantizedDifferenceImage; // quantized difference image
    public int[] histogram; // histogram of the difference image




    /*
    /////////////////////////////////
        Utility functions for DPCM  
    /////////////////////////////////
     */




    // - Order-1: Predicted(i,j) = Pixel(i,j-1)
    // iterates over the image and sets the predicted value to the pixel to the left of the current pixel
    // leaves the first column unchanged
    public static int[][][] order1Predictor(int[][][] image) {
        int[][][] predictedImage = new int[image.length][image[0].length][image[0][0].length];

        // Copy the first column of the image to the predicted image
        for (int i = 0; i < image.length; i++) {
            for (int k = 0; k < image[i][0].length; k++) {
                predictedImage[i][0][k] = image[i][0][k];
            }
        }

        // predict the rest of the image using the order-1 predictor
        // iterates over the predicted image and sets the predicted 
        // value to the pixel to the left of the current pixel
        for (int i = 0; i < image.length; i++) {
            for (int j = 1; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    predictedImage[i][j][k] = image[i][j - 1][k];
                }
            }
        }
        return predictedImage;
    }

    // - Order-2: Predicted(i,j) = Pixel(i,j-1) + Pixel(i-1,j) - Pixel(i-1,j-1)
    // iterates over the image and sets the predicted value to the pixel to the left of the current pixel + 
    // the pixel above the current pixel - the pixel above and to the left of the current pixel
    // leaves the first row and first column unchanged
    public static int[][][] order2Predictor(int[][][] image) {
        int[][][] predictedImage = new int[image.length][image[0].length][image[0][0].length];

        // Copy the first column
        for (int i = 0; i < image.length; i++) {
            for (int k = 0; k < image[i][0].length; k++) {
                predictedImage[i][0][k] = image[i][0][k];
            }
        }

        // Copy the first row
        for (int j = 0; j < image[0].length; j++) {
            for (int k = 0; k < image[0][j].length; k++) {
                predictedImage[0][j][k] = image[0][j][k];
            }
        }

        // predict the rest of the image using the order-2 predictor
        // iterates over the predicted image and sets the predicted
        // value to the pixel to the left of the current pixel +
        // the pixel above the current pixel - the pixel above and to the left of the current pixel
        for (int i = 1; i < image.length; i++) {
            for (int j = 1; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    predictedImage[i][j][k] = image[i][j - 1][k] + image[i - 1][j][k] - image[i - 1][j - 1][k];
                }
            }
        }
        return predictedImage;
    }


    // $$
    // P(A,B,C) =
    // \begin{cases}
    // \max(A,C), & B \le \min(A,C),\\[6pt]
    // \min(A,C), & B \ge \max(A,C),\\[6pt]
    // A + C - B, & \text{otherwise}.
    // \end{cases}
    // $$
    // iterates over the image and sets the predicted value according to the formula above
    // leaves the first row and first column unchanged
    public static int[][][] adaptivePredictor(int[][][] image) {
        int[][][] predictedImage = new int[image.length][image[0].length][image[0][0].length];

        // Copy the first column
        for (int i = 0; i < image.length; i++) {
            for (int k = 0; k < image[i][0].length; k++) {
                predictedImage[i][0][k] = image[i][0][k];
            }
        }

        // Copy the first row
        for (int j = 0; j < image[0].length; j++) {
            for (int k = 0; k < image[0][j].length; k++) {
                predictedImage[0][j][k] = image[0][j][k];
            }
        }

        // predict the rest of the image using the order-3 predictor
        // iterates over the predicted image and sets the predicted value according to the formula above
        for (int i = 1; i < image.length; i++) {
            for (int j = 1; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    int A = image[i - 1][j][k];
                    int B = image[i - 1][j - 1][k];
                    int C = image[i][j - 1][k];
                    if (B <= Math.min(A, C)) {
                        predictedImage[i][j][k] = Math.max(A, C);
                    } else if (B >= Math.max(A, C)) {
                        predictedImage[i][j][k] = Math.min(A, C);
                    } else {
                        predictedImage[i][j][k] = A + C - B;
                    }
                }
            }
        }
        return predictedImage;
    }


    // difference tensor
    // calculates the difference between the original image and the predicted image
    // iterates over the image and sets the difference value to the difference between the original image and the predicted image

    public static int[][][] differenceTensor(int[][][] image, int[][][] predictedImage) {
        int[][][] differenceImage = new int[image.length][image[0].length][image[0][0].length];

        // initialize the with the zeros across the first row and first column
        for (int i = 0; i < image.length; i++) {
            for (int k = 0; k < image[i][0].length; k++) {
                differenceImage[i][0][k] = 0;
            }
        }

        for (int j = 0; j < image[0].length; j++) {
            for (int k = 0; k < image[0][j].length; k++) {
                differenceImage[0][j][k] = 0;
            }
        }



        // iterate over the image and set the difference value to the difference between the original image and the predicted image
        for (int i = 1; i < image.length; i++) {
            for (int j = 1; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    differenceImage[i][j][k] =  image[i][j][k] - predictedImage[i][j][k];
                }
            }
        }
        return differenceImage;
    }


    // interval mean function
    // get the mean pixel value of the interval
    // not the mean number of pixels in the interval
    public static int intervalMean(int[] histogram, QuantInterval interval, int histogramOffset) {
        int sum = 0;
        int count = 0;
        for (int i = interval.getMin(); i <= interval.getMax(); i++) {
            int histIndex = i - histogramOffset;
            if (histIndex >= 0 && histIndex < histogram.length) {
                sum += histogram[histIndex] * i;
                count += histogram[histIndex];
            }
        }
        // Prevent division by zero
        if (count == 0) return (interval.getMin() + interval.getMax()) / 2;
        return sum / count;
    }


    public static int[] imageHistogram (int[][][] image, int min, int max) {
        int range = max - min + 1;
        int[] histogram = new int[range]; // assuming 8-bit grayscale image

        // iterate over the image and set the histogram value to the number of pixels with the same value
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    histogram[image[i][j][k] - min]++;
                }
            }
        }
        return histogram;
    }


    // default imageHistogram function
    public static int[] imageHistogram (int[][][] image) {
        return imageHistogram(image, 0, 255);
    }


    public static int[] getMinMax(int[][][] image) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // find the min and max values of the image
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    if (image[i][j][k] < min) {
                        min = image[i][j][k];
                    }
                    if (image[i][j][k] > max) {
                        max = image[i][j][k];
                    }
                }
            }
        }
        return new int[]{min, max};
    }


    // quantization function
    // creates a quantizer which will holde the quantization intervals in a list
    // and the quantization intervals will be used to quantize the difference image

    public static List<QuantInterval> quantization(int[][][] differenceImage, int numIntervals) {
        List<QuantInterval> quantizer = new ArrayList<>();
        int[] minMax = getMinMax(differenceImage);
        int min = minMax[0];
        int max = minMax[1];


        // Create histogram of difference values
        int range = max - min + 1;
        int[] histogram = imageHistogram(differenceImage, min, max);
        
        // Calculate total number of pixels
        int totalPixels = differenceImage.length * differenceImage[0].length * differenceImage[0][0].length;
        
        // Target count per interval (approximately equal number of samples per interval)
        int targetCountPerInterval = totalPixels / numIntervals;
        
        // Create intervals based on cumulative histogram
        int currentCount = 0;
        int intervalIndex = 0;
        int intervalMin = min;
        
        for (int i = 0; i < histogram.length; i++) {
            currentCount += histogram[i];
            
            // When we've reached target count or at the end
            if ((currentCount >= targetCountPerInterval || i == histogram.length - 1) && 
                intervalIndex < numIntervals - 1) {
                int intervalMax = min + i;
                
                // Create and add interval
                QuantInterval interval = new QuantInterval(intervalIndex, intervalMin, intervalMax);
                quantizer.add(interval);
                
                // Prepare for next interval
                intervalIndex++;
                intervalMin = intervalMax + 1;
                currentCount = 0;
            }
        }

        if (intervalIndex <= numIntervals - 1 && intervalMin <= max) {
            QuantInterval lastInterval = new QuantInterval(intervalIndex, intervalMin, max);
            quantizer.add(lastInterval);
        }

        // Then AFTER the for loop, ADD this code to ensure all intervals are created:
        // Add the last interval if needed
        // Ensure we have exactly numIntervals intervals
        while (quantizer.size() < numIntervals) {
            // Find largest interval to split
            int largestIdx = 0;
            int largestRange = 0;
            
            for (int j = 0; j < quantizer.size(); j++) {
                QuantInterval interval = quantizer.get(j);
                int rangeJ = interval.getMax() - interval.getMin();
                if (rangeJ > largestRange) {
                    largestRange = rangeJ;
                    largestIdx = j;
                }
            }
            
            // Split the largest interval
            QuantInterval toSplit = quantizer.get(largestIdx);
            int mid = toSplit.getMin() + (toSplit.getMax() - toSplit.getMin()) / 2;
            
            quantizer.set(largestIdx, new QuantInterval(largestIdx, toSplit.getMin(), mid));
            quantizer.add(new QuantInterval(quantizer.size(), mid + 1, toSplit.getMax()));
            
            // Fix indices
            for (int j = 0; j < quantizer.size(); j++) {
                quantizer.get(j).setIndex(j);
            }
        }

        return quantizer;
    }
        

    // quantized difference tensor
    // quantizes the difference image using the quantization intervals
    // iterates over the difference image and sets the quantized value to the index of the quantization interval that contains the difference value
    // where contain means strictly greater than the min value and less than or equal to the max value of the quantization interval
    public static int[][][] quantizedDifferenceTensor(int[][][] differenceImage, List<QuantInterval> quantizer) {
        int[][][] quantizedDifferenceImage = new int[differenceImage.length][differenceImage[0].length][differenceImage[0][0].length];

        // iterate over the difference image and set the quantized value to the index of the quantization interval that contains the difference value
        for (int i = 0; i < differenceImage.length; i++) {
            for (int j = 0; j < differenceImage[i].length; j++) {
                for (int k = 0; k < differenceImage[i][j].length; k++) {
                    for (QuantInterval interval : quantizer) {
                        if (differenceImage[i][j][k] >= interval.getMin() && differenceImage[i][j][k] <= interval.getMax()) {
                            quantizedDifferenceImage[i][j][k] = interval.getIndex();
                            break;
                        }
                    }
                }
            }
        }
        return quantizedDifferenceImage;
    }


    // dequantized difference tensor
    
    public static int[][][] dequantizedDifferenceTensor(int[][][] quantizedDifferenceImage, List<QuantInterval> quantizer, int min, int max, int[] histogram) {
        
        // initialize the dequantized difference image
        int[][][] dequantizedDifferenceImage = new int[quantizedDifferenceImage.length][quantizedDifferenceImage[0].length][quantizedDifferenceImage[0][0].length];


        // iterate over the quantized difference image and set the dequantized value to the median value of the quantization interval that contains the quantized value
        for (int i = 0; i < quantizedDifferenceImage.length; i++) {
            for (int j = 0; j < quantizedDifferenceImage[i].length; j++) {
                for (int k = 0; k < quantizedDifferenceImage[i][j].length; k++) {
                    int index = quantizedDifferenceImage[i][j][k];
                    dequantizedDifferenceImage[i][j][k] = intervalMean(histogram, quantizer.get(index), min); // get the mean pixel value of the interval
                }
            }
        }
        return dequantizedDifferenceImage;
    }

    public static int[][][] reconstructImage(int[] topLeft, int[][] firstRow, int[][] firstColumn, int[][][] dequantizedDifferenceImage, String predictor) {
        int[][][] reconstructedImage = new int[dequantizedDifferenceImage.length][dequantizedDifferenceImage[0].length][dequantizedDifferenceImage[0][0].length];

        // set the top left of the reconstructed image to the variable topLeft, which is the top left of the original image
        for (int i = 0; i < topLeft.length; i++) {
            reconstructedImage[0][0][i] = topLeft[i];
        }

        // set the first row of the reconstructed image to the first row of the original image
        for (int j = 0; j < firstRow.length; j++) {
            for (int k = 0; k < firstRow[j].length; k++) {
                reconstructedImage[0][j+1][k] = firstRow[j][k];
            }
        }

        // set the first column of the reconstructed image to the first column of the original image
        for (int i = 0; i < firstColumn.length; i++) {
            for (int k = 0; k < firstColumn[i].length; k++) {
                reconstructedImage[i+1][0][k] = firstColumn[i][k];
            }
        }

        // reconstruct the rest of the image using the dequantized difference image
        // iterates over the dequantized difference image and sets the reconstructed 
        // value to the sum of the dequantized difference value and the predicted value

        for (int i = 1; i < dequantizedDifferenceImage.length; i++) {
            for (int j = 1; j < dequantizedDifferenceImage[i].length; j++) {
                for (int k = 0; k < dequantizedDifferenceImage[i][j].length; k++) {

                    // get the predicted value based on the predictor type
                    int predictedValue = 0;
                    if (predictor.equals("order1")) {
                        predictedValue = reconstructedImage[i][j - 1][k];
                    } else if (predictor.equals("order2")) {
                        predictedValue = reconstructedImage[i][j - 1][k] + reconstructedImage[i - 1][j][k] - reconstructedImage[i - 1][j - 1][k];
                    } else if (predictor.equals("adaptive")) {
                        int A = reconstructedImage[i - 1][j][k];
                        int B = reconstructedImage[i - 1][j - 1][k];
                        int C = reconstructedImage[i][j - 1][k];
                        if (B <= Math.min(A, C)) {
                            predictedValue = Math.max(A, C);
                        } else if (B >= Math.max(A, C)) {
                            predictedValue = Math.min(A, C);
                        } else {
                            predictedValue = A + C - B;
                        }
                    }

                    // set the reconstructed value to the sum of the dequantized difference value and the predicted value
                    reconstructedImage[i][j][k] = Math.min(255, Math.max(0, dequantizedDifferenceImage[i][j][k] + predictedValue));
                }
            }
        }

        return reconstructedImage;
    }


    // mean squared error function
    public static Double  calculateMSE(int[][][] originalImage, int[][][] reconstructedImage) {
        double mse = 0.0;
        int numPixels = originalImage.length * originalImage[0].length * originalImage[0][0].length;

        // iterate over the original image and the reconstructed image and calculate the mean squared error
        for (int i = 0; i < originalImage.length; i++) {
            for (int j = 0; j < originalImage[i].length; j++) {
                for (int k = 0; k < originalImage[i][j].length; k++) {
                    mse += Math.pow(originalImage[i][j][k] - reconstructedImage[i][j][k], 2);
                }
            }
        }
        mse /= numPixels;
        return mse;
    }

    
}