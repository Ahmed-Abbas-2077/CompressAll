package DPCM.src;


public class DPCM {


    // - Order-1: Predicted(i,j) = Pixel(i,j-1)
    // iterates over the image and sets the predicted value to the pixel to the left of the current pixel
    // leaves the first column unchanged
    public static int[][][] order1Predictor(int[][][] image) {
        int[][][] predictedImage = new int[image.length][image[0].length][image[0][0].length];
        for (int i = 0; i < image.length; i++) {
            for (int j = 1; j < image[i].length; j++) {
                for (int k = 0; k < image[i][j].length; k++) {
                    predictedImage[i][j][k] = image[i][j - 1][k];
                }
            }
        }
        return predictedImage;
    }

    

}