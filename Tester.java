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
    private static final int[] MEMORY_SIZES = {5, 11, 17, 31};
    private static final Random random = new Random();
    private static int avgProcessesFinished = 0;
    private static int avgProcessesMissed = 0;
    private static double avgPagesHitMiss = 0;
    private static int totalPages = 0;
    private static ArrayList<Paging> pagings = new ArrayList<>();
    private static int algRunCount = 0;

    public static void main(String args[]) {
        for (int i = 0; i < 5; i++) {
            //creating a new paging object and adding it to pagings arraylist
            pagings.add(new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new FIFO()));
        }
//        for (int i = 0; i < 5; i++) {
//            pagings.add(new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new RandomSwap()));
//        }
//        for (int i = 0; i < 5; i++) {
//            pagings.add(new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LFU()));
//        }
//        for (int i = 0; i < 5; i++) {
//            pagings.add(new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new MFU()));
//        }
//        for (int i = 0; i < 5; i++) {
//            pagings.add(new Paging(MEMORY_LIMIT, PAGE_SIZE, MIN_PAGES_REQUIRED, new LRU()));
//        }

        final Timer timer = new Timer();
        final LinkedList<Process> jobQueue = generateProcessesList();
        timer.schedule(new JobScheduler(timer, pagings.remove(0), jobQueue), 0, 100);
    }

    private static LinkedList<Process> generateProcessesList() {
        final LinkedList<Process> jobQueue = new LinkedList<>();
        totalPages = 0;
        for (int i = 0; i < MAX_JOBS + 1; i++) {
            char name = (char) ('!' + i);
            if (name == '.') {
                name = (char) ('!' + ++i);
            }
            Process p = generateProcess(name, MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME);
            totalPages += p.getPageCount();
            jobQueue.add(p);
        }
        Collections.sort(jobQueue, Process::compareTo);
        return jobQueue;
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

        JobScheduler(Timer timer, Paging paging, LinkedList<Process> jobQueue) {
            this.timer = timer;
            this.paging = paging;
            this.jobQueue = jobQueue;
        }

        @Override
        public void run() {
            final long elapsedTime = System.currentTimeMillis() - t0;

            if (elapsedTime >= MAX_ARRIVAL_TIME * 1000) {
                // Cancel after 1 minute (60 * 1000 msec)
                timer.cancel();
                final int processesFinishedThisRun = paging.getFinishedProcessCount();
                avgProcessesFinished += processesFinishedThisRun;
                final int processesMissedThisRun = (MAX_JOBS - paging.getFinishedProcessCount());
                avgProcessesMissed += processesMissedThisRun;
                final double pagesHitMissThisRun = paging.getPagesHit() / paging.getPagesMissed();
                avgPagesHitMiss += pagesHitMissThisRun;

                System.out.println("Total Number of processes finished: " + processesFinishedThisRun);
                System.out.println("Processes Missed: " + processesMissedThisRun);
                System.out.println("Total Pages in the job queue: " + totalPages);
                System.out.printf("Total Pages hit in this run: %.0f\n", paging.getPagesHit());
                System.out.printf("Total Pages missed in this run: %.0f\n", paging.getPagesMissed());
                System.out.println("Page Hit/Miss Ratio for this run: " + pagesHitMissThisRun + "\n");

                if (!pagings.isEmpty()) {
                    if (++algRunCount == 5) {
                        algRunCount = 0;
                        avgPagesHitMiss = 0;
                        avgProcessesFinished = 0;
                        avgProcessesMissed = 0;
                    }
                    final Timer newTimer = new Timer();
                    final LinkedList<Process> newJobQueue = generateProcessesList();
                    newTimer.schedule(new JobScheduler(newTimer, pagings.remove(0), newJobQueue), 0, 100);
                } else {
                    System.out.println("Average number of processes finished in 5 runs: " + avgProcessesFinished / 5.0);
                    System.out.println("Average number of processes missed in 5 runs: " + avgProcessesMissed / 5.0);
                    System.out.println("Average hits/miss ratio of pages in 5 runs: " + avgPagesHitMiss / 5.0);

                }
            } else if (!paging.isFull() && !jobQueue.isEmpty()) {
                // Every 100 msec, run new job if at least 4 pages can be assigned to each running job
                final Process p = jobQueue.getFirst();

                // Check if a new job is arriving
                if (elapsedTime / 1000.0 >= p.getArrivalTime()) {
                    System.out.printf("%s (SIZE: %d, DURATION: %.0f) ENTER: %.2fsec\n%s\n",
                            p.getName(), p.getPageCount(), p.getServiceDuration(), elapsedTime / 1000.0,
                            paging.toString());
                    paging.executeProcess(p, elapsedTime, MAX_ARRIVAL_TIME); //executes the process
                    jobQueue.removeFirst();
                }
            }
        }
    }
}