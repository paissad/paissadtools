package net.paissad.paissadtools.diff.exception;

import net.paissad.paissadtools.api.IToolException;

/**
 * This exception is thrown by the Diff Tool.
 * 
 * @author paissad
 */
public class DiffToolException extends IToolException {

    private static final long serialVersionUID = 1L;

    public DiffToolException() {
        super();
    }

    public DiffToolException(final String message) {
        super(message);
    }

    public DiffToolException(final Throwable cause) {
        super(cause);
    }

    public DiffToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
