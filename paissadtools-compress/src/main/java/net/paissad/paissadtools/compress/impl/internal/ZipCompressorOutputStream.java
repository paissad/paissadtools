package net.paissad.paissadtools.compress.impl.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorOutputStream;

/**
 * @author paissad
 */
class ZipCompressorOutputStream extends CompressorOutputStream {

    private OutputStream out;

    ZipCompressorOutputStream(final OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }

}
