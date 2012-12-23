package net.paissad.paissadtools.xml.exception;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolException;
import net.paissad.paissadtools.xml.XmlTool;

/**
 * This exception is thrown by the xml {@link ITool} -> {@link XmlTool}.
 * 
 * @author paissad
 */
public class XmlToolException extends IToolException {

    private static final long serialVersionUID = 1L;

    public XmlToolException() {
        this("");
    }

    public XmlToolException(final String message) {
        super(message);
    }

    public XmlToolException(final Throwable cause) {
        super(cause);
    }

    public XmlToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
