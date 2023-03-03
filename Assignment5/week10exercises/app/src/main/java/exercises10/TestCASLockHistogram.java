// For week 10
// raup@itu.dk * 10/10/2021
package exercises10;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntToDoubleFunction;

class TestCASLockHistogram {

	// Testing correctness and evaluating performance
	public static void main(String[] args) {

		Histogram histogramLock = new Histogram2(25);
		Histogram histogramCAS = new CasHistogram(25);

		for (int n = 0; n <= 4; n++) {
				int threadCount = BigInteger.TWO.pow(n).intValue();

				mark7(String.format("Histogram2   (countParallel; %s threads): ", threadCount), val -> {
					countParallel(5_000_000, threadCount, histogramLock);
					return 2;
				});

				mark7(String.format("HistogramCAS (countParallel; %s threads): ", threadCount), val -> {
					countParallel(5_000_000, threadCount, histogramCAS);
					return 2;
				});
			}
		

		// Create an object `histogramCAS` with your Histogram CAS implementation
		// Create an object `histogramLock` with your Histogram Lock from week 5

		// Evaluate the performance of CAS vs Locks histograms
		// Below you have the code for `countParallel`
		// You also have the benchmarking code for Mark7

	}

	// Function to count the prime factors of a number `p`
	public static int countFactors(int p) {
		if (p < 2)
			return 0;
		int factorCount = 1, k = 2;
		while (p >= k * k) {
			if (p % k == 0) {
				factorCount++;
				p = p / k;
			} else
				k = k + 1;
		}
		return factorCount;
	}

	// Parallel execution of counting the number of primes for numbers in `range`
	private static void countParallel(int range, int threadCount, Histogram h) {
		final int perThread = range / threadCount;
		Thread[] threads = new Thread[threadCount];
		for (int t = 0; t < threadCount; t = t + 1) {
			final int from = perThread * t,
					to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			threads[t] = new Thread(() -> {
				for (int i = from; i < to; i++)
					h.increment(countFactors(i));

			});
		}
		for (int t = 0; t < threadCount; t = t + 1)
			threads[t].start();
		try {
			for (int t = 0; t < threadCount; t = t + 1)
				threads[t].join();
		} catch (InterruptedException exn) {
		}
	}

	// Benchmark function
	public static double mark7(String msg, IntToDoubleFunction f) {
		int n = 10, count = 1, totalCount = 0;
		double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
		do {
			count *= 2;
			st = sst = 0.0;
			for (int j = 0; j < n; j++) {
				Timer t = new Timer();
				for (int i = 0; i < count; i++)
					dummy += f.applyAsDouble(i);
				runningTime = t.check();
				double time = runningTime * 1e9 / count;
				st += time;
				sst += time * time;
				totalCount += count;
			}
		} while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
		double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
		System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
		return dummy / totalCount;
	}
}
