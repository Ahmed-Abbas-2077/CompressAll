package DPCM.src;

import java.util.List;
import java.util.ArrayList;
import DPCM.src.QuantInterval;


public class DPCM {


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

        // iterate over the image and set the difference value to the difference between the original image and the predicted image
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    differenceImage[i][j][k] = image[i][j][k] - predictedImage[i][j][k];
                }
            }
        }
        return differenceImage;
    }

    // quantization function
    // creates a quantizer which will holde the quantization intervals in a list
    // and the quantization intervals will be used to quantize the difference image

    public static List<QuantInterval> quantization(int[][][] differenceImage, int numIntervals) {
        List<QuantInterval> quantizer = new ArrayList<>();
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        // find the min and max values of the difference image
        for (int i = 0; i < differenceImage.length; i++) {
            for (int j = 0; j < differenceImage[i].length; j++) {
                for (int k = 0; k < differenceImage[i][j].length; k++) {
                    if (differenceImage[i][j][k] < min) {
                        min = differenceImage[i][j][k];
                    }
                    if (differenceImage[i][j][k] > max) {
                        max = differenceImage[i][j][k];
                    }
                }
            }
        }

        // calculate the range of the difference image
        int range = max - min;

        // calculate the width of each quantization interval
        int intervalWidth = range / numIntervals;

        int intervalMin = -1;
        int intervalMax = -1;
        // create the quantization intervals
        for (int i = 0; i < numIntervals; i++) {
            intervalMin = min + i * intervalWidth;
            intervalMax = min + (i + 1) * intervalWidth;
            if (i == numIntervals - 1) {
                intervalMax = max; // set the last interval to the max value of the difference image
            }
            QuantInterval interval = new QuantInterval(i, intervalMin, intervalMax);
            quantizer.add(interval);
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
                        if (differenceImage[i][j][k] > interval.getMin() && differenceImage[i][j][k] <= interval.getMax()) {
                            quantizedDifferenceImage[i][j][k] = interval.getIndex();
                            break;
                        }
                    }
                }
            }
        }
        return quantizedDifferenceImage;
    }



    

    
}