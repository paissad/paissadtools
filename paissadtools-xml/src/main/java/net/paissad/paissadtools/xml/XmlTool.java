package net.paissad.paissadtools.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.paissad.paissadtools.api.ServiceEntry;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlTool implements ServiceEntry {

    private static final long serialVersionUID = 1L;

    public static void validate(final File xmlFile, final File xsdFile) throws XmlServiceException {

        BufferedInputStream xsdStream = null;
        try {
            xsdStream = new BufferedInputStream(new FileInputStream(xsdFile));
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final InputSource xsdSource = new InputSource(xsdStream);
            final SAXSource saxSource = new SAXSource(xsdSource);
            final Schema schema = schemaFactory.newSchema(saxSource);
            final Validator validator = schema.newValidator();
            validator.setErrorHandler(new CustomErrorHandler());
            validator.validate(new StreamSource(xmlFile));

        } catch (Exception e) {
            final String errMsg = "Error while validating the XML file -> " + xmlFile.getAbsolutePath() + " : "
                    + e.getMessage();
            throw new XmlServiceException(errMsg, e);

        } finally {
            if (xsdStream != null) {
                try {
                    xsdStream.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace(System.err);
                }
            }
        }
    }

    private static class CustomErrorHandler implements ErrorHandler {

        public CustomErrorHandler() {
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            this.printInfoAndThrowException("WARNING", e);
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            this.printInfoAndThrowException("ERROR", e);
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            this.printInfoAndThrowException("FATAL", e);
        }

        private void printInfoAndThrowException(final String errorLevel, final SAXParseException e) throws SAXException {
            System.err.println("[" + errorLevel + "]");
            System.err.println("   Public ID: " + e.getPublicId());
            System.err.println("   System ID: " + e.getSystemId());
            System.err.println("   Line number: " + e.getLineNumber());
            System.err.println("   Column number: " + e.getColumnNumber());
            System.err.println("   Message: " + e.getMessage());
            throw new SAXException(e);
        }
    }

    // XXX
    public static void main(String[] args) throws Exception {
        
       final File xmlFile = new File("pom.xml");
//       final String xmlContent = xmlFile.
//       final Matcher = Pattern.compile("<properties(\r|\n|.)*>", Pattern.DOTALL)
    }
}
