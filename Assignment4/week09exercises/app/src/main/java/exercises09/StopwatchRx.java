package exercises09;

import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/* This example is inspired by the StopWatch app in Head First. Android Development
   http://shop.oreilly.com/product/0636920029045.do
   Modified to Java, October 2020 by Jørgen Staunstrup, ITU, jst@itu.dk */

public class StopwatchRx {
  public static void main(String[] args) {
    new StopwatchRx();
  }

  final static JFrame f = new JFrame("Stopwatch");
  static final JButton startButton = new JButton("Start");
  static final JButton stopButton = new JButton("Stop");
  static final JButton resetButton = new JButton("Reset");

  /*
   * We use an adapted UI so that we can add event listeners directly to the
   * buttons from this class.
   */
  final static stopwatchRxUI myUI = new stopwatchRxUI(0, f);

  public StopwatchRx() {
    f.setBounds(0, 0, 220, 220);
    f.setLayout(null);

    /*
     * Creating and adding all buttons.
     */
    startButton.setBounds(50, 50, 95, 25);
    stopButton.setBounds(50, 90, 95, 25);
    resetButton.setBounds(50, 130, 95, 25);
    f.add(startButton);
    f.add(stopButton);
    f.add(resetButton);
    f.setVisible(true);

    /*
     * Make the display subscribe to events emitted from the Oberservables.
     */
    timer.subscribe(display);
    start.subscribe(display);
    stop.subscribe(display);
    reset.subscribe(display);
  }

  /*
   * Observable simulating clock ticking every second.
   */
  final static Observable<Integer> timer = Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      new Thread() {
        @Override
        public void run() {
          try {
            while (true) {
              TimeUnit.MILLISECONDS.sleep(100);
              e.onNext(1);
            }
          } catch (java.lang.InterruptedException e) {
            System.out.println(e.toString());
          }
        }
      }.start();
    }
  });

  /*
   * Create observables and add event listeners to the respective buttons. Emit a
   * number 2-4 if the button is clicked.
   */
  final static Observable<Integer> start = Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      startButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ee) {
          e.onNext(2);
        }
      });
    }
  });

  final static Observable<Integer> stop = Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      stopButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ee) {
          e.onNext(3);
        }
      });
    }
  });

  final static Observable<Integer> reset = Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      resetButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ee) {
          e.onNext(4);
        }
      });
    }
  });

  // Observer updating the display
  final static Observer<Integer> display = new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {
    }

    boolean running = true;

    @Override
    public void onNext(Integer value) {
      /*
       * 1 and running: The UI should update
       * 2: start updating UI
       * 3: stop UI from udpating
       * 4: reset Timer
       */
      if (value == 1 && running) {
        System.out.println(running);
        myUI.updateTime();
      }
      if (value == 2) {
        running = true;
        myUI.setRunning(true);
      }
      if (value == 3) {
        running = false;
        myUI.setRunning(false);
      }
      if (value == 4) {
        running = false;
        myUI.setRunning(false);
        myUI.reset();
      }
    }

    @Override
    public void onError(Throwable e) {
      System.out.println("onError: ");
    }

    @Override
    public void onComplete() {
      System.out.println("onComplete: All Done!");
    }
  };
}
