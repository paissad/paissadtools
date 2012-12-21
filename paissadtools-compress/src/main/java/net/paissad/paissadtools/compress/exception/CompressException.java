package net.paissad.paissadtools.compress.exception;

/**
 * This exception is thrown by the compression tool.
 * 
 * @author paissad
 */
public class CompressException extends Exception {

    private static final long serialVersionUID = 1L;

    public CompressException() {
        this("");
    }

    public CompressException(final String message) {
        super(message);
    }

    public CompressException(final Throwable cause) {
        super(cause);
    }

    public CompressException(final String message, final Throwable cause) {
        super(message, cause);
    }

}