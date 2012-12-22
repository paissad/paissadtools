package net.paissad.paissadtools.mail;

/**
 * Represent some mail protocols.
 * 
 * @author paissad
 */
enum MailProtocol {

    /** smtp. */
    SMTP("smtp"),

    /** smtps. */
    SMTPS("smtps"),

    /** imap. */
    IMAP("imap"),

    /** imaps. */
    IMAPS("imaps"),

    /** pop3. */
    POP3("pop3"),

    /** pop3s. */
    POP3S("pop3s");

    private final String formalName;

    private MailProtocol(final String formalName) {
        this.formalName = formalName;
    }

    public String getFormalName() {
        return this.formalName;
    }

}
