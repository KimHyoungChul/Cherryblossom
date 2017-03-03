package com.commax.settings.onvif;

import android.content.Context;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.onvif.ver10.schema.nativeParcel.ProbeMatch;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @brief Probe메시지를 통해 Onvif디바이스를 찾고 정보를 파싱하는 클래스
 */
public class OnvifProbe {
    /*************************************************************************
     * private Defines
     *************************************************************************/
    private final static String TAG = OnvifProbe.class.getSimpleName();
    private final static int TIMEOUT = 3000;
    /*************************************************************************
     * Variables
     *************************************************************************/
    private String mIPAddress = null;
    private String mProbeMessage;
    private String mProbeMessageUUID;
    private ArrayList<ProbeMatch> mProbeMatchesList = null;

    // Socket
    private DatagramPacket mDatagramPacket = null;
    private MulticastSocket mMulticastSocket = null;
    private boolean mRun = true;

    /*************************************************************************
     * 생성자
     *************************************************************************/
    public OnvifProbe(Context context, String remoteIP) {
        this.mIPAddress = remoteIP;
        this.mProbeMessageUUID = UUID.randomUUID().toString();
        this.mProbeMessage = createProbeMessage(mProbeMessageUUID);
    }

    //원래 코드

    /*************************************************************************
     * create ProbeMessage
     *************************************************************************/
    private String createProbeMessage(String uuid) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<Envelope xmlns:dn=\"http://www.onvif.org/ver10/network/wsdl\" xmlns=\"http://www.w3.org/2003/05/soap-envelope\">");
        sb.append("<Header>");
        sb.append("<wsa:MessageID xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">uuid:");
        sb.append(uuid);
        sb.append("</wsa:MessageID>");
        sb.append("<wsa:To xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To>");
        sb.append("<wsa:Action xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\">http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action>");
        sb.append("</Header>");
        sb.append("<Body>");
        sb.append("<Probe xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\">");
        sb.append("<Types>dn:NetworkVideoTransmitter</Types>");
        //sb.append("<Types />");
        sb.append("<Scopes />");
        sb.append("</Probe>");
        sb.append("</Body>");
        sb.append("</Envelope>");
        return sb.toString();
    }

    //테스트후 삭제하세요!!
//	/*************************************************************************
//	 * create ProbeMessage
//	 *************************************************************************/
//	private String createProbeMessage(String uuid) {
//
//		return "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:tns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><soap:Header><wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action><wsa:MessageID>urn:uuid:c032cfdd-c3ca-49dc-820e-ee6696ad63e2</wsa:MessageID><wsa:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To></soap:Header><soap:Body><tns:Probe/></soap:Body></soap:Envelope>";
//
//	}

    /*************************************************************************
     * sendProbeMessage
     *************************************************************************/
    public ArrayList<ProbeMatch> sendProbeMessage() {
        mProbeMatchesList = new ArrayList<ProbeMatch>();
        try {
            mMulticastSocket = new MulticastSocket(3702);
            if (mIPAddress == null) {
                mIPAddress = "239.255.255.250"; //onvif discovery multicast IP(수정금지)
                //mIPAddress = "10.40.100.100"; //for test
                //mIPAddress = "10.1.1.37"; //for test
                mMulticastSocket.setBroadcast(true);
            } else {
                // 특정 device를 IP주소로 검색 할 경우 broadcast를 하지 않음.
                mMulticastSocket.setBroadcast(false);
            }

            mDatagramPacket = new DatagramPacket(mProbeMessage.getBytes(), mProbeMessage.getBytes().length);
            mDatagramPacket.setAddress(InetAddress.getByName(mIPAddress));

            mDatagramPacket.setPort(3702);
            mMulticastSocket.setSoTimeout(TIMEOUT);
            mMulticastSocket.setTimeToLive(64);
            mMulticastSocket.send(mDatagramPacket);
            // mMulticastSocket.send(mDatagramPacket);
            byte buf[] = new byte[4096];
            mDatagramPacket.setData(buf);

            while (mRun) {
                try {
                    mMulticastSocket.receive(mDatagramPacket);
                    SoapSerializationEnvelope mEnvelope;
                    mEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
                    mEnvelope.setAddAdornments(false);
                    mEnvelope.dotNet = false;
                    XmlPullParser xp = new KXmlParser();
                    xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                    String receiveData = new String(mDatagramPacket.getData()).trim();
                    StringReader reader = new StringReader(receiveData);
                    Log.d(TAG, "readProbe:" + receiveData);
                    xp.setInput(reader);
                    mEnvelope.parse(xp);
                    if (mEnvelope.bodyIn instanceof SoapFault) {
                        Log.e(TAG, "error SoapFault:" + mEnvelope.bodyIn.toString());
                    } else {
                        ProbeMatch probe = new ProbeMatch(mEnvelope, mProbeMessageUUID, mIPAddress);
//						if (probe.isSBOXDevice) {
                        //if (probe.mTypes != null && probe.mTypes.contains("Device")) {
                        if (probe.mTypes != null) {
                            if (probe.mXAddrs != null) {
                                if (!isAdded(probe.mXAddrs)) {
                                    Log.d(TAG, "add Success ProbeMatchesList:" + probe.mXAddrs);
                                    mProbeMatchesList.add(probe);
                                    try {
                                        if (!mIPAddress.equals("239.255.255.250")) {
                                            close();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Log.e(TAG, "add Failure ProbeMatchesList:" + probe.mXAddrs);
                                }
                            } else {
                                Log.e(TAG, "error Probe XAddrs:" + probe.mXAddrs);
                            }
                        } else {
                            Log.e(TAG, "error Probe Type:" + probe.mTypes);
                        }
//						} else {
//							close();
//							LOG.e(TAG, "error Device:" + probe.isSBOXDevice);
//						}
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    Log.e(TAG, "------------ Discover Timeout ----------------");
                    close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mProbeMatchesList;
    }

    /*************************************************************************
     * isAdded
     *************************************************************************/
    private boolean isAdded(String XAddr) {
        int size = mProbeMatchesList.size();
        for (int i = 0; i < size; i++) {
            if (mProbeMatchesList.get(i).mXAddrs.equalsIgnoreCase(XAddr)) {
                return true;
            }
        }
        return false;
    }

    /*************************************************************************
     * close
     *************************************************************************/
    private void close() {
        mRun = false;
        mDatagramPacket = null;
        if (mMulticastSocket != null) {
            mMulticastSocket.close();
        }
        mMulticastSocket = null;
    }
}
