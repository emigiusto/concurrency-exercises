package exercise06;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadsAccountExperimentsMany {

  private static final int NO_ACCOUNTS = 10;
  // Increased from 5 to 50
  // Before we had a total of NO_THREADS * NO_TRANSACTIONS
  private static final int NO_TRANSACTIONS = 50;
  private static final int NO_THREADS = 10;
  private static final Account[] accounts = new Account[NO_ACCOUNTS];
  private static Random rnd = new Random();

  // Create variable for pool
  private static ExecutorService pool;

  public static void main(String[] args) {
    new ThreadsAccountExperimentsMany();
  }

  public ThreadsAccountExperimentsMany() {

    // Initializes the array of accounts
    initAccounts();

    // Initializes the thread pool with a fixed number of 10 threads
    pool = Executors.newFixedThreadPool(NO_THREADS);

    // Creates 50 TransactionTasks's and executes them in the ExecutorService
    for (int i = 0; i < NO_TRANSACTIONS ; i++) {
      try {
        pool.execute(new TransactionTask());
      } catch (Exception e) {
        System.out.println("At i = " + i + " I got exception: " + e);
        System.exit(0);
      }
    }
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

  // Represents the task that performs a single random transaction
  class TransactionTask implements Runnable {

    @Override
    public void run() {
      doTransaction(createRandomTransaction());
    }

    private void doTransaction(Transaction t) {
      System.out.println(t);
      t.transfer();
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

    public void transfer() {
      Account min = accounts[Math.min(source.id, target.id)];
      Account max = accounts[Math.max(source.id, target.id)];

      synchronized (min) {
        synchronized (max) {
          source.withdraw(amount);
          try {
            // Simulate transaction time
            Thread.sleep(50);
          } catch (Exception ignore) {
          }
          target.deposit(amount);
        }
      }
    }

    public String toString() {
      return "Transfer " + amount + " from " + source.id + " to " + target.id;
    }
  }

  static class Account {
    // Should have transaction history, owners, account-type, and 100 other real things
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
