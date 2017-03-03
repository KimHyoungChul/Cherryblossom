package com.commax.security;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SktManager extends Service {
    
	public static SktManager m_hInstance = null;			/* Socket Manager Object Instance */
	private Context m_Context = null;
	
	private static String CSS_NameSpace = "urn:css";		/* System Service Name Space */
	private static String CMM_NameSpace = "urn:cmm";       /* Media Service Name Space */
	
	/* Register Check Event Request Broadcast Message */
	static final String REG_CHECK_EVENT_REQUEST_ACTION = "com.commax.service.system.REG_CHECK_EVENT_REQUEST";
	/* Emergency Occur Request Broadcast Message */
	static final String EMERGENCY_OCCUR_REQUEST_ACTION = "com.commax.service.system.EMERGENCY_OCCUR_REQUEST";



	/* Get Socket Manager Instance */
	public static SktManager getInstance() {
		/* Socket Manager Object Instance */
		return m_hInstance;
	}

	public SktManager(Context context) {
		/* Socket Manager Object Instance */
		m_hInstance = this;
		m_Context = context;
	}

    /* Get Agent Type */
    public void getAgentType() {
        String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

        SoapObject soapClient = new SoapObject(CSS_NameSpace, "getAgentType");
        soapClient.addProperty("in", 0);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.bodyOut = soapClient;

        AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

        try {
            httpTrans.call("", soapEnvelope);
            SoapObject soapObj = (SoapObject)soapEnvelope.bodyIn;
            NameSpace.nAgentType = Integer.valueOf(soapObj.getProperty(0).toString());
            
            Log.i(NameSpace.DEBUG_TAG, "Get Agent Type : " + NameSpace.nAgentType);
        }
        catch(Exception e) {
            Log.e(NameSpace.DEBUG_TAG, "Get Agent Type failed. !!!");
        }

        soapClient = null;
        soapEnvelope = null;
        httpTrans = null;
    }

	/* Set Sensor Test Mode Request(true : 센서 테스트 모드 설정 요청, false : 센서 테스트 모드 해제 요청) */
	public void setSensorTestModeRequest(String strIPAddr, boolean bSensorTestModeFlag) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "setSensorTestMode");
		soapClient.addProperty("szIPAddr", strIPAddr);
		soapClient.addProperty("nSensorTestMode", (bSensorTestModeFlag == true) ? 1 : 0);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			Log.e(NameSpace.DEBUG_TAG, "Set Sensor Test Mode : " + soapEnvelope.getResponse().toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Set Sensor Test Mode failed. !!!");
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}

	/* Get Sensor Area Used */
	public void getSensorAreaUsed(String strIPAddr) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "getSensorAreaUsed");
		soapClient.addProperty("szIPAddr", strIPAddr);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {		    
		    httpTrans.call("", soapEnvelope);
			SoapObject soapObj = (SoapObject)soapEnvelope.bodyIn;

			/* 센서 1구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */				
			NameSpace.nSensorUsed1State = Integer.valueOf(soapObj.getProperty(0).toString());
			/* 센서 2구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nSensorUsed2State = Integer.valueOf(soapObj.getProperty(1).toString());
            /* 센서 3구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nSensorUsed3State = Integer.valueOf(soapObj.getProperty(2).toString());
			/* 센서 4구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nSensorUsed4State = Integer.valueOf(soapObj.getProperty(3).toString());
            /* 센서 5구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nSensorUsed5State = Integer.valueOf(soapObj.getProperty(4).toString());
			
			Log.i(NameSpace.DEBUG_TAG, "Sensor 1 Used State : " + NameSpace.nSensorUsed1State);
			Log.i(NameSpace.DEBUG_TAG, "Sensor 2 Used State : " + NameSpace.nSensorUsed2State);
			Log.i(NameSpace.DEBUG_TAG, "Sensor 3 Used State : " + NameSpace.nSensorUsed3State);
			Log.i(NameSpace.DEBUG_TAG, "Sensor 4 Used State : " + NameSpace.nSensorUsed4State);
			Log.i(NameSpace.DEBUG_TAG, "Sensor 5 Used State : " + NameSpace.nSensorUsed5State);
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Get Sensor Used State failed. !!!");
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}

	/* Set Sensor Area Used */
	public void setSensorAreaUsed(String strIPAddr, int nSensor1, int nSensor2, int nSensor3, int nSensor4, int nSensor5) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "setSensorAreaUsed");
		soapClient.addProperty("szIPAddr", strIPAddr);
		soapClient.addProperty("nSensorArea1Use", nSensor1);		/* 센서 1구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea2Use", nSensor2);		/* 센서 2구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea3Use", nSensor3);		/* 센서 3구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea4Use", nSensor4);		/* 센서 4구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea5Use", nSensor5);		/* 센서 5구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			Log.e(NameSpace.DEBUG_TAG, "Set Sensor Used State : " + soapEnvelope.getResponse().toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Set Sensor Used State failed. !!!");
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}

	/* Get Security Mode */
    public int getSecurityMode(String strIPAddr) {
        int nReturnValue = 0;
        String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

        SoapObject soapClient = new SoapObject(CSS_NameSpace, "getSecurityMode");
        soapClient.addProperty("szIPAddr", strIPAddr);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.bodyOut = soapClient;

        AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

        try {
            httpTrans.call("", soapEnvelope);
            SoapObject soapObj = (SoapObject)soapEnvelope.bodyIn;

            /* 보안 없음 : 0, 방범 모드 : 1, 외출 모드 : 2 */
            nReturnValue = Integer.valueOf(soapObj.getProperty(0).toString());

            Log.i(NameSpace.DEBUG_TAG, "Security Mode : " + soapObj.getProperty(0).toString());
        }
        catch(Exception e) {
            Log.e(NameSpace.DEBUG_TAG, "Get Security Mode failed. !!!");
        }

        soapClient = null;
        soapEnvelope = null;
        httpTrans = null;
        
        return nReturnValue;
    }
    
	/* Get Prevent Security Mode */
	public void getPreventSecurityMode(String strIPAddr) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "getPreventSecurityMode");
		soapClient.addProperty("szIPAddr", strIPAddr);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			SoapObject soapObj = (SoapObject)soapEnvelope.bodyIn;

			/* 센서 1구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nPreventSensorUsed1State = Integer.valueOf(soapObj.getProperty(0).toString());
			/* 센서 2구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nPreventSensorUsed2State = Integer.valueOf(soapObj.getProperty(1).toString());
			/* 센서 3구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nPreventSensorUsed3State = Integer.valueOf(soapObj.getProperty(2).toString());
			/* 센서 4구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nPreventSensorUsed4State = Integer.valueOf(soapObj.getProperty(3).toString());
			/* 센서 5구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nPreventSensorUsed5State = Integer.valueOf(soapObj.getProperty(4).toString());
			
			Log.i(NameSpace.DEBUG_TAG, "Prevent Sensor 1 Used State : " + soapObj.getProperty(0).toString());
			Log.i(NameSpace.DEBUG_TAG, "Prevent Sensor 2 Used State : " + soapObj.getProperty(1).toString());
			Log.i(NameSpace.DEBUG_TAG, "Prevent Sensor 3 Used State : " + soapObj.getProperty(2).toString());
			Log.i(NameSpace.DEBUG_TAG, "Prevent Sensor 4 Used State : " + soapObj.getProperty(3).toString());
			Log.i(NameSpace.DEBUG_TAG, "Prevent Sensor 5 Used State : " + soapObj.getProperty(4).toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Get Prevent Sensor Used State failed. !!!");
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}
	
	/* Set Prevent Security Mode */
	public void setPreventSecurityMode(String strIPAddr, int nSensor1, int nSensor2, int nSensor3, int nSensor4, int nSensor5) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "setPreventSecurityMode");
		soapClient.addProperty("szIPAddr", strIPAddr);
		soapClient.addProperty("nSensorArea1Use", nSensor1);		/* 센서 1구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea2Use", nSensor2);		/* 센서 2구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea3Use", nSensor3);		/* 센서 3구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea4Use", nSensor4);		/* 센서 4구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea5Use", nSensor5);		/* 센서 5구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			Log.e(NameSpace.DEBUG_TAG, "Set Prevent Sensor Used State : " + soapEnvelope.getResponse().toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Set Prevent Sensor Used State failed. !!!");
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}
	
	/* Release Prevent Security Mode */
	public void releasePreventSecurityMode(String strIPAddr) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "setPreventReleaseMode");
		soapClient.addProperty("szIPAddr", strIPAddr);
		
		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			Log.e(NameSpace.DEBUG_TAG, "Release Prevent Sensor Used State : " + soapEnvelope.getResponse().toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Release Prevent Sensor Used State failed. !!!");
		}
		
		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}

	/* Get Outing Security Mode */
	public void getOutingSecurityMode(String strIPAddr) {
	    String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "getOutingSecurityMode");
		soapClient.addProperty("szIPAddr", strIPAddr);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			SoapObject soapObj = (SoapObject)soapEnvelope.bodyIn;
			
			/* 방문자 저장(사용안함 : 0, 사용함 : 1) */
			NameSpace.nOutingVisitorCatpure = Integer.valueOf(soapObj.getProperty(0).toString());
			/* 전등 전체 끄기(사용안함 : 0, 사용함 : 1) */
			NameSpace.nOutingLightAllOff = Integer.valueOf(soapObj.getProperty(1).toString());
			/* 가스 닫기(사용안함 : 0, 사용함 : 1) */
			NameSpace.nOutingGasClose = Integer.valueOf(soapObj.getProperty(2).toString());
			/* 우회 통화(사용안함 : 0, 사용함 : 1) */
			NameSpace.nOutingBypassCall = Integer.valueOf(soapObj.getProperty(3).toString());

			/* 센서 1구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nOutingSensorUsed1State = Integer.valueOf(soapObj.getProperty(4).toString());
			/* 센서 2구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nOutingSensorUsed2State = Integer.valueOf(soapObj.getProperty(5).toString());
			/* 센서 3구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nOutingSensorUsed3State = Integer.valueOf(soapObj.getProperty(6).toString());
			/* 센서 4구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nOutingSensorUsed4State = Integer.valueOf(soapObj.getProperty(7).toString());
			/* 센서 5구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
			NameSpace.nOutingSensorUsed5State = Integer.valueOf(soapObj.getProperty(8).toString());
			/* 외출모드 Delay Time(Default : 60초) */
			NameSpace.nOutingDelayTime = Integer.valueOf(soapObj.getProperty(9).toString());
            /* 귀가모드 Delay Time(Default : 0초) */
			NameSpace.nOutingReturnTime = Integer.valueOf(soapObj.getProperty(10).toString());
			
			Log.i(NameSpace.DEBUG_TAG, "Visitor Capture State : " + soapObj.getProperty(0).toString());
			Log.i(NameSpace.DEBUG_TAG, "Light All OFF State : " + soapObj.getProperty(1).toString());
			Log.i(NameSpace.DEBUG_TAG, "Gas Close State : " + soapObj.getProperty(2).toString());
			Log.i(NameSpace.DEBUG_TAG, "Bypass Call State : " + soapObj.getProperty(3).toString());

			Log.i(NameSpace.DEBUG_TAG, "Sensor 1 Used State : " + soapObj.getProperty(4).toString());
			Log.i(NameSpace.DEBUG_TAG, "Sensor 2 Used State : " + soapObj.getProperty(5).toString());
			Log.i(NameSpace.DEBUG_TAG, "Sensor 3 Used State : " + soapObj.getProperty(6).toString());
			Log.i(NameSpace.DEBUG_TAG, "Sensor 4 Used State : " + soapObj.getProperty(7).toString());
			Log.i(NameSpace.DEBUG_TAG, "Sensor 5 Used State : " + soapObj.getProperty(8).toString());
			
			Log.i(NameSpace.DEBUG_TAG, "Outing Delay Time : " + soapObj.getProperty(9).toString());
            Log.i(NameSpace.DEBUG_TAG, "Outing Release Delay Time : " + soapObj.getProperty(10).toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Get Outing Option State failed. !!!" + e);	
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}	
		
	/* Set Outing Security Mode */
	public void setOutingSecurityMode(String strIPAddr, int nOutingReqFlag, int nVisitorCaptuer, int nLightAllOff, int nGasClose, 
	        int nBypassCall, int nSensor1, int nSensor2, int nSensor3, int nSensor4, int nSensor5, int nOutingDelayTime, int nOutingReleaseDelayTime) {   
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "setOutingSecurityMode");
		soapClient.addProperty("szIPAddr", strIPAddr);
		soapClient.addProperty("nOutingReqFlag", nOutingReqFlag);		/* 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1) */
		soapClient.addProperty("nVisitorCaptuer", nVisitorCaptuer);		/* 방문자 저장(사용안함 : 0, 사용함 : 1) */
		soapClient.addProperty("nLightAllOff", nLightAllOff);			/* 전등 전체 끄기(사용안함 : 0, 사용함 : 1) */
		soapClient.addProperty("nGasClose", nGasClose);					/* 가스 닫기(사용안함 : 0, 사용함 : 1) */
		soapClient.addProperty("nBypassCall", nBypassCall);				/* 우회통화(사용안함 : 0, 사용함 : 1) */
		soapClient.addProperty("nSensorArea1Use", nSensor1);			/* 센서 1구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea2Use", nSensor2);			/* 센서 2구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea3Use", nSensor3);			/* 센서 3구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea4Use", nSensor4);			/* 센서 4구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nSensorArea5Use", nSensor5);			/* 센서 5구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
		soapClient.addProperty("nOutingDelayTime", nOutingDelayTime);             /* 외출모드 Delay Time(Default : 60초, 설정 않함 : -1) */
        soapClient.addProperty("nOutingReleaseDelayTime", nOutingReleaseDelayTime); /* 귀가모드 Delay Time(Default : 0초, 설정 않함 : -1) */

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			Log.e(NameSpace.DEBUG_TAG, "Set Outing Option State : " + soapEnvelope.getResponse().toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Set Outing Option State failed. !!!");
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}

	/* Release Outing Security Mode */
	public void releaseOutingSecurityMode(String strIPAddr) {
		String strEndPoint = String.format("http://127.0.0.1:%d", NameSpace.CSS_SERVICE_PORT);

		SoapObject soapClient = new SoapObject(CSS_NameSpace, "setOutingReleaseMode");
		soapClient.addProperty("szIPAddr", strIPAddr);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.bodyOut = soapClient;

		AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

		try {
			httpTrans.call("", soapEnvelope);
			Log.e(NameSpace.DEBUG_TAG, "Outing Prevent Sensor Used State : " + soapEnvelope.getResponse().toString());
		}
		catch(Exception e) {
			Log.e(NameSpace.DEBUG_TAG, "Outing Prevent Sensor Used State failed. !!!");			
		}

		soapClient = null;
		soapEnvelope = null;
		httpTrans = null;
	}	
	
	
	/* Send Request Check Event */
    public void sendRequestCheckEvent(String strIPAddr, int nCheckValue, String strDemmy) {
//    	Intent intent = new Intent();
//        intent.putExtra("nCheckValue", REG_CHECK_EVENT_REQUEST_ACTION);
//        m_Context.sendBroadcast(intent);
    	
    	String strEndPoint = String.format("http://%s:%d", strIPAddr, NameSpace.CMM_SERVICE_PORT);

        SoapObject soapClient = new SoapObject(CMM_NameSpace, "reqCheckEvent");
        soapClient.addProperty("nCheckValue", nCheckValue);
        soapClient.addProperty("chDummy", strDemmy);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.bodyOut = soapClient;

        AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

        try {
            httpTrans.call("", soapEnvelope);
            Log.e(NameSpace.DEBUG_TAG, "Send Request Check Event : " + soapEnvelope.getResponse().toString());
        }
        catch(Exception e) {
            Log.e(NameSpace.DEBUG_TAG, "Send Request Check Event failed. !!!");
        }
        
        soapClient = null;
        soapEnvelope = null;
        httpTrans = null;
    }

	/* Send Request Emergency Event */
    public void sendRequestEmergencyEvent(String strIPAddr, int nIsTest, int nMasterPort, int nSlavePort, int nEmerType, int nEmerState) {
//    	Intent intent = new Intent(EMERGENCY_OCCUR_REQUEST_ACTION);
//    	intent.putExtra("EmerType", nEmerType);
//        intent.putExtra("EmerState", nEmerState);
//        m_Context.sendBroadcast(intent);
    	
    	String strEndPoint = String.format("http://%s:%d", strIPAddr, NameSpace.CMM_SERVICE_PORT);

        SoapObject soapClient = new SoapObject(CMM_NameSpace, "reqEmer");
        soapClient.addProperty("is-test", nIsTest);
        soapClient.addProperty("master-port", nMasterPort);
        soapClient.addProperty("slave-port", nSlavePort);
        soapClient.addProperty("emer-type", nEmerType);
        soapClient.addProperty("emer-state", nEmerState);
        
        Log.e(NameSpace.DEBUG_TAG, "emer_type " + nEmerType);
        Log.e(NameSpace.DEBUG_TAG, "emer_state " + nEmerState);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.bodyOut = soapClient;

        AndroidHttpTransport httpTrans = new AndroidHttpTransport(strEndPoint);

        try {
            httpTrans.call("", soapEnvelope);
            Log.e(NameSpace.DEBUG_TAG, "Send Request Emergency Event : " + soapEnvelope.getResponse().toString());
        }
        catch(Exception e) {            
            Log.e(NameSpace.DEBUG_TAG, "Send Request Emergency Event failed. !!!");
        }
        
        soapClient = null;
        soapEnvelope = null;
        httpTrans = null;      
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
