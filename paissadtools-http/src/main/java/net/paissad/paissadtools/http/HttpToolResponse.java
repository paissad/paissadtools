package net.paissad.paissadtools.http;

import java.io.InputStream;
import java.io.Serializable;

import lombok.Getter;

/**
 * Represents the HTTP response of after an operation is finished by the
 * {@link HttpTool}.<br>
 * The instance of this class is immutable.
 * 
 * @author paissad
 */
@Getter
public class HttpToolResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int         statusCode;
    private final String      statusMessage;
    private final InputStream responseBody;

    public HttpToolResponse(final int statusCode, final String statusMessage, final InputStream responseBody) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = responseBody;
    }

}
