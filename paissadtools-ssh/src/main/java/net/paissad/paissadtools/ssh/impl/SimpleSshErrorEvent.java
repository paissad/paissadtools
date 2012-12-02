package net.paissad.paissadtools.ssh.impl;

import net.paissad.paissadtools.ssh.SshErrorEvent;

/**
 * @author paissad
 */
public class SimpleSshErrorEvent implements SshErrorEvent {

    private Exception exception;

    public SimpleSshErrorEvent(Exception e) {
        this.exception = e;
    }

    @Override
    public Object getReason() {
        return this.exception;
    }

}
