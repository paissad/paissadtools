package net.paissad.paissadtools.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * This class contains some common utilities that can be used in lots of other
 * projects.
 * 
 * @author paissad
 * @since 0.1
 */
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * 
     * @param objects
     * @throws IllegalArgumentException If at least one of the specified objects
     *             is <tt>null</tt>.
     * @see #assertNotNull(Object...)
     * @since 0.1
     */
    public static void assertNotNullOrThrowException(final Object... objects) throws IllegalArgumentException {
        if (!assertNotNull(objects)) throw new IllegalArgumentException("Cannot be null !");
    }

    /**
     * @param strings
     * @throws IllegalArgumentException If at least one of the specified strings
     *             is blank or <tt>null</tt>.
     * @see #assertNotBlank(String...)
     * @since 0.1
     */
    public static void assertNotBlankOrThrowException(final String... strings) throws IllegalArgumentException {
        if (!assertNotBlank(strings)) throw new IllegalArgumentException("Cannot be blank ! (null or empty)");
    }

    /**
     * @param objects
     * @return <code>true</code> if none of the objects is <code>null</code>,
     *         <code>false</code> otherwise.
     * @since 0.1
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
     * @param strings
     * @return <code>true</code> if no <tt>String</tt> is null or blank,
     *         <code>false</code> otherwise.
     * @since 0.1
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
     * @since 0.1
     */
    public static void closeAllStreamsQuietly(Closeable... closeables) {
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
     * @since 0.1
     */
    public static void closeStreamQuietly(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (IOException ioe) {
            // ...
        }
    }

    /**
     * Generates a temporary file name.
     * 
     * @param in
     * @param out
     * @throws IOException
     * @since 0.1
     */
    public static void copyStream(final InputStream in, final OutputStream out) throws IOException {
        final int BUFFER = 8192;
        byte[] data = new byte[BUFFER];
        int bytesRead;
        while ((bytesRead = in.read(data, 0, BUFFER)) > 0) {
            out.write(data, 0, bytesRead);
        }
    }

    /**
     * Generates a temporary file name.
     * 
     * @param prefix
     * @param suffix
     * @return The temporary filename.
     * @throws IOException
     * @see #createTempFilename(String, String, File)
     */
    public static String createTempFilename(final String prefix, final String suffix) throws IOException {
        return createTempFilename(prefix, suffix, null);
    }

    /**
     * Generates a temporary file name.
     * 
     * @param prefix
     * @param suffix
     * @param dir
     * @return The temporary filename.
     * @throws IOException
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
     * @param bytes
     * @param si - If <code>true</code> then use 1000 unit, 1024 otherwise.
     * @return A String representation of the file's size.
     * @since 0.1
     */
    @SuppressWarnings("boxing")
    public static String humanReadableByteCount(final long bytes, final boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * This is the equivalent of createTempDir(prefix, suffix, null).
     * 
     * @param prefix
     * @param suffix
     * @return The new created temporary directory.
     * @throws IOException
     */
    public static File createTempDir(final String prefix, final String suffix) throws IOException {
        return createTempDir(prefix, suffix, null);
    }

    /**
     * @param prefix
     * @param suffix
     * @param directory - The parent directory where to create the temporary
     *            directory.
     * @return The new created temporary directory.
     * @throws IOException
     */
    public static File createTempDir(final String prefix, final String suffix, final File directory) throws IOException {
        final File tmpFile = File.createTempFile(prefix, suffix, directory);
        FileUtils.deleteQuietly(tmpFile);
        FileUtils.forceMkdir(tmpFile);
        return new File(tmpFile.getCanonicalPath());
    }

    public static String removeExtension(final String filename) {
        return filename.replaceAll("\\.[^.]+$", "");
    }
    
    public static String removeExtension(final String filename, final String extension) {
        final String regex = Pattern.quote(extension) + "$";
        return filename.replaceAll(regex, "");
    }
}
