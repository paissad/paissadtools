package net.paissad.paissadtools.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import net.paissad.paissadtools.mail.exception.MailToolException;
import net.paissad.paissadtools.util.CommonUtils;

/**
 * Contains utilities/methods to retrieve emails.
 * 
 * @author paissad
 */
class MailRetrieve {

    private final MailToolSMTPSettings smtpSettings;

    MailRetrieve(final MailToolSMTPSettings smtpSettings) {
        this.smtpSettings = smtpSettings;
    }

    private MailToolSMTPSettings getSmtpSettings() {
        return this.smtpSettings;
    }

    List<Message> getMessages(final String folderName, final boolean debug) throws MailToolException {

        final String smtpUser = this.getSmtpSettings().getSmtpUser();
        if (!CommonUtils.assertNotBlank(smtpUser)) throw new IllegalArgumentException("The SMTP user is not set !");

        final Properties props = new MailTool(this.getSmtpSettings()).initializeSmtpProperties(this.getSmtpSettings());
        final Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(debug);

        try {
            final List<Message> result = new ArrayList<Message>();
            final boolean useSSL = this.getSmtpSettings().isSsl();
            final String protocol = (useSSL) ? MailProtocol.IMAPS.getFormalName() : MailProtocol.IMAP.getFormalName();
            final Store store = mailSession.getStore(protocol);
            final String portAsString = this.getSmtpSettings().getSmtpPort();
            if (CommonUtils.assertNotBlank(portAsString)) { // port is specified.
                store.connect(this.getSmtpSettings().getSmtpHost(), smtpUser, this.getSmtpSettings().getSmtpPassword());
            } else {
                store.connect(this.getSmtpSettings().getSmtpHost(), Integer.valueOf(portAsString).intValue(), smtpUser,
                        this.getSmtpSettings().getSmtpPassword());
            }
            final Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            for (final Message msg : folder.getMessages()) {
                result.add(msg);
            }
            return result;

        } catch (final Exception e) {
            throw new MailToolException("Error while retrieving messages : " + e.getMessage(), e);
        }
    }
}
