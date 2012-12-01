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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.paissad.paissadtools.compress.api.CompressException;
import net.paissad.paissadtools.compress.api.CompressionHandler;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Gzip / Gunzip
 * 
 * @author paissad
 * @since 0.1
 */
public class GzipTool extends AbstractCompressionHandler<GzipTool> {

    public static final String GZIP_EXTENSION = ".gz";

    /**
     * <b><span style='color:green'>NOTES</span></b> :
     * <ul>
     * <li>the parameter baseDir is not used here.</li>
     * <li>can only compress files, not directories.</li>
     * </ul>
     */
    @Override
    public GzipTool compress(final File from, final File baseDir, final File to) throws CompressException {
        if (!from.isFile()) {
            throw new IllegalArgumentException("GZIP can compress only files.");
        }
        if (to.isDirectory()) {
            throw new IllegalArgumentException("GZIP : The destination cannot be a directory");
        }
        InputStream in = null;
        GZIPOutputStream out = null;
        try {
            in = new FileInputStream(from);
            FileUtils.forceMkdir(to.getParentFile());
            out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(to), BUFFER_8192), BUFFER_8192);
            IOUtils.copyLarge(in, out);
            out.finish();
            out.flush();
            return this;
        } catch (IOException ioe) {
            throw new CompressException("Error while gzipping the file '" + from + "' to '" + to + "'.", ioe);
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
    public GzipTool decompress(final File gzipFile, final File destFile) throws CompressException {
        if (!gzipFile.isFile()) {
            throw new IllegalArgumentException("The specified resource to decompress is not a file : " + gzipFile);
        }
        if (destFile.isDirectory()) {
            throw new IllegalArgumentException("The specified destination is a directory : " + destFile);
        }
        GZIPInputStream in = null;
        OutputStream out = null;
        try {
            in = new GZIPInputStream(new BufferedInputStream(new FileInputStream(gzipFile), BUFFER_8192), BUFFER_8192);
            out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_8192);
            FileUtils.forceMkdir(destFile.getParentFile());
            IOUtils.copyLarge(in, out);
            return this;
        } catch (IOException ioe) {
            throw new CompressException("Error while uncompressing the file '" + gzipFile + "' to" + "'" + destFile
                    + "'.", ioe);
        } finally {
            CommonUtils.closeAllStreamsQuietly(in, out);
        }
    }

    @Override
    public GzipTool addResources(final File gzipFile, final File baseDir, final List<File> resourcesToAdd)
            throws CompressException {
        throw new CompressException("Add resource not supported for GZIP.");
    }

    @Override
    public List<String> list(final File gzipFile) throws CompressException {
        if (!gzipFile.isFile()) {
            throw new IllegalArgumentException("(GZIP) '" + gzipFile + "' is not a file");
        }
        final String content = CommonUtils.removeExtension(gzipFile.getName(), this.getConventionalExtension());
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
        return GZIP_EXTENSION;
    }

}
