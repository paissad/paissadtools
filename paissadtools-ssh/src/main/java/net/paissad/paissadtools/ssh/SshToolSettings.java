package net.paissad.paissadtools.ssh;

import java.util.Properties;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.IToolSettings;

/**
 * @author paissad
 */
// CHECKSTYLE:OFF
@Getter
// CHECKSTYLE:ON
@Setter
public class SshToolSettings implements IToolSettings {

    private static final long        serialVersionUID     = 1L;

    /** The default SSH port. */
    public static final int          DEFAULT_SSH_PORT     = 22;

    /** The default SSH provider. */
    public static final SSH_PROVIDER DEFAULT_SSH_PROVIDER = SSH_PROVIDER.JSCH;

    /**
     * This is a key that may be used to set the property for defining a non
     * conventional location for the pemfile when using public key
     * authentication.
     */
    public static final String       PROPS_KEYFILE        = "pemfile";

    /**
     * A type of ssh provider. It corresponds to the third Java library which
     * has implemented the ssh specifications.
     * 
     * @author paissad
     */
    public static enum SSH_PROVIDER {
        /** The JSCH provider/implementation. */
        JSCH,
        /** The GANYMED provider/implementation. */
        GANYMED;
    }

    private SSH_PROVIDER sshProvider = DEFAULT_SSH_PROVIDER;
    private String       user;
    private String       password;
    private String       host;
    private int          port        = DEFAULT_SSH_PORT;
    private String       passPhrase;
    private int          connectionTimeout;
    private Properties   properties;

    public SshToolSettings() {
    }

    public SshToolSettings(final String user, final String password, final String host) {
        this(user, password, host, DEFAULT_SSH_PORT);
    }

    public SshToolSettings(final String user, final String password, final String host, final int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.connectionTimeout = 0;
        this.properties = new Properties();
    }

}
