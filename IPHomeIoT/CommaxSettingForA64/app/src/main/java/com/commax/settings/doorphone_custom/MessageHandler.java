package com.commax.settings.doorphone_custom;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by OWNER on 2016-11-23.
 */

public class MessageHandler {

    private String LOG_TAG = MessageHandler.class.getSimpleName();

    public static MessageHandler mInst = null;

    //공통 XML 형식 TAG
    public static final String MESSAGE_HEADER           = "MESSAGE_HEADER";
    public static final String MESSAGE_BODY             = "MESSAGE_BODY";

    //Message Type(Msg_type)
    public static final String MSG_SYSTEM_NOTIFY        = "SYSTEM_NOTIFY_INFO_MESSAGE";
    public static final String MSG_AUXPTZ_HEARTBEAT     = "AUXPTZ_HEARTBEAT_MESSAGE";
    public static final String MSG_SYSTEM_CONTROL       = "SYSTEM_CONTROL_MESSAGE";
    public static final String MSG_RESPONSE_PARAM       = "RESPONSE_PARAM";


    //종버튼 3초 누르면 오는 XML형식의 브로드케스팅 정보(255.255.255.255:3003)
    public static final String DEVICE_TYPE              = "DEVICE_TYPE";
    public static final String IPC_SERIALNUMBER         = "IPC_SERIALNUMBER";
    public static final String LAN_CONFIG                = "LANConfig";

    //Items Type 정의
    public static final String MSG_TYPE                   = "Msg_type";
    public static final String DEV_TYPE                   = "DeviceType";
    public static final String DEV_MODULE                 = "DeviceModule";
    public static final String SER_NUM                    = "SerialNumber";
    public static final String MAC_ADDR                   = "MacAddress";
    public static final String IP_ADDR                    = "IPAddress";
    public static final String NET_MASK                   = "Netmask";
    public static final String GATE_WAY                   = "Gateway";


    public static MessageHandler getInst() {
        if (mInst == null) {
            mInst = new MessageHandler();
        }
        return mInst;
    }

    /**
     * Received Data 핸들링
     *
     * @param data
     * @return
     */
    public void ReceivedDataParser(MessageData message, final String data) {

        String InputData = data;
        try {

            //XmlPullParser 초기화
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(InputData.trim()));

            //이벤트 저장할 변수선언
            int Itemcount=0;
            int eventType = xpp.getEventType();
            String tagName="";
            String Attribute="";

            Log.d(LOG_TAG, "==========================================");

            //xml의 데이터의 끝까지 돌면서 원하는 데이터를  얻어옴
            while(eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_TAG){ //시작 태그를 만났을때
                    //태그명을 저장
                    tagName = xpp.getName();
                    Itemcount = xpp.getAttributeCount();
                    //Item 저장
                    if(Itemcount > 0) {
                        for(int i=0; i<Itemcount ; i++) {
                            putData(message, xpp.getAttributeName(i), xpp.getAttributeValue(i));
                            Attribute += " (" + xpp.getAttributeName(i) + "," + xpp.getAttributeValue(i) + ") ";
                        }
                    }
                    Log.d(LOG_TAG, "S: " + tagName + Attribute);

                }else if(eventType == XmlPullParser.TEXT){ //내용
                    if(Itemcount > 0) {
//                        tagName +=  xpp.getText().trim();
//                        Log.d(LOG_TAG, "+: " + tagName + " " + xpp.getAttributeCount());
                    }

                }else if(eventType == XmlPullParser.END_TAG){ //닫는 태그를 만났을때
                    //태그명을 저장
//                    tagName = xpp.getName();
//                    Log.d(LOG_TAG, "E: " + tagName);

                }
                eventType = xpp.next(); //다음 이벤트 타입
            }
            Log.d(LOG_TAG, "==========================================");

        }catch (Exception e) {

        }
    }

    private void putData(MessageData message, String name, String value) {

        switch(name) {
            case MSG_TYPE: message.setMsgType(value);break;

            case DEV_TYPE: message.setDeviceName(value);break;

            case DEV_MODULE: message.setDeviceModule(value);break;

            case SER_NUM: message.setUsn(value);break;

            case MAC_ADDR: message.setMac(value);break;

            case IP_ADDR: message.setIpv4(value);break;

            case NET_MASK: message.setIpv4subnet(value);break;

            case GATE_WAY: message.setIpv4gateway(value);break;

        }
    }

    /**
     * Host IP 구함
     * @return
     */
    public String getHostMacAddr() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.d(LOG_TAG, ex.toString());
        }
        return null;
    }

    public String getHostIPAddr() {
        final String IP_NONE = "N/A";
        final String WIFI_DEVICE_PREFIX = "eth";

        String LocalIP = IP_NONE;
        try {
            NetworkInterface.getNetworkInterfaces();
            for (Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if( LocalIP.equals(IP_NONE) )
                            LocalIP = inetAddress.getHostAddress().toString();
                        else if( intf.getName().startsWith(WIFI_DEVICE_PREFIX) )
                            LocalIP = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            Log.d(LOG_TAG, e.toString());
        }
        return LocalIP;
    }


    /**
     * 도어카메라에 월패드 IP 등록함
     * @return
     */
    public void SendHostConfigResponse(String doorIP) {

        try {
            String hostIP = getHostIPAddr();
            //String cmdInfo = "http://10.15.1.110/settings/platform/&enable=1&server=10.15.1.107&port=5060&user=admin&password=123456/";
            String cmdInfo = String.format("http://%s/settings/platform/&enable=1&server=%s&port=5060&user=admin&password=123456/" ,doorIP, hostIP);
            URL putcmd = new URL(cmdInfo);
            InputStream httpio = putcmd.openStream();
            httpio.close();

//            URLConnection t_connection = putcmd.openConnection();
//            t_connection.setReadTimeout(3000);
//            InputStream t_inputStrem = t_connection.getInputStream();

            //Log.d(LOG_TAG, "SendHostConfigResponse...OK " + cmdInfo);

        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
    }


}
