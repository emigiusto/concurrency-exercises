package exercises09;

import java.awt.event.*;
import javax.swing.*;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
/* This example is inspired by the StopWatch app in Head First. Android Development
   http://shop.oreilly.com/product/0636920029045.do
   Modified to Java, October 7, 2021 by Jørgen Staunstrup, ITU, jst@itu.dk
   Updated October 30, 2022*/

public class Stopwatch2 {

    // We chaged the fields to be not static so that each stop watch has its own
    // JFrame and stopwatchUI
    final JFrame f = new JFrame("Stopwatch");
    // Setting up the three streams for the Buttons and the display
    final stopwatchUI myUI = new stopwatchUI(0, f);

    /**
     * gradle -PmainClass=exercises09.Stopwatch2 run --args="3"
     * 
     * @param args
     */
    public static void main(String[] args) {
        // create multiple independent stopwatches
        for (int i = 0; i < 2; i++) {
            new StopwatchN(i);
        }
    }

    public Stopwatch2(int index) {
        int side = 220;
        int xPosition = (index % 8) * side;
        int yPosition = (index / 8) * side;
        f.setBounds(xPosition, yPosition, side, side);
        f.setLayout(null);
        f.setVisible(true);

        // Background Thread simulating a clock ticking every 0.1 second
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        // TimeUnit.SECONDS.sleep(1);
                        myUI.updateTime();
                    }
                } catch (java.lang.InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }.start();

    }
}
