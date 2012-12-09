package net.paissad.paissadtools.xml;

import net.paissad.paissadtools.api.AbstractToolService;
import net.paissad.paissadtools.api.IToolService;
import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

/**
 * The {@link IToolService} implementation for {@link XmlTool}.
 * 
 * @author paissad
 * 
 */
public class XmlService extends AbstractToolService<XmlTool, XmlToolSettings> {

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
    public XmlTool createEntry() throws IToolServiceException {
        return this.createEntry(new XmlToolSettings());
    }

    @Override
    public XmlTool createEntry(final XmlToolSettings xmlSettings) throws IToolServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
