package com.commax.login;

        import android.os.Handler;
        import android.util.Log;

        import com.commax.login.Common.AboutFile;
        import com.commax.login.Common.TypeDef;
        import com.commax.login.LocalServer.ResourceNO_Initial;
        import com.commax.login.LocalServer.ResourceNO_Send;
        import com.commax.login.UC.UC_Group_Register;
        import com.commax.login.UC.UC_User_Register;
        import com.commax.login.Uracle.AccessRegister;
        import com.commax.login.Uracle.ChangePassword;
        import com.commax.login.Uracle.GetProfile;
        import com.commax.login.Uracle.Initialize;
        import com.commax.login.Uracle.RequestToken;

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

public class JSONHelper_sub {

    JSONHelper_sub() {

    }

    public static String TAG = JSONHelper_sub.class.getSimpleName();
    //Uracle Server
    ChangePassword changePassword = new ChangePassword();
    Initialize initialize = new Initialize();
    AboutFile aboutFile = new AboutFile();
    RequestToken requestToken = new RequestToken();
    AccessRegister accessRegister = new AccessRegister();
    //UC Server
    UC_Group_Register uc_grounp_register = new UC_Group_Register();
    UC_User_Register uc_user_register = new UC_User_Register();
    //Local Server
    ResourceNO_Send resourceNO_send = new ResourceNO_Send();
    ResourceNO_Initial resourceNO_initial = new ResourceNO_Initial();

    GetProfile getProfile = new GetProfile();

    public static int TIMEOUT_VALUE = 10000;   // 10초

