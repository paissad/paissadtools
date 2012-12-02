package net.paissad.paissadtools.exception;

public class IToolServiceRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IToolServiceRuntimeException() {
        this("");
    }

    public IToolServiceRuntimeException(String message) {
        super(message);
    }

    public IToolServiceRuntimeException(Throwable cause) {
        super(cause);
    }

    public IToolServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
