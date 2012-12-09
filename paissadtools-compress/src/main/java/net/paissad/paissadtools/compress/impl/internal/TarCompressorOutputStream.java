package net.paissad.paissadtools.compress.impl.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorOutputStream;

/**
 * @author paissad
 */
class TarCompressorOutputStream extends CompressorOutputStream {

    private OutputStream out;

    TarCompressorOutputStream(final OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }

}
