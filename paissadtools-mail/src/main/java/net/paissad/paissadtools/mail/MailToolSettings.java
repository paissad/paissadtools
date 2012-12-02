package net.paissad.paissadtools.mail;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.IToolSettings;

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
    /** If set to true, then receive an acknowledgement after sending the mail. */
    private boolean           autoAcknowledge;

    public MailToolSettings() {
        // By default, do not ask for an acknowledgement when sending a mail.
        this.autoAcknowledge = false;
    }

}
