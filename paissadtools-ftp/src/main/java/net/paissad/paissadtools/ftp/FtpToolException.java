package net.paissad.paissadtools.ftp;

import net.paissad.paissadtools.exception.IToolServiceException;

/**
 * This exception is thrown by the FTP Tool.
 * 
 * @author paissad
 */
public class FtpToolException extends IToolServiceException {

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
