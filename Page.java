/**
 * Created by Jaylan Tse on 11/2/2016.
 */
public class Page {

    private int number;
    private int size;
    private int referencedPage;

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

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && this.number == ((Page) o).getNumber();
    }
}
