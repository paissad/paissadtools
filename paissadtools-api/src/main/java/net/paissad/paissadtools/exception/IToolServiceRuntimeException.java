package net.paissad.paissadtools.exception;

import net.paissad.paissadtools.api.IToolService;

/**
 * Unchecked exception generally thrown when a problem occurs with a
 * {@link IToolService}.
 * 
 * @author paissad
 */
public class IToolServiceRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IToolServiceRuntimeException() {
        this("");
    }

    public IToolServiceRuntimeException(final String message) {
        super(message);
    }

    public IToolServiceRuntimeException(final Throwable cause) {
        super(cause);
    }

    public IToolServiceRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
