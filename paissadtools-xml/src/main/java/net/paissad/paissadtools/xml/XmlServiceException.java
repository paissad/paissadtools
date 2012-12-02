package net.paissad.paissadtools.xml;

import net.paissad.paissadtools.exception.IToolServiceException;

public class XmlServiceException extends IToolServiceException {

    private static final long serialVersionUID = 1L;

    public XmlServiceException() {
        this("");
    }

    public XmlServiceException(String message) {
        super(message);
    }

    public XmlServiceException(Throwable cause) {
        super(cause);
    }

    public XmlServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
