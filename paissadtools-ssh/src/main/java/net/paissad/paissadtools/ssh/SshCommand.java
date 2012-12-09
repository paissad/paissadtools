package net.paissad.paissadtools.ssh;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * This class holds the ssh command and the options related to a specific the
 * command itself.
 * 
 * @author paissad
 */
@Getter
@Setter
public class SshCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            command;
    private int               timeout;

    public SshCommand() {
        this(null);
    }

    public SshCommand(final String command) {
        this(command, 0);
    }

    public SshCommand(final String command, final int timeout) {
        this.command = command;
        this.timeout = timeout;
    }

}
