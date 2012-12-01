package net.paissad.paissadtools.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ClassUtils;

public class ReflectUtils {

    private ReflectUtils() {
    }

    /**
     * @param className - The class for which we want to retrieve all the
     *            interfaces it has implemented.
     * @param jarFile - The jar file where to do the search.
     * @return The list of implemented interfaces or an empty list if no
     *         interface is implemented.
     * @throws IllegalArgumentException If one of the arguments is
     *             <code>null</code> or if the <tt>className</tt> is blank.
     * @throws IOException If an error occurs while searching the implemented
     *             interfaces.
     */
    public static Set<String> getAllInterfaces(final String className, final File jarFile) throws IOException {

        CommonUtils.assertNotBlankOrThrowException(className);
        CommonUtils.assertNotNullOrThrowException(jarFile);

        try {
            final URL jarURL = new URL("jar", "", jarFile.getCanonicalPath());
            final ClassLoader parentClassloader = ClassLoader.getSystemClassLoader();
            final URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { jarURL }, parentClassloader);
            Class<?> clazz = classLoader.loadClass(className);
            ClassUtils.getAllInterfaces(clazz);
            final Set<String> result = new TreeSet<String>();
            // TODO
            return result;

        } catch (Exception e) {
            final String errMsg = "Error while searching interfaces for the class '" + className + "'";
            throw new IOException(errMsg, e);

        } finally {
        }
    }

    /**
     * 
     * @param className - The class for which we want to retrieve all the
     *            interfaces it has implemented.
     * @param classDirname - The directory which contains .class file and from
     *            where to do the search.
     * @return The list of implemented interfaces or an empty list if no
     *         interface is implemented.
     */
    public static Set<String> getAllInterfaces(final String className, final String classDirname) {
        final Set<String> result = new TreeSet<String>();
        // TODO
        return result;
    }

    /*
     * XXX
     */
    public static void main(String[] args) throws Exception {
        Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("java.util.TreeSet");
        List<String> result = ClassUtils.getAllInterfaces(clazz);
        System.out.println(result);
        final File jarFile = new File("/home/paissad/Downloads/minus-java-core-0.0.1-jar-with-dependencies.jar");
        Set<String> aaa = ReflectUtils.getAllInterfaces("org.hibernate.ejb.metamodel.BasicTypeImpl", jarFile);
        System.out.println(aaa);
    }

}
