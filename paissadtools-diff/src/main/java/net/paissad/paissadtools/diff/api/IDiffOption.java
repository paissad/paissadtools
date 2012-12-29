package net.paissad.paissadtools.diff.api;

import java.util.List;

/**
 * @author paissad
 * @param <T>
 */
public interface IDiffOption<T> {

    boolean isVerbose();

    /**
     * @return The list of exclusions to use while processing diff operations.
     */
    List<? extends IDiffExclusionRule<T>> getExclusionRules();

}
