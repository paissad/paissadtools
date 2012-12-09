package net.paissad.paissadtools.compress.impl.internal;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

/**
 * This class extends {@link CompressorStreamFactory} and adds the support for
 * .tar & .zip streams.
 * 
 * @author paissad
 * @see CompressorStreamFactory
 */
public class InternalCompressorStreamFactory extends CompressorStreamFactory {

    /** The flag representing the .tar type. */
    public static final String TAR = ".tar";

    /** The flag representing the .zip type. */
    public static final String ZIP = ".zip";

    @Override
    public CompressorInputStream createCompressorInputStream(final String name, final InputStream in)
            throws CompressorException {

        CompressorInputStream result = null;
        if (TAR.equals(name)) {
            result = new TarCompressorInputStream(in);
        } else if (ZIP.equals(name)) {
            result = new ZipCompressorInputStream(in);
        } else {
            result = super.createCompressorInputStream(name, in);
        }
        return result;
    }

    @Override
    public CompressorOutputStream createCompressorOutputStream(final String name, final OutputStream out)
            throws CompressorException {

        CompressorOutputStream result = null;
        if (TAR.equals(name)) {
            result = new TarCompressorOutputStream(out);
        } else if (ZIP.equals(name)) {
            result = new ZipCompressorOutputStream(out);
        } else {
            result = super.createCompressorOutputStream(name, out);
        }
        return result;
    }

}
