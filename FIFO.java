import java.util.List;

/**
 * Created by jaylantse on 11/3/16.
 */
public class FIFO implements ReplacementAlgorithm {

    @Override
	public Page findPageToReplace(List<Page> occupiedPages) {
        if (occupiedPages.isEmpty()) return null;
        
		return occupiedPages.remove(0);
    }
}
