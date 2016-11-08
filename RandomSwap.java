import java.util.List;
import java.util.Random;

public class RandomSwap implements ReplacementAlgorithm {

	private final Random random = new Random();

	@Override
	public Page findPageToReplace(List<Page> occupiedPages) {
		if (occupiedPages.isEmpty()) return null;

		return occupiedPages.remove(random.nextInt(occupiedPages.size()));
	}

	@Override
	public String toString() {
		return "RandomSwap";
	}
	
	
}
