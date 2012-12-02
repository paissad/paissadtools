package net.paissad.paissadtools.compress.api;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import net.paissad.paissadtools.util.CommonUtils;

/**
 * <p>
 * This class can be considered as an extra utility and or proxy which
 * compress/uncompress files based on their extension. Almost all methods
 * available in this class do all use the {@link CompressionHandlerFactory}
 * class in order to know the correct {@link CompressionHandler} implementation
 * to use for achieving the specified tasks (compressing, uncompressing,
 * resource adding or contents listing)
 * </p>
 * <p>
 * By the way, this class is not an implementation of {@link CompressionHandler}
 * at all.
 * </p>
 * 
 * @see CompressionHandler
 * @see CompressionHandlerFactory
 * @author paissad
 */
public class CompressionTool {

    private final CompressionHandlerFactory toolFactory;

    public CompressionTool() {
        this.toolFactory = CompressionHandlerFactory.newInstance();
    }

    /**
     * <p>
     * Compress a file.
     * </p>
     * <p>
     * <b><span style='color:orange;text-decoration:underline'>NOTE</span></b> :
     * the compression tool {@link CompressionHandler} that will be used is
     * based on the file's extension of the file to create specified by the "to"
     * argument.
     * </p>
     * 
     * @param from - The resource (file or directory) to compress
     * @param to - The compressed file to create.
     * @throws CompressException
     * @see CompressionHandler#compress(File, File)
     * @see CompressionHandlerFactory#getCompressionHandler(File)
     */
    public void compress(final File from, final File to) throws CompressException {
        this.smartCompress(from, from.getParentFile(), to);
    }

    /**
     * <p>
     * Compress a file.
     * </p>
     * <p>
     * <b><span style='color:orange;text-decoration:underline'>NOTE</span></b> :
     * the compression tool {@link CompressionHandler} that will be used is
     * based on the file's extension of the file to create specified by the "to"
     * argument.
     * </p>
     * 
     * @param from - The resource (file or directory) to compress
     * @param baseDir - The directory from which to compress the resource.
     * @param to - The compressed file to create.
     * @throws CompressException
     * @see CompressionHandler#compress(File, File, File)
     * @see CompressionHandlerFactory#getCompressionHandler(File)
     */
    public void compress(final File from, final File baseDir, final File to) throws CompressException {
        this.smartCompress(from, baseDir, to);
    }

    private void smartCompress(final File from, final File baseDir, final File to) throws CompressException {

        // XXX xxx.tar.gz.zip
        final List<String> knownExtensions = this.retrieveKnownExtensions(to.getName());
        Collections.reverse(knownExtensions);
        this.toolFactory.getCompressionHandler(to.getName()).compress(from, baseDir, to);

        boolean isLastExtension = false;
        File currentResourceToCompress = null;

        try {
            for (int i = 0; i < knownExtensions.size(); i++) {

                final String currentExtension = knownExtensions.get(i);
                currentResourceToCompress = new File(CommonUtils.createTempFilename(
                        "compressiontool_compress_", currentExtension));
                FileUtils.copyFile(from, currentResourceToCompress);

                if (isLastExtension) {
                    this.toolFactory.getCompressionHandler(currentExtension)
                            .compress(currentResourceToCompress, baseDir, to);
                } else {

                }
            }
        } catch (Exception e) {
            throw new CompressException(e);
        }
    }

    /**
     * <p>
     * Uncompress a file.
     * </p>
     * <p>
     * <b><span style='color:orange;text-decoration:underline'>NOTE</span></b> :
     * the compression tool {@link CompressionHandler} that will be used is
     * based on the file's extension of the file to create specified by the
     * "from" argument.
     * </p>
     * 
     * @param from - The file to uncompress
     * @param destination - The location where to put the uncompressed
     *            resource(s).
     * @throws CompressException
     * @see CompressionHandler#decompress(File, File)
     * @see CompressionHandlerFactory#getCompressionHandler(String)
     */
    public void decompress(final File from, final File destination) throws CompressException {
        // TODO handle successive decompression (ex: tar.gz, tar.bz2 ..)
        this.toolFactory.getCompressionHandler(from.getName()).decompress(from, destination);
    }

