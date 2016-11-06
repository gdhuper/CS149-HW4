import java.util.LinkedList;

/**
 * Helper class to generate process objects and manage them
 */
public class Process implements Comparable<Process> {

    private final String name;
    private final float arrivalTime;
    private final float serviceDuration;
    private final int pageCount;
    private final LinkedList<Page> pages;
    private Page lastReferenced;

    public Process(String name, int pageCount, float arrivalTime, int serviceDuration) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.pageCount = pageCount;
        this.serviceDuration = serviceDuration;
        this.lastReferenced = null;

        // Initialize pages list
        pages = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    /**
     * Checks if page has already been referenced.
     * @param pageNumber the page to check
     * @return whether page is referenced
     */
    public boolean isPageReferenced(int pageNumber) {
        for (Page p : pages) {
            if (p.getReferencedPage() == pageNumber)
                return true;
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
        setLastReferenced(page);
    }

    /**
     * Dereference specified page from this process.
     * @param i the page to dereference
     */
    public void dereferencePage(int i) {
        for (Page p : pages) {
            if (p.getReferencedPage() == i) {
                p.setReferencedPage(-1);
                break;
            }
        }
    }
    
    public void setLastReferenced(Page p)
    {
    	this.lastReferenced = p;
    }
    public Page getLastReferenced()
    {
    	return this.lastReferenced;
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
        return this.name.equals(p.getName());
    }
}
