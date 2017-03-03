package com.commax.security;

import android.content.ContentResolver;
import android.net.Uri;

public class NameSpace extends Config {
    
    public NameSpace(ContentResolver cr) {
        super(cr);
    }
   
	public static final String DEBUG_TAG						= "Security";

	/* gSOAP Port Define */
	static final int CMM_SERVICE_PORT              = 29720;
    static final int CVS_PORT                              = 29740;
	static final int CSS_SERVICE_PORT					= 29742;			/* CSS Service Port */
	static final int CSS_EVENT_PORT						= 29743;			/* CSS Service Event Port */	

    /* User Agent Type */
    static final int NonUserAgentType                   = 0;
    static final int LocalUserAgentType                 = 1;                /* Local UA(Master Wallpad) */
    static final int RemoteUserAgentType                = 2;                /* Remote UA(Slave Wallpad) */
    public static final int MobileUserAgentType                = 3;                /* Remote UA(Home Mobile) */

	/* Packet Event Information 비상/가스/화재/방범 Type Define */
    static final int EmerEventNonType                   = 0;
	static final int EmerEventEmerType					= 1;				/* 비상 */
	static final int EmerEventFireType					= 2;				/* 화재 */
	static final int EmerEventGasType					= 3;				/* 가스 */
	static final int EmerEventPrevType					= 4;				/* 방범 */
	static final int EmerEventLadderType                 = 5;                /* 피난사다리 */
	static final int EmerEventSafeType                 = 6;                /* 금고 */
	
	/* 센서테스트 상태 */
    static final int SensorTestHappen = 5;
    static final int SensorTestStop = 6;
    static final int SensorTestClear = 7;

    static final int MAXSENSOR                           = 5;                /* 방범센서 개수 */
	static final int MasterPortPrev01Type				= 1;				/* 방범 1 구역 */
	static final int MasterPortPrev02Type				= 2;				/* 방범 2 구역 */
	static final int MasterPortPrev03Type				= 3;				/* 방범 3 구역 */
	static final int MasterPortPrev04Type				= 4;				/* 방범 4 구역 */
	static final int MasterPortPrev05Type				= 5;				/* 방범 5 구역 */	

	/* Packet Event Information 발생/정지/복귀 Type Define */
	static final int EmerEventOccurType					= 1;				/* 발생 */
	static final int EmerEventStopType					= 2;				/* 정지 */
	static final int EmerEventReturnType				= 3;				/* 복귀 */
	
	/* Request Check Event */
	static final int ReqCheckEventSuccess           = 1;               /* 성공 */
    static final int ReqCheckEventFail          = -1;                     /* 실패 */
    
    /*Request Emergency Event Check */
    static final int ReqEmergencyEventCheckSuccess    = 1;       /* 성공 */
    static final int ReqEmergencyEventCheckFail          = -1;      /* 실패 */
	
	/* Signal Post Check Event Signal Value Defined */
    static final int ReqEventParkIn                     = 1;                /* 입차 */
    static final int ReqEventPartOut                    = 2;                /* 출차 */
    static final int ReqEventParcelIn                   = 3;                /* 택배도착 */
    static final int ReqEventParcelOut                  = 4;                /* 택배찾아감 */
    static final int ReqEventNotifyMessage              = 5;                /* 공지사항 */

    static final int ReqEventSetOutMode                 = 21;               /* 외출설정 */
    static final int ReqEventRelOutMode                 = 22;               /* 외출해제 */
    static final int ReqEventSetPrevMode                = 23;               /* 방범설정 */
    static final int ReqEventRelPrevMode                = 24;               /* 방범해제 */
    static final int ReqEventChangePrevMode             = 25;               /* 방범 구역 변경 */
    public static final int ReqEventEmerEventReq               = 29;               /* 비상관련 이벤트 체크 요청 */

    static final int RegOutingSensorFailed              = 61;               /* 센서 open으로 외출 설정 실패 */
    static final int RegOutingSensorSuccess             = 62;               /* 외출 설정 성공 */
    static final int RegOutingSensorRelease             = 63;               /* 외출 해제 성공 */
    
    public static final int nOutingTimeMinute2 = 1;
    public static final int nOutingTimeSecond1 = 2;
    public static final int nOutingTimeSecond2 = 3;
    
