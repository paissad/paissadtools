package net.paissad.paissadtools.diff.api;

import net.paissad.paissadtools.diff.exception.DiffToolException;

/**
 * This class represent an exclusion to apply while using the {@link IDiffTool} in order to exclude some object from
 * being processed.
 * 
 * @author paissad
 * @param <T> - The type of the object for which we want to verify the exclusion rule.
 */
public interface IDiffExclusionRule<T> {

    /**
     * Whether or not to exclude the specified object.
     * 
     * @param obj - The object for which we want to verify the exclusion rule.
     * @return <code>true</code> if the object is to be excluded, <code>false</code> otherwise
     * @throws DiffToolException If an error occurs while verifying the rule.
     */
    boolean match(final T obj) throws DiffToolException;
}
