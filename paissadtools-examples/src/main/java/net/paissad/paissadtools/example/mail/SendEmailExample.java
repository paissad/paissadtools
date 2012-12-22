package net.paissad.paissadtools.example.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.paissad.paissadtools.mail.MailTool;
import net.paissad.paissadtools.mail.MailToolMessageSettings;
import net.paissad.paissadtools.mail.MailToolSMTPSettings;
import net.paissad.paissadtools.mail.exception.MailToolException;

/**
 * Email sending code example.
 * 
 * @author paissad
 */
public class SendEmailExample {

    /*
     * Basically, sends an email to multiple users.
     */
    public static void main(final String[] args) throws MailToolException {

        final MailToolSMTPSettings smtpSettings = new MailToolSMTPSettings("from@gmail.com", "pass", "smtp.gmail.com",
                "465");
        smtpSettings.setAuth(true); // We need authentication
        smtpSettings.setSsl(true); // use SSL
        smtpSettings.setStartTLS(true); // use TLS

        final MailToolMessageSettings msgSettings = new MailToolMessageSettings();
        msgSettings.setSubject("[MailTool] - Test for mail sending.");
        msgSettings.setContent("This is a just a test with some attachment files ...");

        final List<String> recipientsTO = msgSettings.getRecipientsTO();
        recipientsTO.add("to@yahoo.fr");

        final List<String> recipientsCC = msgSettings.getRecipientsCC();
        recipientsCC.add("cc@gmail.com");

        msgSettings.setRecipientsTO(recipientsTO);
        msgSettings.setRecipientsCC(recipientsCC);

        final List<File> attachments = new ArrayList<File>();
        attachments.add(new File("pom.xml"));
        attachments.add(new File("src/main/java/" + SendEmailExample.class.getName().replaceAll("\\.", "/") + ".java"));
        msgSettings.setAttachments(attachments);

        // Let's ask for an acknowledgment
        msgSettings.setAutoAcknowledge(true);

        final MailTool mailTool = new MailTool(smtpSettings);
        // mailTool.send(msgSettings);
        mailTool.send(msgSettings, true); // use debugging, for testing purpose only !
    }
}
