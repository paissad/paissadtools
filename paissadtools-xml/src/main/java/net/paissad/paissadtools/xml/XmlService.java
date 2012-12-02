package net.paissad.paissadtools.xml;

import net.paissad.paissadtools.api.AbstractToolService;
import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

public class XmlService extends AbstractToolService<XmlTool, XmlSettings> {

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
        return this.createEntry(new XmlSettings());
    }

    @Override
    public XmlTool createEntry(XmlSettings xmlSettings) throws IToolServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
