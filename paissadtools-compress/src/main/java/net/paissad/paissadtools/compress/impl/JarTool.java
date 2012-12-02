package net.paissad.paissadtools.compress.impl;

/**
 * Jar tool.
 * 
 * @author paissad
 * @see ZipTool
 */
public class JarTool extends ZipTool {

    public static final String JAR_EXTENSION = ".jar";

    @Override
    public String getConventionalExtension() {
        return JAR_EXTENSION;
    }

}
