package net.paissad.paissadtools.diff.impl.xml;

import java.io.File;
import java.io.Reader;

import net.paissad.paissadtools.diff.api.IDiffExclusionRule;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * This is the type of {@link IDiffExclusionRule} to use for XML file comparisons.
 * 
 * @param <T> - The type of the object for which to apply the exclusion rule:<br>
 *            <span style='color:red'><b>NOTE :</b></span> The object must be of one of the following types :
 *            <ul>
 *            <li>{@link String}</li>
 *            <li>{@link File}</li>
 *            <li>{@link Reader}</li>
 *            <li>{@link InputSource}</li>
 *            <li>{@link Document}</li>
 *            </ul>
 * 
 * @author paissad
 */
public interface XmlDiffExclusionRule<T> extends IDiffExclusionRule<T> {

}
