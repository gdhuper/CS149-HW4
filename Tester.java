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
    private static int avgProcessesFinished = 0;
    private static int avgProcessesMissed = 0;
    private static double avgPagesHitMiss = 0;
    private static ArrayList<Paging> pagings;

    private static final Random random = new Random();

    public static void main(String args[]) {
        final LinkedList<Process> jobQueue = new LinkedList<>();
        for (int i = 0; i < MAX_JOBS + 1; i++) {
            char name = (char) ('!' + i);
            if (name == '.') {
                name = (char) ('!' + ++i);
            }
            jobQueue.add(generateProcess(name, MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME));
        }

        int totalPages = 0; //to store total pages in job queue combined

        for(Process p: jobQueue)
        {
        	totalPages += p.getPageCount();
        }
        Collections.sort(jobQueue, Process::compareTo);

        final Paging FIFOPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new FIFO());
        final Paging randomPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new RandomSwap());
        final Paging LFUPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LFU());
        final Paging MFUPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new MFU());
        final Paging LRUPaging = new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LRU());

        pagings = new ArrayList<>();
        int i = 0;
        while(i < 4)
        {
       pagings.add(new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LRU())); //creating a new paging object and adding it to pagings arraylist
        i++;
       }


        final Timer timer = new Timer();
        timer.schedule(new JobScheduler(timer, LRUPaging, jobQueue, totalPages), 0, 100);
    }

    private static Process generateProcess(char name, float minArrivalTime, float maxArrivalTime) {
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
        final int totalPages;
        int scheduledJobsCount = 0;

        JobScheduler(Timer timer, Paging paging, LinkedList<Process> jobQueue, int totalPages) {
            this.timer = timer;
            this.paging = paging;
            this.jobQueue = jobQueue;
            this.totalPages = totalPages;
        }

        @Override
        public void run() {
            final long elapsedTime = System.currentTimeMillis() - t0;

            if (elapsedTime >= MAX_ARRIVAL_TIME * 1000) {
                // Cancel after 1 minute (60 * 1000 msec)
                timer.cancel();
                avgProcessesFinished += paging.getFinishedProcessCount();
                avgProcessesMissed += (150 - paging.getFinishedProcessCount());
                double pageHitMiss = (double)(paging.getPagesHit()/(totalPages-paging.getPagesHit()));
                avgPagesHitMiss += pageHitMiss;
                System.out.println("Total Number of processes finished: " + paging.getFinishedProcessCount());
                System.out.println("Processes Missed: " + (150- paging.getFinishedProcessCount()));
                System.out.println("Total Pages in the job queue combined :" + totalPages);
                System.out.println("Total Pages hit in this run: " + paging.getPagesHit());
                System.out.println("Total Pages missed in this run: " + (totalPages - paging.getPagesHit()));
                System.out.println("Page Hit/Miss Ratio for this run: " + (double)(paging.getPagesHit()/(totalPages-paging.getPagesHit())));

                if (!pagings.isEmpty()) {
                    Timer newTimer = new Timer();
                    newTimer.schedule(new JobScheduler(newTimer, pagings.remove(0), jobQueue, this.totalPages), 0, 100);
                } else {
                    System.out.println("Average number of processes finished in 5 runs: " + avgProcessesFinished / 5);
                    System.out.println("Average number of processes missed in 5 runs: " + avgProcessesMissed / 5);
                    System.out.println("Average hits/miss ratio of pages in 5 runs: " + avgPagesHitMiss / 5.0);

                }
            } else if (!paging.isFull() && scheduledJobsCount != jobQueue.size()) {
                // Every 100 msec, run new job if at least 4 pages can be assigned to each running job
                final Process p = jobQueue.get(scheduledJobsCount);

                // Check if a new job is arriving
                if (elapsedTime / 1000.0 >= p.getArrivalTime()) {
                    System.out.printf("%s (SIZE: %d, DURATION: %.0f) ENTER: %.2fsec\n%s\n",
                            p.getName(), p.getPageCount(), p.getServiceDuration(), elapsedTime / 1000.0,
                            paging.toString());
                    paging.executeProcess(p, elapsedTime, MAX_ARRIVAL_TIME); //executes the process
                    scheduledJobsCount++;
                }
            }
        }
    }
}