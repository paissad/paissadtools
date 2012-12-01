package net.paissad.paissadtools.xml;

import net.paissad.paissadtools.api.AbstractService;
import net.paissad.paissadtools.exception.ServiceException;
import net.paissad.paissadtools.exception.ServiceRuntimeException;

public class XmlService extends AbstractService<XmlTool, XmlSettings> {

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
    public XmlTool createEntry() throws ServiceException {
        return this.createEntry(new XmlSettings());
    }

    @Override
    public XmlTool createEntry(XmlSettings xmlSettings) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

}
