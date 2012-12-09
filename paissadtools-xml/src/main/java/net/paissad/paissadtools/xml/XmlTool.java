package net.paissad.paissadtools.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.paissad.paissadtools.api.ITool;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML tool.
 * 
 * @author paissad
 */
public class XmlTool implements ITool {

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

}
