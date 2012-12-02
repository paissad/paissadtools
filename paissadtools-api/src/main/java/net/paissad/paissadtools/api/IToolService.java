package net.paissad.paissadtools.api;

import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

public interface IToolService<T extends ITool, S extends IToolSettings> {

    IToolService<T, S> getInstance();

    void init() throws IToolServiceRuntimeException;

    void destroy() throws IToolServiceRuntimeException;

    boolean isReady();

    T createEntry() throws IToolServiceException;

    T createEntry(final S serviceSettings) throws IToolServiceException;

}
