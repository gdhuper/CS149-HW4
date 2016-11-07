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
    private static final int[] MEMORY_SIZES = { 5, 11, 17, 31 };

    private static final Random random = new Random();

    public static void main(String args[]) {
        final LinkedList<Process> jobQueue = new LinkedList<>();
        for (int i = 0; i < MAX_JOBS; i++) {
            jobQueue.add(generateProcess("P" + i, MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME));
        }
        Collections.sort(jobQueue, Process::compareTo);

        final Timer timer = new Timer();

        final Paging FIFOPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new FIFO());
        final Paging randomPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new RandomSwap());
        final Paging LFUPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LFU());
        final Paging MFUPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new MFU());
        final Paging LRUPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LRU());

        timer.schedule(new JobScheduler(timer, MFUPaging, jobQueue), 0, 100);
    }

    private static Process generateProcess(String name, float minArrivalTime, float maxArrivalTime) {
        float arrivalTime = nextRandomFloat(minArrivalTime, maxArrivalTime);
        arrivalTime = formatDecimal(arrivalTime, 2);

        int size = MEMORY_SIZES[random.nextInt(4)];
        int serviceDuration = random.nextInt(5) + 1;

        return new Process(name, size, arrivalTime, serviceDuration);
    }

    private static float nextRandomFloat(float min, float max) {
        return max + random.nextFloat() * (min - max);
    }

    private static float formatDecimal(float d, int decimalPlace) {
        return new BigDecimal(Float.toString(d)).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    private static class JobScheduler extends TimerTask {

        final long t0 = System.currentTimeMillis();
        final Timer timer;
        final Paging paging;
        final LinkedList<Process> jobQueue;

        JobScheduler(Timer timer, Paging paging, LinkedList<Process> jobQueue) {
            this.timer = timer;
            this.paging = paging;
            this.jobQueue = jobQueue;
        }

        @Override
        public void run() {
            final long elapsedTime = System.currentTimeMillis() - t0;

            if (elapsedTime >= MAX_ARRIVAL_TIME * 1000 || jobQueue.isEmpty()) {
                // Cancel after 1 minute (60 * 1000 msec)
                timer.cancel();
                timer.purge();

                System.out.println("Total Number of processes finished: " + paging.getFinishedProcessCount());
                System.out.println("Processes Missed: " + (150- paging.getFinishedProcessCount()));

                // Exit here to stop all other threads
                System.exit(0);
            } else if (!paging.isFull()) {
                // Every 100 msec, run new job if at least 4 pages can be assigned to each running job
                final Process p = jobQueue.getFirst();

                // Check if a new job is arriving
                if (elapsedTime / 1000.0 >= p.getArrivalTime()) {
                    System.out.printf("%s (SIZE: %d, DURATION: %.0f) ENTER: %.2fsec\n%s\n",
                            p.getName(), p.getPageCount(), p.getServiceDuration(), elapsedTime / 1000.0,
                            paging.toString());
                    paging.executeProcess(p, elapsedTime); //executes the process
                    jobQueue.removeFirst();
                }
            }
        }
    }
}