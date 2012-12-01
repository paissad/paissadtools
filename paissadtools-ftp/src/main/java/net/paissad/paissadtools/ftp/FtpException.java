package net.paissad.paissadtools.ftp;

import net.paissad.paissadtools.exception.ServiceException;

/**
 * 
 * @author paissad
 * @since 0.1
 */
public class FtpException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public FtpException() {
        this("");
    }

    public FtpException(String message) {
        super(message);
    }

    public FtpException(Throwable cause) {
        super(cause);
    }

    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }

}
