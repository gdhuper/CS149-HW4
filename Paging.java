import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Paging {

    private final int minPagesRequired;
    private final int pagesCount;
    private final ReplacementAlgorithm alg;
    private final Process[] pageMap;
    private final ConcurrentLinkedQueue<Page> freePagesList;
    private final List<Process> runningProcesses;
    private final List<Page> occupiedPagesList;
    private final Random random = new Random();

	private int finishedProcessCount;
    private AtomicBoolean continueExecuting;

    public Paging(int memorySize, int pageSize, int minPagesRequired, ReplacementAlgorithm alg) {
        this.minPagesRequired = minPagesRequired;
        this.alg = alg;

        pagesCount = memorySize / pageSize;
        pageMap = new Process[pagesCount]; // array to keep track of running processes
        freePagesList = new ConcurrentLinkedQueue<>();
        runningProcesses = Collections.synchronizedList(new LinkedList<Process>());
        occupiedPagesList = Collections.synchronizedList(new LinkedList<Page>()); // List of all pages that reference some processes' page
        
        finishedProcessCount = 0; //keep track of finished processes
        continueExecuting = new AtomicBoolean(true);
        // Initialize free pages
        for (int i = 0; i < pagesCount; i++)
            freePagesList.add(new Page(i));
    }

    /**
     * Checks if there are at least specified minimum free pages for each running process.
     * i.e., if minimum is 4, and max page count is 100, then there can be only 100/4 = 25 running processes.
     *
     * @return whether there are at least specified minimum free pages
     */
    public boolean isFull() {
        return minPagesRequired * runningProcesses.size() >= pagesCount;
    }
    
    public int getFinishedProcessCount() {
    	return this.finishedProcessCount;
    }

	public void stopExecution() {
        continueExecuting.set(false);
	}

    /**
     * Execute process for its provided duration. Create a new thread for each process.
     *
     * @param process the process to be executed
     * @param startTime the start time of the process
     */
    public void executeProcess(Process process, float startTime) {
        initializeProcess(process);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();

            @Override
            public void run() {
                if (!continueExecuting.get())
                    timer.cancel();

                final long elapsedTime = System.currentTimeMillis() - t0;

                // Run each process for its service duration
                if (elapsedTime >= process.getServiceDuration() * 1000) {
                    // Process is finished
                    System.err.printf("%s (SIZE: %d, DURATION: %.0f) EXIT: %.2fsec\n%s\n",
                            process.getName(), process.getPageCount(), process.getServiceDuration(),
                            (startTime + elapsedTime) / 1000.0, Paging.this.toString());
                    runningProcesses.remove(process);
                    finishedProcessCount++;

                    timer.cancel();
                } else {
                    // Every 100 msec make a memory reference to another page in that process
                    referencePage(process);
                }
            }
        }, 0, 100);
    }

    /**
     * Initialize process by referencing page 0.
     *
     * @param process the process to initialize
     */
    private synchronized void initializeProcess(Process process) {
        // Get page from freePagesList or find a page to swap out
        Page page = freePagesList.isEmpty() ? findPageToSwap() : freePagesList.remove();

        if (page != null && continueExecuting.get()) {
            System.out.println("Referencing page for " + process.getName() + ": 0");
            process.setPageReferenced(0, page);
            page.setReferencedProcess(process);
            // Add process to page map
            updatePageMap(page, process);
            System.out.println(this.toString());

            occupiedPagesList.add(page);
            runningProcesses.add(process);
        }
    }

    /**
     * Reference page from freePagesList if not already referenced. Synchronized to prevent race conditions.
     *
     * @param process the process to reference a new page for
     */
    private synchronized void referencePage(Process process) {
        final int pageToRefer = localityRef(process.getPageCount(), process.getLastReferencedPage());

        // Attempt to access the page of the process. Returns false if page is not in memory.
        if (process.accessPage(pageToRefer)) return;

        // Get page from freePagesList or find a page to swap out
        Page page = freePagesList.isEmpty() ? findPageToSwap() : freePagesList.remove();

        if (page != null && continueExecuting.get()) {
            System.out.println("Referencing page for " + process.getName() + ": " + pageToRefer);
            process.setPageReferenced(pageToRefer, page);
            page.setReferencedProcess(process);
            // Add process to page map
            updatePageMap(page, process);
            System.out.println(this.toString());
            
            if(page.getIdxLRU() != -1) //if LRU is used as swapping algorithm
            {
            	occupiedPagesList.set(page.getIdxLRU(), page); //setting the value of the least recently used page to new page of current process
            }
           else { //if LRU is not used as swapping algorithm 
           	occupiedPagesList.add(page);
           }
        }
    }

    /**
     * Use given algorithm to find a page to swap. If found, dereference page from process and update map.
     *
     * @return the page to swap
     */
    private synchronized Page findPageToSwap() {
        final Page pageToSwap = alg.findPageToReplace(occupiedPagesList);
        if (pageToSwap != null) {
            // Found a page to swap out. Dereference the page in the process.
            final Process referencedProcess = pageToSwap.getReferencedProcess();
            referencedProcess.dereferencePage(pageToSwap.getReferencedPage());
            // Remove process from page map
            updatePageMap(pageToSwap, null);
            
//            System.out.println(this.toString());

            return pageToSwap;
        }

        return null;
    }

	/**
     * Helper method to make random reference to the pages of a process
     *
     * @return index of next
     */
    private int localityRef(int pagesCount, int lastReferencedPage) {
        int nextPage;

        final int r = random.nextInt(10);

        // 70% chance change is -1, 0, 1
        if (r >= 0 && r < 7) {
            final int delta = random.nextInt(3) - 1;
            nextPage = (delta + lastReferencedPage) % pagesCount;
            if (nextPage < 0)
                nextPage += pagesCount;
        } else {
            // Else change in i is 2 to process page count (exclusive)
            final int[] ex = { lastReferencedPage - 1, lastReferencedPage, lastReferencedPage + 1 };
            nextPage = getRandomWithExclusion(0, pagesCount, ex);
        }

        return nextPage;
    }

    /**
     * Updates the page map with a process or null.
     * @param page the page that has been updated
     * @param process the process or null that the now page refers to
     */
    private void updatePageMap(Page page, Process process) {
        synchronized (pageMap) {
            pageMap[page.getNumber()] = process;
        }
    }

    /**
     * Get random number in range with exclusions.
     * @param start minimum value inclusive
     * @param end maximum value exclusive
     * @param exclude the values to exclude
     * @return a random number in given range excluding given values
     */
    private int getRandomWithExclusion(int start, int end, int... exclude) {
        int r = start + random.nextInt(end - start - exclude.length);
        for (int ex : exclude) {
            if (r < ex) {
                break;
            }
            r++;
        }
        return r;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        synchronized (pageMap) {
            for (Process p : pageMap) {
                if (p != null) {
                    sb.append(p.getName());
                } else {
                    sb.append(".");
                }
            }
        }

        return sb.toString();
    }
}