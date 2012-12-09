package net.paissad.paissadtools.compress.impl.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.CompressorInputStream;

/**
 * @author paissad
 */
class TarCompressorInputStream extends CompressorInputStream {

    private InputStream in;

    TarCompressorInputStream(final InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

}
