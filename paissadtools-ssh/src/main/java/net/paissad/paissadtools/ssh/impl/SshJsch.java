package net.paissad.paissadtools.ssh.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import net.paissad.paissadtools.ssh.AbstractSsh;
import net.paissad.paissadtools.ssh.SshCommand;
import net.paissad.paissadtools.ssh.SshToolSettings;
import net.paissad.paissadtools.util.CommonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * @author paissad
 */
public class SshJsch extends AbstractSsh {

    private static final long serialVersionUID = 1L;

    private static Logger     logger           = LoggerFactory.getLogger(SshJsch.class);

    private JSch              jSch;
    private Session           session;

    public SshJsch(final SshToolSettings sshSettings) {
        super(sshSettings);
        this.jSch = new JSch();
    }

    @Override
    public boolean connect() {
        try {
            this.initialize();
            this.session.connect(this.getSshSettings().getConnectionTimeout());
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(JSCH) Error while connecting to the server.", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;
        }
    }

    @Override
    public boolean connectByUsingPublicKey() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean executeCommands(List<SshCommand> sshCommands, OutputStream stdout) {
        return executeCommands(sshCommands, stdout, null);
    }

    @Override
    public boolean executeCommands(List<SshCommand> sshCommands, OutputStream stdout, OutputStream stderr) {
        return executeCommands(sshCommands, stdout, stderr, false);
    }

    @Override
    public boolean executeCommands(List<SshCommand> sshCommands, OutputStream stdout, OutputStream stderr,
            boolean useCompression) {

        InputStream out = null;
        InputStream err = null;
        try {
            this.configureCompression(useCompression);

            logger.debug("({}) Execution of the specified ssh commands ...", this.getImplementationName());
            for (SshCommand sshCommand : sshCommands) {
                if (sshCommand == null || sshCommand.getCommand() == null || sshCommand.getCommand().trim().isEmpty()) {
                    continue;
                }

                logger.debug("-- ({}) Executing the ssh command --> {}",
                        this.getImplementationName(), sshCommand.getCommand());
                final ChannelExec channelExec = (ChannelExec) this.session.openChannel("exec");
                channelExec.setCommand(sshCommand.getCommand());
                out = channelExec.getInputStream();
                err = channelExec.getErrStream();
                channelExec.connect(sshCommand.getTimeout());
                CommonUtils.copyStream(out, stdout);
                CommonUtils.copyStream(err, stderr);
                channelExec.disconnect();
            }

            logger.debug("({}) All ssh commands executed successfully !", this.getImplementationName());
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(JSCH) Error during the execution of the commands.", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;

        } finally {
            CommonUtils.closeAllStreamsQuietly(out, err);
        }
    }

    @Override
    public boolean disconnect() {
        try {
            this.session.disconnect();
            return true;

        } catch (Exception e) {
            final Exception cause = new Exception("(GANYMED) Error during the disconnection", e);
            this.executeErrorListener(new SimpleSshErrorEvent(cause));
            return false;
        }
    }

    @Override
    protected String getImplementationName() {
        return "JSCH";
    }

    @Override
    protected void configureCompression(boolean useCompression) {
        if (useCompression) {
            logger.debug("");
            this.session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
            this.session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
            this.session.setConfig("compression_level", "9");
        }
    }

    private void initialize() throws JSchException {
        this.session = this.jSch.getSession(
                this.getSshSettings().getUser(), this.getSshSettings().getHost(), this.getSshSettings().getPort());
        if (this.getKnownHostsFile().isFile()) {
            this.jSch.setKnownHosts(this.getKnownHostsFile().getAbsolutePath());
        }
        final UserInfo userInfo = new CustomUserInfo(this.getSshSettings().getPassword(), this.getSshSettings()
                .getPassPhrase());
        this.session.setPassword(this.getSshSettings().getPassword());
        this.session.setUserInfo(userInfo);
        this.session.setConfig("StrictHostKeyChecking", "no");
    }

    private class CustomUserInfo implements UserInfo {

        private String password;
        private String passPhrase;

        CustomUserInfo(final String password, final String passPhrase) {
            this.password = password;
            this.passPhrase = passPhrase;
        }

        @Override
        public String getPassphrase() {
            return this.passPhrase;
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public boolean promptPassword(String message) {
            return false;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return false;
        }

        @Override
        public boolean promptYesNo(String message) {
            return false;
        }

        @Override
        public void showMessage(String message) {
        }

    }

}
