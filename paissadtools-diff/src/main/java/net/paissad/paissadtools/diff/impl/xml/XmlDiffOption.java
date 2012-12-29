package net.paissad.paissadtools.diff.impl.xml;

import java.io.File;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.paissad.paissadtools.diff.impl.AbstractDiffOption;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * <p>
 * The oriented option to use when using XML diff tool {@link XmlDiffTool}.
 * </p>
 * <p>
 * The <b>default</b> options are :
 * <ol>
 * <li>Comments are ignored.</li>
 * <li>White spaces are ignored.</li>
 * <li>Attributes order is ignored.</li>
 * </ol>
 * </p>
 * 
 * @param <T> - The type of the object:<br>
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
@Getter
@Setter
public class XmlDiffOption<T> extends AbstractDiffOption<T> {

    private boolean                       ignoreComments;
    private boolean                       ignoreWhiteSpaces;
    private boolean                       ignoreAttributesOrder;
    private List<XmlDiffExclusionRule<T>> exclusionRules;

    public XmlDiffOption() {
        this.ignoreComments = true;
        this.ignoreWhiteSpaces = true;
        this.ignoreAttributesOrder = true;
        this.exclusionRules = new LinkedList<XmlDiffExclusionRule<T>>();
    }

}
