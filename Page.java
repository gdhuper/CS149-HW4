/**
 * A page in memory.
 */
public class Page {

    private final int number;
    private int referencedPage;
    private Process referencedProcess;

    public Page(int number) {
        this.number = number;

        referencedPage = -1;
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
     * Sets the process that this page is currently referencing.
     * @param process the process to reference
     */
    public void setReferencedProcess(Process process) {
        this.referencedProcess = process;
    }

    /**
     * Gets the process that this page is currently referencing.
     * @return the process that is referenced
     */
    public Process getReferencedProcess() {
        return referencedProcess;
    }
}
