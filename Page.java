/**
 * Created by Jaylan Tse on 11/2/2016.
 */
public class Page {

    private int number;
    private int size;

    public Page(int number, int size) {
        this.number = number;
        this.size = size;
    }

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && this.number == ((Page) o).getNumber();
    }
}
