package net.paissad.paissadtools.ftp.exception;

import net.paissad.paissadtools.api.IToolException;

/**
 * This exception is thrown by the FTP Tool.
 * 
 * @author paissad
 */
public class FtpToolException extends IToolException {

    private static final long serialVersionUID = 1L;

    public FtpToolException() {
        this("");
    }

    public FtpToolException(final String message) {
        super(message);
    }

    public FtpToolException(final Throwable cause) {
        super(cause);
    }

    public FtpToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
