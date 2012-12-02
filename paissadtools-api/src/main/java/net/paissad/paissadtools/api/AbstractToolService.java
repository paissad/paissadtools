package net.paissad.paissadtools.api;

public abstract class AbstractToolService<T extends ITool, S extends IToolSettings> implements IToolService<T, S> {

    @Override
    public IToolService<T, S> getInstance() {
        return this;
    }

}
