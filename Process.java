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

    public boolean isPageReferenced(int i) {
        for (Page p : pages) {
            if (p.getReferencedPage() == i)
                return true;
        }
        return false;
    }
    
    

    /**
     * Set specified page of this process as referenced in main memory
     * @param i the page to reference
     * @param p the page in main memory
     */
    public void setPageReferenced(int i, Page p) {
        p.setReferencedPage(i);
        pages.add(p);
        setLastReferenced(p);
    }

    /**
     * Dereference specified page from this process.
     * @param i the page to dereference
     * @return the unreferenced page
     */
    public Page dereferencePage(int i) {
        for (Page p : pages) {
            if (p.getReferencedPage() == i) {
                p.setReferencedPage(-1);
                return p;
            }
        }
        return null;
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

    public void printList(LinkedList<Process> list) {
        System.out.printf("%5s%20s%20s%25s%n", "Process Name", "Size (in pages)", "Arrival Time ", "Service Duration(secs)");

        for (Process p : list) {
            System.out.printf("%5s%20s%20s%20s%n", p.getName(), p.getPageCount(), p.getArrivalTime(), p.getServiceDuration());

        }
    }

    @Override
    public String toString() {
        return "ProcessName: \n" + this.getName() + "\nSize (in pages):\n" + this.pageCount + "\nArrival Time:\n" + this.getArrivalTime() + "\nService duration:\n" + this.serviceDuration;
    }

    @Override
    public int compareTo(Process p) {
        return Float.compare(arrivalTime, p.arrivalTime);
    }

}
