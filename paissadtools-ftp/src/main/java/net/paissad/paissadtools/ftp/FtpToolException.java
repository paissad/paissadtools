package net.paissad.paissadtools.ftp;

import net.paissad.paissadtools.exception.IToolServiceException;

/**
 * 
 * @author paissad
 */
public class FtpToolException extends IToolServiceException {

    private static final long serialVersionUID = 1L;

    public FtpToolException() {
        this("");
    }

    public FtpToolException(String message) {
        super(message);
    }

    public FtpToolException(Throwable cause) {
        super(cause);
    }

    public FtpToolException(String message, Throwable cause) {
        super(message, cause);
    }

}
