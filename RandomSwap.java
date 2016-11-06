import java.util.List;
import java.util.Random;

public class RandomSwap implements ReplacementAlgorithm {

	private final Random random = new Random();

	@Override
	public Page findPageToReplace(List<Page> occupiedPages) {
		return occupiedPages.remove(random.nextInt(occupiedPages.size()));
	}
}
