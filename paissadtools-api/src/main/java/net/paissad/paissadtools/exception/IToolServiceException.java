package net.paissad.paissadtools.exception;

public class IToolServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public IToolServiceException() {
        this("");
    }

    public IToolServiceException(String message) {
        super(message);
    }

    public IToolServiceException(Throwable cause) {
        super(cause);
    }

    public IToolServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
