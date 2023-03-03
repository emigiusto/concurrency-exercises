package exercises09;
import javax.swing.*; 

public class SecCounter {
  final private String allzero = "0:00:00:0";
  private int seconds= -1;
  private boolean running= false;
  private JTextField tf;

  public SecCounter(int s, boolean r, JTextField tf){
    //Synchronized to ensure initialization happens before first tick
    synchronized(this) {
      seconds= s;
      running= r;
      this.tf= tf;
      this.tf.setText(allzero);
    }
  }
	
  public synchronized void reset() {
    running= false;
    seconds= 0;
    tf.setText(allzero);
  }

  public synchronized void setRunning(boolean running) {
    this.running= running;
  }

  public synchronized int incr(){
    // when the display is not running -1 is returned
    if (running) { 
      seconds++; return seconds;
    } else return -1;
  }

  public synchronized boolean running(){
    return this.running;
  }
}
