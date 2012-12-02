package net.paissad.paissadtools.ftp.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import net.paissad.paissadtools.ftp.FtpTool;
import net.paissad.paissadtools.ftp.FtpToolException;
import net.paissad.paissadtools.ftp.FtpToolSettings;
import net.paissad.paissadtools.ftp.FtpToolSettings.FTP_TRANSFER_MODE;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP implementation for {@link FtpTool}
 * 
 * @author paissad
 */
public class FtpToolImpl implements FtpTool {

    private static final long serialVersionUID = 1L;

    private static Logger     logger           = LoggerFactory.getLogger(FtpToolImpl.class);

    private static final int  BUFFER_SIZE      = 8192;

    private FTPClient         ftpClient;
    private FtpToolSettings   ftpSettings;

    public FtpToolImpl(final FtpToolSettings ftpSettings) {
        if (ftpSettings == null) throw new IllegalArgumentException("The FTP settings cannot be null.");
        this.ftpClient = new FTPClient();
        this.ftpSettings = ftpSettings;
    }

    @Override
    public boolean connect() {

        try {
            String errMsg;

            this.ftpClient.setBufferSize(BUFFER_SIZE);
            this.ftpClient.setSendBufferSize(BUFFER_SIZE);
            this.ftpClient.setReceiveBufferSize(BUFFER_SIZE);
            this.ftpClient.setConnectTimeout(this.ftpSettings.getConnectionTimeout());
            this.ftpClient.setListHiddenFiles(true);

            // Connects to the FTP server
            logger.info("Connecting to the FTP server.");
            this.ftpClient.connect(this.ftpSettings.getHost(), this.ftpSettings.getPort());
            if (!FTPReply.isPositiveCompletion(this.ftpClient.getReplyCode())) {
                this.ftpClient.disconnect();
                errMsg = "Unable to connect to the server " + this.ftpSettings.getHost();
                this.verifyReplyCode(errMsg);
            }

            // Login to the FTP server
            logger.info("Log in to the FTP server.");
            if (!this.ftpClient.login(this.ftpSettings.getUser(), this.ftpSettings.getPassword())) {
                errMsg = "Unable to login to " + this.ftpSettings.getHost();
                this.verifyReplyCode(errMsg);
            }

            logger.info("Connected and logged in successfully to the FTP server.");
            return true;

        } catch (Exception e) {
            logger.error("Error while connecting to the FTP server.", e);
            return false;
        }
    }

    @Override
    public void getResource(String remoteResource, File localResource, boolean overwrite) throws FtpToolException {
        OutputStream out = null;
        try {
            if (!overwrite) {
                // Do not overwrite an existing local file or directory !
                if (localResource.exists()) return;
            }

            if (this.isFtpFile(remoteResource) || this.isFtpSymbolicLink(remoteResource)) {
                out = new BufferedOutputStream(new FileOutputStream(localResource));
                this.getResource(remoteResource, out);

            } else if (this.isFtpDir(remoteResource)) {
                this.downloadRemoteDir(this.ftpClient, remoteResource, localResource);

            } else {
                throw new IllegalStateException("Unknown remote ftp resource type, cannot download !");
            }

        } catch (Exception e) {
            throw new FtpToolException(e);
        } finally {
            CommonUtils.closeStreamQuietly(out);
        }
    }

    @Override
    public void getResource(String remoteResource, OutputStream localResourceStream) throws FtpToolException {

        CommonUtils.assertNotBlank(remoteResource);
        CommonUtils.assertNotNullOrThrowException(localResourceStream);

        try {
            this.ftpClient.retrieveFile(remoteResource, localResourceStream);
            this.verifyReplyCode("Unable to retrieve the the remote resource " + remoteResource);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new FtpToolException(e);
        }
    }

    @Override
    public void putResource(final File localResource, final String destination, boolean overwrite)
            throws FtpToolException {

        if (localResource == null) throw new NullPointerException("The local resource to upload cannot be null.");
        if (!localResource.exists()) throw new IllegalStateException("The local resource to upload does not exit.");

        InputStream in = null;
        try {

            if (!overwrite) {
                if (this.isFtpDir(destination) || this.isFtpFile(destination) || this.isFtpSymbolicLink(destination)) {
                    return;
                }
            }

            if (localResource.isFile()) { // file ...
                try {
                    in = new BufferedInputStream(new FileInputStream(localResource));
                    this.putResource(in, destination);
                } finally {
                    CommonUtils.closeStreamQuietly(in);
                }

            } else if (localResource.isDirectory()) { // directory ...
                this.uploadLocalDir(this.ftpClient, localResource, destination);

            } else {
                throw new IllegalStateException("Unknown type of file, cannot upload !");
            }

        } catch (Exception e) {
            throw new FtpToolException(e);
        } finally {
            CommonUtils.closeStreamQuietly(in);
        }
    }

