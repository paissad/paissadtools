package net.paissad.paissadtools.diff.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author paissad
 */
public final class DiffUtil {

    private DiffUtil() {
    }

    /**
     * Checks whether or not a file is a text file or a binary one.
     * 
     * @param file - The file to check.
     * @return <tt>true</tt> if the File is a text file, <tt>false</tt> otherwise.
     * @throws IOException I/O error.
     * @throws IllegalArgumentException If the file is <code>null</code> or is not a file.
     */
    // CHECKSTYLE:OFF
    public static boolean isText(final File file) throws IOException, IllegalArgumentException {

        if (file == null || !file.isFile()) throw new IllegalArgumentException("Must not be null & must be a file.");
        RandomAccessFile raf = null;

        try {
            raf = new RandomAccessFile(file, "r");
            int numberOfNonTextChars = 0;
            while (raf.getFilePointer() < raf.length()) {
                final int b = raf.readUnsignedByte();
                // http://www.table-ascii.com/
                if (b == 0x09 || // horizontal tabulation
                        b == 0x0A || // line feed
                        b == 0x0C || // form feed
                        b == 0x0D || // carriage return
                        (b >= 0x20 && b <= 0x7E) || // "normal" characters
                        (b >= 0x80 && b <= 0x9F) || // latin-1 symbols
                        (b >= 0xA0 && b <= 0xFF)) // latin-1 symbols
                {
                    // OK
                } else {
                    numberOfNonTextChars++;
                }
            }
            return numberOfNonTextChars <= 2 && (raf.length() - numberOfNonTextChars / raf.length()) >= 0.99;

        } finally {
            IOUtil.closeQuietly(raf);
        }
    }
    // CHECKSTYLE:ON

}
