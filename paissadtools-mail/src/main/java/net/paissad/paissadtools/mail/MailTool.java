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
import net.paissad.paissadtools.util.CommonUtils;

/**
 * 
 * @author paissad
 */
@Getter
@Setter
public class MailTool implements ITool {

    /*
     * http://java.sun.com/developer/onlineTraining/JavaMail/contents.html
     */

    private static final String SMTP_PROTOCOL  = "smtp";
    private static final String SMTPS_PROTOCOL = "smtps";

    private MailToolSettings    mailSettings;

    private List<File>          attachements;
    private String              subject;
    private String              content;
    private Set<String>         recipientsTO;
    private Set<String>         recipientsCC;
    private Set<String>         recipientsBCC;

    // _________________________________________________________________________

    /**
     * @param mailSettings
     * @see #setRecipientsTO(Set)
     * @see #setAttachements(List)
     */
    public MailTool(final MailToolSettings mailSettings) {
        this(mailSettings, null, null);
    }

    /**
     * @param mailSettings
     * @param subject - The subject of the mail.
     * @param content - The content (body) of the mail. May be TEXT or HTML as
     *            well.
     * 
     * @see #setRecipientsTO(Set)
     * @see #setAttachements(List)
     */
    public MailTool(final MailToolSettings mailSettings, final String subject, final String content) {
        this.setMailSettings(mailSettings);
        this.setSubject(subject);
        this.setContent(content);
    }

    // _________________________________________________________________________

    /**
     * Sends the email to the recipients with no debugging messages.
     * 
     * @throws MessagingException If an error occurs while sending the message.
     * @see #send(boolean)
     */
    public void send() throws MessagingException {
        this.send(false);
    }

    /**
     * Sends the email to the recipients.
     * 
     * @param debug - Whether or not to debug the process.
     * @throws MessagingException If an error occurs while sending the message.
     */
    public void send(final boolean debug) throws MessagingException {

        final String smtpUser = this.getMailSettings().getSmtpUser();
        if (!CommonUtils.assertNotBlank(smtpUser)) throw new IllegalArgumentException("The SMTP user is not set !");

        final Properties props = this.initializeSmtpProperties();

        final Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(debug);
        Transport transport = null;
        try {
            // mail.transport.protocol => smtp
            transport = mailSession.getTransport();

            final MimeMessage message = new MimeMessage(mailSession);
            final String msgSubject = this.getSubject();
            message.setSubject(msgSubject);
            message.setSentDate(new Date());

            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(this.getContent(), "text/html; charset=utf-8");

            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);

            // Add the attachements files.
            this.addAttachementFilesToMessage(mp);

            message.setContent(mp);

            // Add the retrieved recipients to the Message object ...
            this.addRecipients(message, this.getRecipientsTO(), Message.RecipientType.TO);
            this.addRecipients(message, this.getRecipientsCC(), Message.RecipientType.CC);
            this.addRecipients(message, this.getRecipientsBCC(), Message.RecipientType.BCC);

            message.setFrom(new InternetAddress(smtpUser, true));

            // Set the acknowledgment ...
            if (this.getMailSettings().isAutoAcknowledge()) {
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

            transport.connect(this.getMailSettings().getSmtpHost(), this.getMailSettings().getSmtpPort(), this
                    .getMailSettings().getSmtpUser(), this.getMailSettings().getSmtpPassword());

            transport.sendMessage(message, message.getAllRecipients());

        } finally {
            if (transport != null) transport.close();
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
        props.setProperty("mail.host", this.getMailSettings().getSmtpHost());
        props.setProperty("mail.user", this.getMailSettings().getSmtpUser());
        props.setProperty("mail.password", this.getMailSettings().getSmtpPassword());
        props.setProperty("mail.smtp.port", String.valueOf(this.getMailSettings().getSmtpPort()));
        props.setProperty("mail.smtp.auth", String.valueOf(this.getMailSettings().isSmtpAuth()));
        props.setProperty("mail.smtp.starttls.enable", String.valueOf(this.getMailSettings().isStarttls()));

        final boolean useSSL = this.getMailSettings().isSsl();
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

        final List<File> files = this.getAttachements();
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
