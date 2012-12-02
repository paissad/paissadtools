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
import java.util.LinkedList;
import java.util.List;

import net.paissad.paissadtools.compress.api.CompressException;
import net.paissad.paissadtools.compress.api.CompressionHandler;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

/**
 * Tar / untar tool.
 * 
 * @author paissad
 */
public class TarTool extends AbstractCompressionHandler<TarTool> {

    public static final String TAR_EXTENSION = ".tar";

    /**
     * @throws IllegalArgumentException If the specified destination is an
     *             existing directory. (If the destination does already exist,
     *             it must be a file so that it can be overwritten correctly.
     */
    @Override
    public TarTool compress(final File from, final File baseDir, final File to) throws CompressException {
        if (to.isDirectory()) {
            throw new IllegalArgumentException("(TAR) The resulting tar file cannot be a directory : " + to);
        }
        TarArchiveOutputStream tarOutputStream = null;
        InputStream in = null;
        try {
            tarOutputStream = new TarArchiveOutputStream(
                    new BufferedOutputStream(new FileOutputStream(to), BUFFER_8192), BUFFER_8192);

            // The list of resources to add into the .tar file to create.
            final List<File> resourcesToCompress = new ArrayList<File>();
            resourcesToCompress.add(from);
            if (from.isDirectory()) {
                resourcesToCompress.addAll(this.getAllChildren(from));
            } else {
                // only files & directories are currently treated !
            }

            for (final File f : resourcesToCompress) {
                final TarArchiveEntry tarEntry = new TarArchiveEntry(f);
                String entryName = this.stripBaseDirPath(baseDir, f);
                if (f.isDirectory()) entryName += "/";
                tarEntry.setName(entryName);
                tarEntry.setModTime(f.lastModified());
                tarOutputStream.putArchiveEntry(tarEntry);
                if (f.isFile()) {
                    in = new BufferedInputStream(new FileInputStream(f), BUFFER_8192);
                    IOUtils.copy(in, tarOutputStream, BUFFER_8192);
                }
                tarOutputStream.closeArchiveEntry();
            }
            tarOutputStream.finish();
            tarOutputStream.flush();

            return this;

        } catch (IOException e) {
            throw new CompressException("(TAR) Error while compressing the resource '" + from + "' to '" + to + "'", e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(tarOutputStream, in);
        }
    }

    /**
     * <span style='color:red'><b>NOTE</b></span> : Symbolic links are not
     * treated.
     * 
     * @throws IllegalArgumentException If the specified .tar file to uncompress
     *             is not a file, or if the the specified destination is a file.
     */
    @Override
    public TarTool decompress(final File tarFile, final File destDir) throws CompressException {
        if (!tarFile.isFile() || !tarFile.canRead()) {
            throw new IllegalArgumentException(
                    "The specified resource to decompress is not a file or cannot be read : " + tarFile);
        }
        if (destDir.isFile()) {
            throw new IllegalArgumentException(
                    "The specified destination is expected to be a directory, but is a file : " + destDir);
        }
        OutputStream out = null;
        TarArchiveInputStream tarInputStream = null;
        try {
            if (!destDir.isDirectory()) FileUtils.forceMkdir(destDir);
            tarInputStream = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile)));
            TarArchiveEntry entry = null;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                final String entryName = entry.getName();
                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(new File(destDir, entryName));
                } else if (entry.isSymbolicLink()) {
                    // we do not treat symbolic links ...
                } else {
                    out = new BufferedOutputStream(new FileOutputStream(new File(destDir, entryName)), BUFFER_8192);
                    final int entrySize = (int) entry.getSize();
                    byte[] content = new byte[entrySize];
                    int bytesRead;
                    while ((bytesRead = tarInputStream.read(content, 0, entrySize)) != -1) {
                        out.write(content, 0, bytesRead);
                    }
                    out.flush();
                }
            }
            return this;
        } catch (IOException e) {
            throw new CompressException("(TAR) Error while uncompressing the file '" + tarFile + "'", e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(tarInputStream, out);
        }
    }

    @Override
    public TarTool addResources(final File tarFile, final File baseDir, final List<File> resourcesToAdd)
            throws CompressException {

        File backupFile = null;
        InputStream in = null;
        TarArchiveOutputStream tarOutputStream = null;
        TarArchiveInputStream tarInputStream = null;

        try {
            backupFile = new File(CommonUtils.createTempFilename("tartool_", "_tempfile.tar"));
            FileUtils.copyFile(tarFile, backupFile);

            tarOutputStream = new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(tarFile),
                    BUFFER_8192), BUFFER_8192);
            tarInputStream = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile),
                    BUFFER_8192), BUFFER_8192);

            // First, let's read the already current tar entries contained into
            // the tar file.
            TarArchiveEntry tarEntry = null;
            while ((tarEntry = tarInputStream.getNextTarEntry()) != null) {
                tarOutputStream.putArchiveEntry(tarEntry);
                tarOutputStream.closeArchiveEntry();
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
                tarEntry = new TarArchiveEntry(f);
                tarEntry.setName(entryName);
                tarEntry.setModTime(f.lastModified());
                tarOutputStream.putArchiveEntry(tarEntry);
                if (f.isFile()) {
                    in = new BufferedInputStream(new FileInputStream(f), BUFFER_8192);
                    IOUtils.copy(in, tarOutputStream, BUFFER_8192);
                }
                tarOutputStream.closeArchiveEntry();
            }
            tarOutputStream.finish();
            tarOutputStream.flush();

            return this;

        } catch (IOException e) {
            try {
                if (backupFile != null && backupFile.exists()) {
                    FileUtils.deleteQuietly(tarFile);
                    FileUtils.moveFile(backupFile, tarFile);
                }
            } catch (IOException e2) {
            }
            throw new CompressException("(TAR) Error while adding resource to the tarFile : " + tarFile, e);
        } finally {
            CommonUtils.closeAllStreamsQuietly(tarOutputStream, tarInputStream, in);
            FileUtils.deleteQuietly(backupFile);
        }
    }

    @Override
    public List<String> list(final File tarFile) throws CompressException {
        final List<String> contents = new LinkedList<String>();
        TarArchiveInputStream tarStream = null;
        try {
            tarStream = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tarFile), BUFFER_8192));
            TarArchiveEntry entry = null;
            while ((entry = tarStream.getNextTarEntry()) != null) {
                contents.add(entry.getName());
            }
        } catch (IOException e) {
            throw new CompressException("(TAR) Error while listing the content of the tar file '" + tarFile + "'", e);
        } finally {
            CommonUtils.closeStreamQuietly(tarStream);
        }
        return contents;
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
        return TAR_EXTENSION;
    }
}
