package net.paissad.paissadtools.mail;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolSettings;

/**
 * This class holds the SMTP settings to use using the mail {@link ITool}.
 * 
 * @author paissad
 */
@Getter
@Setter
public class MailToolSMTPSettings implements IToolSettings {

    private static final long serialVersionUID = 1L;

    private String            smtpUser;
    private String            smtpPassword;
    private String            smtpHost;
    private String            smtpPort;
    private boolean           auth;
    private boolean           startTLS;
    private boolean           ssl;

    public MailToolSMTPSettings() {
        this(null, null, null, null);
    }

    public MailToolSMTPSettings(final String smtpUser, final String smtpPassword, final String smtpHost,
            final String smtpPort) {
        this.smtpUser = smtpUser;
        this.smtpPassword = smtpPassword;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }
}
