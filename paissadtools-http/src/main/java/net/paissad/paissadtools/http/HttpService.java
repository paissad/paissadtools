package net.paissad.paissadtools.http;

import net.paissad.paissadtools.api.AbstractService;
import net.paissad.paissadtools.exception.ServiceException;
import net.paissad.paissadtools.exception.ServiceRuntimeException;

public class HttpService extends AbstractService<HttpTool, HttpSettings> {

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
    public HttpTool createEntry() throws ServiceException {
        return this.createEntry(new HttpSettings());
    }

    @Override
    public HttpTool createEntry(HttpSettings httpSettings) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
