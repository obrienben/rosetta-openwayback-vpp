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

    private String hostName = "http://localhost/OWResourceStore";
    private String apiKey = "";
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
//		try {
        StringBuilder responseData = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();
        String updateResponse = "";
        try {
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
            con.setRequestProperty("Content-Type", "application/json");
//            setHeaderProperties(con, getDefaultHeaderRequestPropertyMap("update"), extraHeaderRequestProperties);

            logger.info("OpenWayback VPP - HttpConnection post to: " + urlBuilder.toString());

            OutputStream out = con.getOutputStream();
            out.write(requestBody);
            if (con.getResponseCode() >= 400) {
                InputStreamReader isr = new InputStreamReader(con.getErrorStream());
                BufferedReader in = new BufferedReader(isr);

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    responseData.append(inputLine);
                }
                in.close();
                con.disconnect();
//                System.out.println(responseData.toString());
                logger.error(responseData.toString());
            } else {
                InputStreamReader isr = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(isr);

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                {
                    logger.info("OpenWayback VPP - HttpConnection response: " + inputLine);
                    responseData.append(inputLine);
                }
                in.close();
                con.disconnect();
                updateResponse = responseData.toString();
            }

        } catch (Exception e){
            logger.error("Error connecting to " + hostName, e);
            throw new Exception(errorMessage);
        }

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
    private HttpsURLConnection getConnection(URL url) throws IOException {
        HttpsURLConnection con;
        if(proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0){
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            con = (HttpsURLConnection)url.openConnection(proxy);
        } else {
            con = (HttpsURLConnection)url.openConnection();
        }

        return con;
    }

    /**
     * Sets the default header request properties on the supplied
     * Https Connection, and any additional properties.
     *
     * @param con           the Https Connection to set the properties
     * @param extraProps    Map of any additional header request properties
     */
    private void setHeaderProperties(HttpsURLConnection con, Map<String, String> defaultProps, Map<String, String> extraProps){

//        Map<String, String> defaultProps = getDefaultHeaderRequestPropertyMap();

        for(Map.Entry<String, String> prop : defaultProps.entrySet()){
            if((prop.getKey() != null) && (!prop.getKey().equals(""))){
                con.setRequestProperty(prop.getKey(), prop.getValue());
            }
        }

        for(Map.Entry<String, String> prop : extraProps.entrySet()){
            if((prop.getKey() != null) && (!prop.getKey().equals(""))){
                con.setRequestProperty(prop.getKey(), prop.getValue());
            }
        }
    }

//    /**
//     * Builds and returns a map of the default header request properties.
//     *
//     * @return a Map of the default header request properties
//     */
//    private Map<String, String> getDefaultHeaderRequestPropertyMap(String type) {
//        HashMap<String, String> propertiesMap = Maps.newHashMap();
//        if(type.equals("search")){
//            propertiesMap.put("Accept", "application/json");
//        }
//        else if(type.equals("update")){
//            propertiesMap.put("Content-Type", "application/json");
//        }
//        return propertiesMap;
//    }

    /**
     * Sets the hostname string.
     *
     * @param hostName a URL hostname
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sets an api key that is used to connect to a REST-ful api service.
     *
     * @param apiKey a REST-ful service api key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
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
