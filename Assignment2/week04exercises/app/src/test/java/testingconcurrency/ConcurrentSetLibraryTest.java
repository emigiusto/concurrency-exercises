package testingconcurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class ConcurrentSetLibraryTest {

  // Variable with set under test
  private ConcurrentIntegerSetLibrary setLib;

  CyclicBarrier barrier; //Barrier to maximize contention and to avoid sequencial execution

  @BeforeEach
  public void initialize() {
    // init set
    setLib = new ConcurrentIntegerSetLibrary();
  }

  @RepeatedTest(5000)
/*   @Disabled */
  @DisplayName("Add one element to Library concurrent Set")
  public void testingIntegerSetLibraryAdd() {
    barrier = new CyclicBarrier(16 + 1); //Testing with 16 threads
    for (int i = 0; i < 16; i++) { //Testing with 16 threads
      new Thread(
        () -> {
          try {
            barrier.await(); // wait until all threads are ready
            setLib.add(1);
            barrier.await(); // wait until all threads are finished
          } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
          }
        }
      ).start();
    }
    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
    assertEquals(1, setLib.size());
  }

  @RepeatedTest(5000)
/*   @Disabled */
  @DisplayName("Remove one element from Library concurrent Set")
  public void testingIntegerSetLibraryRemove() {
    barrier = new CyclicBarrier(16 + 1); //Testing with 16 threads and barrier to increase probability of interleavings

    for (int  i = 0; i < 16; i++) { //Testing with 16 threads
      new Thread(
        () -> {
          try {
            barrier.await(); // wait until all threads are ready
            setLib.add(2);
            setLib.remove(2);
            barrier.await(); // wait until all threads are finished
          } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
          }
        }
      ).start();
    }
    try {
      barrier.await();
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
    assertEquals( 0, setLib.size());
  }
}
