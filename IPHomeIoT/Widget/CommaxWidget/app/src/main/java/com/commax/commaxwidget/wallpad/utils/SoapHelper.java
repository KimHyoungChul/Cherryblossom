package com.commax.commaxwidget.wallpad.utils;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SoapHelper {

    public static int TIMEOUT_VALUE = 10000;   // 5초

    private static final String URL_FORMAT = "http://%1$S/ces/ces.php";

    // callEDS, urn:ces, in
    private static final String REQ_FORMAT = "<v:Envelope "
            + "xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" "
            + "xmlns:d=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" "
            + "xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<v:Header />"
            + "<v:Body>"
            + "<n0:callEDS id=\"o0\" c:root=\"1\" xmlns:n0=\"urn:ces\">"
            + "<in i:type=\"d:string\">%1$s</in></n0:callEDS></v:Body></v:Envelope>";


    public static String call(String localServerIP, String content) throws IOException, IllegalArgumentException {
        // http://developer.android.com/reference/java/net/HttpURLConnection.html
        // http://www.xyzws.com/javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
        if (content == null) {
            throw new IllegalArgumentException();
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException();
        }
        URL url;
        try {
            url = new URL(String.format(URL_FORMAT, localServerIP));
        } catch (MalformedURLException e) {
            throw new IOException();
        }
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Type", "application/soap+xml;charset=utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();

        }

        try {
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        urlConnection.setDoOutput(true);
//		urlConnection.setChunkedStreamingMode(0);

        OutputStream out;
        try {
            out = new BufferedOutputStream(urlConnection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
        writeStream(out, String.format(REQ_FORMAT, content));

        BufferedReader in;
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
            in = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
        String ret = readStream(in);

        urlConnection.disconnect();

        try {
            return parse(ret);
        } catch (XmlPullParserException e) {
            throw new IOException();
        } catch (IOException e) {
            throw new IOException();
        }

    }

    private static String readStream(BufferedReader in) {
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = in.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("tag", total.toString());
        return total.toString();
    }

    private static void writeStream(OutputStream out, String content) {

        DataOutputStream wr = new DataOutputStream(out);
        try {
            wr.writeBytes(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            wr.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // private String parse(InputStream in) throws XmlPullParserException,
    // IOException {
    // XmlPullParser parser = Xml.newPullParser();
    // parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
    // parser.setInput(in, null);
    // parser.nextTag();
    // return read(parser);
    // }

    private static String parse(String value) throws XmlPullParserException, IOException {
        Reader in = new StringReader(value);
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in);
        parser.nextTag();
        return read(parser);
    }

    // 05-21 14:24:46.254: D/dump Request:(27165): <v:Envelope
    // xmlns:i="http://www.w3.org/2001/XMLSchema-instance"
    // xmlns:d="http://www.w3.org/2001/XMLSchema"
    // xmlns:c="http://schemas.xmlsoap.org/soap/encoding/"
    // xmlns:v="http://schemas.xmlsoap.org/soap/envelope/"><v:Header
    // /><v:Body><n0:callLs id="o0" c:root="1" xmlns:n0="urn:crs"><arg
    // i:type="d:string">login&amp;id=cmxcdv1&amp;password=123456&amp;push=APA91bF7YuFL64UdB2hibAxoWurSP6odP3k-agivCU-64EOZZdbpgIdAYUJPAjBMzpwCmiwXVomhikqIogJVsqnQtyd7VYt-XZaHKb-EMpGgOBSN-KOle9nJWIdVvGjmPq1guItJCx5YA2zZ2d4kxiYa7IDfasrrAA&amp;os=1&amp;lang=en</arg></n0:callLs></v:Body></v:Envelope>
    // <?xml version="1.0" encoding="ISO-8859-1"?><SOAP-ENV:Envelope
    // xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    // xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    // xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    // xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"><SOAP-ENV:Body><ns1:callLsResponse
    // xmlns:ns1="urn:crs"><result
    // xsi:type="xsd:string">code=Z01&amp;ls=220.120.109.3&amp;home=192.168.0.2&amp;device=192.168.0.2&amp;dong=1234&amp;ho=5678&amp;facebook=
    // &amp;supports=&amp;mac=0432F1F1033D</result><ret-val xsi:nil="true"
    // xsi:type="xsd:string"/></ns1:callLsResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>

    private static String read(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        String ret = null;
        String name = parser.getName();// SOAP-ENV:Envelope
        if ("SOAP-ENV:Envelope".equals(name)) {
            parser.next();
            name = parser.getName();// SOAP-ENV:Body
            parser.next();
            name = parser.getName();// ns1:callLsResponse
            parser.next();
            name = parser.getName();// result
            if (parser.next() == XmlPullParser.TEXT) {
                ret = parser.getText();// code=Z01&ls=220.120.109.3&home=192.168.0.2&device=192.168.0.2&dong=1234&ho=5678&facebook=
                // &supports=&mac=0432F1F1033D

            }
        }

        return ret;
    }

}
