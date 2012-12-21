package net.paissad.paissadtools.example.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import net.paissad.paissadtools.http.HttpTool;
import net.paissad.paissadtools.http.HttpToolResponse;
import net.paissad.paissadtools.http.HttpToolSettings;
import net.paissad.paissadtools.http.exception.HttpToolException;

/**
 * HTTP upload code example.
 * 
 * @author paissad
 */
public class UploadHTTPExample {

    /*
     * Uploads a file to a HTTTP server.
     */
    public static void main(final String[] args) throws IllegalArgumentException, HttpToolException, IOException {

        final HttpTool httpTool = new HttpTool();
        final File fileToUpload = new File("src/net/paissad/paissadtools/example/http/UploadHTTPExample.java");
        final String destUrl = "http://stub.paissad.net/upload/";

        final HttpToolSettings requestSettings = new HttpToolSettings();
        requestSettings.setUsername("user");
        requestSettings.setPassword("pass");

        final HttpToolResponse response = httpTool.upload(fileToUpload, destUrl, requestSettings);
        System.out.println(response);
        final InputStream respBody = response.getResponseBody();
        if (respBody != null) {
            System.out.println(IOUtils.toString(respBody));
        }
    }
}
