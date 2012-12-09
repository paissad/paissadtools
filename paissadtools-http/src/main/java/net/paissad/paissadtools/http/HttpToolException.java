package net.paissad.paissadtools.http;

import net.paissad.paissadtools.api.ITool;

/**
 * This exception is thrown by the HTTP {@link ITool} -> {@link HttpTool}.
 * 
 * @author paissad
 */
public class HttpToolException extends Exception {

    private static final long serialVersionUID = 1L;

    public HttpToolException() {
        this("");
    }

    public HttpToolException(final String message) {
        super(message);
    }

    public HttpToolException(final Throwable cause) {
        super(cause);
    }

    public HttpToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
