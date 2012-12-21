package net.paissad.paissadtools.example.svn;

import net.paissad.paissadtools.svn.SvnTool;
import net.paissad.paissadtools.svn.exception.SVNToolException;

/**
 * 'svn info' code example.
 * 
 * @author paissad
 */
public final class InfoSVNExample {

    /*
     * Basically, execute the 'svn info' command, verify the exit status code and print the results into the standard
     * outputs.
     */
    public static void main(final String[] args) throws IllegalArgumentException, SVNToolException {
        final SvnTool svnTool = new SvnTool();
        final int exitCode = svnTool.execute(new String[] { "info", "https://svn.paissad.net/misc/stuffs" },
                System.out, System.err);
        if (exitCode != SvnTool.EXIT_CODE_SUCCESS) {
            throw new SVNToolException("The exit code status is not ok (" + exitCode + ")");
        }
    }

}
