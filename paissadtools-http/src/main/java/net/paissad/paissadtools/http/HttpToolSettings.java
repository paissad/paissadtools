package net.paissad.paissadtools.http;

import java.io.Serializable;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.IToolSettings;

/**
 * <p>
 * Contains the HTTP settings to use when processing a request.
 * </p>
 * <p>
 * The settings are basically request parameters, headers, credentials and proxy
 * settings.
 * </p>
 * <b>NOTE :</b> If there is no need to use the proxy settings, then the
 * {@link ProxySettings} attribute must be <code>null</code>.
 * 
 * @author paissad
 */
@Getter
@Setter
public class HttpToolSettings implements IToolSettings {

    private static final long   serialVersionUID = 1L;

    private String              username;
    private String              password;
    private ProxySettings       proxySettings;
    private Map<String, String> parameters;
    private Map<String, String> headers;

    /**
     * Represents the HTTP proxy settings.
     * 
     * @author paissad
     */
    @Getter
    @Setter
    public static class ProxySettings implements Serializable {

        private static final long serialVersionUID = 1L;

        private String            user;
        private String            pass;
        private String            host;
        private int               port;
    }

}
