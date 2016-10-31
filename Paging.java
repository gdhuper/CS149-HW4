import java.util.Random;

public class Paging {
	
	public static final int TOTAL_PAGES = 100;
	public static final int MIN_PAGES_REQ = 4;
	
	
	public Paging()
	{
		
	}
	
	
	
	public int localityRef(int pageSize)
	{
		int nextIdx = 0;
		
		Random random = new Random();
		int r = random.nextInt(pageSize);
		int[] deltaIs = {-1, 0, 1};
		if(r >= 0  && r < (pageSize - MIN_PAGES_REQ))
		{
			int deltaIdx = random.nextInt(3);
			nextIdx = deltaIs[deltaIdx];
			
		}
		else if(r >= (pageSize - MIN_PAGES_REQ) && r <	pageSize -1)
		{
			int j = random.nextInt(pageSize -1 ) + 2;
			nextIdx = j;
		}
		
		return nextIdx;
				
		
		
	}
	
	
	public static void main(String[] args)
	{
		Paging p = new Paging();
		
		//Testing locality reference algorithm
		//System.out.println(p.localityRef(11));
		
	}
	
	

}
