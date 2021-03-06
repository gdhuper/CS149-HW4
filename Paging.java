import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Paging {

    private final int minPagesRequired;
    private final int pagesCount;
    private final ReplacementAlgorithm alg;
    private final Process[] pageMap;
    private final ConcurrentLinkedQueue<Page> freePagesList;
    private final List<Process> runningProcesses;
    private final List<Page> occupiedPagesList;
    private final Random random = new Random();
    private double pagesHit;
    private double pagesMissed;
	private int finishedProcessCount;

    public Paging(int memorySize, int pageSize, int minPagesRequired, ReplacementAlgorithm alg) {
        this.minPagesRequired = minPagesRequired;
        this.alg = alg;
        this.pagesHit = 0;
        pagesCount = memorySize / pageSize;
        pageMap = new Process[pagesCount]; // array to keep track of running processes
        freePagesList = new ConcurrentLinkedQueue<>();
        runningProcesses = Collections.synchronizedList(new LinkedList<Process>());
        occupiedPagesList = Collections.synchronizedList(new LinkedList<Page>()); // List of all pages that reference some processes' page
        
        finishedProcessCount = 0; //keep track of finished processes
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

    /**
     * Execute process for its provided duration. Create a new thread for each process.
     *
     * @param process the process to be executed
     * @param startTime the start time of the process
     */
    public void executeProcess(Process process, float startTime, float maxRunTime) {
        initializeProcess(process, startTime);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();

            @Override
            public void run() {
                final long elapsedTime = System.currentTimeMillis() - t0;

                // Run each process for its service duration
                if (elapsedTime >= process.getServiceDuration() * 1000) {
                    // Process is finished
                    if ((startTime + elapsedTime) <= maxRunTime * 1000) {
                        System.err.printf("%s (SIZE: %d, DURATION: %.0f) EXIT: %.2fsec\n%s\n",
                                process.getName(), process.getPageCount(), process.getServiceDuration(),
                                (startTime + elapsedTime) / 1000.0, Paging.this.toString());
                        runningProcesses.remove(process);
                        finishedProcessCount++;
                    }

                    timer.cancel();
                } else if ((startTime + elapsedTime) <= maxRunTime * 1000) {
                    // Every 100 msec make a memory reference to another page in that process
                    referencePage(process, startTime + elapsedTime);
                }
            }
        }, 0, 100);
    }

    /**
     * Initialize process by referencing page 0.
     *
     * @param process the process to initialize
     */
    private synchronized void initializeProcess(Process process, float startTime) {
        pagesMissed++;
        // Get page from freePagesList or find a page to swap out
        boolean isPagesListEmpty;
        Page page;
        if (isPagesListEmpty = freePagesList.isEmpty()) {
            page = findPageToSwap();
        } else {
            page  = freePagesList.remove();
        }

        if (page != null) {
            if (isPagesListEmpty) {
                Process evicted = page.getReferencedProcess();
                System.out.printf("Referencing page 0 for %s: %.2fsec, MISS, EVICT %s / %s\n",
                        process.getName(), startTime / 1000.0, evicted.getName(),
                        page.getNumber());
            } else {
                System.out.printf("Referencing page 0 for %s: %.2fsec, MISS, EVICT NONE\n",
                        process.getName(), startTime / 1000.0);
            }

            process.setPageReferenced(0, page);
            page.setReferencedProcess(process);
            // Add process to page map
            updatePageMap(page, process);

            occupiedPagesList.add(page);
            runningProcesses.add(process);
        }
    }

    public ReplacementAlgorithm getAlg() {
		return alg;
	}

	/**
     * Reference page from freePagesList if not already referenced. Synchronized to prevent race conditions.
     *
     * @param process the process to reference a new page for
     */
    private synchronized void referencePage(Process process, float referenceTime) {
        final int pageToRefer = localityRef(process.getPageCount(), process.getLastReferencedPage());

        // Attempt to access the page of the process. Returns false if page is not in memory.
        if (process.accessPage(pageToRefer)) {
            pagesHit++;
            System.out.printf("Referencing page %d for %s: %.2fsec, HIT\n",
                    pageToRefer, process.getName(), referenceTime / 1000.0);
            return;
        }
        pagesMissed++;

        // Get page from freePagesList or find a page to swap out
        boolean isPagesListEmpty;
        Page page;
        if (isPagesListEmpty = freePagesList.isEmpty()) {
            page = findPageToSwap();
        } else {
            page  = freePagesList.remove();
        }

        if (page != null) {
            if (isPagesListEmpty) {
                Process evicted = page.getReferencedProcess();
                System.out.printf("Referencing page %d for %s: %.2fsec, MISS, EVICT %s / %s\n",
                        pageToRefer, process.getName(), referenceTime / 1000.0, evicted.getName(),
                        page.getNumber());
            } else {
                System.out.printf("Referencing page %d for %s: %.2fsec, MISS, EVICT NONE\n",
                        page.getNumber(), process.getName(), referenceTime / 1000.0);
            }
            process.setPageReferenced(pageToRefer, page);
            page.setReferencedProcess(process);
            // Add process to page map
            updatePageMap(page, process);

            occupiedPagesList.add(page);
        }
    }

    public double getPagesHit() {
		return pagesHit;
	}

	public double getPagesMissed() {
        return pagesMissed;
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