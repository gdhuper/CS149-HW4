import java.util.List;

/**
 * Created by jaylantse on 11/3/16.
 */
public class FIFO implements ReplacementAlgorithm {
	
	public  int idxToRemove =0; //variable to keep track of next  idx to remove
    @Override
	public Page findPageToReplace(List<Page> occupiedPages) {
    	
    	Page page = null;
        if (occupiedPages.isEmpty()) 
        	{
        	return page;
        	}
        else
        {
        if(idxToRemove < 100)
        	{	
		page =  occupiedPages.remove(idxToRemove++);
        	}
         if(idxToRemove == 100) { //if variable reaches 100 start from 0
        	page =  occupiedPages.remove(0);
         	}
        }
         
         return page;
    
}
}
