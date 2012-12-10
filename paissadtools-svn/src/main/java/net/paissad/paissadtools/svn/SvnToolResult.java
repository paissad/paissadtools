package net.paissad.paissadtools.svn;

import java.io.InputStream;

/**
 * Represents the result after the excecution of a SVN command. This class holds
 * the content of STDOUT, STDERR and the status exit code of the command
 * executed.
 * 
 * @author paissad
 */
public class SvnToolResult {

    private final int         exitCode;
    private final InputStream stdout;
    private final InputStream stderr;

    public SvnToolResult(final int exitCode, final InputStream stdout, final InputStream stderr) {
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    /**
     * @return The exit value. <span style='color:orange'><b>NOTE :
     *         </b></span>By convention, an exit code of '0' means that the
     *         command exited successfully. But it is not entirely guaranteed
     *         this will be always the case.
     */
    public int getExitCode() {
        return this.exitCode;
    }

    /**
     * @return The STDERR content.
     */
    public InputStream getStderr() {
        return this.stderr;
    }

    /**
     * @return The STDOUT content.
     */
    public InputStream getStdout() {
        return this.stdout;
    }

}
