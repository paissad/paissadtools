package net.paissad.paissadtools.ssh.exception;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolException;
import net.paissad.paissadtools.ssh.SshTool;

/**
 * This exception is thrown by the ssh {@link ITool} -> {@link SshTool}.
 * 
 * @author paissad
 */
public class SshToolException extends IToolException {

    private static final long serialVersionUID = 1L;

    public SshToolException() {
        this("");
    }

    public SshToolException(final String message) {
        super(message);
    }

    public SshToolException(final Throwable cause) {
        super(cause);
    }

    public SshToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
