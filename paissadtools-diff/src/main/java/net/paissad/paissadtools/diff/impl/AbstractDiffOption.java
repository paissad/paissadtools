package net.paissad.paissadtools.diff.impl;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.diff.api.IDiffOption;

/**
 * Abstract implementation of {@link IDiffOption}.
 * 
 * @param <T>
 * 
 * @author paissad
 */
public abstract class AbstractDiffOption<T> implements IDiffOption<T> {

    @Getter
    @Setter
    private boolean verbose;

}
