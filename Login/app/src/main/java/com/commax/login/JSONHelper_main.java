package com.commax.login;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.LocalServer.Get_SiteCode;
import com.commax.login.Uracle.IdOverlapCheck;
import com.commax.login.Uracle.NationCode;
import com.commax.login.Uracle.Register;
import com.commax.login.Uracle.RequestToken;
import com.commax.login.Uracle.ResourceCheck;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by OWNER on 2016-04-22.
 */

public class JSONHelper_main {

    JSONHelper_main() {

    }

    public static String TAG = JSONHelper_main.class.getSimpleName();
    //for Server array count number
    public static int TIMEOUT_VALUE = 10000;   // 10초
    static Context mContext;
    int overlap_error = 1;
    Register register = new Register();
    IdOverlapCheck idOverlapCheck = new IdOverlapCheck();
    RequestToken requestToken = new RequestToken();
    ResourceCheck resourceCheck = new ResourceCheck();
    NationCode nationCode = new NationCode();
    Get_SiteCode get_siteCode = new Get_SiteCode();

    public String restCall(Context context, String LocalIp, String cloud_svr, String type, String param1, String[] params, Handler mHandler) throws IOException, IllegalArgumentException {

        String serverip = "";
        if (LocalIp.equals("127.0.0.1")) serverip = cloud_svr;
        else serverip = LocalIp;
        serverip = "https://" + cloud_svr + "/";
        String host = "www.commax.co.kr";
        HttpURLConnection urlConnection = null;
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (type.isEmpty()) {
            throw new IllegalArgumentException();
        }
        URL url = null;
        try {
            Log.d(TAG, " server ip  : " + serverip + type + "/" + param1);
            if (type.equals("cmx")) {
                if (param1.equals("register"))
                {
                    Log.d(TAG, "register");
                    // num : 1 user and resource register
                    //params[0] : type , [1] : param1 , [2] : name , [3] : id , [4] :password , [5] : mac ,[6] :model name , [7] : nation code ,[8] : new type
                    url = new URL(serverip + type + "/" + param1);
                    if (serverip.contains("https")) {
                        //TODO fot https test
                        trustAllHosts();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        });
                        urlConnection = httpsURLConnection;
                    } else {
                        urlConnection = (HttpURLConnection) url.openConnection();
                    }
                    //time out
                    try {
                        urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                        urlConnection.setReadTimeout(TIMEOUT_VALUE);
                    } catch (Exception e) {
                        Log.e(TAG, "Timeout error");
                        e.printStackTrace();
                    }
                    urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                    urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                    urlConnection.setRequestProperty("host", host);
                    urlConnection.setRequestProperty("content-type", "application/json");
                    register.RegisterID(urlConnection, params, mHandler);
                } else if (param1.equals("user")) {
                    Log.d(TAG, "id overlap check");
                    //num : 4 user overlapped check
                    //parmas[0] : type , [1] : param1 , [2] : id
                    url = new URL(serverip + type + "/" + param1 + "/exist/" + params[2] + "?client_id=" + MainActivity.getInstance().client_id +
                            "&client_secret=" + MainActivity.getInstance().client_secret);
                    if (serverip.contains("https")) {
                        //TODO fot https test
                        trustAllHosts();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        });
                        urlConnection = httpsURLConnection;
                    } else {
                        urlConnection = (HttpURLConnection) url.openConnection();
                    }
                    //time out
                    try {
                        urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                        urlConnection.setReadTimeout(TIMEOUT_VALUE);
                    } catch (Exception e) {
                        Log.e(TAG, "Timeout error");
                        e.printStackTrace();
                    }


                    urlConnection.setRequestProperty("content-type", "application/json; charset=utf-8");
                    urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                    urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                    urlConnection.setRequestProperty("host", host);

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(false);
                    urlConnection.setUseCaches(false);