    @Override
    public void putResource(final InputStream localResourceStream, final String destination) throws FtpToolException {

        try {
            String errMsg;

            int filesize = localResourceStream.available();
            this.ftpClient.allocate(filesize);
            final String humanFileSize = CommonUtils.humanReadableByteCount(filesize, false);
            errMsg = "Unable to allocate the amount of size " + humanFileSize + " for the file " + destination;
            this.verifyReplyCode(errMsg);

            this.ftpClient.storeFile(destination, localResourceStream);
            errMsg = "Unable to store the file " + destination + " to the server";
            this.verifyReplyCode(errMsg);

        } catch (Exception e) {
            throw new FtpToolException("Unable to send the resource to " + destination, e);
        }
    }

    @Override
    public void deleteResource(final String remoteResource) throws FtpToolException {

        CommonUtils.assertNotBlankOrThrowException(remoteResource);
        final String errMsg = "Error while deleting the resource '" + remoteResource + "'";

        try {
            if (this.isFtpFile(remoteResource) || this.isFtpSymbolicLink(remoteResource)) {
                if (!this.ftpClient.deleteFile(remoteResource)) {
                    throw new FtpToolException(errMsg);
                } else {
                    this.verifyReplyCode(errMsg);
                }

            } else if (this.isFtpDir(remoteResource)) {
                this.deleteDirectory(remoteResource);

            } else {
                throw new IllegalStateException(
                        "Unable to delete this type of resource (not a file, not a symlink and not a directory)");
            }

        } catch (Exception e) {
            logger.error(errMsg, e);
            throw new FtpToolException(errMsg, e);
        }
    }

    @Override
    public void mkdirs(String dirname) throws FtpToolException {

        try {
            char firstChar = dirname.charAt(0);
            String currentRoot = (firstChar == '/') ? "/" : "";

            String[] subDirnames = dirname.split("/");

            for (final String name : subDirnames) {
                if (!name.isEmpty()) {
                    currentRoot += name + "/";
                    if (!this.isFtpDir(currentRoot)) {
                        this.ftpClient.makeDirectory(currentRoot);
                    }
                    this.verifyReplyCode("Error while creating the directory " + currentRoot);
                }
            }

        } catch (Exception e) {
            logger.error("", e);
            throw new FtpToolException(e);
        }
    }

    @Override
    public void sendCommand(String ftpCommand, String arguments) throws FtpToolException {
        CommonUtils.assertNotBlankOrThrowException(ftpCommand);
        try {
            logger.info("Sending the following FTP command --> {} {}", ftpCommand, (arguments == null) ? "" : arguments);
            this.ftpClient.sendCommand(ftpCommand, arguments);
            this.verifyReplyCode("Error while sending the FTP command --> " + ftpCommand);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new FtpToolException(e);
        }
    }

    @Override
    public void setCurrentFtpMode(final FTP_TRANSFER_MODE ftpMode) {
        if (ftpMode == null) return;
        try {
            switch (ftpMode) {
            case BINARY:
                this.ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                this.verifyReplyCode("Error while setting FTP transfer mode to BINARY");
                break;
            case ASCII:
                this.ftpClient.setFileTransferMode(FTP.ASCII_FILE_TYPE);
                this.verifyReplyCode("Error while setting FTP transfer mode to ASCII");
                break;
            default:
                logger.warn("The specified ftp transfer mode '{}' is not supported !", ftpMode);
                break;
            }
        } catch (Exception e) {
            final String errMsg = "Error while setting the FTP transfer mode.";
            logger.error(errMsg, e);
            throw new IllegalStateException(errMsg, e);
        }
    }

