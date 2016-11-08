import java.util.List;

public class LFU implements ReplacementAlgorithm {

	@Override
	public Page findPageToReplace(List<Page> occupiedPages) {
		if (occupiedPages.isEmpty()) return null;

		Page lowestUseCount = occupiedPages.get(0);
		for (Page page : occupiedPages) {
			if (page.getUseCount() < lowestUseCount.getUseCount())
				lowestUseCount = page;
		}
		occupiedPages.remove(lowestUseCount);
		return lowestUseCount;
	}

	@Override
	public String toString() {
		return "LFU";
	}
	
}
