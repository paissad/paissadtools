package net.paissad.paissadtools.diff.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * {@link IDiffTool} factory.
 * 
 * @author paissad
 */
@SuppressWarnings("rawtypes")
public class DiffToolFactory {

    private final ServiceLoader<IDiffTool> serviceLoader;
    private final List<IDiffTool>          diffTools;
    private final Object                   lock;
    private boolean                        initialized;

    public DiffToolFactory() {
        this.serviceLoader = ServiceLoader.load(IDiffTool.class);
        this.diffTools = new ArrayList<IDiffTool>();
        this.lock = new Object();
    }

    /**
     * @return The available {@link IDiffTool} tools found into the classpath.
     */
    public List<IDiffTool> getAvailablesDiffTools() {

        synchronized (this.lock) {
            this.diffTools.clear();
            this.initialized = false;
            this.serviceLoader.reload();
            final Iterator<IDiffTool> iter = this.serviceLoader.iterator();
            while (iter.hasNext()) {
                this.diffTools.add(iter.next());
            }
            this.initialized = true;
            return this.diffTools;
        }
    }

    /**
     * @param classname - The qualified class name of the {@link IDiffTool} implementation.
     * @return An instance of the specified {@link IDiffTool}.
     * @throws NullPointerException If the specified class name is <code>null</code>.
     * @throws ClassNotFoundException If no {@link IDiffTool} of the specified class name if found into the classpath.
     */
    public IDiffTool getDiffTool(final String classname) throws NullPointerException, ClassNotFoundException {

        if (classname == null) throw new NullPointerException("The class name must not be null.");

        if (!this.initialized) {
            this.getAvailablesDiffTools();
        }

        synchronized (this.lock) {
            for (final IDiffTool tool : this.diffTools) {
                if (tool.getClass().getName().equals(classname)) {
                    return tool;
                }
            }
        }
        throw new ClassNotFoundException(classname);
    }
}
