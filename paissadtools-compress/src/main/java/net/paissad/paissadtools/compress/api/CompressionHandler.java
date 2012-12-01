package net.paissad.paissadtools.compress.api;

import java.io.File;
import java.util.List;

public interface CompressionHandler<T extends CompressionHandler<T>> {

    int BUFFER_8192 = 8192;

    /**
     * <p>
     * Compress the specified resource (file or directory) and puts the new file
     * into the same location by appending the extension
     * </p>
     * <p>
     * Is the equivalent of :<br>
     * <code>compress(from, to);</code><br>
     * where to has as the same name as the file 'from' and the extension for
     * the specified type of compression.
     * </p>
     * <p>
     * <span style='color:red'><b>NOTE</b></span> : The resource to compress
     * must not be a directory if the method {@link #canCompressDirectories()}
     * returns <code>false</code>.
     * </p>
     * <p>
     * <b>NOTE</b> : The original file does not change and is not removed at
     * all.
     * </p>
     * 
     * @param from - The resource to compress (file or directory)
     * @return This instance for chaining purposes.
     * @throws CompressException
     * @see #compress(File, File, File)
     */
    T compress(final File from) throws CompressException;

    /**
     * <p>
     * Compress the specified resource (file or directory).
     * </p>
     * <p>
     * Is the equivalent of :<br>
     * <code>compress(from, from.getParentFile(), to);</code>
     * </p>
     * <p>
     * <span style='color:red'><b>NOTE</b></span> : The resource to compress
     * must not be a directory if the method {@link #canCompressDirectories()}
     * returns <code>false</code>.
     * </p>
     * 
     * @param from - The resource to compress (file or directory)
     * @param to - The output file.
     * @return This instance for chaining purposes.
     * @throws CompressException
     * @see #compress(File, File, File)
     */
    T compress(final File from, final File to) throws CompressException;

    /**
     * <p>
     * Compress the specified resource (file or directory).
     * </p>
     * <p>
     * <span style='color:red'><b>NOTE</b></span> : The resource to compress
     * must not be a directory if the method {@link #canCompressDirectories()}
     * returns <code>false</code>.
     * </p>
     * 
     * @param from - The resource to compress (file or directory)
     * @param baseDir - The directory from which to compress the resource.
     * @param to - The output file.
     * @return This instance for chaining purposes.
     * @throws CompressException
     * @see #compress(File, File)
     */
    T compress(final File from, final File baseDir, final File to) throws CompressException;

    /**
     * <p>
     * Uncompress the specified and already compressed file.
     * </p>
     * <p>
     * <b><span style='color:blue'>NOTE</span></b> : The created resource (file
     * or directory) after the uncompressing process will be located into as in
     * the same location as the original file by removing the extension. The
     * original file does not change or is not removed at all.
     * </p>
     * 
     * @param from - The file to uncompress.
     * @return This instance for chaining purposes.
     * @throws CompressException
     * @throws IllegalArgumentException If the specified file to uncompress does
     *             not end with the expected extension.
     */
    T decompress(final File from) throws CompressException;

    /**
     * Uncompress the specified and already compressed file.
     * 
     * @param from - The file to uncompress.
     * @param destination - Where to put the uncompressed resources.
     * @return This instance for chaining purposes.
     * @throws CompressException
     */
    T decompress(final File from, final File destination) throws CompressException;

    /**
     * <p>
     * Add new files and/or directories to an already compressed file.
     * </p>
     * <p>
     * Is the equivalent of :<br>
     * <code>addResources(compressedFile, compressedFile.getParentFile(), resourcesToAdd</code>
     * </p>
     * <p>
     * <span style='color:red'><b>NOTE</b></span> : This must be used only and
     * only if {@link #canAddResources()} returns <code>true</code>.
     * </p>
     * 
     * @param compressedFile - The compressed file to which we want to add the
     *            new resources.
     * @param resourcesToAdd - The resources (files and/or directories) to add.
     * @return This instance for chaining purposes.
     * @throws CompressException
     * @see #addResources(File, File, List)
     */
    T addResources(final File compressedFile, final List<File> resourcesToAdd) throws CompressException;

    /**
     * <p>
     * Add new files and/or directories to an already compressed file.
     * </p>
     * <p>
     * <span style='color:red'><b>NOTE</b></span> : This must be used only and
     * only if {@link #canAddResources()} returns <code>true</code>.
     * </p>
     * 
     * @param compressedFile - The compressed file to which we want to add the
     *            new resources.
     * @param baseDir - The directory from which to add the resources.
     * @param resourcesToAdd - The resources (files and/or directories) to add.
     * @return This instance for chaining purposes.
     * @throws CompressException
     * @see #addResources(File, List)
     */
    T addResources(final File compressedFile, final File baseDir, final List<File> resourcesToAdd)
            throws CompressException;

    /**
     * List the content of the specified compressed file.
     * 
     * @param compressedFile
     * @return The contents.
     * @throws CompressException
     */
    List<String> list(final File compressedFile) throws CompressException;

    /**
     * @param clazz
     * @return Another type of compression handler.
     */
    CompressionHandler<?> getAdapter(final Class<CompressionHandler<?>> clazz);

    /**
     * @return <code>true</code> if the compression of directories is supported,
     *         <code>false</code> otherwise.
     */
    boolean canCompressDirectories();

    /**
     * @return <code>true</code> if the possibility to add new resources to a
     *         compressed file is supported, <code>false</code> otherwise.
     */
    boolean canAddResources();

    /**
     * <b><span style='color:red'>NOTE</span></b> : Must not be blank or
     * <code>null</code>.
     * 
     * @return The conventional extension for compressed files for this
     *         specified <tt>CompressionHandler</tt>
     */
    String getConventionalExtension();

}
