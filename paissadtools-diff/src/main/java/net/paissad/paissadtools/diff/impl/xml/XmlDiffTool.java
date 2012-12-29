package net.paissad.paissadtools.diff.impl.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import net.paissad.paissadtools.diff.api.IDiffResult;
import net.paissad.paissadtools.diff.api.IDiffTool;
import net.paissad.paissadtools.diff.exception.DiffToolException;
import net.paissad.paissadtools.diff.impl.DiffResult;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tool for comparing XML contents.
 * 
 * @param <T> - The type of the object to compare:<br>
 *            <span style='color:red'><b>NOTE :</b></span> The object must be of one of the following types :
 *            <ul>
 *            <li>{@link String}</li>
 *            <li>{@link File}</li>
 *            <li>{@link Reader}</li>
 *            <li>{@link InputSource}</li>
 *            <li>{@link Document}</li>
 *            </ul>
 * 
 * @see XmlDiffOption
 * @see XmlDiffExclusionRule
 * 
 * @author paissad
 */
public class XmlDiffTool<T> implements IDiffTool<T, XmlDiffOption<T>> {

    static {
        XMLUnit.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        XMLUnit.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
    }

    @Override
    public IDiffResult compare(final T obj1, final T obj2, final XmlDiffOption<T> option) throws DiffToolException {

        this.assertNonNull(obj1, obj2);

        try {

            final XmlDiffOption<T> xmlDiffOption = (option != null) ? option : this.getDefaultOptions();
            final DiffResult result = new DiffResult();

            XMLUnit.setIgnoreComments(xmlDiffOption.isIgnoreComments());
            XMLUnit.setIgnoreWhitespace(xmlDiffOption.isIgnoreWhiteSpaces());
            XMLUnit.setIgnoreAttributeOrder(xmlDiffOption.isIgnoreAttributesOrder());
            XMLUnit.setNormalize(true);

            if (xmlDiffOption.getExclusionRules() != null) {
                for (final XmlDiffExclusionRule<T> exclusionRule : xmlDiffOption.getExclusionRules()) {
                    if (exclusionRule.match(obj1) || exclusionRule.match(obj2)) {
                        result.setExcluded(true);
                        result.setReport(obj1 + " and " + obj2 + " are not compared due to exclusion rule match.");
                        return result;
                    }
                }
            }

            final Diff diff = this.newDiffInstance(obj1, obj2, xmlDiffOption.isVerbose());

            result.setSimilar(diff.similar());

            if (xmlDiffOption.isVerbose()) {
                final DetailedDiff detailedDiff = (DetailedDiff) diff;
                final StringBuilder sb = new StringBuilder();
                for (final Object aDiff : detailedDiff.getAllDifferences()) {
                    sb.append(((Difference) aDiff).toString());
                }
                result.setReport(sb.toString());
            } else {
                result.setReport(diff.toString());
            }

            return result;

        } catch (final Exception e) {
            throw new DiffToolException("An error occurred during XML comparison : " + e.getMessage(), e);
        }
    }

    /**
     * 
     * @param obj1
     * @param obj2
     * @param detailed
     * @return
     * @throws UnsupportedOperationException If the object type is not supported.
     * @throws IOException
     * @throws SAXException
     */
    private Diff newDiffInstance(final T obj1, final T obj2, final boolean detailed)
            throws UnsupportedOperationException, IOException, SAXException {

        Diff diff = null;
        if (obj1 instanceof File) {
            diff = new Diff(new BufferedReader(new FileReader((File) obj1)), new BufferedReader(new FileReader(
                    (File) obj2)));

        } else if (obj1 instanceof InputSource) {
            diff = new Diff((InputSource) obj1, (InputSource) obj2);

        } else if (obj1 instanceof Document) {
            diff = new Diff((Document) obj1, (Document) obj2);

        } else if (obj1 instanceof Reader) {
            diff = new Diff((Reader) obj1, (Reader) obj2);

        } else if (obj1 instanceof String) {
            diff = new Diff((String) obj1, (String) obj2);

        } else {
            throw new UnsupportedOperationException("This type of object '" + obj1.getClass().getName()
                    + "' is not supported.");
        }

        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        return (detailed) ? new DetailedDiff(diff) : diff;
    }

    private XmlDiffOption<T> getDefaultOptions() {
        return new XmlDiffOption<T>();
    }

    private void assertNonNull(final Object... objects) throws IllegalArgumentException {
        for (final Object obj : objects) {
            if (obj == null) throw new IllegalArgumentException("The object to compare must not be null");
        }
    }

}
