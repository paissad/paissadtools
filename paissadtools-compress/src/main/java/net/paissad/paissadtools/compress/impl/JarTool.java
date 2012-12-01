package net.paissad.paissadtools.compress.impl;

public class JarTool extends ZipTool {

    public static final String JAR_EXTENSION = ".jar";

    @Override
    public String getConventionalExtension() {
        return JAR_EXTENSION;
    }

}
