package net.paissad.paissadtools.exception;

import net.paissad.paissadtools.api.IToolService;

/**
 * Exception generally thrown when a problem occurs with a {@link IToolService}.
 * 
 * @author paissad
 */
public class IToolServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public IToolServiceException() {
        this("");
    }

    public IToolServiceException(final String message) {
        super(message);
    }

    public IToolServiceException(final Throwable cause) {
        super(cause);
    }

    public IToolServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
