import replacementalgorithms.FIFO;
import replacementalgorithms.ReplacementAlgorithm;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Paging {

	private int minPagesRequired;
	private ReplacementAlgorithm alg;
	private LinkedList<Page> freePagesList;
	
	public Paging(int memorySize, int pageSize, int minPagesRequired, ReplacementAlgorithm alg) {
		this.minPagesRequired = minPagesRequired;
		this.alg = alg;	

		int pagesCount = memorySize / pageSize;
		freePagesList = new LinkedList<>();

		for (int i = 0; i < pagesCount; i++) {
			freePagesList.add(new Page("", pageSize));
		}
	}

	/**
	 * Checks if there are at least 4 free pages.
	 * @return whether there are at least 4 free pages
     */
	public boolean isFull() {
		return freePagesList.size() < minPagesRequired;
	}

	/**
	 *
	 * @param p
     */
	public void addProcess(Process p) {
		
	}
	
	/**
	 * Method to execute a process
	 * @param p the process to be executed
	 */
	public void executeProcess(Process p)
	{
		Page[] tempArray = new Page[p.getPsize()];
		for(int i = 0; i < p.getPsize(); i++)
		{
			tempArray[i] = new Page(p.getName() + "-" + i, 1);
		}
		   final Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            final long t0 = System.currentTimeMillis();

	            @Override
	            public void run() {
	                final long elapsedTime = System.currentTimeMillis() - t0;

	                if (elapsedTime > p.getArrivalTime() * 1000) {
	                    //run each process for its service duration
	                	
	                    timer.cancel();
	                } else if (!isFull() && freePagesList.size() >=4) {
	                	//Every 100 msec process will make a memory reference to another page in that process
	                    if (elapsedTime >= 100) {
	                    	int idx = localityRef(p.getPsize()); //gets next random index its page reference
	                        System.out.println("Refering to another page: " + tempArray[idx].getName());
	                        
	                    }
	                }
	                else
	                {
	                	System.out.println("NO more free pages! waiting for free page...");
	                	//this is where swapping algorithm goes
	                }
	            }
	        }, 0, 100);
		
		
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
		int[] deltaIs = {0, 1};
		if(r >= 0  && r < (pageSize - minPagesRequired))
		{
			int deltaIdx = random.nextInt(2);
			nextIdx = deltaIs[deltaIdx];
			
		}
		else if(r >= (pageSize - minPagesRequired) && r <	pageSize -1)
		{
	        int serviceDuration = random.nextInt(5) + 1;
	        
			int j = random.nextInt(pageSize -2 ) + 2;
			nextIdx = j;
		}
		
		return nextIdx;
				
		
		
	}
	
	
	public static void main(String[] args)
	{
		Paging p = new Paging(100, 1, 4, new FIFO());
		//System.out.println(p.freePagesList.size());
		
		//Testing locality reference algorithm
		//System.out.println(p.localityRef(11));
		
	}
	
	

}