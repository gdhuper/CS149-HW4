package replacementalgorithms;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.Process;
/**
 * Created by jaylantse on 11/3/16.
 */
public class FIFO implements ReplacementAlgorithm {


    @Override
    public void replace() {
    	
    	
   		String className = "Paging";
   		try {
			Class c =  Class.forName(className);
			Class page = Class.forName("Page");
			
			Field list = 	c.getDeclaredField("pageMap");
			Field pageMap = c.getDeclaredField("pageMap");
			
			list.setAccessible(true);
			pageMap.setAccessible(true);
			Process[] process =   (Process[]) pageMap.get(c);
			
		  ConcurrentLinkedQueue<?> freePagesList = (ConcurrentLinkedQueue<?>) list.get(c);
		 
		  
		  

			
			
			
			
			
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		
		

    }
}
