package exercise06;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadsAccountExperimentsManyTerminating {

  private static final int NO_ACCOUNTS = 10;
  private static final Account[] accounts = new Account[NO_ACCOUNTS];

  // Increased from 5 to 50
  // Before we had a total of NO_THREADS * NO_TRANSACTIONS
  private static final int NO_TRANSACTIONS = 50;

  private static final int NO_THREADS = 10;

  private static Random rnd = new Random();

  public static void main(String[] args) {
    new ThreadsAccountExperimentsManyTerminating();
  }

  public ThreadsAccountExperimentsManyTerminating() {
    boolean successful = true;

    // Initializes the array of accounts
    initAccounts();

    // Initializes the thread pool with a fixed number of 10 threads
    ExecutorService pool = Executors.newFixedThreadPool(NO_THREADS);

    // List of tasks to perform
    // Create 50 TransactionTasks's add adds them to list transactionTasks
    List<TransactionTask> transactionTasks = initTransactionTasks();

    try {
      // Add all futures to the execution pool at once
      // Executes transactions tasks
      List<Future<Boolean>> futures = pool.invokeAll(transactionTasks);

      for (Future<Boolean> future : futures) {
        // Wait for each future to be executed
        // and add partial result
        successful &= future.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    // We are sure to be done, so we shut down the pool
    pool.shutdown();

    // Print whether all transaction have been successful
    System.out.println("All transactions successfull: " + successful);
  }

  private List<TransactionTask> initTransactionTasks() {
    List<TransactionTask> list = new ArrayList<>();
    for (int i = 0; i < NO_TRANSACTIONS; i++) {
      list.add(new TransactionTask());
    }
    return list;
  }

  private void initAccounts() {
    for (int i = 0; i < NO_ACCOUNTS; i++) {
      accounts[i] = new Account(i);
    }
  }

  private static Transaction createRandomTransaction() {
    long amount = rnd.nextInt(5000) + 100;
    int source = rnd.nextInt(NO_ACCOUNTS);
    int target = (source + rnd.nextInt(NO_ACCOUNTS - 2) + 1) % NO_ACCOUNTS; // make sure target <> source
    return new Transaction(amount, accounts[source], accounts[target]);
  }

  /**
   * Represents the task that performs a single random transaction
   * Implemented as a Callable
   * It returns a boolean indicating whether the transaction was successful
   */
  class TransactionTask implements Callable<Boolean> {

    @Override
    public Boolean call() throws Exception {
      return doTransaction(createRandomTransaction());
    }

    private boolean doTransaction(Transaction t) {
      System.out.println(t);
      return t.transfer();
    }

  }

  static class Transaction {
    final Account source, target;
    final long amount;

    Transaction(long amount, Account source, Account target) {
      this.amount = amount;
      this.source = source;
      this.target = target;
    }

    public boolean transfer() {
      Account min = accounts[Math.min(source.id, target.id)];
      Account max = accounts[Math.max(source.id, target.id)];

      boolean successful = false;

      synchronized (min) {
        synchronized (max) {
          source.withdraw(amount);
          try {
            // Simulate transaction time
            Thread.sleep(50);
          } catch (Exception ignore) {
            successful = false;
          }
          target.deposit(amount);
          successful = true;
        }
      }
      return successful;
    }

    public String toString() {
      return "Transfer " + amount + " from " + source.id + " to " + target.id;
    }
  }

  static class Account {
    // Should have transaction history, owners, account-type, and 100 other real
    // things
    public final int id;
    private long balance = 0;

    Account(int id) {
      this.id = id;
    }

    public void deposit(long sum) {
      balance += sum;
    }

    public void withdraw(long sum) {
      balance -= sum;
    }

    public long getBalance() {
      return balance;
    }
  }

}
