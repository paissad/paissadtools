package net.paissad.paissadtools.diff.impl;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.diff.api.IDiffResult;

/**
 * Simple implementation of {@link IDiffResult}.
 * 
 * @author paissad
 */
public class DiffResult implements IDiffResult {

    @Setter
    private boolean similar;

    @Getter
    @Setter
    private boolean excluded;

    @Getter
    @Setter
    private String  report;

    public DiffResult() {
    }

    public DiffResult(final boolean similar, final String report) {
        this.similar = similar;
        this.report = report;
    }

    @Override
    public boolean similar() {
        return this.similar;
    }

    @Override
    public String toString() {
        return "DiffResult [similar=" + this.similar + ", excluded=" + this.excluded + ", report=" + this.report + "]";
    }

}
