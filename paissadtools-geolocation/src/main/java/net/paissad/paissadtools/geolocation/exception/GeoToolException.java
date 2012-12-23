package net.paissad.paissadtools.geolocation.exception;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.api.IToolException;
import net.paissad.paissadtools.geolocation.GeoTool;

/**
 * This exception is thrown by the geolocation {@link ITool} -> {@link GeoTool}.
 * 
 * @author paissad
 */
public class GeoToolException extends IToolException {

    private static final long serialVersionUID = 1L;

    public GeoToolException() {
        this("");
    }

    public GeoToolException(final String message) {
        super(message);
    }

    public GeoToolException(final Throwable cause) {
        super(cause);
    }

    public GeoToolException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
