import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Paging {

    private final int minPagesRequired;
    private final ReplacementAlgorithm alg;
    private final ConcurrentLinkedQueue<Page> freePagesList;
    private final int pagesCount;
    private final Process[] pageMap;
    private final ConcurrentLinkedQueue<Process> runningProcesses;
    private final List<Page> occupiedPages; // List of all pages that reference some processes' page

    public Paging(int memorySize, int pageSize, int minPagesRequired, ReplacementAlgorithm alg) {
        this.minPagesRequired = minPagesRequired;
        this.alg = alg;

        pagesCount = memorySize / pageSize;
        freePagesList = new ConcurrentLinkedQueue<>();
        pageMap = new Process[pagesCount]; // array to keep track of processes
        runningProcesses = new ConcurrentLinkedQueue<>();
        occupiedPages = Collections.synchronizedList(new LinkedList<Page>());

        // Initialize free pages
        for (int i = 0; i < pagesCount; i++)
            freePagesList.add(new Page(i, pageSize));
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
                final long elapsedTime = System.currentTimeMillis() - t0;

                // Run each process for its service duration
                if (elapsedTime >= process.getServiceDuration() * 1000) {
                    // Process is finished
                    System.err.printf("%s (SIZE: %d, DURATION: %.0f) EXIT: %.2fsec\n%s\n",
                            process.getName(), process.getPageCount(), process.getServiceDuration(), (startTime + elapsedTime) / 1000.0,
                            Paging.this.toString());
                    runningProcesses.remove(process);
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
        Page page;
        if (!freePagesList.isEmpty()) {
            page = freePagesList.remove();
        } else {
            page = findPageToSwap();
        }

        if (page != null) {
            process.setPageReferenced(0, page);
            page.setReferencedProcess(process);
            pageMap[page.getNumber()] = process;
            occupiedPages.add(page);
            runningProcesses.add(process);
        }
    }

    /**
     * Reference page from freePagesList if not already referenced. Synchronized to prevent race conditions.
     *
     * @param process the process to reference a new page for
     */
    private synchronized void referencePage(Process process) {
        final int pageToRefer = localityRef(process.getPageCount()); // gets next random index to its page reference

        // Return if already referenced
        if (process.isPageReferenced(pageToRefer)) return;

        Page page;

        if (!freePagesList.isEmpty()) {
            // Free pages available
            page = freePagesList.remove();
        } else {
            page = findPageToSwap();
        }

        if (page != null) {
            System.out.println("Referencing page for " + process.getName() + ": " + pageToRefer);
            process.setPageReferenced(pageToRefer, page);
            page.setReferencedProcess(process);
            pageMap[page.getNumber()] = process;
            occupiedPages.add(page);
        }
    }

    /**
     * Use given algorithm to find a page to swap.
     *
     * @return the page to swap
     */
    private synchronized Page findPageToSwap() {
        final Page pageToSwap = alg.findPageToReplace(occupiedPages);
        if (pageToSwap != null) {
            // Found a page to swap out. Dereference the page in the process.
            final Process referencedProcess = pageToSwap.getReferencedProcess();
            referencedProcess.dereferencePage(pageToSwap.getReferencedPage());

            return pageToSwap;
        }

        return null;
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
}