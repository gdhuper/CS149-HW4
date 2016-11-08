import java.util.LinkedList;

/**
 * Helper class to generate process objects and manage them
 */
public class Process implements Comparable<Process> {

    private final char name;
    private final float arrivalTime;
    private final float serviceDuration;
    private final int pageCount;
    private final LinkedList<Page> pages;
    private int lastReferencedPage;

    public Process(char name, int pageCount, float arrivalTime, int serviceDuration) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.pageCount = pageCount;
        this.serviceDuration = serviceDuration;
        this.lastReferencedPage = -1;

        // Initialize pages list
        pages = new LinkedList<>();
    }

    public char getName() {
        return name;
    }

    /**
     * Attempt to access page. Increment use count if no page fault.
     * @param pageNumber the page to check
     * @return whether page is referenced
     */
    public boolean accessPage(int pageNumber) {
        for (Page p : pages) {
            if (p.getReferencedPage() == pageNumber) {
                p.incrementUseCount();
                p.incrementOldestRef(); //for LRU
                return true;
            }
        }
        return false;
    }

    /**
     * Set specified page of this process as referenced in main memory
     *
     * @param pageToRefer the page to reference
     * @param page the page in main memory
     */
    public void setPageReferenced(int pageToRefer, Page page) {
        page.setReferencedPage(pageToRefer);
        pages.add(page);
        setLastReferencedPage(pageToRefer);
    }

    /**
     * Dereference specified page from this process.
     * @param i the page to dereference
     */
    public void dereferencePage(int i) {
        for (Page p : pages) {
            if (p.getReferencedPage() == i) {
                p.setReferencedPage(-1);
                p.setOldestRef(-1); //for LRU
                break;
            }
        }
    }
    
    public void setLastReferencedPage(int pageNumber) {
    	this.lastReferencedPage = pageNumber;
    }
    public int getLastReferencedPage() {
    	return this.lastReferencedPage;
    }

    public float getArrivalTime() {
        return arrivalTime;
    }

    public float getServiceDuration() {
        return serviceDuration;
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    public String toString() {
        return "ProcessName: \n" + this.getName() + "\nSize (in pages):\n" + this.pageCount + "\nArrival Time:\n"
                + this.getArrivalTime() + "\nService duration:\n" + this.serviceDuration;
    }

    @Override
    public int compareTo(Process p) {
        return Float.compare(arrivalTime, p.arrivalTime);
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass())
            return false;

        Process p = (Process) o;
        return this.name == p.getName();
    }
}