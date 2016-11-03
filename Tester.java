import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Tester
 */
public class Tester {

    private static final int MAX_JOBS = 150;

    public static void main(String args[]) {
        LinkedList<Process> jobQueue = new LinkedList<>();
        for (int i = 0; i < MAX_JOBS; i++) {
            jobQueue.add(Process.generateProcess("PID" + i));
        }
        Collections.sort(jobQueue, Process::compareTo);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
        long t0 = System.currentTimeMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() - t0 > 60 * 1000) {
                    // Cancel after 1 minute (60 * 1000msec)
                    timer.cancel();
                } else {
                    // Do stuff here every 100msec
                    System.out.println("test");
                    //Execute a process 
                }

            }
        }, 0, 100);
    }
}