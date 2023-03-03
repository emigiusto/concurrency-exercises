package exercises09;

import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.Locale;
class rxButton {
  public static void main(String[] args){ new rxButton(); }

	private final JButton start = new JButton("Start");
  private final JButton stop = new JButton("Stop");
  private final JButton reset = new JButton("Reset");
  final static private JTextField tf = new JTextField();

  final static JFrame f = new JFrame("Stopwatch");

  // Setting up the three streams for the Buttons and the display
  private SecCounter lC= new SecCounter(0, false, tf); 

  public rxButton() { 
    JFrame f = new JFrame("RxStopwatch");  	
		f.setBounds(0, 0, 220, 300);
    start.setBounds(50, 50, 95, 25); 
    stop.setBounds(50, 110, 95, 25); 
    reset.setBounds(50, 170, 95, 25);
    tf.setBounds(50, 0, 95, 25);

    // set up user interface
    f.add(start); 
    f.add(stop);
  	f.add(reset);
    f.add(tf);
    
    f.setLayout(null);  
		f.setVisible(true);
 
    rxStart.subscribe(display);
    rxStop.subscribe(display);
    rxReset.subscribe(display);
  }	

  public void updateTime(){
    // (9.1.1) Renamed variable to "tenthOfSeconds"
    // and kept variable "seconds" by dividing by 10
    int tenthOfSeconds = lC.incr();
    int seconds = tenthOfSeconds / 10;
    // Potentila race condition !!!
    if ( tenthOfSeconds >= 0 ) {
      int hours = seconds/3600;
      int minutes = (seconds%3600)/60;
      int secs = seconds%60;
      // (9.1.1) tenths of second are divided by modulo 10 as they range from 0-9
      String time = String.format(Locale.getDefault(),	"%d:%02d:%02d:%d", hours, minutes, secs, tenthOfSeconds%10);
      tf.setText(time);
    }
  };

  final Observable<Integer> rxStart= Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      start.addActionListener(new ActionListener(){  
        public void actionPerformed(ActionEvent ee){  
          lC.setRunning(true);
          e.onNext(1);  
        }  
      }); 
    }
  }); 
  final Observable<Integer> rxStop= Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      stop.addActionListener(new ActionListener(){  
        public void actionPerformed(ActionEvent ee){  
          lC.setRunning(false);
          e.onNext(1);  
        }  
      }); 
    }
  }); 
  final Observable<Integer> rxReset= Observable.create(new ObservableOnSubscribe<Integer>() {
    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
      reset.addActionListener(new ActionListener(){  
        public void actionPerformed(ActionEvent ee){ 
          lC.reset(); 
          e.onNext(1);  
        }  
      }); 
    }
  }); 

  final Observer<Integer> display= new Observer<Integer>() {
    @Override
    public void onSubscribe(Disposable d) {  }
    @Override
    public void onNext(Integer value) {
      try {
          lC.setRunning(true);
          TimeUnit.SECONDS.sleep(1);
        
      } catch (java.lang.InterruptedException e) { System.out.println(e.toString()); };
      System.out.println("Pushed");
    }
    
    @Override
    public void onError(Throwable e) {System.out.println("onError: "); }
    @Override
    public void onComplete() { System.out.println("onComplete: All Done!");   }
  };
}
