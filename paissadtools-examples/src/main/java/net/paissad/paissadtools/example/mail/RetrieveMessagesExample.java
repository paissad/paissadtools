package net.paissad.paissadtools.example.mail;

import java.util.Collections;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;

import org.apache.commons.io.IOUtils;

import net.paissad.paissadtools.mail.MailTool;
import net.paissad.paissadtools.mail.MailToolSMTPSettings;

/**
 * Retrieve mails from an inbox.
 * 
 * @author paissad
 */
public class RetrieveMessagesExample {

    public static void main(final String[] args) throws Exception {
        final MailToolSMTPSettings smtpSettings = new MailToolSMTPSettings("from@gmail.com", "pass", "smtp.gmail.com",
                "465");
        smtpSettings.setAuth(true); // We need authentication
        smtpSettings.setSsl(true); // use SSL
        smtpSettings.setStartTLS(true); // use TLS

        final MailTool mailTool = new MailTool(smtpSettings);
        final List<Message> messages = mailTool.getMessages("inbox");
        Collections.reverse(messages);
        // final List<Message> messages = mailTool.getMessages("inbox", true); // use debugging
        for (final Message msg : messages) {
            System.out.println("No              : " + msg.getMessageNumber());
            System.out.println("Subject         : " + msg.getSubject());
            System.out.println("Content-type    : " + msg.getContentType());
            System.out.println("Disposition     : " + msg.getDisposition());
            System.out.println("Received date   : " + msg.getReceivedDate());
            System.out.println("From            : " + msg.getFrom()[0].toString());
            final Object content = msg.getContent();
            if (!(content instanceof Part)) {
                System.out.println("Content         : " + IOUtils.toString(msg.getInputStream()));

            } else {
                // The message has attachments.
                final Multipart partContent = (Multipart) content;
                for (int i = 0; i < partContent.getCount(); i++) {
                    final BodyPart bodyPart = partContent.getBodyPart(i);
                    if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                        // Usually text/plain, text/html, ...
                        System.out.println("Content            : " + IOUtils.toString(msg.getInputStream()));
                    } else {
                        System.out.println("Filename           : " + bodyPart.getFileName());
                        bodyPart.getInputStream(); // retrieves the file's content.
                        // continue ...
                    }
                }
            }
            System.out.println("------------------------------------------------------------------------------------");
        }
    }
}
