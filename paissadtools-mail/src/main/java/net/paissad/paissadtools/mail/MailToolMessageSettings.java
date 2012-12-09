package net.paissad.paissadtools.mail;

import java.io.File;
import java.util.List;
import java.util.Set;

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

    private List<File>  attachements;
    private String      subject;
    private String      content;
    private Set<String> recipientsTO;
    private Set<String> recipientsCC;
    private Set<String> recipientsBCC;

}
