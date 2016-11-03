import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Helper class to generate process objects and manage them
 *
 */
public class Process implements Comparable<Process> {
	
	private String name;
	private float arrivalTime;
	private float serviceDuration;
	private int Psize;
	
	public static final float MIN_ARRIVAL_TIME = 149;
	public static final float MAX_ARRIVAL_TIME = 0;
	public static final float MIN_RUNTIME = (float) 0.1;
	public static final float MAX_RUNTIME = 10;

	public Process()
	{
		//
	}
	
	
	public Process(String name, int Psize, float arrivalTime, int serviceDuration)
	{
		this.name = name;
		this.arrivalTime = arrivalTime;
		this.Psize = Psize;
		this.serviceDuration = serviceDuration;
		
	}

	
	
	
	public static Process generateProcess(String name)
	{
		Random random = new Random();
        float arrivalTime =  nextRandomFloat(MIN_ARRIVAL_TIME, MAX_ARRIVAL_TIME); 
        arrivalTime = formatDecimal(arrivalTime, 2);

        int[] Stimes = {5, 11,17,31};
        int idx = random.nextInt(4);
        int size = Stimes[idx];
        int serviceDuration = random.nextInt(5) + 1;
        
        Process pro = new Process(name, size, arrivalTime, serviceDuration);
        return pro;
	}




	public static float nextRandomFloat(float min, float max)
	{
		Random random = new Random();
		float temp = min + random.nextFloat() * (max - min);
		return temp;
	}
	
	
	
	/**
	 * Sorts the process list by arrival time
	 * @param list the process list
	 */
	public static void sortByAt(LinkedList<Process> list)
	{
		Collections.sort(list, new Comparator<Process>(){
			   public int compare(Process p1, Process p2){
				   
				      return 	Float.compare(p1.getArrivalTime(), p2.getArrivalTime());
				   }
				});
	}
	
	
	public static float formatDecimal(float d, int decimalPlace) {

        BigDecimal bd = new BigDecimal(Float.toString(d));

        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);       

        return bd.floatValue();

    }


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public float getArrivalTime() {
		return arrivalTime;
	}


	public void setArrivalTime(float arrivalTime) {
		this.arrivalTime = arrivalTime;
	}


	
	public String toString()
	{
		return "ProcessName: \n" + this.getName() + "\nSize (in pages):\n" + this.Psize + "\nArrival Time:\n" + this.getArrivalTime() + "\nService duration:\n"+ this.serviceDuration;
	}
	
	/**
	 * Helper method to sort and rename the process list
	 * @param list the list to be sorted and renamed
	 */
	public void sortAndRename(LinkedList<Process> list)
	{
		Process.sortByAt(list); //sorting the processes based on arrival time
		int i = 1;
		for(Process p : list)
		{	
			p.setName("P" + i);
			i++;
		}
	}
	
	public void updateIdx(LinkedList<Process> list)
	{
		int i = 1;
		for(Process p : list)
		{	
			p.setName("P" + i);
			i++;
		}
	}
	
	
	public void printList(LinkedList<Process> list)
	{
		System.out.printf("%5s%20s%20s%25s%n", "Process Name", "Size (in pages)", "Arrival Time ", "Service Duration(secs)");

		for(Process p : list)
		{	
			System.out.printf("%5s%20s%20s%20s%n", p.getName(), p.getPsize(), p.getArrivalTime(), p.getServiceDuration());

		}
	
	}
	
	
	

	
	
	public float getServiceDuration() {
		return serviceDuration;
	}


	public void setServiceDuration(float serviceDuration) {
		this.serviceDuration = serviceDuration;
	}


	public int getPsize() {
		return Psize;
	}


	public void setPsize(int psize) {
		Psize = psize;
	}


	public static void main(String[] args)
	{
		Process temp = new Process();
		
		LinkedList<Process> list = new LinkedList<Process>();
		
		for(int i = 1;  i < 151; i++)
		{
			Process tempProcess = temp.generateProcess("P" + i);
			list.add(tempProcess);
		}
		
		temp.sortAndRename(list);
		
		temp.printList(list);
		//After removing an element from index 0 call updateIdX(list) to update index of elements 
		
	}

	@Override
	public int compareTo(Process p)
	{
		return Float.compare(arrivalTime, p.arrivalTime);
	}

}
	