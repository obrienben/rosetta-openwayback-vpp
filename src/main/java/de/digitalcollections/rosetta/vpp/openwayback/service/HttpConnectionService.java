package de.digitalcollections.rosetta.vpp.openwayback.service;


//import com.google.common.collect.Maps;
//import nz.govt.natlib.spineLabel.exceptions.SpineLabelException;
import com.exlibris.core.infra.common.exceptions.logging.ExLogger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a service for querying a REST-ful api web service, and in the process
 * establishing an HTTPS connection to that remote host which exposes the REST-ful web service.
 *
 * @author Ben O'Brien
 */
public class HttpConnectionService {
    private static final ExLogger logger = ExLogger.getExLogger(HttpConnectionService.class);

    private String hostName = "http://localhost:8080/OWResourceStore/";
    private String proxyHost = null;
    private int proxyPort;
    private String errorMessage = "Error executing query. Please try again, and if the issue persists please contact Support";

    public HttpConnectionService() {
    }


    /**
     * Executes an POST update through a REST-ful api call.
     * Builds default query string and appends any additional. Sets any additional
     * header request properties.
     *
     * @param requestBody    additional query string parameters for api call
     * @return                              string response
     */
    public String post(byte[] requestBody) throws Exception {

        StringBuilder responseData = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();
        String updateResponse = "";
        // Build and append query string
        urlBuilder = new StringBuilder(hostName);
//            urlBuilder.append(apiCall);
//            buildQueryString(urlBuilder, extraQueryStringParameters);
        URL restUrl = new URL(urlBuilder.toString());

        // Establish HTTP connection
        HttpURLConnection con = getConnection(restUrl);
        con.setRequestMethod("POST");
        con.setFixedLengthStreamingMode(requestBody.length);
        con.setDoOutput(true);

        // Set any Header request properties
//            con.setRequestProperty("Content-Type", "application/json");
//            setHeaderProperties(con, getDefaultHeaderRequestPropertyMap("update"), extraHeaderRequestProperties);

        logger.info("OpenWayback VPP - HttpConnection post to: " + urlBuilder.toString());

        OutputStream out = con.getOutputStream();
        out.write(requestBody);

        if (con.getResponseCode() >= 400) {
            logger.error("OpenWayback VPP - Error occurred in updating Resource Store.");
        }

        InputStreamReader isr = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        while ((inputLine = in.readLine()) != null)
        {
//              logger.info("OpenWayback VPP - HttpConnection response: " + inputLine);
            responseData.append(inputLine);
        }
        in.close();
        con.disconnect();
        updateResponse = responseData.toString();

        return updateResponse;
    }

    /**
     * Establishes HTTPS connection to a URL.
     * If proxy properties are provided then set proxy details to
     * be used by HTTPS connection.
     *
     * @param url URL to connect
     * @return a new Https Connection
     * @throws IOException
     */
    private HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection con;
        if(proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0){
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            con = (HttpURLConnection)url.openConnection(proxy);
        } else {
            con = (HttpURLConnection)url.openConnection();
        }

        return con;
    }

    /**
     * Sets the hostname string.
     *
     * @param hostName a URL hostname
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sets the Proxy Server hostname. Used when establishing an HTTP(S)
     * connection.
     *
     * @param proxyHost a URL for the Proxy Server
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Sets the Proxy Server port. Used when establishing an HTTP(S)
     * connection.
     *
     * @param proxyPort a port number for the Proxy Server
     */
    public void setProxyPort(String proxyPort) {
        if(!proxyPort.equals("")){
            this.proxyPort = Integer.parseInt(proxyPort);
        }
    }

    /**
     * Sets the default error message used when there is an error connecting to
     * the RESTful service.
     *
     * @param errorMessage an error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
