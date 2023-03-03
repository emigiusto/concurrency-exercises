package exercise06;

import benchmarking.Benchmark;

public class HistogramPrimesThreadsH3Test {


	public static void main(String[] args) {
		int range = 5_000_000;
		int threadCount = 8;
		for (int i = 1; i < 21; i = i+2) {
			int stripe = i;
			Benchmark.Mark7(String.format("H3 Histogram with %s threads, %s range, and %s stripes", threadCount, range, i),
				a -> HistogramPrimesThreadsH3.generateHistogram(range, threadCount, stripe, false));
		}
	}
}
