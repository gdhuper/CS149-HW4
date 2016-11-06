import replacementalgorithms.FIFO;
import replacementalgorithms.ReplacementAlgorithm;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Paging {

    private int minPagesRequired;
    private ReplacementAlgorithm alg;
    private ConcurrentLinkedQueue<Page> freePagesList;
    private int runningProcessCount;
    private int pagesCount;
    private Process[] pageMap;

    public Paging(int memorySize, int pageSize, int minPagesRequired, ReplacementAlgorithm alg) {
        this.minPagesRequired = minPagesRequired;
        this.alg = alg;

//        final int pagesCount = memorySize / pageSize;
        pagesCount = memorySize / pageSize;
        freePagesList = new ConcurrentLinkedQueue<>();
        pageMap = new Process[pagesCount]; //array to keep track of processes
        runningProcessCount = 0;

        // Initialize free pages
        for (int i = 0; i < pagesCount; i++)
            freePagesList.add(new Page(i, pageSize));
    }

    /**
     * Checks if there are at least specified minimum free pages.
     *
     * @return whether there are at least specified minimum free pages
     */
    public boolean isFull() {
//        return freePagesList.size() < minPagesRequired;
        return minPagesRequired * runningProcessCount >= pagesCount;
    }

    /**
     * Method to execute a process
     *
     * @param p the process to be executed
     */
    public void executeProcess(Process p) {
        initializeProcess(p);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();

            @Override
            public void run() {
                final long elapsedTime = System.currentTimeMillis() - t0;

                // run each process for its service duration
                if (elapsedTime >= p.getServiceDuration() * 1000) {
                    // Process is finished
                    System.err.println(p.getName() + " terminating after " + elapsedTime / 1000 + " seconds");
                    runningProcessCount--;
                    timer.cancel();
                } else {
                    //Every 100 msec make a memory reference to another page in that process
                    referencePage(p);
                }
            }
        }, 0, 100);
    }

    /**
     * Initialize process by referencing page 0.
     *
     * @param p the process to initialize
     */
    private synchronized void initializeProcess(Process p) {
        if (!freePagesList.isEmpty()) {
            final Page page = freePagesList.remove();
            p.setPageReferenced(0, page);
            pageMap[page.getNumber()] = p;
            runningProcessCount++;
        }
    }

    /** 
     * Reference page from freePagesList if not already referenced. Synchronized to prevent race conditions.
     *
     * @param p the process to reference a new page for
     */
    private synchronized void referencePage(Process p) {
        final int i = localityRef(p.getPageCount()); // gets next random index to its page reference

        // Return if already referenced
        if (p.isPageReferenced(i)) return;

        if (!freePagesList.isEmpty()) {
            // Free pages available
            System.out.println("Referencing page for " + p.getName() + ": " + i);
            final Page page = freePagesList.remove();
            p.setPageReferenced(i, page);
            pageMap[page.getNumber()] = p;
        } else {
            System.out.println("NO more free pages! waiting for free page...");
            //this is where swapping algorithm goes
           
//            freePagesList.add(new Page(0, 1));
//            final Page page =  freePagesList.remove();
//            p.setPageReferenced(i, page);
//            pageMap[0] = p;
        }
    }

    /**
     * Helper method to make random reference to the pages of a process
     *
     * @param pageSize the size of process
     * @return index of next
     */
    private int localityRef(int pageSize) {
        int nextIdx = 0;

        Random random = new Random();
        int r = random.nextInt(pageSize);
        int[] deltaIs = {0, 1};
        if (r >= 0 && r < (pageSize - minPagesRequired)) {
            int deltaIdx = random.nextInt(2);
            nextIdx = deltaIs[deltaIdx];
        } else if (r >= (pageSize - minPagesRequired) && r < pageSize - 1) {
            nextIdx = random.nextInt(pageSize - 2) + 2;
        }

        return nextIdx;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Process p : pageMap) {
            if (p != null) {
                sb.append(p.getName());
            } else {
                sb.append(".");
            }
            sb.append(" ");
        }
        return sb.toString();
    }
    
    public static void main(String[] args)
    {
    	Paging p = new Paging(100, 1,4, new FIFO());
    	//System.out.println(p.freePagesList.size());
       	//p.printPageMap();
    	
    }
}