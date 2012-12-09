package net.paissad.paissadtools.xml;

import net.paissad.paissadtools.exception.IToolServiceException;

/**
 * This exception is thrown by the {@link XmlTool}.
 * 
 * @author paissad
 */
public class XmlServiceException extends IToolServiceException {

    private static final long serialVersionUID = 1L;

    public XmlServiceException() {
        this("");
    }

    public XmlServiceException(final String message) {
        super(message);
    }

    public XmlServiceException(final Throwable cause) {
        super(cause);
    }

    public XmlServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
