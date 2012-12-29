package net.paissad.paissadtools.diff.impl.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.paissad.paissadtools.diff.api.IDiffResult;
import net.paissad.paissadtools.diff.api.IDiffTool;
import net.paissad.paissadtools.diff.exception.DiffToolException;
import net.paissad.paissadtools.diff.impl.DiffResult;
import net.paissad.paissadtools.diff.util.IOUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paissad
 */
public class FileDiffTool implements IDiffTool<File, FileDiffOption> {

    private static Logger       logger   = LoggerFactory.getLogger(FileDiffTool.class);

    private static final String LINE_SEP = System.getProperty("line.separator");

    @Override
    public IDiffResult compare(final File file1, final File file2, final FileDiffOption option)
            throws DiffToolException {

        if (option != null && option.getExclusionRules() != null) {
            for (final FileDiffExclusionRule exclusionRule : option.getExclusionRules()) {
                if (exclusionRule.match(file1) || exclusionRule.match(file2)) {
                    final DiffResult diffResult = new DiffResult();
                    diffResult.setExcluded(true);
                    diffResult
                            .setReport(file1 + " and " + file2 + " won't be compared due to exclusion pattern match.");
                    return diffResult;
                }
            }
        }

        IDiffResult result = null;
        if (file1.isDirectory() && file2.isDirectory()) {
            final List<IDiffResult> allResults = this.compareDirs(file1, file2, option);
            boolean similar = true;
            final StringBuilder sb = new StringBuilder();
            for (final IDiffResult oneResult : allResults) {
                similar = similar && oneResult.similar();
                sb.append(LINE_SEP).append(oneResult.getReport());
            }
            return new DiffResult(similar, sb.toString());
        } else if (file1.isFile() && file2.isFile()) {
            result = this.compareFiles(file1, file2, option);
        } else {
            final String report = this.getFileTypeStatus(file1) + ", but " + this.getFileTypeStatus(file2) + ".";
            result = new DiffResult(false, report);
        }
        return result;
    }

    private IDiffResult compareFiles(final File file1, final File file2, final FileDiffOption option)
            throws DiffToolException {

        try {
            if (option != null && !option.isVerbose() && file1.length() != file2.length()) {
                final String report = this.getQuietReport(file1, file2, false);
                return new DiffResult(false, report);
            }
            return this.compareFileContents(file1, file2, option);

        } catch (final Exception e) {
            throw new DiffToolException(
                    "Error while comparing '" + file1 + "' and '" + file2 + "' : " + e.getMessage(), e);
        }
    }

