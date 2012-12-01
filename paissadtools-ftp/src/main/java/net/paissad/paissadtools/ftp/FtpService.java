package net.paissad.paissadtools.ftp;

import net.paissad.paissadtools.api.AbstractService;
import net.paissad.paissadtools.exception.ServiceException;
import net.paissad.paissadtools.exception.ServiceRuntimeException;
import net.paissad.paissadtools.ftp.impl.FtpImpl;

/**
 * FTP service implementation.
 * 
 * @author paissad
 * @since 0.1
 */
public class FtpService extends AbstractService<Ftp, FtpSettings> {

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
    public Ftp createEntry() throws ServiceException {
        return this.createEntry(new FtpSettings());
    }

    @Override
    public Ftp createEntry(FtpSettings ftpSettings) throws ServiceException {
        return new FtpImpl(ftpSettings);
    }

}
