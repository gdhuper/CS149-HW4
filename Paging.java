import replacementalgorithms.FIFO;
import replacementalgorithms.ReplacementAlgorithm;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Paging {

    private final int minPagesRequired;
    private final ReplacementAlgorithm alg;
    private final ConcurrentLinkedQueue<Page> freePagesList;
    private final int pagesCount;
    private final Process[] pageMap;
    private int runningProcessCount;
    private int finishedProcessCount;
    private  int initialIdx = 0;
   

    public Paging(int memorySize, int pageSize, int minPagesRequired, ReplacementAlgorithm alg) {
        this.minPagesRequired = minPagesRequired;
        this.alg = alg;

        pagesCount = memorySize / pageSize;
        freePagesList = new ConcurrentLinkedQueue<>();
        pageMap = new Process[pagesCount]; // array to keep track of processes
        runningProcessCount = 0;
        finishedProcessCount = 0; //keep track of finished processes
        initialIdx = 0;

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
        return minPagesRequired * runningProcessCount >= pagesCount;
    }
    
    
    public int getFinishedProcessCount()
    {
    	return this.finishedProcessCount;
    }

    /**
     * Execute process for its provided duration. Create a new thread for each process.
     *
     * @param p the process to be executed
     * @param startTime the start time of the process
     */
    public void executeProcess(Process p, float startTime) {
    	if(freePagesList.size() >= 4)
    	{
        initializeProcess(p);
    	}
    	else if(freePagesList.isEmpty())
    	{
    		freePagesAndInitializeProcess(p);
    	}

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            final long t0 = System.currentTimeMillis();

            @Override
            public void run() {
                final long elapsedTime = System.currentTimeMillis() - t0;

                // Run each process for its service duration
                if (elapsedTime >= p.getServiceDuration() * 1000) {
                    // Process is finished
                    System.err.printf("%s (SIZE: %d, DURATION: %.0f) EXIT: %.2fsec\n%s",
                            p.getName(), p.getPageCount(), p.getServiceDuration(), (startTime + elapsedTime) / 1000.0,
                            Paging.this.toString());
                    runningProcessCount--;
                    finishedProcessCount++;
                    timer.cancel();
                } else {
                    // Every 100 msec make a memory reference to another page in that process
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
     * Checking swapping by hardcoding FIFO
     * adds 4 new pages to freepagesList and initializes the process
     * @param p
     */
    private synchronized void freePagesAndInitializeProcess(Process p)
    {
    	System.out.println("Freeing pages from memory..");
    	for(int i = 0; i < 4; i++)
    	{
    		
    		 freePagesList.add(new Page(i, 1));
    	}
    	final Page page = freePagesList.remove();
        p.setPageReferenced(0, page);
        if(initialIdx < 100){
        pageMap[initialIdx] = p;
        }
        else{
        incrementCount(); //Increments the index at which next page in pageMap is to be removed
        pageMap[initialIdx] = p;
        }
        runningProcessCount++;
    
    }
    
    
    /*
     * Helper method to increment the index at which next page in pageMap is to be removed
     * Testing FIFO by Hardcoding
     */
    private  synchronized void incrementCount() {
    	if(initialIdx == 100)
        {
        	initialIdx = 0;
        }else
        {
        initialIdx++;
        }
    	
    }


    /** 
     * Reference page from freePagesList if not already referenced. Synchronized to prevent race conditions.
     *
     * @param p the process to reference a new page for
     */
    private synchronized void referencePage(Process p) {
        final int pageToRefer = localityRef(p.getPageCount()); // gets next random index to its page reference

        // Return if already referenced
        if (p.isPageReferenced(pageToRefer)) return;

        if (!freePagesList.isEmpty()) {
            // Free pages available
          
            final	 	 Page page = freePagesList.remove();
            p.setPageReferenced(pageToRefer, page);
            pageMap[page.getNumber()] = p;
            System.out.println("Referencing page for " + p.getName() + ": " + pageToRefer);
        } else {
            System.out.println("NO more free pages! waiting for free page...");
            //this is where swapping algorithm goes
           
         //  freePagesList.add(new Page(0, 1));
         //  final Page page =  freePagesList.remove();
         //  p.setPageReferenced(pageToRefer, page);
         //   pageMap[0] = p;
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
            nextIdx = random.nextInt(pageSize - 1) + 2;
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