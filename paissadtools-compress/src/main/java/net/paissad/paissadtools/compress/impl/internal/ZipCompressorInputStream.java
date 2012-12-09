package net.paissad.paissadtools.compress.impl.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.CompressorInputStream;

/**
 * @author paissad
 */
class ZipCompressorInputStream extends CompressorInputStream {

    private InputStream in;

    ZipCompressorInputStream(final InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

}
