package net.paissad.paissadtools.example.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import net.paissad.paissadtools.http.HttpTool;
import net.paissad.paissadtools.http.HttpTool.Http_Method;
import net.paissad.paissadtools.http.HttpToolResponse;
import net.paissad.paissadtools.http.HttpToolSettings;
import net.paissad.paissadtools.http.exception.HttpToolException;

/**
 * HTTP Get request code example.
 * 
 * @author paissad
 */
public class GetHTTPExample {

    public static void main(final String[] args) throws IllegalArgumentException, HttpToolException, IOException {

        final HttpTool httpTool = new HttpTool();
        final String url = "http://paissad.net/server_services.php";
        final HttpToolSettings requestSettings = new HttpToolSettings();

        final HttpToolResponse resp = httpTool.sendRequest(url, Http_Method.GET, requestSettings);

        System.out.println("Status code    : " + resp.getStatusCode());
        System.out.println("Status message : " + resp.getStatusMessage());
        final InputStream body = resp.getResponseBody();
        if (body != null) System.out.println(IOUtils.toString(body));
    }
}
