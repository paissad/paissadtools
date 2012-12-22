package net.paissad.paissadtools.mail;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;

import lombok.Getter;
import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.mail.exception.MailToolException;

/**
 * Mail tool.
 * 
 * @author paissad
 */
public class MailTool implements ITool {

    /*
     * FAQ : http://www.oracle.com/technetwork/java/javamail/faq-135477.html
     * 
     * http://java.sun.com/developer/onlineTraining/JavaMail/contents.html
     */

    /** The default SMTP port to use for SMTP settings if none is specified. */
    public static final int            DEFAULT_SMTP_PORT = 25;

    @Getter
    private final MailToolSMTPSettings smtpSettings;

    /**
     * @param smtpSettings - The SMTP settings.
     * @throws IllegalArgumentException If the SMTP settings is <code>null</code>.
     */
    public MailTool(final MailToolSMTPSettings smtpSettings) throws IllegalArgumentException {
        if (smtpSettings == null) throw new IllegalArgumentException("The SMTP settings cannot be null.");
        this.smtpSettings = smtpSettings;
    }

    /**
     * Sends the email to the recipients with no debugging messages.
     * 
     * @param msgSettings - The message settings.
     * @throws MailToolException If an error occurs while sending the message.
     * @see #send(MailToolMessageSettings, boolean)
     */
    public void send(final MailToolMessageSettings msgSettings) throws MailToolException {
        this.send(msgSettings, false);
    }

    /**
     * Sends the email to the recipients.
     * 
     * @param msgSettings - The message settings.
     * @param debug - Whether or not to debug the process.
     * @throws MailToolException If an error occurs while sending the message.
     */
    public void send(final MailToolMessageSettings msgSettings, final boolean debug) throws MailToolException {
        new MailSend(this.getSmtpSettings()).send(msgSettings, debug);
    }

    /**
     * Retrieves the list of messages for a specified folder.
     * 
     * @param folderName - The name of the folder for which we want to retrieve the messages.
     * @return The list of message for the specified folder.
     * @throws MailToolException If an error occurs while retrieving the messages.
     * @see #getMessages(String, boolean)
     */
    public List<Message> getMessages(final String folderName) throws MailToolException {
        return this.getMessages(folderName, false);
    }

    /**
     * Retrieves the list of messages for a specified folder.
     * 
     * @param folderName - The name of the folder for which we want to retrieve the messages.
     * @param debug - Whether or not to debug the process.
     * @return The list of message for the specified folder.
     * @throws MailToolException If an error occurs while retrieving the messages.
     * @see #getMessages(String)
     */
    public List<Message> getMessages(final String folderName, final boolean debug) throws MailToolException {
        return new MailRetrieve(this.getSmtpSettings()).getMessages(folderName, debug);
    }

    // _________________________________________________________________________

    /**
     * Initializes the properties of the SMTP service to use.
     * 
     * @return The properties for the SMTP service to use.
     */
    Properties initializeSmtpProperties(final MailToolSMTPSettings settings) {

        /*
         * http://javamail.kenai.com/nonav/javadocs/com/sun/mail/smtp/package-summary.html
         */

        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", MailProtocol.SMTP.getFormalName());
        props.setProperty("mail.host", this.getSmtpSettings().getSmtpHost());
        props.setProperty("mail.user", this.getSmtpSettings().getSmtpUser());
        props.setProperty("mail.password", this.getSmtpSettings().getSmtpPassword());
        props.setProperty("mail.smtp.port", String.valueOf(this.getSmtpSettings().getSmtpPort()));
        props.setProperty("mail.smtp.auth", String.valueOf(this.getSmtpSettings().isAuth()));
        props.setProperty("mail.smtp.starttls.enable", String.valueOf(this.getSmtpSettings().isStartTLS()));

        final boolean useSSL = this.getSmtpSettings().isSsl();
        if (useSSL) {
            props.setProperty("mail.transport.protocol", MailProtocol.SMTPS.getFormalName());
            props.setProperty("mail.smtp.ssl.enable", String.valueOf(useSSL));
        }

        props.put("mail.smtp.dsn.notify", "SUCCESS,FAILURE,DELAY ORCPT=rfc822;" + settings.getSmtpUser());
        props.put("mail.smtp.dsn.ret", "HDRS");

        return props;
    }

}
