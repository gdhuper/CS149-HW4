import java.util.List;

public class LRU implements ReplacementAlgorithm {
	
	private int leastused = -1;

	@Override
	public Page findPageToReplace(List<Page> occupiedPages) {
		if(occupiedPages.isEmpty()) 
			{
			return null;
			}
		
		else{
			Page lru = occupiedPages.get(0);
			for(int i = 1; i < occupiedPages.size(); i++)
			{
				Page temp = occupiedPages.get(i);
				if(temp.getOldestRef() < lru.getOldestRef() && temp.getTime().compareTo(lru.getTime()) > 1)
				{
					lru = temp;
				}
					
			}
			return occupiedPages.remove(occupiedPages.indexOf(lru));
		}
	 
		
	}

	@Override
	public String toString() {
		return "LRU";
	}
	

}