                    Log.d(TAG, "url : " + url);
                    idOverlapCheck.Check(context, urlConnection, params, mHandler);
                } else if (param1.equals("resources")) {
                    // 현재 사용하지 않으며 리소스 체크를 토큰 발급 여부로 대체
                    //num : 2 resource register
                    //parmas[0] : type , [1] : param1 , [2] :mac ,  [3] : model name , [4] : nation code
                    url = new URL(serverip + type + "/" + param1);

                    if (serverip.contains("https")) {
                        //TODO fot https test
                        trustAllHosts();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        });
                        urlConnection = httpsURLConnection;

                    } else {
                        urlConnection = (HttpURLConnection) url.openConnection();
                    }
                    //time out
                    try {
                        urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                        urlConnection.setReadTimeout(TIMEOUT_VALUE);
                    } catch (Exception e) {
                        Log.e(TAG, "Timeout error");
                        e.printStackTrace();
                    }
                    urlConnection.setRequestProperty("content-type", "application/json");
                    urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                    urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                    urlConnection.setRequestProperty("host", host);

                    resourceCheck.resource_register_check(urlConnection, params, mHandler);

                } else if (param1.equals("countries")) {
                    Log.d(TAG, "get contries list");
                    //num : 5  get nations list
                    //parmas[0] : type , [1] : param1 , [2] : client id ,  [3] : model name , [4] : client secret

                    url = new URL(serverip + type + "/" + param1 + "?client_id=" + MainActivity.getInstance().client_id + "&client_secret=" + MainActivity.getInstance().client_secret);
                    Log.d(TAG, "url : " + url);

                    if (serverip.contains("https")) {
                        //TODO fot https test
                        trustAllHosts();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        });
                        urlConnection = httpsURLConnection;

                    } else {
                        urlConnection = (HttpURLConnection) url.openConnection();
                    }

                    try {
                        urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                        urlConnection.setReadTimeout(TIMEOUT_VALUE);
                    } catch (Exception e) {
                        Log.e(TAG, "Timeout error");
                        e.printStackTrace();
                    }
//                    urlConnection.setRequestProperty("Content-Length","320");
                    urlConnection.setRequestProperty("content-type", "application/json; charset=utf-8");
                    urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                    urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                    urlConnection.setRequestProperty("host", host);

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(false);
                    urlConnection.setUseCaches(false);


                    nationCode.GetNationList(urlConnection, params, mHandler);
                }
            }
            else if (type.equals("oauth")) {
                Log.d(TAG, " get Token");
                //num   : get Token
                //parmas[0] :type , [1] :param1 , [2] : mac , [3] : model name
                url = new URL(serverip + type + "/" + param1);

                // get token   -> model name  encoding
                params[3] = URLEncoder.encode(params[3], "UTF-8");

                if (serverip.contains("https")) {
                    //TODO fot https test
                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });
                    urlConnection = httpsURLConnection;
                } else {
                    urlConnection = (HttpURLConnection) url.openConnection();
                }

                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                } catch (Exception e) {
                    Log.e(TAG, "Timeout error");
                    e.printStackTrace();
                }
                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                urlConnection.setRequestProperty("host", host);
                urlConnection.setRequestProperty("cmx-dvc-model", params[3]);
                requestToken.GetToken(urlConnection, params, mHandler);
            }
            else if (type.equals("sitecode")) {
                Log.d(TAG, " get Site Code");
                //num   : get Site Code
                //parmas[0] :type , [1] :param1 , [2] : mac , [3] : model name
//                url = new URL("http://10.0.0.42/public/server_info.php?method=siteInfo&mac=" + params[1]);
                url = new URL("http://" + params[2] + "/public/server_info.php?method=siteInfo&mac=" + params[1]);

                Log.d(TAG, "url : " + url);
                serverip = String.valueOf(url);
                if (serverip.contains("https")) {
                    //TODO fot https test
                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });
                    urlConnection = httpsURLConnection;
                } else {
                    urlConnection = (HttpURLConnection) url.openConnection();
                }

                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                } catch (Exception e) {
                    Log.e(TAG, "Timeout error");
                    e.printStackTrace();
                }

                MainActivity.getInstance().site_code = get_siteCode.get_site_code(urlConnection, params, mHandler);
                Log.d(TAG, "site_code = " + MainActivity.getInstance().site_code);
               //loading view stop
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
            else {
                url = new URL("");
            }
            Log.d(TAG, "server URL : " + String.valueOf(url));
        } catch (MalformedURLException e) {
            Log.e(TAG, "url error");
            throw new IOException();
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
