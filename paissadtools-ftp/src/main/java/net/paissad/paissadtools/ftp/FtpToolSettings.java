package net.paissad.paissadtools.ftp;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.IToolSettings;

/**
 * The FTP settings to use (credentials, server hostname & port, timeout and so
 * on ...).
 * 
 * @author paissad
 */
// CHECKSTYLE:OFF
@Getter
// CHECKSTYLE:ON
@Setter
public class FtpToolSettings implements IToolSettings {

    private static final long             serialVersionUID = 1L;

    /** The default FTP port which used when no one is set. */
    public static final int               DEFAULT_FTP_PORT = 21;

    /** The default FTP transfer mode which is used if no one is set. */
    public static final FTP_TRANSFER_MODE DEFAULT_FTP_MODE = FTP_TRANSFER_MODE.BINARY;

    /**
     * Represents a FTP transfer mode.
     * 
     * @author paissad
     */
    public static enum FTP_TRANSFER_MODE {
        /** The binary mode. */
        BINARY,
        /** The ascii mode. */
        ASCII;
    }

    private String            user;
    private String            password;
    private String            host;
    private int               port    = DEFAULT_FTP_PORT;
    private FTP_TRANSFER_MODE ftpMode = DEFAULT_FTP_MODE;
    /** The connection timeout in milliseconds. */
    private int               connectionTimeout;

    public FtpToolSettings() {
        this.connectionTimeout = 0;
    }

    public FtpToolSettings(final String user, final String password, final String host) {
        this(user, password, host, DEFAULT_FTP_PORT);
    }

    public FtpToolSettings(final String user, final String password, final String host, final int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }

}
