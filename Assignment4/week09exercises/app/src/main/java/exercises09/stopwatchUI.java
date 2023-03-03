package exercises09;

import java.awt.event.*;  
import javax.swing.*; 
import java.util.Locale;
import java.util.concurrent.TimeUnit;
// User interface for Stopwatch, October 7, 2021 by Jørgen Staunstrup, ITU, jst@itu.dk
// Updated October 30, 2022

// Question: Why does the class name start with a small letter?
class stopwatchUI {
  private static JFrame lf;
  final private JButton startButton= new JButton("Start");	
  final private JButton stopButton= new JButton("Stop");
  final private JButton resetButton= new JButton("Reset");		
  final private JTextField tf= new JTextField();

  // (9.1.1) Added the last part for 1/10 of a second
  final private String allzero = "0:00:00:0";
  private SecCounter lC= new SecCounter(0, false, tf); 
  
  public void updateTime(){
    // (9.1.1) Renamed variable to "tenthOfSeconds"
    // and kept variable "seconds" by dividing by 10
    int tenthOfSeconds = lC.incr();
    int seconds = tenthOfSeconds / 10;
    // Potential race condition !!!
    if ( tenthOfSeconds >= 0 ) {
      int hours = seconds/3600;
      int minutes = (seconds%3600)/60;
      int secs = seconds%60;
      // (9.1.1) tenths of second are divided by modulo 10 as they range from 0-9
      String time = String.format(Locale.getDefault(),	"%d:%02d:%02d:%d", hours, minutes, secs, tenthOfSeconds%10);
      tf.setText(time);
    }
  };

  public stopwatchUI(int x, JFrame jF){
    int lx= x+50; lf= jF;	
    tf.setBounds(lx, 10, 120, 20); 
    tf.setText(allzero);

    startButton.setBounds(lx, 50, 95, 25); 
    startButton.addActionListener(new ActionListener(){  
      public void actionPerformed(ActionEvent e){  
        lC.setRunning(true);  
      }  
    });  
      
    stopButton.setBounds(lx, 90, 95, 25); 
    stopButton.addActionListener(new ActionListener(){  
      public void actionPerformed(ActionEvent e){  
        lC.setRunning(false); 
      }  
    });

    resetButton.setBounds(lx, 130, 95, 25); 				
    resetButton.addActionListener(new ActionListener(){  
      public void actionPerformed(ActionEvent e){  
        lC.reset();
      } 
    }); 

    // set up user interface
    lf.add(tf);
    lf.add(startButton);  
    lf.add(stopButton);
    lf.add(resetButton);
  }		
}