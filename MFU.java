import java.util.List;

public class MFU implements ReplacementAlgorithm {

	@Override
	public Page findPageToReplace(List<Page> occupiedPages) {
		if (occupiedPages.isEmpty()) return null;

		Page highestUseCount = occupiedPages.get(0);
		for (Page page : occupiedPages) {
			if (page.getUseCount() > highestUseCount.getUseCount())
				highestUseCount = page;
		}
		occupiedPages.remove(highestUseCount);
		return highestUseCount;
	}
}
