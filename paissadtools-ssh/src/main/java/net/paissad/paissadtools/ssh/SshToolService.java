package net.paissad.paissadtools.ssh;

import net.paissad.paissadtools.api.AbstractToolService;
import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

/**
 * The SSH tool service implementation.
 * 
 * @author paissad
 */
public class SshToolService extends AbstractToolService<SshTool, SshToolSettings> {

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
    public SshTool createEntry() throws IToolServiceException {
        return this.createEntry(new SshToolSettings());
    }

    @Override
    public SshTool createEntry(SshToolSettings sshSettings) throws IToolServiceException {
        return SshFactory.getSsh(sshSettings);
    }

}
