package algorithms;

/**
 * Created by evikuzn on 7/10/2017.
 * Interface for the CombiningTree data structures.
 */
public interface CombiningTree {
    /**
     * Gets the current value and increments.
     * @return the value currently stored in the root element.
     * @throws PanicException
     */
    int getAndIncrement() throws PanicException;
}
