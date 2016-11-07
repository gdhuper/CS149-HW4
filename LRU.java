import java.util.List;

public class LRU implements ReplacementAlgorithm {
	
	private int leastRecentlyUsedIdx = 0;

	@Override
	public Page findPageToReplace(List<Page> occupiedPages) {
		if(occupiedPages.isEmpty()) 
			{
			return null;
			}
		
		else{
			if(occupiedPages.size() < 100)   //if size < 100 start switching page at index 0 and increment index
			{
				int occupiedPagesIdx = leastRecentlyUsedIdx; 
				Page LRU = occupiedPages.get(leastRecentlyUsedIdx++);
				LRU.setIdxLRU(occupiedPagesIdx);
				return LRU;
				
			}
			if(occupiedPages.size() == 100) //else if size = 100 start again from index 0
			{
				leastRecentlyUsedIdx = 0;
				Page LRU = occupiedPages.get(leastRecentlyUsedIdx++);
				LRU.setIdxLRU(0);
				return LRU;
			}
			
		}
		
		return null;
		

	
		
	}

	@Override
	public String toString() {
		return "LRU";
	}
}
