package net.paissad.paissadtools.api;

import net.paissad.paissadtools.exception.IToolServiceException;
import net.paissad.paissadtools.exception.IToolServiceRuntimeException;

/**
 * This is the top entry interface which allows the initialization, destruction,
 * instance creation of {@link ITool} (tools) to use.
 * 
 * @author paissad
 * 
 * @param <T>
 * @param <S>
 * 
 * @see ITool
 * @see IToolSettings
 */
public interface IToolService<T extends ITool, S extends IToolSettings> {

    /**
     * Returns an instance of this service.
     * 
     * @return An instance of this service.
     */
    IToolService<T, S> getInstance();

    /**
     * Initialize the instance of this service so that it can be used.
     * 
     * @throws IToolServiceRuntimeException If an error occurs while
     *             initializing the instance of the service.
     */
    void init() throws IToolServiceRuntimeException;

    /**
     * Destroy the instance of this service.
     * 
     * @throws IToolServiceRuntimeException If an error occurs while destroying
     *             the instance of the service.
     */
    void destroy() throws IToolServiceRuntimeException;

    /**
     * Checks whether or not this instance of service is ready for use.
     * 
     * @return <code>true</code> if this instance of service is ready for use,
     *         <code>false</code> otherwise.
     */
    boolean isReady();

    /**
     * Create a {@link ITool} entry to use by using the default tool settings.
     * 
     * @return An entry of the {@link ITool} served by this service.
     * @throws IToolServiceException If an error occurs while creating a tool
     *             entry.
     */
    T createEntry() throws IToolServiceException;

    /**
     * Create a {@link ITool} entry to use by using the specified settings.
     * 
     * @param serviceSettings - The custom settings to use while creating the
     *            tool to use.
     * @return An entry of the {@link ITool} served by this service.
     * @throws IToolServiceException If an error occurs while creating a tool
     *             entry.
     */
    T createEntry(final S serviceSettings) throws IToolServiceException;

}
