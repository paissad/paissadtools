package net.paissad.paissadtools.ssh;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.ServiceSettings;

/**
 * @author paissad
 * @since 0.1
 */
@Getter
@Setter
public class SshSettings implements ServiceSettings {

    public static final SSH_PROVIDER DEFAULT_SSH_PROVIDER = SSH_PROVIDER.JSCH;

    /**
     * A type of ssh provider. It corresponds to the third Java library which
     * has implemented the ssh specifications.
     * 
     * @author paissad
     * @since 0.1
     */
    public static enum SSH_PROVIDER {
        JSCH, GANYMED;
    }

    private static final long  serialVersionUID = 1L;

    public static int          DEFAULT_SSH_PORT = 22;

    /**
     * This is a key that may be used to set the property for defining a non
     * conventional location for the pemfile when using public key
     * authentication.
     */
    public static final String PROPS_KEYFILE    = "pemfile";

    private SSH_PROVIDER       sshProvider      = DEFAULT_SSH_PROVIDER;
    private String             user;
    private String             password;
    private String             host;
    private int                port             = DEFAULT_SSH_PORT;
    private String             passPhrase;
    private int                connectionTimeout;
    private Properties         properties;

    public SshSettings() {
    }

    public SshSettings(final String user, final String password, final String host) {
        this(user, password, host, DEFAULT_SSH_PORT);
    }

    public SshSettings(final String user, final String password, final String host, final int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.connectionTimeout = 0;
        this.properties = new Properties();
    }

}
