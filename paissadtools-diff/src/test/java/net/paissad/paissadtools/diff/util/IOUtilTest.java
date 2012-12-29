package net.paissad.paissadtools.diff.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;

import org.junit.Test;

public class IOUtilTest {

    @Test(expected = ClosedChannelException.class)
    public final void testCloseQuietly() throws IOException {
        final File f = File.createTempFile("prefix_", "_suffix");
        try {
            final FileChannel channel = new RandomAccessFile(f, "r").getChannel();
            IOUtil.closeQuietly(channel);
            channel.lock(); // no further operation can be done, ... exception will be thrown here !
        } finally {
            f.delete();
        }
    }

    @Test
    public final void testCloseQuietly_Null() {
        IOUtil.closeQuietly(null, null);
    }

    @Test
    public final void testCloseQuietly_Already_Closed() throws IOException {
        final File f = File.createTempFile("prefix_", "_suffix");
        try {
            FileWriter fileWriter = new FileWriter(f);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Hello World");
            fileWriter.close();
            IOUtil.closeQuietly(bufferedWriter); // The internal exception thrown while closing will be caught.
        } finally {
            f.delete();
        }
    }

}
