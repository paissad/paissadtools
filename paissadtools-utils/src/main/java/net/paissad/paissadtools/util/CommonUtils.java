package net.paissad.paissadtools.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * This class contains some common utilities that can be used in lots of other
 * projects.
 * 
 * @author paissad
 */
public final class CommonUtils {

    private CommonUtils() {
    }

    /**
     * @param objects - The list of objects to check.
     * @throws IllegalArgumentException If at least one of the specified objects
     *             is <tt>null</tt>.
     * @see #assertNotNull(Object...)
     * @throws IllegalArgumentException If at least one of the specified objects
     *             is <tt>null</tt>.
     */
    public static void assertNotNullOrThrowException(final Object... objects) throws IllegalArgumentException {
        if (!assertNotNull(objects)) throw new IllegalArgumentException("Cannot be null !");
    }

    /**
     * @param strings - The list of String to check.
     * @throws IllegalArgumentException If at least one of the specified strings
     *             is blank or <tt>null</tt>.
     * @see #assertNotBlank(String...)
     * @throws IllegalArgumentException If at least on of the specified
     *             arguments 'strings' is blank or <code>null</code>.
     */
    public static void assertNotBlankOrThrowException(final String... strings) throws IllegalArgumentException {
        if (!assertNotBlank(strings)) throw new IllegalArgumentException("Cannot be blank ! (null or empty)");
    }

    /**
     * @param objects - The list of objects to check.
     * @return <code>true</code> if none of the objects is <code>null</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean assertNotNull(final Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param strings - The list of String to check.
     * @return <code>true</code> if no <tt>String</tt> is null or blank,
     *         <code>false</code> otherwise.
     */
    public static boolean assertNotBlank(final String... strings) {
        for (String s : strings) {
            if (s == null || s.trim().isEmpty()) return false;
        }
        return true;
    }

    /**
     * Closes a list of <tt>Closeable</tt> objects quietly.<br>
     * The specified list may contain objects with null <tt>null</tt> values.
     * 
     * @param closeables - The list of <tt>Closeable</tt> object to close.
     * @see #closeStreamQuietly(Closeable)
     */
    public static void closeAllStreamsQuietly(final Closeable... closeables) {
        for (Closeable aCloseable : closeables) {
            closeStreamQuietly(aCloseable);
        }
    }

    /**
     * Close quietly an object which implements the {@link Closeable} interface
     * such as <tt>InputStream</tt>, <tt>OutputStream</tt>, <tt>Reader</tt> ...
     * 
     * @param closeable - The stream to close, if <tt>null</tt> nothing is done.
     * @see #closeAllStreamsQuietly(Closeable...)
     */
    public static void closeStreamQuietly(final Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException ioe) {
            // Do nothing.
        }
    }

    /**
     * Generates a temporary file name.
     * 
     * @param prefix - The prefix of the temporary file.
     * @param suffix - The suffix of the temporary file.
     * @return The temporary filename.
     * @throws IOException If an error occurs while creating the temporary file.
     * @see #createTempFilename(String, String, File)
     */
    public static String createTempFilename(final String prefix, final String suffix) throws IOException {
        return createTempFilename(prefix, suffix, null);
    }

    /**
     * Generates a temporary file name.
     * 
     * @param prefix - The prefix of the temporary file.
     * @param suffix - The suffix of the temporary file.
     * @param dir - The directory where to create the temporary file.
     * @return The temporary filename.
     * @throws IOException If an error occurs while creating the temporary file.
     * @see #createTempFilename(String, String)
     */
    public static String createTempFilename(final String prefix, final String suffix, final File dir)
            throws IOException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile(prefix, suffix, dir);
            return tempFile.getAbsolutePath();
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    /**
     * @param bytes - The size of the file in bytes.
     * @param si - If <code>true</code> then use 1000 unit, 1024 otherwise.
     * @return A String representation of the file's size.
     */
    public static String humanReadableByteCount(final long bytes, final boolean si) {
        final int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", Double.valueOf(bytes / Math.pow(unit, exp)), pre);
    }

    /**
     * This is the equivalent of createTempDir(prefix, suffix, null).
     * 
     * @param prefix - The prefix of the temporary directory.
     * @param suffix - The suffix of the temporary directory.
     * @return The new created temporary directory.
     * @throws IOException If an error occurs while creating the temporary
     *             directory.
     * @see #createTempDir(String, String, File)
     */
    public static File createTempDir(final String prefix, final String suffix) throws IOException {
        return createTempDir(prefix, suffix, null);
    }

    /**
     * @param prefix - The prefix of the temporary directory.
     * @param suffix - The suffix of the temporary directory.
     * @param directory - The parent directory where to create the temporary
     *            directory.
     * @return The new created temporary directory.
     * @throws IOException If an error occurs while creating the temporary
     *             directory.
     */
    public static File createTempDir(final String prefix, final String suffix, final File directory) throws IOException {
        final File tmpFile = File.createTempFile(prefix, suffix, directory);
        FileUtils.deleteQuietly(tmpFile);
        FileUtils.forceMkdir(tmpFile);
        return new File(tmpFile.getCanonicalPath());
    }

    /**
     * Removes the extension from a filename. The last is part of the removal.
     * 
     * @param filename - A name of the file.
     * @return The new filename without the extension.
     */
    public static String removeExtension(final String filename) {
        return filename.replaceAll("\\.[^.]+$", "");
    }

    /**
     * Removes the extension from a filename.
     * 
     * @param filename - The name of the file.
     * @param extension - The end part to remove from the filename.
     * @return The new filename after the extension's removal.
     */
    public static String removeExtension(final String filename, final String extension) {
        final String regex = Pattern.quote(extension) + "$";
        return filename.replaceAll(regex, "");
    }
}
