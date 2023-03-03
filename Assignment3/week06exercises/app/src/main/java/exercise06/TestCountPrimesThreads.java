package exercise06;

/**
 * Counting primes, using multiple threads for better performance.
 * (Much simplified from CountprimesMany.java)
 * sestoft@itu.dk * 2014-08-31, 2015-09-15
 * modified rikj@itu.dk 2017-09-20
 * modified jst@itu.dk 2021-09-24
 * raup@itu.dk * 05/10/2022
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import benchmarking.Benchmark;

public class TestCountPrimesThreads {
	public static void main(String[] args) {
		new TestCountPrimesThreads();
	}

	public TestCountPrimesThreads() {
		final int range = 100_000;
		Benchmark.Mark7("countSequential", i -> countSequential(range));
		for (int c = 1; c <= 32; c++) {
			final int threadCount = c;
			Benchmark.Mark7(String.format("countParallelN %2d", threadCount),
					i -> countParallelN(range, threadCount));
			Benchmark.Mark7(String.format("countParallelNLocal %2d", threadCount),
					i -> countParallelNLocal(range, threadCount));
			Benchmark.Mark7(String.format("countParallelNTasks %2d", threadCount),
					i -> countParallelNTasks(range, threadCount));
		}
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
			k++;
		return n >= 2 && k * k > n;
	}

	// Sequential solution
	private static long countSequential(int range) {
		long count = 0;
		final int from = 0, to = range;
		for (int i = from; i < to; i++)
			if (isPrime(i))
				count++;
		return count;
	}

	// General parallel solution, using multiple threads
	private static long countParallelN(int range, int threadCount) {
		// Assign numbers to check per thread
		final int perThread = range / threadCount;
		final AtomicLong primesFound = new AtomicLong(0);
		Thread[] threads = new Thread[threadCount];

		// Divide range of numbers to parts and assign to threads
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t;
			final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);

			threads[t] = new Thread(() -> {
				for (int i = from; i < to; i++)
					if (isPrime(i))
						primesFound.incrementAndGet();
			});
		}

		// Start all threads
		for (int t = 0; t < threadCount; t++)
			threads[t].start();

		// Wait for threads to finish
		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
			// System.out.println("Primes: "+lc.get());
		} catch (InterruptedException exn) {
		}

		// Return number of primes found
		return primesFound.get();
	}

	// General parallel solution, using multiple threads
	private static long countParallelNLocal(int range, int threadCount) {
		// Assign numbers to check per thread
		final int perThread = range / threadCount;
		// Assign each thread a long within an array to store its result
		final long[] results = new long[threadCount];
		Thread[] threads = new Thread[threadCount];

		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t;
			final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			final int threadNo = t;

			threads[t] = new Thread(() -> {
				long count = 0;
				for (int i = from; i < to; i++)
					if (isPrime(i))
						count++;
				results[threadNo] = count;
			});
		}

		for (int t = 0; t < threadCount; t++)
			threads[t].start();

		try {
			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		} catch (InterruptedException exn) {
		}

		long result = 0;

		for (int t = 0; t < threadCount; t++)
			result += results[t];

		return result;
	}

	// =====================================================
	// Our implementation starts here
	// =====================================================

	// Parallel solution using executor and tasks
	private static long countParallelNTasks(int range, int threadCount) {
		long result = 0L;

		// Assign numbers to check per thread
		final int perThread = range / threadCount;

		// Create thread pool
		final ExecutorService pool = Executors.newFixedThreadPool(threadCount);

		// Create task list...
		final List<PrimeCountingTask> tasks = new ArrayList<>();

		// ...and add individual tasks to that list ("split the work")
		for (int t = 0; t < threadCount; t++) {
			final int from = perThread * t;
			final int to = (t + 1 == threadCount) ? range : perThread * (t + 1);
			tasks.add(new PrimeCountingTask(from, to));
		}

		try {
			// Execute all tasks in the pool...
			List<Future<Long>> futures = pool.invokeAll(tasks);

			// ...and block until they're finished and collect them in a result
			for (Future<Long> future : futures) {
				result += future.get();
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		// We are sure to be done, so we shut down the pool
		pool.shutdown();

		return result;
	}

	/**
	 * Represent a single task that can be delegated to a thread pool for checking
	 * prime numbers.
	 */
	static class PrimeCountingTask implements Callable<Long> {
		private int from;
		private int to;
		private long primesFound;

		// Iniate the task with a given range
		// (from is inclusive, to is exclusive)
		public PrimeCountingTask(int from, int to) {
			this.from = from;
			this.to = to;
			this.primesFound = 0;
		}

		@Override
		public Long call() throws Exception {
			// Find the number of primes in the given range
			for (int i = from; i < to; i++) {
				if (isPrime(i))
					primesFound++;
			}
			return primesFound;
		}
	}
}
