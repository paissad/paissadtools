package net.paissad.paissadtools.mail;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolSettings;

/**
 * Represents the mail settings to use using the mail {@link ITool}.
 * 
 * @author paissad
 */
@Getter
@Setter
public class MailToolSettings implements IToolSettings {

    private static final long serialVersionUID = 1L;

    private String            smtpUser;
    private String            smtpPassword;
    private String            smtpHost;
    private int               smtpPort;
    private boolean           smtpAuth;
    private boolean           starttls;
    private boolean           ssl;
    /** If set to true, then receive an acknowledgment after sending the mail. */
    private boolean           autoAcknowledge;

    public MailToolSettings() {
        // By default, do not ask for an acknowledgment when sending a mail.
        this.autoAcknowledge = false;
    }

}
