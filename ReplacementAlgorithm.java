import java.util.List;

/**
 * Created by jaylantse on 11/3/16.
 */
public interface ReplacementAlgorithm {

    Page findPageToReplace(List<Page> occupiedPages);
}