    static final int RegPrevSensor1Failed               = 71;               /* 방범 센서 1구역 open으로 방범설정불가 */
    static final int RegPrevSensor2Failed               = 72;               /* 방범 센서 2구역 open으로 방범설정불가 */
    static final int RegPrevSensor3Failed               = 73;               /* 방범 센서 3구역 open으로 방범설정불가 */
    static final int RegPrevSensor4Failed               = 74;               /* 방범 센서 4구역 open으로 방범설정불가 */
    static final int RegPrevSensor5Failed               = 75;               /* 방범 센서 5구역 open으로 방범설정불가 */
    static final int RegPrevSensorSuccess               = 76;               /* 방범 설정 성공 */
    static final int RegPrevSensorRelease               = 77;               /* 방범 해제 성공 */

    public static final int SecurityNonMode                    = 0;                /* 보안 없음 */
    public static final int SecurityPreventMode                = 1;                /* 방범 모드 */
    public static final int SecurityOutingMode                 = 2;                /* 외출 모드 */
    
    static final int FakeSecurityNonMode                    = 0;                /* 보안 없음 */
    static final int FakeSecurityPreventMode                = 0;                /* 방범 모드 */
    static final int FakeSecurityOutingMode                 = 0;                /* 외출 모드 */
    
    static final int EMERGENCY_OCCUR_STREAM_MUSIC       = 12;               /* Emergency Occur Stream Music Level */
    
    /* 외출 설정 SendBroadCast */
    public static String ACTION_GOOUT_START                 = "com.commax.gooutsetting.GO_OUT_START";
    public static String ACTION_GOOUTRELEASE_BY_APP     = "com.commax.services.gooutrelease.GOOUTRELEASE_BY_APP";
        
    /* msg id for handler */
    public static final int MSG_NETWORK_FAIL            = 1;
    public static final int MSG_KSOAP_FAIL                 = 2;
    public static final int MSG_SETTING_SUCCESS        = 3;
    public static final int MSG_ON_WORKING               = 4;
    
    public static final int MSG_IS_GOON = 5;
    public static final int DIALOG_IS_GOON = 6;
    
    /* dialogs */
    public static final int DIALOG_NETWORK_FAIL             = 7;
    public static final int DIALOG_ALREADY_OUT_SETTED          = 8;
    public static final int DIALOG_TRUNOFF_SECURITY      = 9;
    
    public static final int OutingDelayTime             = 10;
    public static final int OutingReturnTime          = 11;
    
    /* HomeKey Event Check */
    public static int HomeKeyEventCheck          = 1;
    public static int SecurityHomeKeyEventCheck          = 1;
    public static int SensorRenameHomeKeyEventCheck          = 1;
    public static int OutModeHomeKeyEventCheck          = 1;
    public static int OutModeOptionHomeKeyEventCheck          = 1;

	/* Home Server IP Context URI */
	static final Uri HOME_SERVER_IP_CONTEXT_URI			= Uri.parse("content://com.commax.provider.settings/setting/6");
	    
	/* System Network Link Broadcast Message */
	public static final String SYSTEM_NETWROK_LINK_ACTION              = "com.commax.service.system.NETLINK";
	/* Register Check Event Broadcast Message */
	public static final String REG_CHECK_EVENT_ACTION			= "com.commax.service.system.REG_CHECK_EVENT";	
	/* Emergency Broadcast Message */
	static final String EMERGENCY_ACTION				   = "com.commax.service.system.EMERGENCY";
	/* DBus Connect Broadcast Message */
    static final String DBUS_CONNECT_ACTION                     = "com.commax.service.system.DBUS_CONNECT";
    /* DBus Disconnect Broadcast Message */
    public static final String DBUS_DISCONNECT_ACTION                  = "com.commax.service.system.DBUS_DISCONNECT";
	
    /* Request Home Button Key Event */
    public static final String REQ_HOME_KEY_EVENT                      = "busybox echo home > /proc/fkey;busybox echo homeup > /proc/fkey";
    
    /* Home Category Event */
    public static final String REQ_HOME_CATEGOTY_EVENT                      = "android.intent.category.HOME";
    
    public static final String SYSTEM_KEY_SHOW_ACTION		= "com.commax.systemkey.SHOW_ACTION";	/* System Key Show Action */
    public static final String SYSTEM_KEY_HIDE_ACTION		= "com.commax.systemkey.HIDE_ACTION";	/* System Key Hide Action */
}
