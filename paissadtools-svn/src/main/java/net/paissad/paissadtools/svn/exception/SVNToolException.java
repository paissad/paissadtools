package net.paissad.paissadtools.svn.exception;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.svn.SvnTool;

/**
 * This exception is thrown by the SVN {@link ITool} -> {@link SvnTool}.
 * 
 * @author paissad
 */
public class SVNToolException extends Exception {

    private static final long serialVersionUID = 1L;

    public SVNToolException() {
        this("");
    }

    public SVNToolException(final String message) {
        super(message);
    }

    public SVNToolException(final Throwable cause) {
        super(cause);
    }

    public SVNToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
