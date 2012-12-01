package net.paissad.paissadtools.api;

public abstract class AbstractService<T extends ServiceEntry, S extends ServiceSettings> implements Service<T, S> {

    @Override
    public Service<T, S> getInstance() {
        return this;
    }

}
