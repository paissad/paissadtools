package net.paissad.paissadtools.http;

import net.paissad.paissadtools.api.AbstractToolService;
import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolService;
import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

/**
 * The {@link IToolService} implementation for the HTTP {@link ITool}.
 * 
 * @author paissad
 */
public class HttpToolService extends AbstractToolService<HttpTool, HttpToolSettings> {

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
    public HttpTool createEntry() throws IToolServiceException {
        return this.createEntry(new HttpToolSettings());
    }

    @Override
    public HttpTool createEntry(final HttpToolSettings httpSettings) throws IToolServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
