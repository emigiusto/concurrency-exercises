package exercises10;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;

// JUnit testing imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestHistograms {

    private Histogram1 histogram;
    private CasHistogram casHistogram;

    // TODO: 10.1.2
    CyclicBarrier barrier; // Barrier to maximize contention and to avoid sequencial execution

    @BeforeEach
    public void initialize() {
        // init set
        histogram = new Histogram1(25);
        casHistogram = new CasHistogram(25);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 4, 8, 16 }) // Executed with 2^n threads where n ∈ { 0, . . . , 4 }
    public void testingIntegerSetSyncAdd(int numberOfThreads) {

        barrier = new CyclicBarrier(numberOfThreads + 1); // Testing with threads provided by @ValueSource
        int range = 5_000_000;

        for (int i = 0; i < range; i++) {
            histogram.increment(TestCASLockHistogram.countFactors(i));
        }
        int perThread = range / numberOfThreads;
        Thread[] threads = new Thread[numberOfThreads];

        for (int t = 0; t < numberOfThreads; t++) {
            int from = perThread * t;
            int to = (t + 1 == numberOfThreads) ? range : perThread * (t + 1);

            threads[t] = new Thread(() -> {
                for (int i = from; i < to; i++) {
                    try {
                        int numberOfFactors = TestCASLockHistogram.countFactors(i);
                        casHistogram.increment(numberOfFactors);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        // Start all threads
        for (int t = 0; t < numberOfThreads; t++) {
            threads[t].start();
        }

        try {
            for (int t = 0; t < numberOfThreads; t++)
                threads[t].join();
        } catch (InterruptedException exn) {
            exn.printStackTrace();
        }

        for (int i = 0; i < 25; i++) {
            assertEquals(histogram.getCount(i), casHistogram.getCount(i), "Failed at bin " + i);
        }
    }
}
