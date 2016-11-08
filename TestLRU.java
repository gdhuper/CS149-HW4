import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/*
 * This is just a tester class i made for testing LRU
 * Will remove this file before turning in HW
 */
public class TestLRU {
	
	public static List<Page> list = new ArrayList<Page>();
	public static List<Page> occupiedList = new ArrayList<Page>();
	public static int idx = 0;
	
	
	public static void main(String[] args)
	{
		
	for(int i = 1; i <= 100; i++)
		{
			list.add(new Page(i));
		}
		final long t0 = System.currentTimeMillis();
		 final Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            final long t0 = System.currentTimeMillis();

	            @Override
	            public void run() {
	                final long elapsedTime = System.currentTimeMillis() - t0;

	                // Run each process for its service duration
	                if (list.size() == 0 ) {
	                	timer.cancel();
	                 
	                } else {
	                    // Every 100 msec make a memory reference to another page in that process
	                	addPage(list.remove(0));
	                
	                }
	            }
	        }, 0, 100);
	        
	  
	        
        }
	
	
	public static void addPage(Page p )
	{
		if(occupiedList.size() < 100)
		{
			occupiedList.add(p);
			printMap();
			System.out.println();
		}
		else if(occupiedList.size() == 100)
		{
			occupiedList.set(idx, p);
			idx++;
			printMap();
			System.out.println();

			
		}
	}
	
	
	
	public static void printMap()
	{
		if(!occupiedList.isEmpty())
		{
			for(Page p: occupiedList)
			{
				System.out.print(p.getNumber()+ " ");
			}
		}
	}
	}
	
