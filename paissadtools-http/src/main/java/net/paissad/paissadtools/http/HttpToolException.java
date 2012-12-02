package net.paissad.paissadtools.http;

public class HttpToolException extends Exception {

    private static final long serialVersionUID = 1L;

    public HttpToolException() {
        this("");
    }

    public HttpToolException(String message) {
        super(message);
    }

    public HttpToolException(Throwable cause) {
        super(cause);
    }

    public HttpToolException(String message, Throwable cause) {
        super(message, cause);
    }

}
