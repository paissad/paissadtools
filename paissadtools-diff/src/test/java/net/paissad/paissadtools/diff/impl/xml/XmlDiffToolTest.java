package net.paissad.paissadtools.diff.impl.xml;

import static net.paissad.paissadtools.diff.TestConstants.XML_WITHOUT_NAMESPACES_2_FILE;
import static net.paissad.paissadtools.diff.TestConstants.XML_WITHOUT_NAMESPACES_3_FILE;
import static net.paissad.paissadtools.diff.TestConstants.XML_WITHOUT_NAMESPACES_1_FILE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilderFactory;

import net.paissad.paissadtools.diff.api.IDiffResult;
import net.paissad.paissadtools.diff.exception.DiffToolException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XmlDiffToolTest {

    @Test(expected = IllegalArgumentException.class)
    public final void testCompare_fist_object_is_null() throws DiffToolException {
        new XmlDiffTool<File>().compare(null, XML_WITHOUT_NAMESPACES_1_FILE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testCompare_second_object_is_null() throws DiffToolException {
        new XmlDiffTool<File>().compare(XML_WITHOUT_NAMESPACES_1_FILE, null, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public final void testCompare_type_of_object_is_not_supported() throws Throwable {
        try {
            new XmlDiffTool<Object>().compare(new Object(), new Object(), null);
        } catch (DiffToolException e) {
            throw (e.getCause());
        }
    }

    @Test
    public final void testCompare_same_xmlfiles_without_namespaces() throws DiffToolException {
        final XmlDiffTool<File> xmlDiffTool = new XmlDiffTool<File>();
        final IDiffResult result = xmlDiffTool.compare(XML_WITHOUT_NAMESPACES_1_FILE, XML_WITHOUT_NAMESPACES_1_FILE,
                null);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_different_xmlfiles_with_verbosity() throws DiffToolException {
        final XmlDiffTool<File> xmlDiffTool = new XmlDiffTool<File>();
        final XmlDiffOption<File> option = new XmlDiffOption<File>();
        option.setVerbose(true);
        final IDiffResult result = xmlDiffTool.compare(XML_WITHOUT_NAMESPACES_1_FILE, XML_WITHOUT_NAMESPACES_2_FILE,
                option);
        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_same_xmlfiles_with_option_but_without_exclusions() throws DiffToolException {
        final XmlDiffTool<File> xmlDiffTool = new XmlDiffTool<File>();
        final XmlDiffOption<File> option = new XmlDiffOption<File>();
        final IDiffResult result = xmlDiffTool.compare(XML_WITHOUT_NAMESPACES_1_FILE, XML_WITHOUT_NAMESPACES_1_FILE,
                option);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_same_xmlfiles_with_first_object_matching_exclusion() throws DiffToolException,
            IOException {
        InputStream in = null;
        try {
            final XmlDiffTool<String> xmlDiffTool = new XmlDiffTool<String>();
            final XmlDiffExclusionRule<String> exclusionRule = new XmlDiffExclusionRule<String>() {

                @Override
                public boolean match(final String content) throws DiffToolException {
                    return content.contains("electronic_1");
                }
            };
            final XmlDiffOption<String> option = new XmlDiffOption<String>();
            option.getExclusionRules().add(exclusionRule);

            in = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_1_FILE));
            final String xmlContent = IOUtils.toString(in);

            final IDiffResult result = xmlDiffTool.compare(xmlContent, xmlContent, option);

            Assert.assertFalse(result.similar());
            Assert.assertTrue(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Test
    public final void testCompare_different_xmlfiles_with_second_object_matching_exclusion() throws DiffToolException,
            IOException {
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            final XmlDiffTool<String> xmlDiffTool = new XmlDiffTool<String>();
            final XmlDiffExclusionRule<String> exclusionRule = new XmlDiffExclusionRule<String>() {

                @Override
                public boolean match(final String content) throws DiffToolException {
                    return content.contains("electronic_2");
                }
            };
            final XmlDiffOption<String> option = new XmlDiffOption<String>();
            option.getExclusionRules().add(exclusionRule);

            in1 = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_1_FILE));
            in2 = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_2_FILE));
            final String xmlContent1 = IOUtils.toString(in1);
            final String xmlContent2 = IOUtils.toString(in2);

            final IDiffResult result = xmlDiffTool.compare(xmlContent1, xmlContent2, option);

            Assert.assertFalse(result.similar());
            Assert.assertTrue(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(in2);
        }
    }

    @Test
    public final void testCompare_different_xmlfiles_with_no_object_matching_exclusion() throws DiffToolException,
            IOException {
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            final XmlDiffTool<String> xmlDiffTool = new XmlDiffTool<String>();
            final XmlDiffExclusionRule<String> exclusionRule = new XmlDiffExclusionRule<String>() {

                @Override
                public boolean match(final String content) throws DiffToolException {
                    return content.contains("electronic_#####");
                }
            };
            final XmlDiffOption<String> option = new XmlDiffOption<String>();
            option.getExclusionRules().add(exclusionRule);

            in1 = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_1_FILE));
            in2 = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_2_FILE));
            final String xmlContent1 = IOUtils.toString(in1);
            final String xmlContent2 = IOUtils.toString(in2);

            final IDiffResult result = xmlDiffTool.compare(xmlContent1, xmlContent2, option);

            Assert.assertFalse(result.similar());
            Assert.assertFalse(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(in2);
        }
    }

    @Test
    public final void testCompare_same_xmlfiles_with_null_exclusions() throws DiffToolException {
        final XmlDiffTool<File> xmlDiffTool = new XmlDiffTool<File>();
        final XmlDiffOption<File> option = new XmlDiffOption<File>();
        option.setExclusionRules(null);
        final IDiffResult result = xmlDiffTool.compare(XML_WITHOUT_NAMESPACES_1_FILE, XML_WITHOUT_NAMESPACES_1_FILE,
                option);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_different_xmlfiles_by_using_string_type() throws DiffToolException, IOException {
        InputStream in1 = null;
        InputStream in2 = null;
        try {
            final XmlDiffTool<String> xmlDiffTool = new XmlDiffTool<String>();

            in1 = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_1_FILE));
            in2 = new BufferedInputStream(new FileInputStream(XML_WITHOUT_NAMESPACES_2_FILE));
            final String xmlContent1 = IOUtils.toString(in1);
            final String xmlContent2 = IOUtils.toString(in2);

            final IDiffResult result = xmlDiffTool.compare(xmlContent1, xmlContent2, null);

            Assert.assertFalse(result.similar());
            Assert.assertFalse(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            IOUtils.closeQuietly(in1);
            IOUtils.closeQuietly(in2);
        }
    }

    @Test
    public final void testCompare_different_xmlfiles_by_using_reader_type() throws DiffToolException, IOException {
        Reader reader1 = null;
        Reader reader2 = null;
        try {
            final XmlDiffTool<Reader> xmlDiffTool = new XmlDiffTool<Reader>();

            reader1 = new FileReader(XML_WITHOUT_NAMESPACES_1_FILE);
            reader2 = new FileReader(XML_WITHOUT_NAMESPACES_2_FILE);

            final IDiffResult result = xmlDiffTool.compare(reader1, reader2, null);

            Assert.assertFalse(result.similar());
            Assert.assertFalse(result.isExcluded());
            Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());

        } finally {
            IOUtils.closeQuietly(reader1);
            IOUtils.closeQuietly(reader2);
        }
    }

    @Test
    public final void testCompare_different_xmlfiles_by_using_inputsource_type() throws DiffToolException, IOException {

        final XmlDiffTool<InputSource> xmlDiffTool = new XmlDiffTool<InputSource>();

        final InputSource source1 = new InputSource(new FileInputStream(XML_WITHOUT_NAMESPACES_1_FILE));
        final InputSource source2 = new InputSource(new FileInputStream(XML_WITHOUT_NAMESPACES_2_FILE));

        final IDiffResult result = xmlDiffTool.compare(source1, source2, null);

        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_different_xmlfiles_by_using_document_type() throws Exception {

        final XmlDiffTool<Document> xmlDiffTool = new XmlDiffTool<Document>();

        final Document document1 = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(XML_WITHOUT_NAMESPACES_1_FILE);
        final Document document2 = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(XML_WITHOUT_NAMESPACES_2_FILE);

        final IDiffResult result = xmlDiffTool.compare(document1, document2, null);

        Assert.assertFalse(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

    @Test
    public final void testCompare_same_xmlfiles_but_with_non_ordered_sequences() throws DiffToolException {
        final XmlDiffTool<File> xmlDiffTool = new XmlDiffTool<File>();
        final IDiffResult result = xmlDiffTool.compare(XML_WITHOUT_NAMESPACES_1_FILE, XML_WITHOUT_NAMESPACES_3_FILE,
                null);
        Assert.assertTrue(result.similar());
        Assert.assertFalse(result.isExcluded());
        Assert.assertTrue(result.getReport() != null && !result.getReport().isEmpty());
    }

}
