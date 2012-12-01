package net.paissad.paissadtools.compress.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import net.paissad.paissadtools.compress.api.CompressException;
import net.paissad.paissadtools.compress.api.CompressionHandler;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.io.IOUtils;

public class Bzip2Tool extends AbstractCompressionHandler<Bzip2Tool> {

    public static final String BZIP2_EXTENSION = ".bzip2";

    /**
     * <b><span style='color:green'>NOTES</span></b> :
     * <ul>
     * <li>the parameter baseDir is not used here.</li>
     * <li>can only compress files, not directories.</li>
     * </ul>
     */
    @Override
    public Bzip2Tool compress(final File from, final File baseDir, final File to) throws CompressException {
        if (!from.isFile()) {
            throw new IllegalArgumentException("BZIP2 can compress only files.");
        }
        if (to.isDirectory()) {
            throw new IllegalArgumentException("BZIP2 : The destination cannot be a directory");
        }
        InputStream in = null;
        BZip2CompressorOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(from), BUFFER_8192);
            out = new BZip2CompressorOutputStream(new BufferedOutputStream(new FileOutputStream(to), BUFFER_8192));
            IOUtils.copyLarge(in, out);
            return this;
        } catch (IOException e) {
            throw new CompressException("(BZIP2) Error while compressing the file -> " + from, e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(in, out);
        }
    }

    /**
     * @throws IllegalArgumentException If the source file to uncompress is not
     *             a file, or if the specified destination is an existent
     *             directory.
     */
    @Override
    public Bzip2Tool decompress(final File bzipFile, final File destFile) throws CompressException {
        if (!bzipFile.isFile()) {
            throw new IllegalArgumentException("The specified resource to decompress is not a file : " + bzipFile);
        }
        if (destFile.isDirectory()) {
            throw new IllegalArgumentException("The specified destination is a directory : " + destFile);
        }
        BZip2CompressorInputStream in = null;
        OutputStream out = null;
        try {
            in = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(bzipFile), BUFFER_8192));
            out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_8192);
            IOUtils.copy(in, out);
            return this;
        } catch (IOException e) {
            throw new CompressException("Error while uncompressing the bzip2 file : " + bzipFile, e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(in, out);
        }
    }

    @Override
    public Bzip2Tool addResources(final File bzipFile, final File baseDir, final List<File> resourcesToAdd)
            throws CompressException {
        throw new CompressException("Adding resources not supported for BZIP2");
    }

    @Override
    public List<String> list(final File bzipFile) throws CompressException {
        if (!bzipFile.isFile()) {
            throw new IllegalArgumentException("(BZIP2) '" + bzipFile + "' is not a file");
        }
        final String content = CommonUtils.removeExtension(bzipFile.getName(), this.getConventionalExtension());
        return Arrays.asList(new String[] { content });
    }

    @Override
    public CompressionHandler<?> getAdapter(final Class<CompressionHandler<?>> clazz) {
        return null;
    }

    @Override
    public boolean canCompressDirectories() {
        return false;
    }

    @Override
    public boolean canAddResources() {
        return false;
    }

    @Override
    public String getConventionalExtension() {
        return BZIP2_EXTENSION;
    }

}
