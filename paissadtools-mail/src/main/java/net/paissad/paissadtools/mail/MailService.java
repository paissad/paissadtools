package net.paissad.paissadtools.mail;

import net.paissad.paissadtools.api.AbstractService;
import net.paissad.paissadtools.exception.ServiceException;
import net.paissad.paissadtools.exception.ServiceRuntimeException;

public class MailService extends AbstractService<Mail, MailSettings> {

    private boolean ready;

    @Override
    public void init() throws ServiceRuntimeException {
        this.ready = true;
    }

    @Override
    public void destroy() throws ServiceRuntimeException {
        this.ready = false;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public Mail createEntry() throws ServiceException {
        return this.createEntry(new MailSettings());
    }

    @Override
    public Mail createEntry(final MailSettings mailSettings) throws ServiceException {
        return new Mail(mailSettings);
    }

}
