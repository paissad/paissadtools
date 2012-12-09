package net.paissad.paissadtools.mail.exception;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.mail.MailTool;

/**
 * This exception is thrown by the mail {@link ITool} -> {@link MailTool}.
 * 
 * @author paissad
 */
public class MailToolException extends Exception {

    private static final long serialVersionUID = 1L;

    public MailToolException() {
        this("");
    }

    public MailToolException(final String message) {
        super(message);
    }

    public MailToolException(final Throwable cause) {
        super(cause);
    }

    public MailToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
