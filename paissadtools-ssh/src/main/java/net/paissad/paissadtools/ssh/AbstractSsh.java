package net.paissad.paissadtools.ssh;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractSsh implements SshTool {

    protected static final File SSH_USER_DIR     = new File(System.getProperty("user.home"), ".ssh");

    private static final long   serialVersionUID = 1L;

    private SshToolSettings         sshSettings;

    private SshErrorListener    errorListener;

    protected AbstractSsh(final SshToolSettings sshSettings) {
        this.sshSettings = sshSettings;
    }

    protected File getKnownHostsFile() {
        return new File(SSH_USER_DIR, "known_hosts");
    }

    protected File getPemFile() {
        final File defaultKeyFile = new File(SSH_USER_DIR, "id_rsa").isFile()
                ? new File(SSH_USER_DIR, "id_rsa") : new File(SSH_USER_DIR, "id_dsa");

        final String pemFilePath = this.getSshSettings().getProperties().getProperty(SshToolSettings.PROPS_KEYFILE);
        File pemFile = null;
        if (pemFilePath == null || pemFilePath.trim().isEmpty()) {
            pemFile = defaultKeyFile;
        } else {
            pemFile = new File(pemFilePath);
        }
        return pemFile;
    }

    /**
     * Executes the current {@link SshErrorListener} if it is not <tt>null</tt>.
     * 
     * @param sshErrorEvent
     */
    protected void executeErrorListener(final SshErrorEvent sshErrorEvent) {
        if (sshErrorEvent == null) return;
        this.getErrorListener().onError(sshErrorEvent);
    }

    protected abstract String getImplementationName();

    /**
     * Sets/configures the correct properties in order to use the compression if
     * ever it is specified.
     * 
     * @param useCompression - Whether or not to use the compression during the
     *            execution of the commands.
     */
    protected abstract void configureCompression(boolean useCompression);

}
