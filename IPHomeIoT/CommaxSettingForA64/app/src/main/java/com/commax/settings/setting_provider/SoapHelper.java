package com.commax.settings.setting_provider;

import android.content.Context;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@SuppressWarnings("deprecation")
public class SoapHelper {
    private static final String URL_FORMAT = "http://%1$S:29740";
    private String NAMESPACE = "urn:cvs";
    private String NAME_GET = "getMySqlDBInfo";
    private String NAME_SET = "setMySqlDBInfo";
    private static final String ARG = "<n0:%1$s id=\"o0\" c:root=\"1\" xmlns:n0=\"%2$s\">"
            + "<in i:type=\"d:string\">%3$s</in></n0:%1$s>";

    private String serverIP;


    public SoapHelper(Context context) {
        serverIP = new ServerIp(context).getHome();
    }

    public String getValue(String query) {


        if (serverIP == null) {
            return null;
        }
        String url = String.format(URL_FORMAT, serverIP);

//		String temp = null;
        String ret = call(url, String.format(ARG, NAME_GET, NAMESPACE, query));
//		<?xml version="1.0" encoding="UTF-8"?><SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns="urn:cvs"><SOAP-ENV:Header></SOAP-ENV:Header><SOAP-ENV:Body SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><ns:getMySqlDBInfoResponse><return>f1&amp;127.0.0.1</return></ns:getMySqlDBInfoResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>
        if (ret != null) {
            int start = ret.indexOf("<return>") + "<return>".length();
            int end = ret.indexOf("</return>");
//			int length=ret.length();
            ret = ret.substring(start, end);

            if (ret.contains("&amp;")) {
                ret = ret.replace("&amp;", "&");
            }
//			if (ret.contains("&amp;")) {
//				ret=ret.substring(ret.indexOf("&amp;")+"&amp;".length());
//			}
        }

        return ret;
    }


    public boolean setValue(String query) {


        if (serverIP == null) {
            return false;
        }
        String url = String.format(URL_FORMAT, serverIP);

        String ret = call(url, String.format(ARG, NAME_SET, NAMESPACE, query));
        //<?xml version="1.0" encoding="UTF-8"?><SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:ns="urn:cvs"><SOAP-ENV:Header></SOAP-ENV:Header><SOAP-ENV:Body SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"><ns:setMySqlDBInfoResponse><out>0</out></ns:setMySqlDBInfoResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>
        if (ret != null) {
            int start = ret.indexOf("<out>") + "<out>".length();
            int end = ret.indexOf("</out>");
            //int length=ret.length();
            ret = ret.substring(start, end);

            if ("0".equals(ret)) {
                return true;
            }
        }


        return false;
    }

    // callEDS, urn:ces, in
    private static final String REQ_FORMAT = "<v:Envelope "
            + "xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xmlns:d=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" "
            + "xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<v:Header />"
            + "<v:Body>%1$s</v:Body></v:Envelope>";


    public String call(String url, String query) {
        String arg = String.format(REQ_FORMAT, query);
        return callSOAPServer(url, arg);
    }


//		public Integer isConnected(String query) {
//			String arg = String.format(REQ_FORMAT, query);
//			String ret = callSOAPServer(arg);
//			if (ret == null) {
//				return -1;
//			} else {
//				return 1;// ok
//			}
//		}


    private String callSOAPServer(String url, String body) {


        // http://stackoverflow.com/questions/12561503/how-to-call-a-soap-webservice-with-a-simple-string-xml-in-string-format
        // http://stackoverflow.com/questions/2559948/android-sending-xml-via-http-post-soap

        String ret = null;
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 35000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

			/*
             * httpclient.getCredentialsProvider().setCredentials( new
			 * AuthScope("os.icloud.com", 80, null, "Digest"), new
			 * UsernamePasswordCredentials(username, password));
			 */
        HttpPost httppost = new HttpPost(url);
        // httppost.setHeader("soapaction", SOAP_ACTION);
        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

        System.out.println("executing request" + httppost.getRequestLine());
        // now create a soap request message as follows:
        // final StringBuffer soap = new StringBuffer();
        // soap.append("\n");
        // soap.append("");
        // // this is a sample data..you have create your own required data
        // BEGIN
        // soap.append(" \n");
        // soap.append(" \n");
        // soap.append("" + body);
        // soap.append(" \n");
        // soap.append(" \n");

			/* soap.append(body); */
        // END of MEssage Body
        // soap.append("");
        // Log.i("SOAP Request", "" + soap.toString());
        // END of full SOAP request message
        try {
            HttpEntity entity = new StringEntity(body, HTTP.UTF_8);
            httppost.setEntity(entity);

            // List<NameValuePair> nameValuePairs = new
            // ArrayList<NameValuePair>(2);
            // nameValuePairs.add(new BasicNameValuePair(NAME, body));
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);// calling
            // server
            HttpEntity r_entity = response.getEntity(); // get response
            Log.i("Reponse Header", "Begin..."); // response headers
            Log.i("Reponse Header", "StatusLine:" + response.getStatusLine());
            Header[] headers = response.getAllHeaders();
            for (Header h : headers) {
                Log.i("Reponse Header", h.getName() + ": " + h.getValue());
            }
            Log.i("Reponse Header", "END...");
            if (r_entity != null) {
                // byte[] result = new byte[(int) r_entity.getContentLength()];
                if (r_entity.isStreaming()) {
                    // DataInputStream is = new DataInputStream(
                    // r_entity.getContent());
                    // is.readFully(result);

                    InputStream inputStream = r_entity.getContent();

                    //ret = parse(inputStream);

                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    ret = total.toString();
                }
            }
        } catch (Exception E) {

        }

        httpclient.getConnectionManager().shutdown(); // shut down the
        // connection
        return ret;
    }


}
