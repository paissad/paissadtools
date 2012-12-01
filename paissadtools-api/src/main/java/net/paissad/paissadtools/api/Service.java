package net.paissad.paissadtools.api;

import net.paissad.paissadtools.exception.ServiceException;
import net.paissad.paissadtools.exception.ServiceRuntimeException;

public interface Service<T extends ServiceEntry, S extends ServiceSettings> {

    public Service<T, S> getInstance();

    public void init() throws ServiceRuntimeException;

    public void destroy() throws ServiceRuntimeException;

    public boolean isReady();

    public T createEntry() throws ServiceException;

    public T createEntry(final S serviceSettings) throws ServiceException;

}
