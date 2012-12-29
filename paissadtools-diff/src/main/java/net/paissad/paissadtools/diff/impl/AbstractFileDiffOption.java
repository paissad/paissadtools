package net.paissad.paissadtools.diff.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.paissad.paissadtools.diff.api.IDiffOption;
import net.paissad.paissadtools.diff.api.IFileDiffExclusionRule;

/**
 * Abstract implementation of {@link IDiffOption} that can be used for files.
 * 
 * @param <E> - The type of {@link IFileDiffExclusionRule} to use for file exclusions.
 * 
 * @author paissad
 */
public abstract class AbstractFileDiffOption<E extends IFileDiffExclusionRule> extends AbstractDiffOption<File> {

    @Getter
    @Setter
    private List<E> exclusionRules;

    public AbstractFileDiffOption() {
        this.exclusionRules = new LinkedList<E>();
    }

}
