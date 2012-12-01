package net.paissad.paissadtools.ssh;

import java.io.OutputStream;
import java.util.List;

import net.paissad.paissadtools.api.ServiceEntry;

/**
 * @author paissad
 * @since 0.1
 */
public interface Ssh extends ServiceEntry {

    /**
     * Connects to a SSH server by using the "user/password" method.
     * 
     * @return <code>true</code> if the connection is ok, <code>false</code>
     *         otherwise.
     * @since 0.1
     */
    public boolean connect();

    /**
     * Connects to a SSH server by using the public key authentication method.
     * 
     * @return <code>true</code> if the connection is ok, <code>false</code>
     *         otherwise.
     * @since 0.1
     */
    public boolean connectByUsingPublicKey();

    /**
     * @param sshCommands - The list of {@link SshCommand} to execute.
     * @param stdout - Where to put the stdout stream of the remotely executed
     *            commands. May be <code>null</code>.
     * @return <code>true</code> if all commands are executed successfully,
     *         <code>false</code> otherwise.
     * @see #executeCommands(List, OutputStream, OutputStream)
     * @see #executeCommands(List, OutputStream, OutputStream, boolean)
     * @since 0.1
     */
    public boolean executeCommands(List<SshCommand> sshCommands, OutputStream stdout);

    /**
     * 
     * @param sshCommands - The list of {@link SshCommand} to execute.
     * @param stdout - Where to put the stdout stream of the remotely executed
     *            commands. May be <code>null</code>.
     * @param stderr - Where to put the stderr stream of the remotely executed
     *            commands. May be <code>null</code>.
     * @return <code>true</code> if all commands are executed successfully,
     *         <code>false</code> otherwise.
     * @see #executeCommands(List, OutputStream)
     * @see #executeCommands(List, OutputStream, OutputStream, boolean)
     * @since 0.1
     */
    public boolean executeCommands(List<SshCommand> sshCommands, OutputStream stdout, OutputStream stderr);

    /**
     * 
     * @param sshCommands - The list of {@link SshCommand} to execute.
     * @param stdout - Where to put the stdout stream of the remotely executed
     *            commands. May be <code>null</code>.
     * @param stderr - Where to put the stderr stream of the remotely executed
     *            commands. May be <code>null</code>.
     * @param useCompression - If <code>true</code> then use the compression
     *            during the execution of the commands.
     * @return <code>true</code> if all commands are executed successfully,
     *         <code>false</code> otherwise.
     * @see #executeCommands(List, OutputStream)
     * @see #executeCommands(List, OutputStream, OutputStream)
     * @since 0.1
     */
    public boolean executeCommands(List<SshCommand> sshCommands, OutputStream stdout, OutputStream stderr,
            boolean useCompression);

    /**
     * @return <code>true</code> if the disconnection is ok, <code>false</code>
     *         otherwise.
     * @since 0.1
     */
    public boolean disconnect();

    /**
     * @param sshErrorListener - The {@link SshErrorListener} to use if ever an
     *            error occurs.
     * @since 0.1
     */
    public void setErrorListener(SshErrorListener sshErrorListener);

}
