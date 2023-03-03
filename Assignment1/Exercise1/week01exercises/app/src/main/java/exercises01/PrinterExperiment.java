package exercises01;

public class PrinterExperiment {
  Printer p = new Printer();

  public PrinterExperiment() {

    Thread t1 = new Thread(() -> callPrintForever());
    Thread t2 = new Thread(() -> callPrintForever());

    t1.start();
    t2.start();

  }

  private void callPrintForever() {
    while (true) {
      p.print();
    }
  }

  public class Printer {
    Printer() {
    }

    public void print() {
      System.out.print("-"); // (1)
      try {
        Thread.sleep(50);
      } catch (InterruptedException exn) {
      }
      System.out.print("|"); // (2)
    }
  }

  public static void main(String[] args) {
    new PrinterExperiment();
  }

}