package net.paissad.paissadtools.mail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
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

import net.paissad.paissadtools.mail.exception.MailToolException;
import net.paissad.paissadtools.util.CommonUtils;

/**
 * This class contains utilities/methods to send emails.
 * 
 * @author paissad
 */
class MailSend {

    private final MailToolSMTPSettings smtpSettings;

    MailSend(final MailToolSMTPSettings smtpSettings) {
        this.smtpSettings = smtpSettings;
    }

    /**
     * Sends the email to the recipients.
     * 
     * @param msgSettings - The message settings.
     * @param debug - Whether or not to debug the process.
     * @throws MailToolException If an error occurs while sending the message.
     */
    void send(final MailToolMessageSettings msgSettings, final boolean debug) throws MailToolException {

        final String smtpUser = this.getSmtpSettings().getSmtpUser();
        if (!CommonUtils.assertNotBlank(smtpUser)) throw new IllegalArgumentException("The SMTP user is not set !");

        final Properties props = new MailTool(getSmtpSettings()).initializeSmtpProperties(this.getSmtpSettings());

        final Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(debug);
        Transport transport = null;
        try {
            // mail.transport.protocol => smtp
            transport = mailSession.getTransport();

            final MimeMessage message = new MimeMessage(mailSession);
            final String msgSubject = msgSettings.getSubject();
            message.setSubject(msgSubject);
            message.setSentDate(new Date());

            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(msgSettings.getContent(), "text/html; charset=utf-8");

            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(htmlPart);

            // Add the attachements files.
            this.addAttachementFilesToMessage(msgSettings.getAttachments(), mp);

            message.setContent(mp);

            // Add the retrieved recipients to the Message object ...
            this.addRecipients(message, msgSettings.getRecipientsTO(), Message.RecipientType.TO);
            this.addRecipients(message, msgSettings.getRecipientsCC(), Message.RecipientType.CC);
            this.addRecipients(message, msgSettings.getRecipientsBCC(), Message.RecipientType.BCC);

            message.setFrom(new InternetAddress(smtpUser, true));

            // Set the acknowledgment ...
            if (msgSettings.isAutoAcknowledge()) {
                message.setHeader("Disposition-Notification-To", smtpUser);
            }

            // Set a custom Message-ID. We concatenate the time and a generated UUID and the sender's host.
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
            final StringBuilder msgId = new StringBuilder()
                    .append(sdf.format(Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US).getTime()))
                    .append("__").append(UUID.randomUUID().toString().toLowerCase(Locale.ENGLISH)).append("__")
                    .append(smtpUser);
            message.setHeader("Message-ID", msgId.toString());

            // Save all settings.
            message.saveChanges();

            final String portAsString = this.getSmtpSettings().getSmtpPort();
            final int smtpPort = (CommonUtils.assertNotBlank(portAsString)) ? Integer.valueOf(portAsString).intValue()
                    : MailTool.DEFAULT_SMTP_PORT;
            transport.connect(this.getSmtpSettings().getSmtpHost(), smtpPort, this.getSmtpSettings().getSmtpUser(),
                    this.getSmtpSettings().getSmtpPassword());

            transport.sendMessage(message, message.getAllRecipients());

        } catch (final Exception e) {
            throw new MailToolException("Error while sending the mail : " + e.getMessage(), e);
        } finally {
            if (transport != null) try {
                transport.close();
            } catch (MessagingException e) { // Do nothing
                }
        }
    }

    // _________________________________________________________________________

    private MailToolSMTPSettings getSmtpSettings() {
        return this.smtpSettings;
    }

    /**
     * @param message - The message to use and where to add the recipients.
     * @param recipients - The recipients to add.
     * @param type - The type to use between (TO, CC, BCC)
     * 
     * @throws MessagingException
     */
    private void addRecipients(final Message message, final List<String> recipients, final Message.RecipientType type)
            throws MessagingException {

        if (recipients == null) return;
        for (final String recipient : recipients) {
            if (recipient == null) throw new IllegalArgumentException("The recipient cannot be null.");
            message.addRecipient(type, new InternetAddress(recipient));
        }
    }

    // _________________________________________________________________________

    /**
     * Adds some attachments files to a {@link Multipart} object.
     * 
     * @param multipart - The multipart object to use for adding the attachments to it !
     * @throws MessagingException
     */
    private void addAttachementFilesToMessage(final List<File> files, final Multipart multipart)
            throws MessagingException {

        if (files == null || files.isEmpty()) {
            return; // Get out ! There is no attachment file ...
        }

        // If there is at least one file, then attach them to the mail ...
        for (final File aFile : files) {
            if (aFile == null) throw new IllegalArgumentException("The file to send cannot be null.");
            if (!aFile.isFile()) throw new IllegalArgumentException(aFile + " is not a file or does not exist.");
            final MimeBodyPart attachFilePart = new MimeBodyPart();
            final FileDataSource fds = new FileDataSource(aFile);
            attachFilePart.setDataHandler(new DataHandler(fds));
            attachFilePart.setFileName(fds.getName());
            attachFilePart.setDisposition(Part.ATTACHMENT);
            multipart.addBodyPart(attachFilePart);
        }
    }
}
