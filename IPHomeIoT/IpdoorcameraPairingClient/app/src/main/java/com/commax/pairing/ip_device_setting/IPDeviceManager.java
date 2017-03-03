package com.commax.pairing.ip_device_setting;

import android.content.Context;
import android.util.Log;

import com.commax.pairing.DeviceManagerConstants;
import com.commax.pairing.util.SettingValues;
import com.commax.pairing.udp.Client;
import com.commax.pairing.udp.CommaxProtocolConstants;
import com.commax.pairing.udp.Server;
import com.commax.pairing.udp.UDPReceiveDataListener;
import com.commax.pairing.util.IPAddressFinder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * IP Device UDP 통신 관리
 * Created by bagjeong-gyu on 2016. 9. 9..
 */
public class IPDeviceManager {

    //UDP 브로드캐스트 IP와 PORT
    //final String RECEIVER_IP = "10.255.255.255";
    final String RECEIVER_IP = "0.0.0.0"; //모든 UDP 통신에 대해 Listen
    final String BROADCAST_IP = "255.255.255.255";
    //final int PORT_NO = 9050;

    //테스트후 삭제하세요!!
//   final int SERVER_PORT_NO = 9050;
//    final int CLIENT_PORT_NO = 9050;


    //원래 포트번호
    //수신포트(월패드):39900, 수신포트(도어폰):39901
    final int SERVER_PORT_NO = 39901;// 받는 포트(자신 도어폰)
    final int CLIENT_PORT_NO = 39900; //보내는 포트(상대방 월패드)


    private final Context mContext;
    private UDPReceiveDataListener mListener;
    private Server mServer;
    private Client mClient;

    public IPDeviceManager(Context context) {
        mContext = context;

        try {
            mListener = (UDPReceiveDataListener) mContext;
        } catch (ClassCastException e) {
            Log.d(DeviceManagerConstants.LOG_TAG, "ClassCastException: " + e.getMessage());
        }

        initUdpServerAndClient();
    }

    public IPDeviceManager(Context context, UDPReceiveDataListener listener) {
        mContext = context;
        mListener = listener;


        initUdpServerAndClient();
    }

    /**
     * UDP 서버와 클라이언트 객체 생성
     */
    private void initUdpServerAndClient() {

        mServer = new Server(RECEIVER_IP, SERVER_PORT_NO, mListener);

        mClient = new Client(BROADCAST_IP, CLIENT_PORT_NO);
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

        mServer.close();
    }

    /**
     * UDP 클라이언트 close
     */
    public void closeUdpClient() {

        mClient.close();
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
    public void sendUdpBroadcast(IPDevice device) {

        mClient.send(getAssignIPBroadcast(device));
    }

    /**
     * 새로운 IP 할당 브로드캐스트에 사용할 JSON 형식의 문자열 생성
     *
     * @param device
     * @return
     */
    public String getAssignIPBroadcast(IPDevice device) {
        // { "action" : "assignIP", "newIP" : "10.150.2.11",
        // "subnetMask" : "255.0.0.0", "gateway" : "10.1.1.254" , "dns" : "168.126.63.1", "mac" : "2E:44:52:98:D0:00"}
        //이런 형식으로 보냄


        String newIP = device.getNewIPAddress();

        String mac = device.getMac();

        String sipPhoneNo = device.getSipPhoneNo();

        SettingValues settingValues = new SettingValues(mContext);

        String subnetMask = settingValues.getSubnetMask();
        String gateway = settingValues.getDefaultGateway();
        String dns = settingValues.getDnsServer();

        String masterIP = IPAddressFinder.getIPAddress(true);


        JSONObject deviceInfoJson = new JSONObject();

        try {
            deviceInfoJson.put(CommaxProtocolConstants.KEY_ACTION, CommaxProtocolConstants.ACTION_ASSIGN_IP);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_NEW_IP, newIP);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_MASTER_IP, masterIP);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_SIP_PHONE_NO, sipPhoneNo);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_SUBNET_MASK, subnetMask);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_GATEWAY, gateway);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_DNS, dns);
            deviceInfoJson.put(CommaxProtocolConstants.KEY_MAC, mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return deviceInfoJson.toString();
    }
}
