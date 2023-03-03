package exercises10;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {

    private final AtomicInteger[] bins;

    /**
     * Creates a histogram with `numberOfBins` bins.
     * Note, that they are zero-based.
     * E.g. if a histogram with 3 bins is created, and one wants to access the last
     * bin, it is bin "2".
     */
    public CasHistogram(int numberOfBins) {
        this.bins = new AtomicInteger[25];
        for (int i = 0; i < numberOfBins; i++) {
            this.bins[i] = new AtomicInteger(0);
        }
    }

    public void increment(int bin){
        int previousValue;
        int newValue;
        do {
            previousValue = bins[bin].get();
            newValue = previousValue + 1;
        } while (!bins[bin].compareAndSet(previousValue, newValue));
    }

    public int getCount(int bin) {
        return bins[bin].get();
    }

    public int getSpan() {
        return bins.length;
    }

    public int getAndClear(int bin) {
        int previousValue;
        do {
            previousValue = bins[bin].get();
        } while (!bins[bin].compareAndSet(previousValue, 0));
        return previousValue;
    }
}