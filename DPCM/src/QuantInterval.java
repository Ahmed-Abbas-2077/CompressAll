
// create a quantization interval class
// that will hold the index, min and max values of the quantization interval, and the median value of the quantization interval

package DPCM.src;

public class QuantInterval {
    private int index; // index of the quantization interval
    private int min; // minimum value of the quantization interval
    private int max; // maximum value of the quantization interval
    private int median; // median value of the quantization interval

    // constructor to initialize the quantization interval
    public QuantInterval(int index, int min, int max) {
        this.index = index;
        this.min = min;
        this.max = max;
        this.median = (min + max) / 2; // calculate the median value
    }

    // getters and setters for the quantization interval class
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMedian() {
        return median;
    }

    public void setMedian(int median) {
        this.median = median;
    }
}
