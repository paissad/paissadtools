package net.paissad.paissadtools.ssh;

import java.util.LinkedList;
import java.util.List;

import net.paissad.paissadtools.ssh.SshToolSettings.SSH_PROVIDER;

public class Main {

    public static void main(String[] args) {

        final String host = "localhost";
        final String user = "paissad";
        final String password = "missmiss";
        final int port = 22;

        final SshToolSettings sshSettings = new SshToolSettings(user, password, host, port);

        final List<SshCommand> sshCommands = new LinkedList<SshCommand>();
        sshCommands.add(new SshCommand("echo 'Hello World !'"));
        sshCommands.add(new SshCommand("echo 'The $PATH is :' && echo $PATH | tr ':' '\n' "));
        sshCommands.add(new SshCommand("echo 'The $HOME is :' && echo $HOME"));
        sshCommands.add(new SshCommand("echo >&2 STDERR"));
        sshCommands.add(new SshCommand("echo Done ..."));

        for (final SSH_PROVIDER provider : SSH_PROVIDER.values()) {
            sshSettings.setSshProvider(provider);
            final SshTool ssh = SshFactory.getSsh(sshSettings);

            final SshErrorListener sshErrorListener = new SshErrorListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void onError(SshErrorEvent sshErrorEvent) {
                    if (sshErrorEvent == null || sshErrorEvent.getReason() == null) return;
                    if (sshErrorEvent.getReason() instanceof Exception) {
                        ((Exception) sshErrorEvent.getReason()).printStackTrace(System.err);
                    }
                }
            };

            ssh.setErrorListener(sshErrorListener);

            if (!ssh.connect()) {
                throw new IllegalStateException("Connection to the SSH server failed !");
            }

            ssh.executeCommands(sshCommands, System.out, System.err, false);

            if (!ssh.disconnect()) {
                throw new IllegalStateException("Disconnection failed !");
            }
        }

    }
}
