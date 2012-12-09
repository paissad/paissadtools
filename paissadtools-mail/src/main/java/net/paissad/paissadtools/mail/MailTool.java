package net.paissad.paissadtools.mail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.mail.exception.MailToolException;
import net.paissad.paissadtools.util.CommonUtils;

/**
 * 
 * @author paissad
 */
public class MailTool implements ITool {

    /*
     * http://java.sun.com/developer/onlineTraining/JavaMail/contents.html
     */

    private static final String        SMTP_PROTOCOL  = "smtp";
    private static final String        SMTPS_PROTOCOL = "smtps";

    @Getter
    private final MailToolSMTPSettings smtpSettings;

    @Getter
    @Setter
    private MailToolMessageSettings    messageSettings;

    // _________________________________________________________________________

    /**
     * @param smtpSettings - The SMTP settings.
     * @throws IllegalArgumentException If the SMTP settings is
     *             <code>null</code>.
     */
    public MailTool(final MailToolSMTPSettings smtpSettings) {
        this(smtpSettings, null);
    }

    /**
     * @param smtpSettings - The SMTP settings.
     * @param messageSettings - The message settings.
     * @throws IllegalArgumentException If the SMTP settings is
     *             <code>null</code>.
     */
    public MailTool(final MailToolSMTPSettings smtpSettings, final MailToolMessageSettings messageSettings)
            throws IllegalArgumentException {
        if (smtpSettings == null) throw new IllegalArgumentException("The SMTP settings cannot be null.");
        this.smtpSettings = smtpSettings;
        this.messageSettings = messageSettings;
    }

    // _________________________________________________________________________

    /**
     * Sends the email to the recipients with no debugging messages.
     * 
     * @throws MailToolException If an error occurs while sending the message.
     * @see #send(boolean)
     */
    public void send() throws MailToolException {
        this.send(false);
    }

    /**
     * Sends the email to the recipients.
     * 
     * @param debug - Whether or not to debug the process.
     * @throws MailToolException If an error occurs while sending the message.
     */
    public void send(final boolean debug) throws MailToolException {

        final String smtpUser = this.getSmtpSettings().getSmtpUser();
        if (!CommonUtils.assertNotBlank(smtpUser)) throw new IllegalArgumentException("The SMTP user is not set !");

        final Properties props = this.initializeSmtpProperties();

        final Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(debug);
        Transport transport = null;
        try {
            // mail.transport.protocol => smtp
            transport = mailSession.getTransport();

            final MimeMessage message = new MimeMessage(mailSession);
            final String msgSubject = this.getMessageSettings().getSubject();
            message.setSubject(msgSubject);
            message.setSentDate(new Date());

            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(this.getMessageSettings().getContent(), "text/html; charset=utf-8");

            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);

            // Add the attachements files.
            this.addAttachementFilesToMessage(mp);

            message.setContent(mp);

            // Add the retrieved recipients to the Message object ...
            this.addRecipients(message, this.getMessageSettings().getRecipientsTO(), Message.RecipientType.TO);
            this.addRecipients(message, this.getMessageSettings().getRecipientsCC(), Message.RecipientType.CC);
            this.addRecipients(message, this.getMessageSettings().getRecipientsBCC(), Message.RecipientType.BCC);

            message.setFrom(new InternetAddress(smtpUser, true));

            // Set the acknowledgment ...
            if (this.getSmtpSettings().isAutoAcknowledge()) {
                message.setHeader("Disposition-Notification-To", smtpUser);
            }

            // Set a custom Message-ID
            // We concatenate the time and a generated UUID and the sender's
            // host.
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
            final StringBuilder msgId = new StringBuilder()
                    .append(sdf.format(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTime()))
                    .append("__").append(UUID.randomUUID().toString().toLowerCase(Locale.ENGLISH)).append("__")
                    .append(smtpUser);
            message.setHeader("Message-ID", msgId.toString());

            // Save all settings.
            message.saveChanges();

            transport.connect(this.getSmtpSettings().getSmtpHost(), this.getSmtpSettings().getSmtpPort(), this
                    .getSmtpSettings().getSmtpUser(), this.getSmtpSettings().getSmtpPassword());

            transport.sendMessage(message, message.getAllRecipients());

        } catch (final Exception e) {
            throw new MailToolException("Error while sending the mail.", e);
        } finally {
            if (transport != null) try {
                transport.close();
            } catch (MessagingException e) {
                // Do nothing
            }
        }
    }

    // _________________________________________________________________________

    /**
     * Initializes the properties of the SMTP service to use.
     * 
     * @return The properties for the SMTP service to use.
     */
    private Properties initializeSmtpProperties() {

        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", SMTP_PROTOCOL);
        props.setProperty("mail.host", this.getSmtpSettings().getSmtpHost());
        props.setProperty("mail.user", this.getSmtpSettings().getSmtpUser());
        props.setProperty("mail.password", this.getSmtpSettings().getSmtpPassword());
        props.setProperty("mail.smtp.port", String.valueOf(this.getSmtpSettings().getSmtpPort()));
        props.setProperty("mail.smtp.auth", String.valueOf(this.getSmtpSettings().isSmtpAuth()));
        props.setProperty("mail.smtp.starttls.enable", String.valueOf(this.getSmtpSettings().isStarttls()));

        final boolean useSSL = this.getSmtpSettings().isSsl();
        if (useSSL) {
            props.setProperty("mail.transport.protocol", SMTPS_PROTOCOL);
            props.setProperty("mail.smtp.ssl.enable", String.valueOf(useSSL));
        }
        return props;
    }

    // _________________________________________________________________________

    /**
     * @param message - The message to use and where to add the recipients.
     * @param recipients - The recipients to add.
     * @param type - The type to use between (TO, CC, BCC)
     * 
     * @throws MessagingException
     */
    private void addRecipients(final Message message, final Set<String> recipients, final Message.RecipientType type)
            throws MessagingException {

        if (recipients == null) return;
        for (final String aRecipient : recipients) {
            message.addRecipient(type, new InternetAddress(aRecipient));
        }
    }

    // _________________________________________________________________________

    /**
     * Adds some attachments files to a {@link Multipart} object
     * 
     * @param multipart - The multipart object to use for adding the attachments
     *            to it !
     * @throws MessagingException
     */
    private void addAttachementFilesToMessage(final Multipart multipart) throws MessagingException {

        final List<File> files = this.getMessageSettings().getAttachements();
        if (files == null || files.isEmpty()) {
            return; // Get out ! There is no attachment file ...
        }

        // If there is at least one file, then attach them to the mail ...
        for (final File aFile : files) {
            final MimeBodyPart attachFilePart = new MimeBodyPart();
            final FileDataSource fds = new FileDataSource(aFile);
            attachFilePart.setDataHandler(new DataHandler(fds));
            attachFilePart.setFileName(fds.getName());
            attachFilePart.setDisposition(Part.ATTACHMENT);
            multipart.addBodyPart(attachFilePart);
        }
    }

}
