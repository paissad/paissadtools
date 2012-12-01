package net.paissad.paissadtools.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

/**
 * This class contains mainly methods that help to compress / uncompress files
 * or directories in ZIP format. This class does not use any third lib as
 * dependency.
 * 
 * @author paissad
 * @version 0.1
 */
public class ZipUtils {

    private static final int    BUFFER       = 4096;

    // It's better to use '/' as file separator into zip files even if the '\'
    // char is the file separator in Windows.
    private static final char   ZIP_SEP_CHAR = '/';

    private static final String FILE_SEP     = File.separator;

    /**
     * Compress a file or directory into a .zip file. If the destination file
     * does already exist, it is overwritten.
     * 
     * @param source - The source file or directory to compress.
     * @param destZipFile - The zip file to create.
     * @throws IOException
     */
    public static void zip(final File source, final File destZipFile) throws IOException {

        BufferedInputStream bis = null;
        ZipOutputStream zos = null;

        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destZipFile)));
            // out.setMethod(ZipOutputStream.DEFLATED);
            byte data[] = new byte[BUFFER];
            int bytesRead;
            String entryName;

            if (source.isFile()) { // If the source is a file.
                entryName = source.getName();
                bis = new BufferedInputStream(new FileInputStream(source), BUFFER);
                ZipEntry entry = new ZipEntry(entryName);
                entry.setTime(source.lastModified());
                zos.putNextEntry(entry);
                while ((bytesRead = bis.read(data, 0, BUFFER)) != -1) {
                    zos.write(data, 0, bytesRead);
                }
                bis.close();

            } else { // If the source is a directory.
                List<File> filesToZip = new ArrayList<File>();
                getChildFiles(source, filesToZip);
                if (!filesToZip.isEmpty()) {
                    for (File aFile : filesToZip) {
                        // We take the relative path of the file
                        entryName = aFile.getAbsolutePath().substring(
                                source.getAbsolutePath().length() + 1);
                        // Use the '/' as file separator.
                        entryName = entryName.replace(File.separatorChar, ZIP_SEP_CHAR);
                        if (aFile.isFile()) {
                            bis = new BufferedInputStream(new FileInputStream(aFile), BUFFER);
                            ZipEntry entry = new ZipEntry(entryName);
                            entry.setTime(aFile.lastModified());
                            zos.putNextEntry(entry);
                            while ((bytesRead = bis.read(data, 0, BUFFER)) != -1) {
                                zos.write(data, 0, bytesRead);
                            }
                            bis.close();
                        } else {
                            // If the directory is empty, we add an '/' at the
                            // end of the entry's name.
                            entryName += "/";
                            ZipEntry entry = new ZipEntry(entryName);
                            entry.setTime(aFile.lastModified());
                            zos.putNextEntry(entry);
                        }
                    }

                } else { // If the directory is empty.
                    ZipEntry emptyEntry = new ZipEntry("./");
                    emptyEntry.setTime(source.lastModified());
                    zos.putNextEntry(emptyEntry);
                }
            }

        } finally {
            CommonUtils.closeAllStreamsQuietly(zos, bis);
        }
    }

    // =========================================================================

    /**
     * Uncompress a .zip file into a destination directory. If the destination
     * directory does already exist, it gets removed before the uncompressing
     * process.
     * 
     * @param sourceZip - The .zip file to uncompress.
     * @param destinationDir - The directory where to put uncompressed files.
     * @throws IllegalArgumentException If the destination directory is
     *             <code>null</code>
     * @throws IOException If a problem occurs during the uncompressing process.
     * @see #unzip(File, File, boolean)
     */
    public static void unzip(File sourceZip, File destinationDir) throws IllegalArgumentException, IOException {
        unzip(sourceZip, destinationDir, true);
    }

    // =========================================================================

    /**
     * Uncompress a .zip file into a destination directory. If the destination
     * directory does already exist, it gets removed before the uncompressing
     * process.
     * 
     * @param sourceZip - The .zip file to uncompress.
     * @param destDir - The directory where to put uncompressed files.
     * @param isDeleteDestDir - If <code>true</code>, then remove the
     *            destination directory before uncompressing the .zip file. If
     *            <code>false</code>, the destination directory will not be
     *            removed, but the files into it will get overwritten.
     * @throws IllegalArgumentException If the destination directory is
     *             <code>null</code>
     * @throws IOException If a problem occurs during the uncompressing process.
     * @see #unzip(File, File)
     */
    public static void unzip(final File sourceZip, final File destDir, boolean isDeleteDestDir)
            throws IllegalArgumentException, IOException {

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            // Remove the destination directory if specified.
            if (isDeleteDestDir) {
                if (destDir == null) throw new IllegalArgumentException("The destination directory cannot be null");

                if (destDir.exists())
                    FileUtils.forceDelete(destDir);
                destDir.mkdirs();
            } else {
                if (!destDir.exists()) destDir.mkdirs();
            }

            ZipFile zipFile = new ZipFile(sourceZip, ZipFile.OPEN_READ);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
            int count;
            byte[] data = new byte[BUFFER];

            while (entries.hasMoreElements()) {
                ZipEntry aEntry = entries.nextElement();
                String entryName = aEntry.getName();
                if (aEntry.isDirectory()) {
                    new File(destDir.getAbsolutePath() + FILE_SEP + entryName).mkdirs();
                } else {
                    // Regexp that permits to retrieve the name of the parent
                    // directory of the current entry.
                    Pattern pattern = Pattern.compile(".*/|.*\\\\");
                    Matcher matcher = pattern.matcher(entryName);
                    // We retrieve the name of the parent's directory
                    String parentEntryName = (matcher.find()) ? matcher.group() : "";
                    new File(destDir.getAbsolutePath() + FILE_SEP + parentEntryName).mkdirs();
                    bis = new BufferedInputStream(zipFile.getInputStream(aEntry), BUFFER);
                    bos = new BufferedOutputStream(new FileOutputStream(destDir.getAbsolutePath()
                            + FILE_SEP + entryName), BUFFER);
                    while ((count = bis.read(data, 0, BUFFER)) != -1)
                        bos.write(data, 0, count);
                    bos.flush();
                    bos.close();
                }
            }

        } finally {
            CommonUtils.closeAllStreamsQuietly(bis, bos);
        }
    }

    // =========================================================================

    /**
     * Get all subfiles and subdirs of a specified directory recursively.
     * 
     * @param root - The root directory.
     * @param children - Contains the list of subfiles and directories.
     */
    private static void getChildFiles(final File root, final List<File> children) {
        final File[] childFiles = root.listFiles();
        for (File aFile : childFiles) {
            if (aFile.isFile() || isEmptyDir(aFile))
                children.add(aFile);
            else
                getChildFiles(aFile, children);
        }
    }

    private static boolean isEmptyDir(File directory) throws IllegalArgumentException, IllegalStateException {
        if (!directory.isDirectory()) throw new IllegalArgumentException("Is not a directory");
        if (!directory.canRead()) throw new IllegalStateException("Cannot read the directory.");
        if (directory.list() == null) throw new IllegalStateException("Error while reading directory");
        return (directory.list().length == 0);
    }
}
