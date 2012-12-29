package net.paissad.paissadtools.diff.impl.properties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import net.paissad.paissadtools.diff.api.IDiffResult;
import net.paissad.paissadtools.diff.api.IDiffTool;
import net.paissad.paissadtools.diff.exception.DiffToolException;
import net.paissad.paissadtools.diff.impl.DiffResult;
import net.paissad.paissadtools.diff.util.IOUtil;

/**
 * Tool for comparing properties files.
 * 
 * @see PropertiesDiffOption
 * @see PropertiesDiffExclusionRule
 * 
 * @author paissad
 */
public class PropertiesDiffTool implements IDiffTool<File, PropertiesDiffOption> {

    private static final String LINE_SEP = System.getProperty("line.separator");

    @Override
    public IDiffResult compare(final File propsFile1, final File propsFile2, final PropertiesDiffOption option)
            throws DiffToolException {

        this.assertNonNull(propsFile1, propsFile2);
        this.assertFiles(propsFile1, propsFile2);

        InputStream in1 = null;
        InputStream in2 = null;
        try {

            final PropertiesDiffOption propertiesDiffOption = (option != null) ? option : this.getDefaultOptions();
            final DiffResult result = new DiffResult();

            if (propertiesDiffOption.getExclusionRules() != null) {
                for (final PropertiesDiffExclusionRule exclusionRule : propertiesDiffOption.getExclusionRules()) {
                    if (exclusionRule.match(propsFile1) || exclusionRule.match(propsFile2)) {
                        result.setExcluded(true);
                        result.setReport(propsFile1 + " and " + propsFile2
                                + " are not compared due to exclusion rule match.");
                        return result;
                    }
                }
            }

            in1 = new BufferedInputStream(new FileInputStream(propsFile1));
            in2 = new BufferedInputStream(new FileInputStream(propsFile2));

            final boolean compareValues = !propertiesDiffOption.isSkipValues();

            final Properties props1 = new CustomProperties(compareValues);
            final Properties props2 = new CustomProperties(compareValues);

            props1.load(in1);
            props2.load(in2);

            final boolean similar = props1.equals(props2);
            
            if (propertiesDiffOption.isVerbose()) {
                if (similar) {
                    result.setReport(this.getQuietReport(propsFile1, propsFile2, similar));
                } else {
                    result.setReport(this.getVerboseReport(propsFile1, propsFile2, props1, props2, compareValues));
                }
            } else {
                result.setReport(this.getQuietReport(propsFile1, propsFile2, similar));
            }

            result.setSimilar(similar);
            return result;

        } catch (final Exception e) {
            throw new DiffToolException("An error occurred during properties comparison : " + e.getMessage(), e);

        } finally {
            IOUtil.closeQuietly(in1, in2);
        }
    }

    private String getVerboseReport(final File f1, final File f2, final Properties props1, final Properties props2,
            final boolean compareValues) {

        final StringBuilder report = new StringBuilder();

        final Iterator<Entry<Object, Object>> iter1 = props1.entrySet().iterator();
        while (iter1.hasNext()) {
            final Entry<Object, Object> o1 = iter1.next();
            final String key1 = (String) o1.getKey();
            final String value1 = (String) o1.getValue();

            boolean key1Found = false;

            final Iterator<Entry<Object, Object>> iter2 = props2.entrySet().iterator();
            while (iter2.hasNext()) {
                final Entry<Object, Object> o2 = iter2.next();
                final String key2 = (String) o2.getKey();
                final String value2 = (String) o2.getValue();

                if (key1.equals(key2)) {
                    key1Found = true;
                    if (compareValues && !value1.equals(value2)) {
                        report.append("Key '").append(key1).append("' : values are different.").append(LINE_SEP);
                    }
                    break;
                }
            }
            if (!key1Found) {
                report.append("Only in ").append(f1).append(" : ").append(key1).append(LINE_SEP);
            }
        }

        // Now we look for keys present in the latter file, but not in the former one.
        final Set<Object> keys2 = props2.keySet();
        keys2.removeAll(props1.keySet());
        for (final Object key2 : keys2) {
            report.append("Only in ").append(f2).append(" : ").append(key2).append(LINE_SEP);
        }

        return report.toString();
    }

    private String getQuietReport(final File f1, final File f2, final boolean similar) {
        if (similar) return f1 + " and " + f2 + " are similar.";
        return f1 + " and " + f2 + " are different.";
    }

    private PropertiesDiffOption getDefaultOptions() {
        return new PropertiesDiffOption();
    }

    private void assertFiles(final File... files) throws IllegalArgumentException {
        for (final File f : files) {
            if (!f.isFile()) throw new IllegalArgumentException("The object to compare must not be null");
        }
    }

    private void assertNonNull(final Object... objects) throws IllegalArgumentException {
        for (final Object obj : objects) {
            if (obj == null) throw new IllegalArgumentException("The object to compare must not be null");
        }
    }

}
