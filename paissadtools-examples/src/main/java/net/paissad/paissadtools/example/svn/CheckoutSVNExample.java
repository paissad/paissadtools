package net.paissad.paissadtools.example.svn;

import java.io.File;

import net.paissad.paissadtools.svn.SvnTool;
import net.paissad.paissadtools.svn.exception.SVNToolException;

/**
 * 'svn checkout' code example.
 * 
 * @author paissad
 */
public class CheckoutSVNExample {

    /*
     * Basically, execute a 'svn checkout' command into a specific directory different from the current working
     * directory.
     */
    public static void main(final String[] args) throws IllegalArgumentException, SVNToolException {

        final SvnTool svnTool = new SvnTool();

        // Let's do the checkout into the temporary directory of the system.

        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        System.out.println("Changing to directory : " + tmpDir);
        svnTool.cd(tmpDir);

        final int exitCode = svnTool.execute(new String[] { "checkout", "https://svn.paissad.net/misc/stuffs",
                "my_svn_tool_checkout" }, System.out, System.err);
        if (exitCode != SvnTool.EXIT_CODE_SUCCESS) {
            throw new SVNToolException("The exit code status is not ok (" + exitCode + ")");
        }
    }

}
