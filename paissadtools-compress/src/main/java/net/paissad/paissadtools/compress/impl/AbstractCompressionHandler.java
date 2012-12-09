package net.paissad.paissadtools.compress.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import net.paissad.paissadtools.compress.api.CompressionHandler;
import net.paissad.paissadtools.compress.exception.CompressException;
import net.paissad.paissadtools.compress.impl.internal.InternalCompressorStreamFactory;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * Abstract implementation of {@link CompressionHandler}.
 * 
 * @author paissad
 * 
 * @param <T>
 */
abstract class AbstractCompressionHandler<T extends CompressionHandler<T>> implements CompressionHandler<T> {

    protected static final String FILE_SEP = File.separator;

    /**
     * @throws IllegalArgumentException - If the specified InputStream is
     *             <code>null</code>.
     */
    @Override
    public InputStream compressStream(final InputStream in) throws IllegalArgumentException, CompressException {

        if (in == null) throw new IllegalArgumentException("The stream cannot be null.");
        OutputStream out = null;
        CompressorOutputStream cos = null;
        try {
            out = new ByteArrayOutputStream(BUFFER_8192);
            IOUtils.copy(in, out);
            cos = new InternalCompressorStreamFactory().createCompressorOutputStream(this.getCompressorType(), out);
            // FIXME
            return null;

        } catch (Exception e) {
            throw new CompressException("Error while compressing stream.", e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(out, cos);
        }
    }

    @Override
    public T compress(final File from) throws CompressException {
        final File to = new File(from.getParentFile(), from.getName() + this.getConventionalExtension());
        return this.compress(from, to);
    }

    @Override
    public T compress(final File from, final File to) throws CompressException {
        return this.compress(from, from.getParentFile(), to);
    }

    /**
     * @throws IllegalArgumentException - If the specified InputStream is
     *             <code>null</code>.
     */
    @Override
    public InputStream decompressStream(final InputStream in) throws IllegalArgumentException, CompressException {

        if (in == null) throw new IllegalArgumentException("The stream cannot be null.");
        try {
            return new InternalCompressorStreamFactory().createCompressorInputStream(this.getCompressorType(), in);
        } catch (Exception e) {
            throw new CompressException("Error while decompressing the stream.");
        }
    }

    @Override
    public T decompress(final File from) throws CompressException {
        if (!from.getName().toLowerCase(Locale.ENGLISH)
                .endsWith(this.getConventionalExtension().toLowerCase(Locale.ENGLISH))) {
            throw new IllegalArgumentException("Unknown file type, unable to uncompress. The expected extension is '"
                    + this.getConventionalExtension() + "'");
        }
        File destination = null;
        if (this.canCompressDirectories()) {
            destination = from.getParentFile();
        } else {
            final String destName = from.getName().replaceAll(
                    "(?i)" + Pattern.quote(this.getConventionalExtension()) + "$", "");
            destination = new File(from.getParentFile(), destName);
        }
        return this.decompress(from, destination);
    }

    @Override
    public T addResources(final File compressedFile, final List<File> resourcesToAdd) throws CompressException {
        return this.addResources(compressedFile, compressedFile.getParentFile(), resourcesToAdd);
    }

    /**
     * Returns all sub files & directories recursively for the specified
     * directory.<br/>
     * <b>NOTE</b> : This method may consume lots of memory if the specified
     * directory contains a huge amount of children (files & directories).
     * 
     * @param directory - The directory for which we want to retrieve all its
     *            children files & directories.
     * @return All sub files & directories recursively.
     * @throws IllegalArgumentException If the specified argument is not a
     *             directory
     * @throws NullPointerException If the specified argument is
     *             <code>null</code>.
     */
    protected List<File> getAllChildren(final File directory) throws IllegalArgumentException, NullPointerException {
        if (directory == null) throw new NullPointerException("The directory cannot be null.");
        if (!(directory.isDirectory())) {
            throw new IllegalArgumentException("'" + directory + "' is not a directory.");
        }
        final List<File> result = new ArrayList<File>();
        for (final File f : directory.listFiles()) {
            if (f.isDirectory()) {
                result.add(f);
                result.addAll(this.getAllChildren(f));
            } else {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * @param baseDir
     * @param file
     * @return The relative path of the file or directory from the reference
     *         (the reference is the baseDir) or an empty string.
     * @throws IOException
     * @throws IllegalArgumentException If the specified baseDir is not a
     *             directory.
     * @throws IOException
     */
    protected String stripBaseDirPath(final File baseDir, final File file) throws IllegalArgumentException, IOException {
        if (!baseDir.isDirectory()) {
            throw new IllegalArgumentException("The specified basedir '" + baseDir + "' is not a directory");
        }
        final String baseDirPath = baseDir.getCanonicalPath();
        final String filePath = file.getCanonicalPath();
        final StringBuilder sb = new StringBuilder();
        if ((filePath.startsWith(baseDirPath))) {
            sb.append(filePath.substring(baseDirPath.length()));
        } else {
            final int numberOfDoubleDots = baseDirPath.split(Pattern.quote(FILE_SEP)).length;
            for (int i = 0; i < numberOfDoubleDots; i++) {
                sb.append("/..");
            }
            sb.append(filePath);
        }
        String result = this.changeAllBackslashToSlash(sb.toString());
        result = result.replaceFirst("^/", "");
        return result;
    }

    protected String changeAllBackslashToSlash(final String path) {
        return path.replaceAll("\\\\", "/");
    }

    protected abstract String getCompressorType();

}
