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
    private int pageSize;
    private Page[] pages;

    public Process(String name, int pageSize, float arrivalTime, int serviceDuration) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.pageSize = pageSize;
        this.serviceDuration = serviceDuration;

        // Initialize pages array
        pages = new Page[pageSize];
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

    public Page unreferencePage(int i) {
        Page page = pages[i];
        pages[i] = null;
        return page;
    }

    public float getArrivalTime() {
        return arrivalTime;
    }

    public String toString() {
        return "ProcessName: \n" + this.getName() + "\nSize (in pages):\n" + this.pageSize + "\nArrival Time:\n" + this.getArrivalTime() + "\nService duration:\n" + this.serviceDuration;
    }

    public void printList(LinkedList<Process> list) {
        System.out.printf("%5s%20s%20s%25s%n", "Process Name", "Size (in pages)", "Arrival Time ", "Service Duration(secs)");

        for (Process p : list) {
            System.out.printf("%5s%20s%20s%20s%n", p.getName(), p.getPageSize(), p.getArrivalTime(), p.getServiceDuration());

        }
    }

    public float getServiceDuration() {
        return serviceDuration;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int compareTo(Process p) {
        return Float.compare(arrivalTime, p.arrivalTime);
    }

}
