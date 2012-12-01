package net.paissad.paissadtools.ssh;

import net.paissad.paissadtools.api.AbstractService;
import net.paissad.paissadtools.exception.ServiceException;
import net.paissad.paissadtools.exception.ServiceRuntimeException;

/**
 * The SSH service implementation.
 * 
 * @author paissad
 * @since 0.1
 */
public class SshService extends AbstractService<Ssh, SshSettings> {

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
    public Ssh createEntry() throws ServiceException {
        return this.createEntry(new SshSettings());
    }

    @Override
    public Ssh createEntry(SshSettings sshSettings) throws ServiceException {
        return SshFactory.getSsh(sshSettings);
    }

}
