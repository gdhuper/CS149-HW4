/**
 * A page in memory.
 */
public class Page {

    private final int number;
    private int referencedPage;
    private Process referencedProcess;
    private int useCount;

    public Page(int number) {
        this.number = number;

        referencedPage = -1;
        useCount = 0;
    }

    /**
     * Gets the page number.
     * @return the page number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Gets the page number of the process that this page is referencing.
     * @return the referenced page number
     */
    public int getReferencedPage() {
        return referencedPage;
    }

    /**
     * Sets the page number of the process' page that this page is referencing.
     * @param referencedPage the page number of the referenced page
     */
    public void setReferencedPage(int referencedPage) {
        this.referencedPage = referencedPage;
    }

    /**
     * Sets the process that this page is now referencing.
     * @param process the process to reference
     */
    public void setReferencedProcess(Process process) {
        this.referencedProcess = process;
        useCount = 1;
    }

    /**
     * Gets the process that this page is currently referencing.
     * @return the process that is referenced
     */
    public Process getReferencedProcess() {
        return referencedProcess;
    }

    public int getUseCount() {
        return useCount;
    }

    public void incrementUseCount() {
        useCount++;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Page)) return false;

        if (o == this) return true;

        Page page = (Page) o;

        return page.getNumber() == this.getNumber();
    }
}
