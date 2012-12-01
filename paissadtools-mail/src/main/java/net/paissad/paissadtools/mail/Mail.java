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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.api.ServiceEntry;
import net.paissad.paissadtools.util.CommonUtils;

@Getter
@Setter
public class Mail implements ServiceEntry {
    
    /*
     * http://java.sun.com/developer/onlineTraining/JavaMail/contents.html
     */

    private static final long   serialVersionUID = 1L;

    private static final String SMTP_PROTOCOL    = "smtp";
    private static final String SMTPS_PROTOCOL   = "smtps";

    private MailSettings        mailSettings;

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
    public Mail(final MailSettings mailSettings) {
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
    public Mail(final MailSettings mailSettings, final String subject, final String content) {
        this.setMailSettings(mailSettings);
        this.setSubject(subject);
        this.setContent(content);
    }

    // _________________________________________________________________________

    /**
     * Sends the email to the recipients with no debugging messages.
     * 
     * @throws MessagingException
     * @see #send(boolean)
     */
    public void send() throws MessagingException {
        this.send(false);
    }

    /**
     * Sends the email to the recipients.
     * 
     * @param debug - Whether or not to debug the process.
     * @throws MessagingException
     */
    public void send(final boolean debug) throws MessagingException {

        final String smtpUser = this.getMailSettings().getSmtpUser();
        if (!CommonUtils.assertNotBlank(smtpUser)) {
            throw new IllegalArgumentException("The SMTP user is not set !");
        }

        final Properties props = this.initializeSmtpProperties();

        final Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(debug);
        Transport transport = null;
        try {
            // mail.transport.protocol => smtp
            transport = mailSession.getTransport();

            MimeMessage message = new MimeMessage(mailSession);
            String msgSubject = this.getSubject();
            message.setSubject(msgSubject);
            message.setSentDate(new Date());

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(this.getContent(), "text/html; charset=utf-8");

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);

            // Add the attachements files.
            this.addAttachementFilesToMessage(mp);

            message.setContent(mp);

            // Retrieve the recipients who should receive the mail.
            final Set<String> to_recipients = this.getRecipientsTO();
            final Set<String> cc_recipients = this.getRecipientsCC();
            final Set<String> bcc_recipients = this.getRecipientsBCC();

            // Add the retrieved recipients to the Message object ...
            this.addRecipients(message, to_recipients, Message.RecipientType.TO);
            this.addRecipients(message, cc_recipients, Message.RecipientType.CC);
            this.addRecipients(message, bcc_recipients, Message.RecipientType.BCC);

            message.setFrom(new InternetAddress(smtpUser, true));

            // Set the acknowledgement ...
            if (this.getMailSettings().isAutoAcknowledge()) {
                message.setHeader("Disposition-Notification-To", smtpUser);
            }

            // Set a custom Message-ID
            // We concatenate the time and a generated UUID and the sender's
            // host.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
            String msgId = sdf.format(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTime());
            msgId += "__" + UUID.randomUUID().toString().toLowerCase();
            msgId += "__" + smtpUser;
            message.setHeader("Message-ID", msgId);

            // Save all settings.
            message.saveChanges();

            transport.connect(
                    this.getMailSettings().getSmtpHost(),
                    this.getMailSettings().getSmtpPort(),
                    this.getMailSettings().getSmtpUser(),
                    this.getMailSettings().getSmtpPassword());
            
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

        boolean isSSL = this.getMailSettings().isSsl();
        if (isSSL) {
            props.setProperty("mail.transport.protocol", SMTPS_PROTOCOL);
            props.setProperty("mail.smtp.ssl.enable", String.valueOf(isSSL));
        }
        return props;
    }

    // _________________________________________________________________________

    /**
     * 
     * @param message - The message to use and where to add the recipients.
     * @param recipients - The recipients to add.
     * @param type - The type to use between (TO, CC, BCC)
     * 
     * @throws AddressException
     * @throws MessagingException
     */
    private void addRecipients(Message message, Set<String> recipients, Message.RecipientType type)
            throws AddressException, MessagingException {

        if (recipients == null) return;
        for (final String aRecipient : recipients) {
            message.addRecipient(type, new InternetAddress(aRecipient));
        }
    }

    // _________________________________________________________________________

    /**
     * Adds some attachements files to a {@link Multipart} object
     * 
     * @param multipart - The multipart object to use for adding the
     *            attachements to it !
     * @throws MessagingException
     */
    private void addAttachementFilesToMessage(Multipart multipart) throws MessagingException {

        final List<File> files = this.getAttachements();
        if (files == null || files.isEmpty()) {
            return; // Get out ! There is no attachement file ...
        }

        // If there is at least one file, then attach them to the mail ...
        for (final File aFile : files) {
            MimeBodyPart attachFilePart = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(aFile);
            attachFilePart.setDataHandler(new DataHandler(fds));
            attachFilePart.setFileName(fds.getName());
            attachFilePart.setDisposition(Part.ATTACHMENT);
            multipart.addBodyPart(attachFilePart);
        }
    }

}
