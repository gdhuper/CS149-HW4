import replacementalgorithms.ReplacementAlgorithm;

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

		// Initialize free pages
		for (int i = 0; i < pagesCount; i++) {
			freePagesList.add(new Page(i + "", pageSize));
		}
	}

	/**
	 * Checks if there are at least specified minimum free pages.
	 * @return whether there are at least specified minimum free pages
     */
	public boolean isFull() {
		return freePagesList.size() < minPagesRequired;
	}

	/**
	 * Method to execute a process
	 * @param p the process to be executed
	 */
	public void executeProcess(Process p) {
		initializeProcess(p);

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			final long t0 = System.currentTimeMillis();

			@Override
			public void run() {
				final long elapsedTime = System.currentTimeMillis() - t0;

				if (elapsedTime >= p.getServiceDuration() * 1000) {
					//run each process for its service duration
					System.err.println(p.getName() + " terminating after " + elapsedTime / 1000.0 + " seconds");
					for (int i = 0; i < p.getPageCount(); i++) {
						Page unreferencedPage = p.unreferencePage(i);
						if (unreferencedPage != null)
							freePagesList.add(unreferencedPage);
					}
					timer.cancel();
				} else {
					//Every 100 msec process will make a memory reference to another page in that process
					referencePage(p);
				}
			}
		}, 0, 100);
	}

	/**
	 * Initialize process by referencing page 0.
	 * @param p the process to initialize
	 */
	private synchronized void initializeProcess(Process p) {
		if (!freePagesList.isEmpty()) {
			final Page page = freePagesList.removeFirst();
			p.setPageReferenced(0, page);
		}
	}

	/**
	 * Reference page from freePagesList if not already referenced. Synchronized to prevent race conditions.
	 * @param p the process to reference a new page for
     */
	private synchronized void referencePage(Process p) {
		if (!freePagesList.isEmpty()) {
			int i = localityRef(p.getPageCount()); //gets next random index its page reference
			if (!p.isPageReferenced(i)) {
				System.out.println("Referencing page for " + p.getName() + ": " + i);
				final Page page = freePagesList.removeFirst();
				p.setPageReferenced(i, page);
			} else {
//				System.out.println("Page already referenced for " + p.getName() + ": " + i);
			}
		} else {
//			System.out.println("NO more free pages! waiting for free page...");
			//this is where swapping algorithm goes
			alg.replace();
		}
	}
	
	/**
	 * Helper method to make random reference to the pages of a process
	 * @param pageSize the size of process
	 * @return index of next
	 */
	private int localityRef(int pageSize)
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
			nextIdx = random.nextInt(pageSize -2 ) + 2;
		}
		
		return nextIdx;
	}
}