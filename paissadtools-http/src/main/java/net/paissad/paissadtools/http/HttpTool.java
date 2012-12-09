package net.paissad.paissadtools.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.paissad.paissadtools.api.ITool;
import net.paissad.paissadtools.http.HttpToolSettings.ProxySettings;
import net.paissad.paissadtools.util.CommonUtils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * HTTP tool for sending requests (GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE)
 * or for uploading files (multipart).
 * 
 * @author paissad
 */
public class HttpTool implements ITool {

    /*
     * Code examples available from here :
     * http://hc.apache.org/httpcomponents-client-ga/examples.html
     */

    private static final String UTF8_ENCODING = "UTF-8";

    /**
     * Represents a HTTP method.
     * 
     * @author paissad
     */
    public static enum Http_Method {
        /** DELETE HTTP method. */
        DELETE,
        /** GET HTTP method. */
        GET,
        /** POST HTTP method. */
        POST,
        /** HEAD HTTP method. */
        HEAD,
        /** PUT HTTP method. */
        PUT,
        /** OPTIONS HTTP method. */
        OPTIONS,
        /** TRACE HTTP method. */
        TRACE;
    }

    /** List of current & active request being processed. */
    private final Set<HttpRequestBase> currentRequests;

    public HttpTool() {
        this.currentRequests = Collections.synchronizedSet(new LinkedHashSet<HttpRequestBase>());
    }