    private List<IDiffResult> compareDirs(final File dir1, final File dir2, final FileDiffOption option)
            throws DiffToolException {

        final List<IDiffResult> allResults = new ArrayList<IDiffResult>();

        List<FileDiffExclusionRule> exclusionRules = null;
        if (option != null) {
            exclusionRules = option.getExclusionRules();
        }
        if (exclusionRules == null) exclusionRules = Collections.EMPTY_LIST;

        for (final FileDiffExclusionRule exclusionRule : exclusionRules) {
            if (exclusionRule.match(dir1) || exclusionRule.match(dir2)) {
                final DiffResult diffResult = new DiffResult();
                diffResult.setExcluded(true);
                diffResult.setReport(dir1 + " and " + dir2 + " won't be compared due to exclusion pattern match.");
                allResults.add(diffResult);
                return allResults;
            }
        }

        final Map<String, File> subFiles1 = new TreeMap<String, File>();
        final Map<String, File> subFiles2 = new TreeMap<String, File>();

        for (final File f : dir1.listFiles()) {
            subFiles1.put(f.getName(), f);
        }
        for (final File f : dir2.listFiles()) {
            subFiles2.put(f.getName(), f);
        }

        // We apply the exclusion rules onto both directories.
        for (final FileDiffExclusionRule exclusionRule : exclusionRules) {
            final Iterator<Entry<String, File>> iter1 = subFiles1.entrySet().iterator();
            while (iter1.hasNext()) {
                final File f = iter1.next().getValue();
                if (exclusionRule.match(f)) iter1.remove();
            }
            final Iterator<Entry<String, File>> iter2 = subFiles2.entrySet().iterator();
            while (iter2.hasNext()) {
                final File f = iter2.next().getValue();
                if (exclusionRule.match(f)) iter2.remove();
            }
        }

        // We start the directories comparison.
        final Iterator<Entry<String, File>> iter1 = subFiles1.entrySet().iterator();
        while (iter1.hasNext()) {
            final Entry<String, File> entry1 = iter1.next();
            final String filename1 = entry1.getKey();
            final File file1 = entry1.getValue();

            final Iterator<Entry<String, File>> iter2 = subFiles2.entrySet().iterator();
            boolean found = false;
            while (!found && iter2.hasNext()) {
                final Entry<String, File> entry2 = iter2.next();
                final String filename2 = entry2.getKey();
                final File file2 = entry2.getValue();

                if (filename1.equals(filename2)) {
                    found = true;
                    if (file1.isFile() && file2.isFile()) {
                        allResults.add(this.compare(file1, file2, option));
                    } else if (file1.isDirectory() && file2.isDirectory()) {
                        allResults.addAll(this.compareDirs(file1, file2, option));
                    } else {
                        final String report = this.getFileTypeStatus(file1) + ", but " + this.getFileTypeStatus(file2)
                                + ".";
                        allResults.add(new DiffResult(false, report));
                    }
                }
            }
            if (!found) {
                final String report = "Only in " + dir1 + " : " + filename1;
                allResults.add(new DiffResult(false, report.toString()));
            }
        }

        // We look for files present in the latter directory, but not in the former one.
        final Iterator<Entry<String, File>> iter2 = subFiles2.entrySet().iterator();
        while (iter2.hasNext()) {
            final Entry<String, File> entry2 = iter2.next();
            final String filename2 = entry2.getKey();
            boolean found = false;
            for (final File f : dir1.listFiles()) {
                if (filename2.equals(f.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                final String report = "Only in " + dir2 + " : " + filename2;
                allResults.add(new DiffResult(false, report));
            }
        }

        return allResults;
    }

    private IDiffResult compareFileContents(final File f1, final File f2, final FileDiffOption option)
            throws IOException {

        final RandomAccessFile raf1 = new RandomAccessFile(f1, "r");
        final RandomAccessFile raf2 = new RandomAccessFile(f2, "r");
        final FileChannel channel1 = raf1.getChannel();
        final FileChannel channel2 = raf2.getChannel();

        try {

            final int bufferSize = 10 * 1024;
            // Both byte buffers must have the same file for good comparison.
            final ByteBuffer buf1 = ByteBuffer.allocate(bufferSize);
            final ByteBuffer buf2 = ByteBuffer.allocate(bufferSize);

            boolean similar = true;
            boolean eof = false;

            if (option != null && option.isVerbose()) {
                logger.warn("VERBOSE option is not yet supported for file comparison");
            }

            while (similar && !eof) {
                final int bytesRead1 = channel1.read(buf1);
                final int bytesRead2 = channel2.read(buf2);

                similar = Arrays.equals(buf1.array(), buf2.array());
                eof = bytesRead1 != -1 && bytesRead2 != -1;
            }

            final String report = this.getQuietReport(f1, f2, similar);

            return new DiffResult(similar, report);

        } finally {
            IOUtil.closeQuietly(raf1, raf2, channel1, channel2);
        }
    }

    private String getQuietReport(final File f1, final File f2, final boolean similar) {
        if (similar) {
            return f1 + " and " + f2 + " are the same.";
        }
        return f1 + " and " + f2 + " differ.";
    }

    private String getFileTypeStatus(final File f) {
        if (f.isFile()) {
            return f + " is a file";
        } else if (f.isDirectory()) {
            return f + " is a directory";
        }
        return f + " has an unknown type";
    }

}
