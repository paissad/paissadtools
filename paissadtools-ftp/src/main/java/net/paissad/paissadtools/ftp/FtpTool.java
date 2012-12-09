package net.paissad.paissadtools.ftp;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.ftp.FtpToolSettings.FTP_TRANSFER_MODE;
import net.paissad.paissadtools.ftp.exception.FtpToolException;

/**
 * The top interface for FTP tool.
 * 
 * @author paissad
 */
public interface FtpTool extends ITool {

    /**
     * @return <code>true</code> if connected successfully to the FTP server,
     *         <code>false</code> otherwise.
     */
    boolean connect();

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
     * @throws FtpToolException - If an error occurs while retrieving the
     *             resource.
     */
    void getResource(final String remoteResource, final File localResource, boolean overwrite) throws FtpToolException;

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
     * @throws FtpToolException - If an error occurs while retrieving the
     *             resource.
     */
    void getResource(final String remoteResource, final OutputStream localResourceStream) throws FtpToolException;

    /**
     * Uploads a resource to the FTP server.<br>
     * If the resource to upload is a directory, then the upload is processed
     * recursively.
     * 
     * @param localResource - The resource to upload.
     * @param destination - Where to save the resource.
     * @param overwrite - If <code>true</code> then overwrite destination
     *            resources (file or directory) if they do already exist.
     * @throws FtpToolException - If an error occurs while sending the resource.
     */
    void putResource(final File localResource, final String destination, boolean overwrite) throws FtpToolException;

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
     * @throws FtpToolException - If an error occurs while sending the resource.
     */
    void putResource(final InputStream localResourceStream, final String destination) throws FtpToolException;

    /**
     * Deletes a resource (file or directory) onto the FTP server.<br>
     * If the resource is a directory, it is deleted recursively.
     * 
     * @param remoteResource - The resource to delete.
     * @throws FtpToolException - If an error occurs while deleting the
     *             resource.
     */
    void deleteResource(final String remoteResource) throws FtpToolException;

    /**
     * Creates a directory onto the FTP server.<br>
     * The directory is recursively create if all or some parents do not exist
     * yet.
     * 
     * @param dirname - The directory to create.
     * @throws FtpToolException - If an error occurs while creating the
     *             directory.
     */
    void mkdirs(final String dirname) throws FtpToolException;

    /**
     * Sends a FTP command to the server. Be aware that not all FTP commands do
     * supports all types of commands, thus you should "be sure" that the
     * command you're trying to send is really supported.
     * 
     * @param ftpCommand - the name of the FTP command to send.
     * @param arguments - The arguments to pass to the FTP command. Can be blank
     *            (<code>null</code> or empty).
     * @throws FtpToolException - If an error occurs while sending the command
     *             to the ftp server.
     */
    void sendCommand(final String ftpCommand, final String arguments) throws FtpToolException;

    /**
     * Sets the current FTP transfer mode to use when downloading or uploading
     * resources.<br>
     * Thus, when it is needed to switch between <em>ASCII</em> and
     * <em>BINARY</em> modes when downloading or uploading a resource, just use
     * this method before proceeding an operation.
     * 
     * @param ftpMode - The {@link FTP_TRANSFER_MODE} to use.
     */
    void setCurrentFtpMode(final FTP_TRANSFER_MODE ftpMode);

    /**
     * @return <code>true</code> if disconnected successfully,
     *         <code>false</code> otherwise.
     */
    boolean disconnect();

    /**
     * Sets the stream which will receives the commands/responses logs. May be
     * <code>null</code>. If <tt>null</tt> no log is handled.
     * 
     * @param out - The <tt>OutputStream</tt> which receives the logs of the FTP
     *            commands which are sent and the responses receives ... May be
     *            <code>null</code>.
     */
    void setLogReceiver(final OutputStream out);

}
