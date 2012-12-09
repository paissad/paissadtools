package net.paissad.paissadtools.api;

/**
 * Abstract class implementing {@link IToolService}.
 * 
 * @author paissad
 * 
 * @param <T>
 * @param <S>
 */
public abstract class AbstractToolService<T extends ITool, S extends IToolSettings> implements IToolService<T, S> {

    @Override
    public IToolService<T, S> getInstance() {
        return this;
    }

}
