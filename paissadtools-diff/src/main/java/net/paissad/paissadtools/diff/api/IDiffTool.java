package net.paissad.paissadtools.diff.api;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.diff.exception.DiffToolException;

/**
 * A tool for comparing objects of the same types.
 * 
 * @author paissad
 * 
 * @param <T> - The type of the objects to compare.
 * @param <O> - The type of the option {@link IDiffOption}.
 */
public interface IDiffTool<T, O extends IDiffOption<?>> extends ITool {

    /**
     * <p>
     * Compare 2 objects of the same type.
     * </p>
     * <p>
     * <b>NOTE</b> : both objects to compare should not be <code>null</code>.
     * </p>
     * 
     * @param obj1 - 1st object.
     * @param obj2 - 2nd object.
     * @param option - The options. May be <code>null</code>.
     * @return The result of the comparison.
     * @throws DiffToolException If an error occurred during comparison.
     * @see IDiffResult
     */
    IDiffResult compare(final T obj1, final T obj2, final O option) throws DiffToolException;

}
