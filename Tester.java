import replacementalgorithms.FIFO;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Tester
 */
public class Tester {

    private static final int MAX_JOBS = 150;
    private static final int MEMORY_LIMIT = 100;
    private static final int PAGE_SIZE = 1;
    private static final int MIN_PAGES_REQUIRED = 4;
    private static final float MIN_ARRIVAL_TIME = 0;
    private static final float MAX_ARRIVAL_TIME = 60;
    static int counter = 0;

    private static LinkedList<Process> jobQueue;
    private static Paging paging;

    public static void main(String args[]) {
        jobQueue = new LinkedList<>();
        for (int i = 0; i < MAX_JOBS; i++) {
            jobQueue.add(Process.generateProcess((char) ('0' + i), MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME));
        }
        Collections.sort(jobQueue, Process::compareTo);

        paging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new FIFO());

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();

            @Override
            public void run() {
                final long elapsedTime = System.currentTimeMillis() - t0;

                if (elapsedTime > MAX_ARRIVAL_TIME * 1000 || jobQueue.isEmpty()) {
                    // Cancel after 1 minute (60 * 1000 msec)
                    timer.cancel();
                } else if (!paging.isFull()) {
                	
                    // Every 100 msec, run new job if at least 4 pages free
                    final Process p = jobQueue.getFirst();
                    // Check if a new job is arriving
                    if (elapsedTime / 1000.0 >= p.getArrivalTime()) {
                        scheduleJob(p);
                      System.out.println(counter); //checking if all the processes arrive or not
                        counter++;
                    }
                }
            }
        }, 0, 100);
    }

    private static void scheduleJob(Process p) {
        System.out.println(p.getName() + " - " + p.getArrivalTime());
        paging.addProcess(p);
        jobQueue.removeFirst();
    }
}