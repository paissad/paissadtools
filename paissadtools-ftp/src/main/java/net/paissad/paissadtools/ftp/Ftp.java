package net.paissad.paissadtools.ftp;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import net.paissad.paissadtools.api.ServiceEntry;
import net.paissad.paissadtools.ftp.FtpSettings.FTP_TRANSFER_MODE;

/**
 * 
 * @author paissad
 * @since 0.1
 */
public interface Ftp extends ServiceEntry {

    /**
     * @return <code>true</code> if connected successfully to the FTP server,
     *         <code>false</code> otherwise.
     * @since 0.1
     */
    public boolean connect();

    /**
     * <P>
     * Downloads a remote resource (file or directory). If the resource is a
     * directory, the download is recursive.
     * </P>
     * 
     * @param remoteResource - The resource to retrieve. Can be either a remote
     *            file or directory.
     * @param localResource - Where to save the downloaded resource.
     * @param overwrite - If <code>true</code> then overwrite the local
     *            destination.
     * @throws FtpException
     * @since 0.1
     */
    public void getResource(final String remoteResource, final File localResource, boolean overwrite)
            throws FtpException;

    /**
     * <p>
     * Downloads a remote <b>file</b>.
     * </p>
     * <p>
     * <span style="color:red;text-decoration:underline;"><b>NOTE:</b></span> In
     * order to download remote directories, use
     * {@link #getResource(String, File, boolean)} instead.
     * </p>
     * 
     * @param remoteResource - The resource to download. <b>(MUST BE A REMOTE
     *            FILE)</b>
     * @param localResourceStream - The <tt>OutputStream</tt> which receives the
     *            downloaded file.
     * @throws FtpException
     * @since 0.1
     */
    public void getResource(final String remoteResource, final OutputStream localResourceStream) throws FtpException;

    /**
     * Uploads a resource to the FTP server.<br>
     * If the resource to upload is a directory, then the upload is processed
     * recursively.
     * 
     * @param localResource - The resource to upload.
     * @param destination - Where to save the resource.
     * @param overwrite - If <code>true</code> then overwrite destination
     *            resources (file or directory) if they do already exist.
     * @throws FtpException
     * @since 0.1
     */
    public void putResource(final File localResource, final String destination, boolean overwrite) throws FtpException;

    /**
     * <p>
     * Uploads a local file to the FTP server.
     * </p>
     * <p>
     * <span
     * style="color:red;text-decoration:underline;font-weight:bold">NOTE:</span>
     * <ul>
     * <li>If the remote file (destination) does already exist, it will be
     * overwritten.</li>
     * </ul>
     * </p>
     * 
     * @param localResourceStream - The local resource to upload.
     * @param destination - The name of the destination file onto the FTP
     *            server.
     * @throws FtpException
     * @since 0.1
     */
    public void putResource(final InputStream localResourceStream, final String destination) throws FtpException;

    /**
     * Deletes a resource (file or directory) onto the FTP server.<br>
     * If the resource is a directory, it is deleted recursively.
     * 
     * @param remoteResource - The resource to delete.
     * @throws FtpException
     * @since 0.1
     */
    public void deleteResource(final String remoteResource) throws FtpException;

    /**
     * Creates a directory onto the FTP server.<br>
     * The directory is recursively create if all or some parents do not exist
     * yet.
     * 
     * @param dirname - The directory to create.
     * @throws FtpException
     * @since 0.1
     */
    public void mkdirs(final String dirname) throws FtpException;

    /**
     * Sends a FTP command to the server. Be aware that not all FTP commands do
     * supports all types of commands, thus you should "be sure" that the
     * command you're trying to send is really supported.
     * 
     * @param ftpCommand - the name of the FTP command to send.
     * @param arguments - The arguments to pass to the FTP commmand. Can be
     *            blank (<code>null</code> or empty).
     * @throws FtpException
     * @since 0.1
     */
    public void sendCommand(final String ftpCommand, final String arguments) throws FtpException;

    /**
     * Sets the current FTP transfer mode to use when downloading or uploading
     * resources.<br>
     * Thus, when it is needed to switch between <em>ASCII</em> and
     * <em>BINARY</em> modes when downloading or uploading a resource, just use
     * this method before proceeding an operation.
     * 
     * @param ftpMode - The {@link FTP_TRANSFER_MODE} to use.
     * @since 0.1
     */
    public void setCurrentFtpMode(final FTP_TRANSFER_MODE ftpMode);

    /**
     * @return <code>true</code> if disconnected successfully,
     *         <code>false</code> otherwise.
     * @since 0.1
     */
    public boolean disconnect();

    /**
     * Sets the stream which will receives the commands/responses logs. May be
     * <code>null</code>. If <tt>null</tt> no log is handled.
     * 
     * @param out - The <tt>OutputStream</tt> which receives the logs of the FTP
     *            commands which are sent and the responses receives ... May be
     *            <code>null</code>.
     * @since 0.1
     */
    public void setLogReceiver(final OutputStream out);

}
