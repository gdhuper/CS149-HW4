/**
 * Created by Jaylan Tse on 11/2/2016.
 */
public class Page {

    private final int number;
    private final int size;
    private int referencedPage;
    private Process referencedProcess;

    public Page(int number, int size) {
        this.number = number;
        this.size = size;

        referencedPage = -1;
    }

	public int getSize() {
		return size;
	}

    public int getNumber() {
        return number;
    }

    public int getReferencedPage() {
        return referencedPage;
    }

    public void setReferencedPage(int referencedPage) {
        this.referencedPage = referencedPage;
    }

    public void setReferencedProcess(Process p) {
        this.referencedProcess = p;
    }

    public Process getReferencedProcess() {
        return referencedProcess;
    }
}
