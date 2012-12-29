package net.paissad.paissadtools.diff.impl.file;

import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;

import net.paissad.paissadtools.diff.api.IDiffOption;
import net.paissad.paissadtools.diff.impl.AbstractFileDiffOption;

/**
 * Implementation of {@link IDiffOption} for file comparisons.
 * 
 * @author paissad
 */
public class FileDiffOption extends AbstractFileDiffOption<FileDiffExclusionRule> {

    @Getter
    @Setter
    private boolean recursive;

    public FileDiffOption() {
        this.setExclusionRules(new LinkedList<FileDiffExclusionRule>());
    }

}
