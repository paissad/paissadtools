package net.paissad.paissadtools.compress;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import net.paissad.paissadtools.util.CommonUtils;

public final class TestUtil {

    private static final File TEMP_DIR;

    static {
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
        try {
            TEMP_DIR = CommonUtils.createTempDir("__TestUtil__PaissadTool__Compress__", "__tempdir__");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private TestUtil() {
    }

    public static File getTempDir() {
        return TEMP_DIR;
    }

    private static class ShutDownHook extends Thread {

        ShutDownHook() {
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void run() {
            FileUtils.deleteQuietly(TestUtil.TEMP_DIR);
        }
    }

}
