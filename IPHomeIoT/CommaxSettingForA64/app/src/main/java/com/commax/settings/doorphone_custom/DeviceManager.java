package com.commax.settings.doorphone_custom;

import android.content.Context;
import android.util.Log;

import com.commax.settings.udp.Client;
import com.commax.settings.udp.Server;
import com.commax.settings.udp.UDPReceiveDataListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;


/**
 * Device UDP 통신 관리
 * Created by bagjeong-gyu on 2016. 9. 9..
 */
public class DeviceManager {

    private static final String LOG_TAG = DeviceManager.class.getSimpleName();
    //UDP 브로드캐스트 IP와 PORT
    //final String RECEIVER_IP = "10.255.255.255";
    final String RECEIVER_IP = "0.0.0.0"; //모든 UDP 통신에 대해 Listen
    final String BROADCAST_IP = "255.255.255.255";
    //final int PORT_NO = 9050;

    //테스트후 삭제하세요!!
//    final int SERVER_PORT_NO = 9050;
//    final int CLIENT_PORT_NO = 9050;

    //원래 포트번호
    //수신포트(월패드):39900, 수신포트(도어폰):39901
    final int SERVER_PORT_NO = 3003; //월패드, 받는 포트(자신 월패드)
    final int CLIENT_PORT_NO = 5060; //도어폰, 보내는 포트(상대방 도어폰)


    private final Context mContext;
    private UDPReceiveDataListener mListener;
    private Server mServer;
    private Client mClient;

    public DeviceManager(Context context) {
        mContext = context;

        try {
            mListener = (UDPReceiveDataListener) mContext;
        } catch (ClassCastException e) {
            Log.d(LOG_TAG, "ClassCastException: " + e.getMessage());
        }


    }

    public DeviceManager(Context context, UDPReceiveDataListener listener) {
        mContext = context;
        mListener = listener;


    }

    /**
     * UDP 서버와 클라이언트 객체 생성
     */
    public void initUdpServerAndClient() {

        //포트번호 한 번 더 확인!!
        mServer = new Server(RECEIVER_IP, SERVER_PORT_NO, mListener);

        //mClient = new Client(BROADCAST_IP, CLIENT_PORT_NO);

/*
        //테스트
        FunclibAgent funclibAgent = FunclibAgent.getInstance();
        int lUser0 = funclibAgent.LoginDev("10.15.1.110", 8091, "admin", "123456");
        if(lUser0 == 0)
        {
            Log.e(LOG_TAG, "login error..");
            return;
        }
        Log.d(LOG_TAG, "login success");

        funclibAgent.release();
*/


    }


    /**
     * UDP 서버 close
     */
    public void closeUdpServer() {
        //클래스를 싱글턴 패턴으로 생성하니 액티비티를 Finish해도 클래스가 살아있고
        //이 클래스가 예전 액티비티 객체를 잡고 있고 그것을 호출해서
        //화면이 갱신되지 않는 문제 발생
        //Server뿐만 아니라 IPDeviceManager도 액티비티 Finish할 때 instance에 null을 할당해야 함

        //Server.getInstance(RECEIVER_IP, PORT_NO, mListener).close();
        if(mServer != null) {
            mServer.close();
        }
    }

    /**
     * UDP 클라이언트 close
     */
    public void closeUdpClient() {

        if(mClient != null) {
            mClient.close();
        }
    }

    /**
     * 브로드캐스팅
     *
     * @param deviceInfo
     */
    public void sendUdpBroadcast(String deviceInfo) {

        mClient.send(deviceInfo);
    }


    /**
     * 브로드캐스팅
     *
     * @param device
     */
    public void sendUdpBroadcast(CustomDevice device) {

        mClient.send(getAssignIPBroadcast(device));
    }

    /**
     * 새로운 IP 할당 브로드캐스트에 사용할 JSON 형식의 문자열 생성
     *
     * @param device
     * @return
     */
    public String getAssignIPBroadcast(CustomDevice device) {
        // { "action" : "assignIP", "newIP" : "10.150.2.11",
        // "subnetMask" : "255.0.0.0", "gateway" : "10.1.1.254" , "dns" : "168.126.63.1", "mac" : "2E:44:52:98:D0:00"}
        //이런 형식으로 보냄

//
//        String newIP = device.getNewIPAddress();
//
//        String mac = device.getMac();
//
//        String sipPhoneNo = device.getSipPhoneNo();
//
//        SettingValues settingValues = new SettingValues(mContext);
//
//        String subnetMask = settingValues.getSubnetMask();
//        String gateway = settingValues.getDefaultGateway();
//        String dns = settingValues.getDnsServer();
//
//        String masterIP = IPAddressFinder.getIPAddress(true);
//
//
//        JSONObject deviceInfoJson = new JSONObject();
//
//        try {
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_ACTION, CommaxProtocolConstants.ACTION_ASSIGN_IP);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_DEVICE_NAME, device.getName());
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_IP, newIP);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_MASTER_IP, masterIP);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_SIP_PHONE_NO, sipPhoneNo);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_SUBNET_MASK, subnetMask);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_GATEWAY, gateway);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_DNS, dns);
//            deviceInfoJson.put(CommaxProtocolConstants.KEY_MAC, mac);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        return deviceInfoJson.toString();


        //위 주석풀면 아래 라인 삭제하세요!!
        return null;
    }

    /**
     * Received Data 핸들링
     *
     * @param data
     * @return
     */
    public void ReceivedDataParser(final String data){
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

            Log.d(LOG_TAG, "==========================================");

            //xml의 데이터의 끝까지 돌면서 원하는 데이터를  얻어옴
            while(eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_TAG){ //시작 태그를 만났을때
                    //태그명을 저장
                    tagName = xpp.getName();
                    Itemcount = xpp.getAttributeCount();
                    if(Itemcount > 0) {
                        for(int i=0; i<Itemcount ; i++) {
                            tagName += " (" + xpp.getAttributeName(0) + " " + xpp.getAttributeValue(0) + ")";
                        }
                    }
                    Log.d(LOG_TAG, "S: " + tagName + " " +  xpp.getAttributeCount()); //xpp.getAttributeCount()

                }else if(eventType == XmlPullParser.TEXT){ //내용
                    if(Itemcount > 0) {
                        //tagName += xpp.getAttributeValue(0);
                        tagName +=  xpp.getText().trim();
                        Log.d(LOG_TAG, "+: " + tagName + " " + xpp.getAttributeCount());
                    }

                }else if(eventType == XmlPullParser.END_TAG){ //닫는 태그를 만났을때
                    //태그명을 저장
                    tagName = xpp.getName();
                    Log.d(LOG_TAG, "E: " + tagName+ " " + xpp.getAttributeCount() );

                }
                eventType = xpp.next(); //다음 이벤트 타입
            }
        }catch (Exception e) {

        }
    }

    private void putData(String name, String value) {

        switch(name) {
            case "Msg_type":
        }
    }


}