    /**
     * <p>
     * Add new files and/or directories to an already compressed file.
     * </p>
     * <p>
     * <b><span style='color:orange;text-decoration:underline'>NOTE</span></b> :
     * the compression tool {@link CompressionHandler} that will be used is
     * based on the file's extension of the file to create specified by the "to"
     * argument.
     * </p>
     * <p>
     * <b><span style='color:red;text-decoration:underline'>NOTE</span></b> : Be
     * aware there are some compression types that do not allow the possibility
     * to add new resources into a compressed file such as GZIP, BZIP2, XZ.
     * </p>
     * 
     * @param compressedFile - The file to which we want to.
     * @param resourcesToAdd - The resources (files and/or directories) to add
     * @throws CompressException
     * @see CompressionHandler#addResources(File, List)
     * @see CompressionHandlerFactory#getCompressionHandler(String)
     */
    public void addResources(final File compressedFile, final List<File> resourcesToAdd)
            throws CompressException {
        this.toolFactory.getCompressionHandler(compressedFile.getName()).addResources(compressedFile, resourcesToAdd);
    }

    /**
     * <p>
     * Add new files and/or directories to an already compressed file.
     * </p>
     * <p>
     * <b><span style='color:orange;text-decoration:underline'>NOTE</span></b> :
     * the compression tool {@link CompressionHandler} that will be used is
     * based on the file's extension of the file to create specified by the "to"
     * argument.
     * </p>
     * <p>
     * <b><span style='color:red;text-decoration:underline'>NOTE</span></b> : Be
     * aware there are some compression types that do not allow the possibility
     * to add new resources into a compressed file such as GZIP, BZIP2, XZ.
     * </p>
     * 
     * @param compressedFile - The compressed file to which we want to add new
     *            resources (files and/or directories)
     * @param baseDir
     * @param resourcesToAdd - The resources (files and/or directories) to add
     * @throws CompressException
     * @see CompressionHandler#addResources(File, File, List)
     * @see CompressionHandlerFactory#getCompressionHandler(String)
     */
    public void addResources(final File compressedFile, final File baseDir, final List<File> resourcesToAdd)
            throws CompressException {
        this.toolFactory.getCompressionHandler(compressedFile.getName()).addResources(compressedFile, baseDir,
                resourcesToAdd);
    }

    /**
     * List the content of the specified compressed file.
     * 
     * @param compressedFile
     * @return The contents of the compressed file.
     * @throws CompressException
     * @see CompressionHandler#list(File)
     * @see CompressionHandlerFactory#getCompressionHandler(String)
     */
    public List<String> list(final File compressedFile) throws CompressException {
        return this.toolFactory.getCompressionHandler(compressedFile.getName()).list(compressedFile);
    }

    private boolean filenameEndsWithSupportedExtension(final String filename) {
        for (final String extension : CompressionHandlerFactory.getSupportedExtensions()) {
            if (filename.endsWith(extension)) return true;
        }
        return false;
    }

    private String stripAllKnownConsecutiveKnownExtensions(final String compressedFilename) {
        String result = compressedFilename;
        final LinkedList<String> extensions = this.retrieveKnownExtensions(compressedFilename);
        for (int i = 0; i < extensions.size(); i++) {
            final String currentExtension = extensions.get(i);
            result = result.replaceAll(Pattern.quote(currentExtension) + "$", "");
        }
        return result;
    }

    /**
     * If for example the filename is "/tmp/tool.tar.gz.bzip2.xz", the result
     * will be ---> [.xz, .bzip2, .gz, .tar]
     * 
     * @param compressedFilename
     * @return
     */
    private LinkedList<String> retrieveKnownExtensions(final String compressedFilename) {
        final LinkedList<String> result = new LinkedList<String>();
        for (final String extension : CompressionHandlerFactory.getSupportedExtensions()) {
            if (compressedFilename.endsWith(extension)) {
                result.add(extension);
                final String regex = Pattern.quote(extension) + "$";
                result.addAll(retrieveKnownExtensions(compressedFilename.replaceAll(regex, "")));
            }
        }
        return result;
    }

    /*
     * XXX
     */
    public static void main(final String... args) throws Exception {
        final CompressionTool tool = new CompressionTool();
        final File from = new File("/tmp/bb");
        final File to = new File("/tmp/tool.tar.gz.bzip2.xz");
        // tool.compress(from, to);
        for (final String s : tool.retrieveKnownExtensions(to.getName())) {
            System.out.println(s);
        }
        System.out.println(tool.stripAllKnownConsecutiveKnownExtensions(to.getName()));
    }

}
