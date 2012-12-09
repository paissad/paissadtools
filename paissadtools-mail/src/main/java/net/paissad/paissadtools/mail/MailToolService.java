package net.paissad.paissadtools.mail;

import net.paissad.paissadtools.api.AbstractToolService;
import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolService;
import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

/**
 * The {@link IToolService} implementation for the mail {@link ITool}.
 * 
 * @author paissad
 */
public class MailToolService extends AbstractToolService<MailTool, MailToolSettings> {

    private boolean ready;

    @Override
    public void init() throws IToolServiceRuntimeException {
        this.ready = true;
    }

    @Override
    public void destroy() throws IToolServiceRuntimeException {
        this.ready = false;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public MailTool createEntry() throws IToolServiceException {
        return this.createEntry(new MailToolSettings());
    }

    @Override
    public MailTool createEntry(final MailToolSettings mailSettings) throws IToolServiceException {
        return new MailTool(mailSettings);
    }

}
