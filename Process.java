import java.util.LinkedList;

/**
 * Helper class to generate process objects and manage them
 */
public class Process implements Comparable<Process> {

    public static final float MIN_RUNTIME = (float) 0.1;
    public static final float MAX_RUNTIME = 10;
    private String name; //changed name type from char to STring
    private float arrivalTime;
    private float serviceDuration;
    private int pageCount;
    private Page[] pages;

    public Process(String name, int pageCount, float arrivalTime, int serviceDuration) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.pageCount = pageCount;
        this.serviceDuration = serviceDuration;

        // Initialize pages array
        pages = new Page[pageCount];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPageReferenced(int i) {
        return pages[i] != null;
    }

    public void setPageReferenced(int i, Page p) {
        pages[i] = p;
    }

    public Page dereferencePage(int i) {
        Page page = pages[i];
        pages[i] = null;
        return page;
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
