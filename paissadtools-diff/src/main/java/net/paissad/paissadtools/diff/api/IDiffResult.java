package net.paissad.paissadtools.diff.api;

/**
 * Represent the result of a objects comparison.
 * 
 * @author paissad
 */
public interface IDiffResult {

    /**
     * <span style='color:red'><b>NOTE : </b></span>this result should not be consider if {@link #isExcluded()} returns
     * <code>true</code> .
     * 
     * @return <code>true</code> if similar, <code>false</code> otherwise.
     */
    boolean similar();

    /**
     * @return <code>true</code> if this result is void due to exclusion.
     */
    boolean isExcluded();

    /**
     * @return The String result of the report.
     */
    String getReport();
}
