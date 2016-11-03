import java.util.LinkedList;
import java.util.Random;

public class Paging {
	
	public static final int TOTAL_PAGES = 100;
	public static final int MIN_PAGES_REQ = 4;
	
	private int pagesLeft = 100;
	
	public Paging()
	{
		
	}
	
	
	public int getPagesLeft() {
		return pagesLeft;
	}


	public void setPagesLeft(int pagesLeft) {
		this.pagesLeft = pagesLeft;
	}


	/**
	 * Method to execute a process
	 * @param p the process to be executed
	 */
	public void executeProcess(Process p)
	{
		//Algorithm:
		//All the processes are sorted based on their arrival time 
		//and then a process from the start of Queue is taken out for processing 
		//if there are atleast 4 pages available on main memory (i.e TOTAL_PAGES >= 4),
		//start executing the process else wait for others to finish
		int processPageSize = p.getPsize(); //No of pages in a process 
		LinkedList<String> tempList = new LinkedList<String>();
		
		for(int i =0; i < p.getPsize(); i++)
		{
			tempList.add(p.getName() + "-" +  i);	
		}
		
		if(getPagesLeft() >= 4)
		{
			//Start executing process
			
		}
		
		
		
		
	}
	
	
	
	
	
	/**
	 * Helper method to make random reference to the pages of a process
	 * @param pageSize the size of process
	 * @return index of next
	 */
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
		//Paging p = new Paging();
		//Process pro = new Process();
		
		//Process process = pro.generateProcess("P0");
		//p.executeProcess(process);
		
		//Testing locality reference algorithm
		//System.out.println(p.localityRef(11));
		
	}
	
	

}