    /**
     * Uploads a file to a HTTP server.<br>
     * <b>NOTE</b> : the request is multipart / encoded.
     * 
     * @param fileToUpload - The file to upload.
     * @param destUrl - The destination URL where to upload the file.
     * @param requestSettings - Contains the optional settings
     *            (username/password if authentication is needed when processing
     *            the request. Contains additional parameters and/or headers to
     *            pass to the request Contains also the proxy settings). This
     *            argument may be <code>null</code> if no optional/additional
     *            setting is required.
     * @return The HTTP response of type {@link HttpToolResponse}.
     * @throws IllegalArgumentException If the file to upload or the specified
     *             destination URL is <tt>null</tt>.
     * @throws HttpToolException - If an error occurs while uploading the file.
     */
    public HttpToolResponse upload(final File fileToUpload, final String destUrl, final HttpToolSettings requestSettings)
            throws IllegalArgumentException, HttpToolException {

        if (fileToUpload == null) throw new IllegalArgumentException("The file to upload cannot be null");
        if (destUrl == null) throw new IllegalArgumentException("The URL cannot be null.");

        HttpClient httpClient = null;
        try {

            final URI targetURI = new URI(destUrl);
            final String host = targetURI.getHost();
            final int port = targetURI.getPort();
            httpClient = this.getNewHttpClient(host, port, requestSettings);

            String url = destUrl;
            if (requestSettings != null) {
                final Map<String, String> params = requestSettings.getParameters();
                if (params != null && !params.isEmpty()) {
                    url += this.encodeParams(params);
                }
            }

            final HttpPost httpPost = new HttpPost(url);
            this.addRequest(httpPost);

            final FileBody fileBody = new FileBody(fileToUpload);
            // StringBody comment = new StringBody("A bin file of some kind");

            final MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("bin", fileBody);
            // reqEntity.addPart("comment", comment);

            httpPost.setEntity(reqEntity);

            ((DefaultHttpClient) httpClient).setHttpRequestRetryHandler(new InternalHttpRequestRetryHandler());
            httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);

            final HttpHost targetHost = new HttpHost(targetURI.getHost(), targetURI.getPort(), targetURI.getScheme());
            final HttpResponse httpResponse = httpClient.execute(targetHost, httpPost);

            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            final String statusMessage = httpResponse.getStatusLine().getReasonPhrase();
            final InputStream responseBody = this.getResponseBody(httpResponse);

            this.removeRequest(httpPost);

            return new HttpToolResponse(statusCode, statusMessage, responseBody);

        } catch (final Exception e) {
            throw new HttpToolException("Error while uploading file.", e);

        } finally {
            if (httpClient != null) httpClient.getConnectionManager().shutdown();
        }
    }

    /**
     * Calls the HTTP client.
     * 
     * @param url - The URL where to send the request.
     * @param method - The HTTP method to use.
     * @param requestSettings - Contains the optional settings
     *            (username/password if authentication is needed when processing
     *            the request. Contains additional parameters and/or headers to
     *            pass to the request Contains also the proxy settings). This
     *            argument may be <code>null</code> if no optional/additional
     *            setting is required.
     * @return The HTTP response of type {@link HttpToolResponse}.
     * @throws IllegalArgumentException If the specified url and/or the http
     *             method to use is <tt>null</tt>
     * @throws HttpToolException - If an error occurs while processing the HTTP
     *             request.
     * @see Http_Method
     */
    public HttpToolResponse sendRequest(final String url, final Http_Method method,
            final HttpToolSettings requestSettings) throws IllegalArgumentException, HttpToolException {

        if (url == null) throw new IllegalArgumentException("The url where to send the request cannot be null.");
        if (method == null) throw new IllegalArgumentException("The HTTP method to use cannot be null.");

        HttpClient httpClient = null;
        try {
            final URI uri = new URI(url);
            final String host = uri.getHost();
            final int port = uri.getPort();
            httpClient = this.getNewHttpClient(host, port, requestSettings);

            final HttpRequestBase request = this.getRequestFromHTTPMethod(method, uri);

            this.addRequest(request);

            this.addAddtionalParamsAndHeaders(request, requestSettings);

            ((DefaultHttpClient) httpClient).setHttpRequestRetryHandler(new InternalHttpRequestRetryHandler());
            httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);

            final HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            final HttpResponse httpResponse = httpClient.execute(targetHost, request);

            final int statusCode = httpResponse.getStatusLine().getStatusCode();
            final String statusMessage = httpResponse.getStatusLine().getReasonPhrase();
            final InputStream responseBody = this.getResponseBody(httpResponse);

            this.removeRequest(request);

            return new HttpToolResponse(statusCode, statusMessage, responseBody);

        } catch (final Exception e) {
            throw new HttpToolException("Error while processing http request.", e);

        } finally {
            if (httpClient != null) httpClient.getConnectionManager().shutdown();
        }
    }

    private HttpRequestBase getRequestFromHTTPMethod(final Http_Method method, final URI uri) {

        HttpRequestBase request = null;

        switch (method) {
        case DELETE:
            request = new HttpDelete(uri);
            break;
        case GET:
            request = new HttpGet(uri);
            break;
        case POST:
            request = new HttpPost(uri);
            break;
        case HEAD:
            request = new HttpHead(uri);
            break;
        case PUT:
            request = new HttpPut(uri);
            break;
        case OPTIONS:
            request = new HttpOptions(uri);
            break;
        case TRACE:
            request = new HttpTrace(uri);
            break;
        default:
            throw new IllegalArgumentException("HTTP method not supported : " + method);
        }

        return request;
    }

    /**
     * Aborts all current and active requests being processed by this HTTP tool
     * client.
     */
    public void abort() {
        synchronized (this.currentRequests) {
            final Iterator<HttpRequestBase> iter = this.currentRequests.iterator();
            while (iter.hasNext()) {
                final HttpRequestBase request = iter.next();
                if (request != null) request.abort();
            }
        }
    }

    private void addRequest(final HttpRequestBase request) {
        synchronized (this.currentRequests) {
            this.currentRequests.add(request);
        }
    }

    private void removeRequest(final HttpRequestBase request) {
        synchronized (this.currentRequests) {
            final Iterator<HttpRequestBase> iter = this.currentRequests.iterator();
            while (iter.hasNext()) {
                final HttpRequestBase req = iter.next();
                if (req != null && req.equals(request)) iter.remove();
            }
        }
    }

    /**
     * See the following sites for examples/documentations.
     * <ul>
     * <li><a href=
     * "http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html"
     * >http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.
     * html</a>.</li>
     * <li><a href=
     * "http://www.makeurownrules.com/secure-rest-web-service-mobile-application-android.html"
     * >http://www.makeurownrules.com/secure-rest-web-service-mobile-application
     * -android.html</a>.</li>
     * </ul>
     * 
     * @param host - The host where the HTTP client will be connected to.
     * @param requestSettings - The request settings. May be <tt>null</tt>.
     * 
     * @return An instance of {@link DefaultHttpClient}.
     * @throws HttpToolException
     */
    private DefaultHttpClient getNewHttpClient(final String host, final int port, final HttpToolSettings requestSettings)
            throws HttpToolException {

        try {
            final TrustStrategy trustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(final X509Certificate[] chain, final String authType)
                        throws CertificateException {
                    return true;
                }
            };
            final SSLSocketFactory sf = new InternalSSLFactory(trustStrategy);

            final HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, UTF8_ENCODING);

            final SchemeRegistry sr = new SchemeRegistry();
            sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            sr.register(new Scheme("https", 443, sf));

            final ClientConnectionManager ccm = new PoolingClientConnectionManager(sr);

            final DefaultHttpClient client = new DefaultHttpClient(ccm);
            client.setHttpRequestRetryHandler(new InternalHttpRequestRetryHandler());
            client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);

            final List<String> authpref = new ArrayList<String>();
            // Choose BASIC over DIGEST for proxy authentication
            authpref.add(AuthPolicy.BASIC);
            authpref.add(AuthPolicy.DIGEST);
            client.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);

            // Add the credentials for authentication
            if (requestSettings != null) {

                // Proxy has the priority over the real target host
                final ProxySettings proxy = requestSettings.getProxySettings();
                if (proxy != null) {
                    client.getCredentialsProvider().setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                            new UsernamePasswordCredentials(proxy.getUser(), proxy.getPass()));

                    final HttpHost proxyRoute = new HttpHost(proxy.getHost(), proxy.getPort());
                    client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyRoute);

                } else {
                    // No proxy settings.
                    final String username = requestSettings.getUsername();
                    final String password = requestSettings.getPassword();

                    if (username != null) {
                        client.getCredentialsProvider().setCredentials(new AuthScope(host, port),
                                new UsernamePasswordCredentials(username, password));
                    }
                }
            }

            client.addRequestInterceptor(new InternalHttpRequestInterceptor());
            client.addResponseInterceptor(new InternalHttResponseInterceptor());

            return client;

        } catch (final Exception e) {
            throw new HttpToolException("Error while creating an instance of HTTPClient", e);
        }
    }

    private void addAddtionalParamsAndHeaders(final HttpRequestBase request, final HttpToolSettings requestSettings) {

        if (requestSettings == null) return;

        final Map<String, String> params = requestSettings.getParameters();
        final Map<String, String> headers = requestSettings.getHeaders();

        if (params != null) { // Use the specified parameters.
            final Iterator<Entry<String, String>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                final Entry<String, String> o = iter.next();
                request.getParams().setParameter(o.getKey(), o.getValue());
            }
        }

        if (headers != null) { // Let's add additional / custom headers.
            final Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                final Entry<String, String> o = iter.next();
                request.addHeader(o.getKey(), o.getValue());
            }
        }

    }

    /**
     * Computes a <tt>String</tt> representing the concatenation of all the
     * parameters. The resulted <tt>String</tt> can be appended to an URL.
     * 
     * @param params - The parameters to use.
     * @return A String representing the concatenation of all the parameters.
     * @throws UnsupportedEncodingException
     */
    private String encodeParams(final Map<String, String> params) throws UnsupportedEncodingException {

        final StringBuilder encodedURL = new StringBuilder();
        boolean isFirst = true;
        encodedURL.append("?");
        final Iterator<Entry<String, String>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            if (!isFirst) {
                encodedURL.append("&");
            }
            final Entry<String, String> o = iter.next();
            encodedURL.append(URLEncoder.encode(o.getKey(), UTF8_ENCODING));
            encodedURL.append("=");
            encodedURL.append(URLEncoder.encode(o.getValue(), UTF8_ENCODING));
            isFirst = false;
        }
        // We must change the '+' chars to '%20'
        return encodedURL.toString().replaceAll("\\+", "%20");
    }

    // _________________________________________________________________________

    private InputStream getResponseBody(final HttpResponse httpResponse) throws IOException {
        final HttpEntity respEntity = httpResponse.getEntity();
        if (respEntity != null) {
            ByteArrayOutputStream baos = null;
            final InputStream respContent = respEntity.getContent();
            try {
                if (respContent != null) {
                    baos = new ByteArrayOutputStream();
                    final byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = respContent.read(buffer)) > -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    baos.flush();
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            } finally {
                EntityUtils.consume(respEntity);
                CommonUtils.closeAllStreamsQuietly(baos, respContent);
            }
        }
        return null;
    }

    // _________________________________________________________________________

    // http://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d4e292
    /**
     * This class contains the policy to use in order to know whether or not a
     * retry must happen.
     * 
     * @author paissad
     */
    @NotThreadSafe
    private static class InternalHttpRequestRetryHandler implements HttpRequestRetryHandler {

        private static final int MAX_RETRIES = 5;

        public InternalHttpRequestRetryHandler() {
        }

        @Override
        public boolean retryRequest(final IOException exception, final int executionCount, final HttpContext context) {
            boolean retry;
            if (executionCount >= MAX_RETRIES) {
                retry = false; // Do not retry if over max retry count
            } else {
                if (exception instanceof NoHttpResponseException) {
                    retry = true; // Retry if the server dropped the connection
                } else if (exception instanceof SSLHandshakeException) {
                    retry = false; // Do not retry on SSL handshake exception
                } else {
                    final HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
                    // Retry if the request is considered idempotent
                    retry = !(request instanceof HttpEntityEnclosingRequest);
                }
            }
            return retry;
        }
    }

    // _________________________________________________________________________

    /**
     * @author paissad
     */
    private static class InternalSSLFactory extends SSLSocketFactory {

        private SSLContext sslContext;

        public InternalSSLFactory(final TrustStrategy trustStrategy) throws Exception {

            super(trustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            final TrustManager tm = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[] {};
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType)
                        throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType)
                        throws CertificateException {
                }
            };

            this.sslContext = SSLContext.getInstance("TLS");
            this.sslContext.init(null, new TrustManager[] { tm }, null);
        }
    }

    // _________________________________________________________________________

    /**
     * @author paissad
     */
    private static class InternalHttpRequestInterceptor implements HttpRequestInterceptor {

        public InternalHttpRequestInterceptor() {
        }

        @Override
        public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
            // Do nothing yet.
        }
    }

    // _________________________________________________________________________

    /**
     * @author paissad
     */
    private static class InternalHttResponseInterceptor implements HttpResponseInterceptor {

        public InternalHttResponseInterceptor() {
        }

        @Override
        public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
            // Do nothing yet
        }
    }

    // _________________________________________________________________________

    /*
     * XXX For testing purpose only !
     */
    public static void main(final String[] args) throws Exception {
        final HttpTool httpTool = new HttpTool();
        final File fileToUpload = new File("pom.xml").getAbsoluteFile();
        final String destUrl = "http://localhost:8080/serverstub/http/upload?simebouchon_exchange=00000&simebouchon_flowid=00000&"
                + "simebouchon_response_file=pom.xml";
        final HttpToolResponse response = httpTool.upload(fileToUpload, destUrl, null);
        System.out.println(IOUtils.toString(response.getResponseBody()));
        final String dummyUrl = "http://localhost:8080/serverstub/http/dummy.do";
        httpTool.sendRequest(dummyUrl, Http_Method.DELETE, null);
        httpTool.sendRequest(dummyUrl, Http_Method.GET, null);
        httpTool.sendRequest(dummyUrl, Http_Method.HEAD, null);
        httpTool.sendRequest(dummyUrl, Http_Method.OPTIONS, null);
        httpTool.sendRequest(dummyUrl, Http_Method.POST, null);
        httpTool.sendRequest(dummyUrl, Http_Method.PUT, null);
        httpTool.sendRequest(dummyUrl, Http_Method.TRACE, null);
        httpTool.abort();
    }

}
