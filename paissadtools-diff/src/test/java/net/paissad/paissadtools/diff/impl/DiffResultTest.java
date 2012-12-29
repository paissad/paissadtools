package net.paissad.paissadtools.diff.impl;

import org.junit.Test;

public class DiffResultTest {

    @SuppressWarnings("unused")
    @Test
    public final void testDiffResult() {
        new DiffResult();
    }

    @SuppressWarnings("unused")
    @Test
    public final void testDiffResultBooleanString() {
        new DiffResult(false, null);
    }

    @Test
    public final void testToString() {
        new DiffResult().toString();
    }

}
