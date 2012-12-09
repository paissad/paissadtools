package net.paissad.paissadtools.ssh.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.paissad.paissadtools.ssh.AbstractSsh;
import net.paissad.paissadtools.ssh.SshCommand;
import net.paissad.paissadtools.ssh.SshToolSettings;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.KnownHosts;
import ch.ethz.ssh2.ServerHostKeyVerifier;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * @author paissad
 */
public class SshGanymed extends AbstractSsh {

    private static Logger logger = LoggerFactory.getLogger(SshGanymed.class);

    private Connection    sshConnection;

    private KnownHosts    database;

    public SshGanymed(final SshToolSettings sshSettings) {
        super(sshSettings);
        this.database = new KnownHosts();
    }

    @Override
    public boolean connect() {
        try {
            this.initialize();
            final boolean isAuthenticated = this.sshConnection.authenticateWithPassword(
                    this.getSshSettings().getUser(), this.getSshSettings().getPassword());
            if (!isAuthenticated) throw new IllegalStateException("Authentication failed !");
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(GANYMED) Error while connecting to the server.", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;
        }
    }

    @Override
    public boolean connectByUsingPublicKey() {
        try {
            final boolean isAuthenticated = this.sshConnection.authenticateWithPublicKey(this.getSshSettings()
                    .getUser(), this.getPemFile(), this.getSshSettings().getPassPhrase());
            if (!isAuthenticated) throw new IllegalStateException("Authentication failed !");
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(GANYMED) Error while connecting to the server.", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;
        }
    }

    @Override
    public boolean executeCommands(final List<SshCommand> sshCommands, final OutputStream stdout,
            final OutputStream stderr, final boolean useShell) {

        Session session = null;
        InputStream out = null;
        InputStream err = null;
        // FIXME : why is the stdin variable not used ?
        final OutputStream stdin = null;

        try {
            logger.debug("({}) Execution of the specified ssh commands ...", this.getImplementationName());
            for (final SshCommand sshCommand : sshCommands) {

                if (sshCommand == null || sshCommand.getCommand() == null || sshCommand.getCommand().trim().isEmpty()) {
                    continue;
                }
                logger.debug("-- ({}) Executing the ssh command --> {}", this.getImplementationName(),
                        sshCommand.getCommand());

                session = this.sshConnection.openSession();

                session.execCommand(sshCommand.getCommand());
                out = new StreamGobbler(session.getStdout());
                err = new StreamGobbler(session.getStderr());

                IOUtils.copy(out, stdout);
                IOUtils.copy(err, stderr);
                session.close();
            }

            logger.debug("({}) All ssh commands executed successfully !", this.getImplementationName());
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(GANYMED) Error during the execution of the commands.", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;

        } finally {
            CommonUtils.closeAllStreamsQuietly(out, err, stdin);
            if (session != null) session.close();
        }

    }

    @Override
    public boolean disconnect() {
        try {
            this.sshConnection.close();
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(GANYMED) Error during the disconnection", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;
        }
    }

    @Override
    protected String getImplementationName() {
        return "GANYMED";
    }

    @Override
    protected void configureCompression(final boolean useCompression) {
        if (useCompression) {
            logger.warn("Compression is not supported by SSH-GANYMED implementation.");
        }
    }

    private void initialize() throws IOException {
        if (this.getSshSettings() == null) throw new IllegalStateException("The SSH settings are not provided yet !");
        this.sshConnection = new Connection(this.getSshSettings().getHost(), this.getSshSettings().getPort());

        if (this.getKnownHostsFile().isFile()) {
            this.database.addHostkeys(this.getKnownHostsFile());
            this.sshConnection.connect(new SimpleVerifier(this.database));
        } else {
            this.sshConnection.connect();
        }
    }

    /**
     * @author paissad
     */
    private static class SimpleVerifier implements ServerHostKeyVerifier {

        private KnownHosts db;

        public SimpleVerifier(final KnownHosts database) {
            if (database == null) throw new IllegalArgumentException("The database cannot be null.");
            this.db = database;
        }

        @Override
        public boolean verifyServerHostKey(final String hostname, final int port, final String serverHostKeyAlgorithm,
                final byte[] serverHostKey) throws Exception {

            final int result = this.db.verifyHostkey(hostname, serverHostKeyAlgorithm, serverHostKey);
            switch (result) {

            case KnownHosts.HOSTKEY_IS_OK:
                return true;

            case KnownHosts.HOSTKEY_IS_NEW:

                // Unknown host? Blindly accept the key and put it into the
                // cache. Well, it is definitely possible to do better (e.g.,
                // ask the user).

                // The following call will ONLY put the key into the memory
                // cache!
                // To save it in a known hosts file, also call
                // "KnownHosts.addHostkeyToFile(...)"
                this.db.addHostkey(new String[] { hostname }, serverHostKeyAlgorithm, serverHostKey);

                return true;

            case KnownHosts.HOSTKEY_HAS_CHANGED:

                // Close the connection if the hostkey has changed.
                // Better: ask user and add new key to database.
                return false;

            default:
                throw new IllegalStateException("Unkown host key option ...");
            }
        }
    }

}
