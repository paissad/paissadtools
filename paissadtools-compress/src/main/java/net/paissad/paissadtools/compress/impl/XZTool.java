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

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

public class XZTool extends AbstractCompressionHandler<XZTool> {

    public static final String XZ_EXTENSION = ".xz";

    /**
     * <b><span style='color:green'>NOTES</span></b> :
     * <ul>
     * <li>the parameter baseDir is not used here.</li>
     * <li>can only compress files, not directories.</li>
     * </ul>
     */
    @Override
    public XZTool compress(File from, File baseDir, File to) throws CompressException {
        if (!from.isFile()) {
            throw new IllegalArgumentException("XZ can compress only files.");
        }
        if (to.isDirectory()) {
            throw new IllegalArgumentException("XZ : The destination cannot be a directory, but a file.");
        }
        XZOutputStream xzOutputStream = null;
        InputStream in = null;
        try {
            in = new FileInputStream(from);
            final FilterOptions options = new LZMA2Options(7);
            xzOutputStream = new XZOutputStream(new FileOutputStream(to), options);
            IOUtils.copyLarge(in, xzOutputStream);
            return this;
        } catch (IOException e) {
            throw new CompressException("(XZ) Error while compressing the file -> " + from, e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(in, xzOutputStream);
        }
    }

    /**
     * @throws IllegalArgumentException If the source file to uncompress is not
     *             a file, or if the specified destination is an existent
     *             directory.
     */
    @Override
    public XZTool decompress(File xzFile, File destFile) throws CompressException {
        if (!xzFile.isFile()) {
            throw new IllegalArgumentException("The specified resource to decompress is not a file : " + xzFile);
        }
        if (destFile.isDirectory()) {
            throw new IllegalArgumentException("The specified destination must be a file : " + destFile);
        }
        XZCompressorInputStream xzInputStream = null;
        OutputStream out = null;
        try {
            xzInputStream = new XZCompressorInputStream(new BufferedInputStream(
                    new FileInputStream(xzFile), BUFFER_8192));
            out = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_8192);
            IOUtils.copyLarge(xzInputStream, out);

        } catch (IOException e) {
            throw new CompressException(
                    "(XZ) Error while uncompressing the file '" + xzFile + "' to '" + destFile + "'", e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(xzInputStream, out);
        }
        return this;
    }

    @Override
    public XZTool addResources(File xzFile, File baseDir, List<File> resourcesToAdd) throws CompressException {
        throw new CompressException("Adding resources not supported for XZ or LZMA");
    }

    @Override
    public List<String> list(final File xzFile) throws CompressException {
        if (!xzFile.isFile()) {
            throw new IllegalArgumentException("(XZ) '" + xzFile + "' is not a file");
        }
        final String content = CommonUtils.removeExtension(xzFile.getName(), this.getConventionalExtension());
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
        return XZ_EXTENSION;
    }

}
