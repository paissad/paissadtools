package net.paissad.paissadtools.ftp;

import net.paissad.paissadtools.api.AbstractToolService;
import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;
import net.paissad.paissadtools.ftp.impl.FtpToolImpl;

/**
 * FTP service implementation.
 * 
 * @author paissad
 */
public class FtpToolService extends AbstractToolService<FtpTool, FtpToolSettings> {

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
    public FtpTool createEntry() throws IToolServiceException {
        return this.createEntry(new FtpToolSettings());
    }

    @Override
    public FtpTool createEntry(final FtpToolSettings ftpSettings) throws IToolServiceException {
        return new FtpToolImpl(ftpSettings);
    }

}
