package net.paissad.paissadtools.compress.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import net.paissad.paissadtools.compress.CompressionTool;
import net.paissad.paissadtools.compress.api.CompressionHandler;
import net.paissad.paissadtools.compress.exception.CompressException;
import net.paissad.paissadtools.compress.impl.internal.InternalCompressorStreamFactory;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

/**
 * Zip / unzip tool.
 * 
 * @author paissad
 * @see CompressionTool
 */
public class ZipTool extends AbstractCompressionHandler<ZipTool> {

    /** The default zip extension. */
    public static final String ZIP_EXTENSION = ".zip";

    /**
     * @throws IllegalArgumentException If the specified destination is an
     *             existing directory. (If the destination does already exist,
     *             it must be a file so that it can be overwritten correctly.
     */
    // CHECKSTYLE:OFF
    @Override
    public ZipTool compress(final File from, final File baseDir, final File to) throws CompressException {
        // CHECKSTYLE:ON
        if (to.isDirectory()) {
            throw new IllegalArgumentException("(ZIP) The resulting tar file cannot be a directory : " + to);
        }
        ZipArchiveOutputStream zipOutputStream = null;
        InputStream in = null;
        try {
            zipOutputStream = new ZipArchiveOutputStream(to);
            // The list of resources to add into the .zip file to create.
            final List<File> resourcesToCompress = new ArrayList<File>();
            resourcesToCompress.add(from);
            if (from.isDirectory()) {
                resourcesToCompress.addAll(this.getAllChildren(from));
            } else {
                // only files & directories are currently treated !
            }

            for (final File f : resourcesToCompress) {
                String entryName = this.stripBaseDirPath(baseDir, f);
                if (f.isDirectory()) entryName += "/";
                final ZipArchiveEntry tarEntry = new ZipArchiveEntry(f, entryName);
                tarEntry.setTime(f.lastModified());
                zipOutputStream.putArchiveEntry(tarEntry);
                if (f.isFile()) {
                    in = new BufferedInputStream(new FileInputStream(f), BUFFER_8192);
                    IOUtils.copy(in, zipOutputStream, BUFFER_8192);
                }
                zipOutputStream.closeArchiveEntry();
            }
            zipOutputStream.finish();
            zipOutputStream.flush();

            return this;

        } catch (final IOException e) {
            throw new CompressException("(ZIP) Error while compressing the resource '" + from + "' to '" + to + "'", e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(in, zipOutputStream);
        }
    }

    /**
     * <span style='color:red'><b>NOTE</b></span> : Symbolic links are not
     * treated.
     * 
     * @throws IllegalArgumentException If the specified .zip file to uncompress
     *             is not a file, or if the the specified destination is a file.
     */
    // CHECKSTYLE:OFF
    @Override
    public ZipTool decompress(final File zipFile, final File destDir) throws CompressException {
        // CHECKSTYLE:ON
        if (!zipFile.isFile() || !zipFile.canRead()) {
            throw new IllegalArgumentException(
                    "The specified resource to decompress is not a file or cannot be read : " + zipFile);
        }
        if (destDir.isFile()) {
            throw new IllegalArgumentException(
                    "The specified destination is expected to be a directory, but is a file : " + destDir);
        }
        OutputStream out = null;
        ZipArchiveInputStream zipInputStream = null;
        try {
            if (!destDir.isDirectory()) FileUtils.forceMkdir(destDir);
            zipInputStream = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
            ZipArchiveEntry entry = null;
            while ((entry = zipInputStream.getNextZipEntry()) != null) {
                final String entryName = entry.getName();
                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(new File(destDir, entryName));
                } else {
                    out = new BufferedOutputStream(new FileOutputStream(new File(destDir, entryName)), BUFFER_8192);
                    final int entrySize = (int) entry.getSize();
                    final byte[] content = new byte[entrySize];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(content, 0, entrySize)) != -1) {
                        out.write(content, 0, bytesRead);
                    }
                    out.flush();
                }
            }
            return this;
        } catch (final IOException e) {
            throw new CompressException("(ZIP) Error while uncompressing the file '" + zipFile + "'", e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(zipInputStream, out);
        }
    }

    @Override
    public ZipTool addResources(final File zipFile, final File baseDir, final List<File> resourcesToAdd)
            throws CompressException {
        File tempFile = null;
        InputStream in = null;
        ZipArchiveOutputStream zipOutputStream = null;
        ZipArchiveInputStream zipInputStream = null;

        try {
            tempFile = new File(CommonUtils.createTempFilename("ziptool_", "_tempfile.zip"));
            FileUtils.copyFile(zipFile, tempFile);

            zipOutputStream = new ZipArchiveOutputStream(tempFile);
            zipInputStream = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile),
                    BUFFER_8192));

            // First, let's read the already current tar entries contained into
            // the .zip file.
            final ZipFile zFile = new ZipFile(zipFile, null);
            final Enumeration<ZipArchiveEntry> previousEntries = zFile.getEntries();
            InputStream tempEntryStream = null;
            try {
                while (previousEntries.hasMoreElements()) {
                    final ZipArchiveEntry ze = previousEntries.nextElement();
                    zipOutputStream.putArchiveEntry(ze);
                    if (!ze.isDirectory()) {
                        tempEntryStream = zFile.getInputStream(ze);
                        IOUtils.copy(tempEntryStream, zipOutputStream, BUFFER_8192);
                    }
                    zipOutputStream.closeArchiveEntry();
                }
            } finally {
                CommonUtils.closeStreamQuietly(tempEntryStream);
            }

            final List<File> allNewResourcesToAdd = new ArrayList<File>();
            for (final File f : resourcesToAdd) {
                allNewResourcesToAdd.add(f);
                if (f.isDirectory()) {
                    allNewResourcesToAdd.addAll(this.getAllChildren(f));
                }
            }

            // Let's add the new entries
            for (final File f : allNewResourcesToAdd) {
                String entryName = this.stripBaseDirPath(baseDir, f);
                if (f.isDirectory()) entryName += "/";
                final ZipArchiveEntry zipEntry = new ZipArchiveEntry(f, entryName);
                zipEntry.setTime(f.lastModified());
                zipOutputStream.putArchiveEntry(zipEntry);
                if (f.isFile()) {
                    in = new BufferedInputStream(new FileInputStream(f), BUFFER_8192);
                    IOUtils.copy(in, zipOutputStream, BUFFER_8192);
                }
                zipOutputStream.closeArchiveEntry();
            }
            zipOutputStream.finish();
            zipOutputStream.flush();

            FileUtils.copyFile(tempFile, zipFile);

            return this;

        } catch (final IOException e) {
            throw new CompressException("(ZIP) Error while adding resource to the zipFile : " + zipFile, e);
        } finally {
            FileUtils.deleteQuietly(tempFile);
            CommonUtils.closeAllStreamsQuietly(zipOutputStream, zipInputStream, in);
        }
    }

    @Override
    public List<String> list(final File zipFile) throws CompressException {
        final List<String> entriesNames = new LinkedList<String>();
        final ZipArchiveInputStream zipInputStream = null;
        try {
            final ZipFile zFile = new ZipFile(zipFile, null);
            final Enumeration<ZipArchiveEntry> entries = zFile.getEntries();
            while (entries.hasMoreElements()) {
                final ZipArchiveEntry currentEntry = entries.nextElement();
                entriesNames.add(currentEntry.getName());
            }
        } catch (final IOException e) {
            throw new CompressException("(ZIP) Error while listing the content of the tar file '" + zipFile + "'", e);
        } finally {
            CommonUtils.closeStreamQuietly(zipInputStream);
        }
        return entriesNames;
    }

    @Override
    public CompressionHandler<?> getAdapter(final Class<CompressionHandler<?>> clazz) {
        return null;
    }

    @Override
    public boolean canCompressDirectories() {
        return true;
    }

    @Override
    public boolean canAddResources() {
        return true;
    }

    @Override
    public String getConventionalExtension() {
        return ZIP_EXTENSION;
    }

    @Override
    protected final String getCompressorType() {
        return InternalCompressorStreamFactory.ZIP;
    }

}