    public String restCall(String LocalIp, String cloud_svr, String type, String[] params, Handler mHandler) throws IOException, IllegalArgumentException {
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
            if (type.equals("v1"))
            {
                Log.d(TAG, " server ip  : " + serverip + type + "/" + params[1]);
                url = new URL(serverip + type + "/" + params[1]);
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

                if (params[2].equals("13")) {
                    Log.d(TAG, "password changed");
                    // num : 14 password change
                    // params[0] : type  , [1] : param1  : /user/me, [2] : param1 , [3] : change password
                    urlConnection.setRequestProperty("content-type", "application/json");
                    urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                    urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                    urlConnection.setRequestProperty("host", host);
                    Log.d(TAG, "token : " + aboutFile.readFile("token"));
                    urlConnection.setRequestProperty("authorization", "Bearer " + aboutFile.readFile("token"));

                    try {
                        urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                        urlConnection.setReadTimeout(TIMEOUT_VALUE);
                    } catch (Exception e) {
                        Log.e(TAG, "Timeout error");
                        e.printStackTrace();
                    }

                    changePassword.password_change(urlConnection, params[3], mHandler);
                } else if (params[2].equals("14")) {
                    Log.d(TAG ,"user initial");
                    //num : 15 user initialize
                    // params[0] : type  , [1] : param1 , [2] : param1 , [3] : token
                    urlConnection.setRequestProperty("content-type", "application/json; charset=utf-8");
                    urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                    urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                    urlConnection.setRequestProperty("host", host);
                    Log.d(TAG, "Token : " + aboutFile.readFile("token"));
                    urlConnection.setRequestProperty("authorization", "Bearer " + aboutFile.readFile("token"));

                    try {
                        urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                        urlConnection.setReadTimeout(TIMEOUT_VALUE);
                    } catch (Exception e) {
                        Log.e(TAG, "Timeout error");
                        e.printStackTrace();
                    }

                    initialize.Initialize_User(urlConnection, params, mHandler);
                }//TODO 사용자 정보 조회 필요 유무 판단 중
                else if (params[2].equals("profile"))
                {
                    Log.d(TAG, "get profile information");
                    //num : 13  get profile information
                    //parmas[0] : type , [1] : param1 : user/me

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
                    urlConnection.setRequestProperty("Authorization", "Bearer " + aboutFile.readFile("token"));

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(false);
                    urlConnection.setUseCaches(false);

                    getProfile.GetProfile_information(urlConnection, params, mHandler);
                }
            } else if (type.equals("oauth")) {

                Log.d(TAG, " get Token");
                Log.d(TAG, " server ip  : " + serverip + type + "/" + params[1]);
                //num 6  : get Token
                //parmas[0] :type , [1] :param1 , [2] : mac , [3] : model name
                url = new URL(serverip + type + "/" + params[1]);

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

                urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                urlConnection.setRequestProperty("host", host);
                urlConnection.setRequestProperty("cmx-dvc-model", params[3]);
                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 10seconds
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                } catch (Exception e) {
                    Log.e(TAG, "Timeout error");
                    e.printStackTrace();
                }
                requestToken.GetToken(urlConnection, params, mHandler);

                if(!TypeDef.access_uc_register)
                {
                    SubActivity.getInstance().access_uc_register_check();
                    Log.d(TAG," access_uc_register_check : " + TypeDef.access_uc_register);
                    TypeDef.access_uc_register = true;
                }
            }
            else if (type.equals("access_register")) {
                //access register
                //params[0] : type , [1] : dong , [2] : ho , [3] : dansi server , [4] workType new/map , [5] : access url

                if(aboutFile.readFile("access_ssl").equals("N"))
                {
                    url = new URL("http://" + params[5] + "/v1/service/wallpad");
                }
                else
                {
                    url = new URL("https://" + params[5] + "/v1/service/wallpad");
                }

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
                //time out
                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 5seconds
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                } catch (Exception e) {
                    Log.e(TAG, "Timeout error");
                    e.printStackTrace();
                }
                urlConnection.setRequestProperty("accept", "text/xml, application/xml, application/json");
                urlConnection.setRequestProperty("accept-language", "{ko|en|jp}");
                urlConnection.setRequestProperty("host", host);
                urlConnection.setRequestProperty("content-type", "application/json");
                urlConnection.setRequestProperty("cmx-dvc-type", "wp");
                Log.d(TAG, "Token : " + aboutFile.readFile("token"));
                urlConnection.setRequestProperty("Authorization", "Bearer " + aboutFile.readFile("token"));
                accessRegister.RegisterID(urlConnection, params, mHandler);

            }
            else if (type.equals("uc_group")) {
                Log.d(TAG, "uc group register");
                //params[0] : type , [1] : ip:port/address , [2] : name , [3] : id , [4] :password , [5] : mac ,[6] :model name , [7] : nation code ,[8] : new type
                String uc_group_ip = "https://" + params[1];
                url = new URL(uc_group_ip);
                serverip = uc_group_ip;
                Log.d(TAG, " server ip  : " + serverip);
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
                uc_grounp_register.UC_Group_Register(urlConnection, params, mHandler);

            } else if (type.equals("uc_user")) {
                Log.d(TAG, "uc user register");
                //params[0] : type , [1] : ip:port/address , [2] : name , [3] : id , [4] :password , [5] : mac ,[6] :model name , [7] : nation code ,[8] : new type
                String uc_register_ip = "https://" + params[1];
                url = new URL(uc_register_ip);
                serverip = uc_register_ip;
                Log.d(TAG, " server ip  : " + serverip);
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
                uc_user_register.UC_User_Register(urlConnection, params, mHandler);
            }
            else if (type.equals("resourceNo_send")) {
                //Local server
                //params[0] : type , [1] : dong , [2] : ho , [3] : dansi server , [4] workType new/map , [5] : access url

//                url = new URL("http://10.0.0.42/public/server_info.php?method=resourceInfo&id="+ params[2] +
//                        "&dong="+ params[3]+"&ho="+ params[4] +"&mac="+ params[5] + "&resource="+params[6]+"&tag=insert");

                url = new URL("http://" + params[1] + "/public/server_info.php?method=resourceInfo&id="+ params[2] +
                        "&dong="+ params[3]+"&ho="+ params[4] +"&mac="+ params[5] + "&resource="+params[6]+"&tag=insert");
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
                //time out
                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 5seconds
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                } catch (Exception e) {
                    Log.e(TAG, "Timeout error");
                    e.printStackTrace();
                }
                resourceNO_send.resource_number_send(urlConnection, params, mHandler);
            }
            else if (type.equals("resourceNo_initial")) {
                //Local server
                //params[0] : type , [1] : dong , [2] : ho , [3] : dansi server , [4] workType new/map , [5] : access url

//                url = new URL("http://10.0.0.42/public/server_info.php?method=resourceInfo&id="+ params[2] +
//                        "&dong="+ params[3]+"&ho="+ params[4] +"&mac="+ params[5] + "&resource="+params[6]+"&tag=delete");

                url = new URL("http://" + params[1] + "/public/server_info.php?method=resourceInfo&id="+ params[2] +
                        "&dong="+ params[3]+"&ho="+ params[4] +"&mac="+ params[5] + "&resource="+params[6]+"&tag=delete");

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
                //time out
                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 5seconds
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                } catch (Exception e) {
                    Log.e(TAG, "Timeout error");
                    e.printStackTrace();
                }
                resourceNO_initial.resource_number_initial(urlConnection, params, mHandler);
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
