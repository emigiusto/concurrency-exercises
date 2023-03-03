package exercises05;

import java.util.function.IntToDoubleFunction;

public class TestVolatile {
    private static volatile int vCtr;
    private static int ctr;

    public TestVolatile() {
        vCtr = 0;
        ctr = 0;

        Mark7("volatile", i -> {vInc(); return 1;}); // returning 1 to make the lamdba a longtoint function.

        vCtr = 0;
        ctr = 0;

        Mark7("non-volatile", i -> {inc(); return 1;});

    }

    public static void vInc() {
        vCtr++;
    }

    public static void inc() {
        ctr++;
    }

    public static void main(String[] args) {
        new TestVolatile();
    }

    public static double Mark7(String msg, IntToDoubleFunction f) {
        int n = 10, count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j = 0; j < n; j++) {
                Timer t = new Timer();
                for (int i = 0; i < count; i++)
                    dummy += f.applyAsDouble(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
        } while (runningTime < 0.25 && count < Integer.MAX_VALUE / 2);
        double mean = st / n, sdev = Math.sqrt((sst - mean * mean * n) / (n - 1));
        System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
        return dummy / totalCount;
    }

}