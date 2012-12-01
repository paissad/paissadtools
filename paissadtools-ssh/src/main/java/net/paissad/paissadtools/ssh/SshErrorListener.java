package net.paissad.paissadtools.ssh;

import java.io.Serializable;

/**
 * @see SshErrorEvent
 * @author paissad
 * @since 0.1
 */
public interface SshErrorListener extends Serializable {

    public void onError(SshErrorEvent sshErrorEvent);
}
