package net.paissad.paissadtools.api;

/**
 * This is the parent exception of all exceptions thrown by a tool {@link ITool}
 * .
 * 
 * @author paissad
 */
public class IToolException extends Exception {

    private static final long serialVersionUID = 1L;

    public IToolException() {
        this("");
    }

    public IToolException(final String message) {
        super(message);
    }

    public IToolException(final Throwable cause) {
        super(cause);
    }

    public IToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
