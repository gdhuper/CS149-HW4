import java.sql.Timestamp;
import java.util.Calendar;

/**
 * A page in memory.
 */
public class Page {

    private final int number;
    private int referencedPage;
    private Process referencedProcess;
    private int useCount;
    private int oldestRef;
    private Timestamp time;

    public Page(int number) {
        this.number = number;

        referencedPage = -1;
        useCount = 0;
        oldestRef = -1;
        time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
    }
    
    public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}
	
	public void setOldestRef(int o)
	{
		this.oldestRef = 0;
	}

	public int getOldestRef()
	{
		return this.oldestRef;
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
        incrementOldestRef();
        setTime(new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()));
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
    
    public void incrementOldestRef() {
    	oldestRef++;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Page)) return false;

        if (o == this) return true;

        Page page = (Page) o;

        return page.getNumber() == this.getNumber();
    }
}