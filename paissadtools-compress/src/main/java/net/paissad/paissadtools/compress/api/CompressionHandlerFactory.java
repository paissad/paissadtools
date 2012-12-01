package net.paissad.paissadtools.compress.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.paissad.paissadtools.compress.impl.Bzip2Tool;
import net.paissad.paissadtools.compress.impl.GzipTool;
import net.paissad.paissadtools.compress.impl.JarTool;
import net.paissad.paissadtools.compress.impl.TarTool;
import net.paissad.paissadtools.compress.impl.XZTool;
import net.paissad.paissadtools.compress.impl.ZipTool;

public class CompressionHandlerFactory {

    /**
     * All implementation classes of CompressionHandler are declared here.<br>
     * <b>This list is immutable.</b>
     */
    static final List<Class<?>>       compressionHandlerClasses;

    private static final List<String> supportedExtensions;

    static {
        compressionHandlerClasses = Collections.unmodifiableList(Arrays.asList(new Class<?>[] {
            GzipTool.class,
            Bzip2Tool.class,
            XZTool.class,
            TarTool.class,
            ZipTool.class,
            JarTool.class
        }));
        final List<String> extensions = new ArrayList<String>();
        for (final Class<?> toolClass : compressionHandlerClasses) {
            try {
                final CompressionHandler<?> tool = (CompressionHandler<?>) toolClass.newInstance();
                extensions.add(tool.getConventionalExtension());
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        supportedExtensions = Collections.unmodifiableList(extensions);
    }

    private CompressionHandlerFactory() {
    }

    public static CompressionHandlerFactory newInstance() {
        return new CompressionHandlerFactory();
    }

    /**
     * <b>NOTE</b> : the guess is based on the file's extension.
     * 
     * @param compressedFile
     * @return The instance of the correct tool / {@link CompressionHandler} to
     *         use for the specified file.
     * @throws CompressException If the specified file has an extension which is
     *             not known by any of the compression tools available in this
     *             library.
     */
    public CompressionHandler<?> getCompressionHandler(final File compressedFile) throws CompressException {
        for (final Class<?> toolClass : compressionHandlerClasses) {
            try {
                final CompressionHandler<?> tool = (CompressionHandler<?>) toolClass.newInstance();
                if (compressedFile.getName().endsWith(tool.getConventionalExtension())) {
                    return tool;
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        throw new CompressException("The file '" + compressedFile
                + "' has an unknown extension, unable to get the correct compress tool.");
    }

    /**
     * @return The list of supported extensions. The returned list is
     *         unmodifiable.
     */
    public static List<String> getSupportedExtensions() {
        return supportedExtensions;
    }

}
