import replacementalgorithms.FIFO;

import java.math.BigDecimal;
import java.util.*;

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

    private static LinkedList<Process> jobQueue;
    private static Paging paging;

    private static Random random = new Random();

    public static void main(String args[]) {
        jobQueue = new LinkedList<>();
        for (int i = 0; i < MAX_JOBS; i++) {
            jobQueue.add(generateProcess("P" + i, MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME));
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
                    }
                }
            }
        }, 0, 100);
    }

    private static void scheduleJob(Process p) {
        System.out.println(p.getName() + " - " + p.getArrivalTime());
        paging.executeProcess(p); //executes the process
        jobQueue.removeFirst();
    }

    private static Process generateProcess(String name, float minArrivalTime, float maxArrivalTime) {
        float arrivalTime = nextRandomFloat(minArrivalTime, maxArrivalTime);
        arrivalTime = formatDecimal(arrivalTime, 2);

        int[] serviceTimes = {5, 11, 17, 31};
        int idx = random.nextInt(4);
        int size = serviceTimes[idx];
        int serviceDuration = random.nextInt(5) + 1;

        return new Process(name, size, arrivalTime, serviceDuration);
    }

    private static float nextRandomFloat(float min, float max) {
        return max + random.nextFloat() * (min - max);
    }

    private static float formatDecimal(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);

        return bd.floatValue();
    }
}