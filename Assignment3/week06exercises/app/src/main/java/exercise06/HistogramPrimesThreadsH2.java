package exercise06;

public class HistogramPrimesThreadsH2 {
	public HistogramPrimesThreadsH2() {
	}

	public static double generateHistogram(int range, int threadCount, boolean printHistogram) {
		Histogram2 histogram = new Histogram2(25); // 25 bins sufficient for a range of 0..4_999_999
		int numberOfThreads = (threadCount > 0 && threadCount < 30) ? threadCount : 8;
		int perThread = range / numberOfThreads;
		Thread[] threads = new Thread[numberOfThreads];

		for (int t = 0; t < numberOfThreads; t++) {
			int from = perThread * t;
			int to = (t + 1 == numberOfThreads) ? range : perThread * (t + 1);
			threads[t] = new Thread(() -> {
				for (int i = from; i < to; i++)
					histogram.increment(countFactors(i));
			});
		}

		for (int t = 0; t < numberOfThreads; t++) // Start all threads
			threads[t].start();
		try {
			for (int t = 0; t < numberOfThreads; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}

		// Finally we plot the result if the parameter is true
		if (printHistogram) {
			dump(histogram);
		}
		//Returns a double for Benchmarking use
		return 0.0;
	}

	// Returns the number of prime factors of `p`
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

	public static void dump(Histogram histogram) {
		for (int bin = 0; bin < histogram.getSpan(); bin = bin + 1) {
			System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
		}
		System.out.printf("      %9d%n", histogram.getTotal());
	}

	public static void main(String[] args) {
		// Thread count should be between 1 and 30, otherwise the code will run with
		// default value of 8
		HistogramPrimesThreadsH2.generateHistogram(5_000_000, 8, true);
	}
}
