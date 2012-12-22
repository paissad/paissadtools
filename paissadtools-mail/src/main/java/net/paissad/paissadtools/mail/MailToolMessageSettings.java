package net.paissad.paissadtools.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * This class contains the settings of mail / message.<br>
 * Settings not related to the mail/smtp server, but only to a message.
 * 
 * @author paissad
 */
@Getter
@Setter
public class MailToolMessageSettings {

    private String       subject;
    private String       content;
    /** If set to true, then receive an acknowledgment after sending the mail. */
    private boolean      autoAcknowledge;
    private List<String> recipientsTO;
    private List<String> recipientsCC;
    private List<String> recipientsBCC;
    private List<File>   attachments;

    /**
     * Default constructor. recipientsTO, recipientsCC, recipientsBCC & attachments lists are each initialized to an
     * empty list.<br>
     * And ACK is set to <code>false</code> too.
     */
    public MailToolMessageSettings() {
        this.recipientsTO = new ArrayList<String>();
        this.recipientsCC = new ArrayList<String>();
        this.recipientsBCC = new ArrayList<String>();
        this.attachments = new ArrayList<File>();
        // By default, do not ask for an acknowledgment when sending a mail.
        this.autoAcknowledge = false;
    }

}
