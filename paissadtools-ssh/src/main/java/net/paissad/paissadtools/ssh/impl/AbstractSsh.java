package net.paissad.paissadtools.ssh.impl;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import net.paissad.paissadtools.ssh.SshCommand;
import net.paissad.paissadtools.ssh.SshErrorEvent;
import net.paissad.paissadtools.ssh.SshErrorListener;
import net.paissad.paissadtools.ssh.SshTool;
import net.paissad.paissadtools.ssh.SshToolSettings;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paissad
 */
@Getter
@Setter
abstract class AbstractSsh implements SshTool {

    /** The default home ssh user directory. */
    protected static final File SSH_USER_DIR = new File(System.getProperty("user.home"), ".ssh");

    private SshToolSettings     sshSettings;

    private SshErrorListener    errorListener;

    protected AbstractSsh(final SshToolSettings sshSettings) {
        this.sshSettings = sshSettings;
    }

    @Override
    public boolean executeCommands(final List<SshCommand> sshCommands, final OutputStream stdout) {
        return this.executeCommands(sshCommands, stdout, null);
    }

    @Override
    public boolean executeCommands(final List<SshCommand> sshCommands, final OutputStream stdout,
            final OutputStream stderr) {
        return this.executeCommands(sshCommands, stdout, stderr, false);
    }

    protected File getKnownHostsFile() {
        return new File(SSH_USER_DIR, "known_hosts");
    }

    protected File getPemFile() {
        final File defaultKeyFile = new File(SSH_USER_DIR, "id_rsa").isFile() ? new File(SSH_USER_DIR, "id_rsa")
                : new File(SSH_USER_DIR, "id_dsa");

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
     * @param sshErrorEvent - The error event.
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
