package net.paissad.paissadtools.ssh;

import java.io.Serializable;

/**
 * @see SshErrorEvent
 * @author paissad
 */
public interface SshErrorListener extends Serializable {

    public void onError(SshErrorEvent sshErrorEvent);
}
