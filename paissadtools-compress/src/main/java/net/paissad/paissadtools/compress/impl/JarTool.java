package net.paissad.paissadtools.compress.impl;

import net.paissad.paissadtools.compress.CompressionTool;

/**
 * Jar tool.
 * 
 * @author paissad
 * @see ZipTool
 * @see CompressionTool
 */
public class JarTool extends ZipTool {

    /** The default jar extension. */
    public static final String JAR_EXTENSION = ".jar";

    @Override
    public String getConventionalExtension() {
        return JAR_EXTENSION;
    }

}
