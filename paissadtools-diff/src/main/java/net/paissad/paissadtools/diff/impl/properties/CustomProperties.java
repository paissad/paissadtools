package net.paissad.paissadtools.diff.impl.properties;

import java.util.Properties;

/**
 * @author paissad
 */
class CustomProperties extends Properties {

    private static final long serialVersionUID = 1L;

    private final boolean     compareValues;

    public CustomProperties(final boolean compareValues) {
        this.compareValues = compareValues;
    }

    @Override
    public int hashCode() {
        if (this.compareValues) return super.hashCode();
        return this.keySet().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this.compareValues) return super.equals(obj);
        return this.keySet().equals(((CustomProperties) obj).keySet());
    }
}
