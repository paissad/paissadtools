package net.paissad.paissadtools.ssh;

import net.paissad.paissadtools.ssh.SshSettings.SSH_PROVIDER;
import net.paissad.paissadtools.ssh.impl.SshGanymed;
import net.paissad.paissadtools.ssh.impl.SshJsch;

/**
 * @author paissad
 * @since 0.1
 */
class SshFactory {

    private SshFactory() {
    }

    /**
     * @param sshSettings
     * @return An instance of {@link Ssh} object.
     */
    static Ssh getSsh(final SshSettings sshSettings) {

        if (sshSettings == null) throw new IllegalArgumentException("The ssh settings cannot be null.");

        final SSH_PROVIDER provider = sshSettings.getSshProvider();

        switch (provider) {
        case JSCH:
            return new SshJsch(sshSettings);
        case GANYMED:
            return new SshGanymed(sshSettings);
        default:
            throw new IllegalArgumentException("The specified SSH provider [" + provider + "] is not supported !");
        }
    }

}
