import java.util.List;

/**
 * Created by jaylantse on 11/3/16.
 */
public interface ReplacementAlgorithm {

    /**
     * Finds a page to swap out using the given algorithm.
     * @param occupiedPages the list of all occupied pages
     * @return the page to swap out
     */
    Page findPageToReplace(List<Page> occupiedPages);
}
