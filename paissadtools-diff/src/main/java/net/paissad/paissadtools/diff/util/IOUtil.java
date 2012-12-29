package net.paissad.paissadtools.diff.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author paissad
 */
public final class IOUtil {

    private IOUtil() {
    }

    public static void closeQuietly(final Closeable... closeables) {
        for (final Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) { // do nothing
                }
            }
        }
    }

}