    @Override
    public boolean disconnect() {

        try {

            logger.info("Disconnecting from the FTP server.");
            if (this.ftpClient.isConnected() && !FTPReply.isPositiveIntermediate(this.ftpClient.getReplyCode())) {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
                logger.info("Disconnected successfully from the FTP server.");
            } else {
                logger.warn("Not connected to the FTP server.");
            }

            if (this.ftpClient.isConnected() && !this.ftpClient.completePendingCommand()) {
                logger.error("Unable to disconnect correctly from the FTP server.");
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error("Error while disconnecting from the FTP server.", e);
            return false;
        }
    }

    @Override
    public void setLogReceiver(OutputStream out) {
        if (out == null) return;
        this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(out)));
    }

    /**
     * Verifies whether or not the last previous FTP action/command finished
     * successfully, if not then display the specified error message and throw a
     * {@link FTPException}.
     * 
     * @param errorMessage - The error message to show if ever the previous ftp
     *            command did not return a correct/positive reply code.
     * @throws FTPException
     */
    private void verifyReplyCode(final String errorMessage) throws FtpToolException {
        this.verifyReplyCode(this.ftpClient, errorMessage);
    }

    private void verifyReplyCode(final FTPClient client, final String errorMessage) throws FtpToolException {
        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
            logger.error(errorMessage);
            throw new FtpToolException(errorMessage);
        }
    }

    /**
     * @param ftpResource
     * @return <code>true</code> if the remote resource is a file and not a
     *         directory or symlink or something else ...
     * @throws IOException
     * @throws FtpToolException
     */
    private boolean isFtpFile(final String ftpResource) throws IOException, FtpToolException {
        if (ftpResource.equals("/")) return false;
        // We list the parent resource before doing a listing ...
        String parentResource = ftpResource;
        if (parentResource.endsWith("/")) {
            parentResource = parentResource.replaceFirst("/*$", "");
        }
        parentResource = parentResource.substring(0, parentResource.lastIndexOf("/") + 1);

        final String[] tokens = ftpResource.replaceFirst("/*$", "").split("/");
        final String ftpResourceName = tokens[tokens.length - 1];
        final FTPFile[] result = this.ftpClient.listFiles(parentResource, new FTPFileFilter() {
            @Override
            public boolean accept(FTPFile f) {
                return f.isFile() && f.getName().equals(ftpResourceName);
            }
        });
        this.verifyReplyCode("Error while testing whether or not the resource is a file.");
        return result.length == 1;
    }

    /**
     * @param ftpResource
     * @return <code>true</code> If the FTP resource is a directory.
     * @throws IOException
     * @throws FtpToolException
     */
    private boolean isFtpDir(final String ftpResource) throws IOException, FtpToolException {
        if (ftpResource.equals("/")) return true;
        // We list the parent resource before doing a listing ...
        String parentResource = ftpResource;
        if (parentResource.endsWith("/")) {
            parentResource = parentResource.replaceFirst("/*$", "");
        }
        parentResource = parentResource.substring(0, parentResource.lastIndexOf("/") + 1);

        final String[] tokens = ftpResource.replaceFirst("/*$", "").split("/");
        final String ftpResourceName = tokens[tokens.length - 1];
        final FTPFile[] result = this.ftpClient.listFiles(parentResource, new FTPFileFilter() {
            @Override
            public boolean accept(FTPFile f) {
                return f.isDirectory() && f.getName().equals(ftpResourceName);
            }
        });
        this.verifyReplyCode("Error while testing whether or not the resource is a directory.");
        return result.length == 1;
    }

    private boolean isFtpSymbolicLink(final String ftpResource) throws IOException, FtpToolException {
        if (ftpResource.equals("/")) return false;
        // We list the parent resource before doing a listing ...
        String parentResource = ftpResource;
        if (parentResource.endsWith("/")) {
            parentResource = parentResource.replaceFirst("/*$", "");
        }
        parentResource = parentResource.substring(0, parentResource.lastIndexOf("/") + 1);

        final String[] tokens = ftpResource.replaceFirst("/*$", "").split("/");
        final String ftpResourceName = tokens[tokens.length - 1];
        final FTPFile[] result = this.ftpClient.listFiles(parentResource, new FTPFileFilter() {
            @Override
            public boolean accept(FTPFile f) {
                return f.isSymbolicLink() && f.getName().equals(ftpResourceName);
            }
        });
        this.verifyReplyCode("Error while testing whether or not the resource is a symbolic link.");
        return result.length == 1;
    }

    private boolean isFtpEmptyDir(final String ftpDirname) throws IOException {
        final FTPFile[] subFiles = this.ftpClient.listFiles(ftpDirname, new FTPFileFilter() {

            @Override
            public boolean accept(FTPFile file) {
                if (file.getName().equals(".") || file.getName().equals("..")) return false;
                return true;
            }
        });
        return subFiles.length == 0;
    }

    /**
     * @param client
     * @param ftpDirname - The remote directory to download from the FTP server.
     * @param destDir - Where to save to downloaded directory.
     * @throws IOException
     * @throws FtpToolException
     */
    private void downloadRemoteDir(final FTPClient client, final String ftpDirname, final File destDir)
            throws IOException, FtpToolException {

        if (destDir.isFile()) throw new IllegalStateException("Cannot download a directory and save it to a file ...");
        if (!destDir.exists()) destDir.mkdirs();
        if (!destDir.exists()) throw new IllegalStateException("Unable to create the directory");

        final FTPFile[] ftpFiles = client.listFiles(ftpDirname);
        for (final FTPFile oneFtpFile : ftpFiles) {
            if (oneFtpFile.getName().equals(".") || oneFtpFile.getName().equals("..")) continue;
            final String resourceName = ftpDirname + "/" + oneFtpFile.getName();
            if (oneFtpFile.isFile()) {
                // Is a FTP file.
                OutputStream out = null;
                try {
                    out = new BufferedOutputStream(new FileOutputStream(new File(destDir, oneFtpFile.getName())));
                    client.retrieveFile(resourceName, out);
                    this.verifyReplyCode(client, "Error while retrieving file.");
                } finally {
                    CommonUtils.closeStreamQuietly(out);
                }
            } else if (oneFtpFile.isDirectory()) {
                // Is a FTP directory.
                this.downloadRemoteDir(
                        client, resourceName, new File(destDir, oneFtpFile.getName()));
            } else {
                throw new IllegalStateException("Cannot retrieve resource which not a file, not a directory");
            }
        }
    }

    private void uploadLocalDir(final FTPClient client, final File localDir, final String ftpDirname)
            throws IOException, FtpToolException {

        if (!localDir.isDirectory()) throw new IllegalArgumentException("is not a directory.");
        if (this.isFtpFile(ftpDirname)) throw new IllegalArgumentException("Cannot upload a directory to a file");

        InputStream in = null;
        final File[] subFiles = localDir.listFiles();

        for (final File oneFile : subFiles) {
            if (oneFile.getName().equals(".") || oneFile.getName().equals("..")) continue;
            final String ftpResourceName = ftpDirname + "/" + oneFile.getName();

            if (oneFile.isFile()) {
                try {
                    in = new BufferedInputStream(new FileInputStream(oneFile));
                    client.storeFile(ftpResourceName, in);
                    this.verifyReplyCode(client, "Error while storing file.");
                } finally {
                    CommonUtils.closeStreamQuietly(in);
                }
            } else if (oneFile.isDirectory()) {
                this.mkdirs(ftpResourceName);
                this.uploadLocalDir(client, oneFile, ftpResourceName);
            } else {
                throw new IllegalStateException("Cannot upload resource which not a file, not a directory");
            }
        }
    }

    private void deleteDirectory(final String ftpDirname) throws FtpToolException {
        try {

            if (this.isFtpEmptyDir(ftpDirname)) {
                this.ftpClient.removeDirectory(ftpDirname);
                this.verifyReplyCode("Error while removing empty directory -> " + ftpDirname);

            } else {
                final FTPFile[] subFiles = this.ftpClient.listFiles(ftpDirname);

                for (final FTPFile f : subFiles) {
                    if (f.getName().equals(".") || f.getName().equals("..")) continue;

                    final String ftpResourceName = ftpDirname + "/" + f.getName();

                    if (f.isDirectory()) {
                        this.deleteDirectory(ftpResourceName);

                    } else {
                        // file or symlink or unknown ...
                        this.ftpClient.deleteFile(ftpResourceName);
                        this.verifyReplyCode("Error while removing file -> " + ftpResourceName);
                        this.deleteDirectory(ftpDirname);
                    }
                }
            }

        } catch (Exception e) {
            throw new FtpToolException(e);
        }
    }

}
