package com.commax.securityservice;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class SecurityService extends Service {
	private static Context m_Context = null;					/* Application Context */
	private static SecurityService m_hInstance = null;			/* Security Service Object Instance */

	public static Config m_Config = null;						/* System Config Object */
	public static SysManager m_SysManager = null;				/* System Manager Object */
	private BroadcastReceiver m_BroadcastReceiver = null;		/* Broadcast Receiver */

	/* Get Security Service Instance */
	public static SecurityService getInstance() {
		/* Security Service Object Instance */
		return (SecurityService)m_hInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		/* Application Context */
		m_Context = getApplicationContext();

		/* Security Service Object Instance */
		m_hInstance = this;

		/* Initialize Object */
		initializeObject();
	}

	/* Initialize Object */
	private void initializeObject() {
		/* System Config Object */
		if(m_Config == null)
			m_Config = new Config(getContentResolver());

		/* System Manager Object */
		if(m_SysManager == null)
			m_SysManager = new SysManager(getApplicationContext());

		/* Create Broadcast Receiver */
		createBroadcastReceiver();
	}

	/* Create Broadcast Receiver */
	private void createBroadcastReceiver() {
		/* Broadcast Receiver */
		if(m_BroadcastReceiver == null) {
			m_BroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					/* System Language Change Broadcast Receiver */
					if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            			/* Create Home Screen Intent */
						Intent inHomeScreen = new Intent();
						try {
							Log.i(NameSpace.DEBUG_TAG, "Goto Home Screen. !!!");

							inHomeScreen.setAction("android.intent.action.MAIN");
							inHomeScreen.addCategory("android.intent.category.HOME");
							inHomeScreen.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_FORWARD_RESULT |
									Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
									Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


							context.startActivity(inHomeScreen);

						} catch(ActivityNotFoundException e) {
							Log.e(NameSpace.DEBUG_TAG, "Home Screen Not Found Exception. !!!" + e);
						}

						inHomeScreen = null;
					}
				}
			};

			/* System Screen ON Action */
			registerReceiver(m_BroadcastReceiver, new IntentFilter("android.intent.action.SCREEN_ON"));
			/* System Screen OFF Action */
			registerReceiver(m_BroadcastReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
		}
	}

	/* Method Post Emergency Signal */
	public static int methodPostEmergencySignal(int nTestFlag, int nMasterPort, int nSlavePort, int nEmerType, int nEmerState) {
		Log.i(NameSpace.DEBUG_TAG, "Post Emergency => Test Flag : " + nTestFlag + ", Master Port : " + nMasterPort + ", Slave Port : " + nSlavePort + ", Emer Type : " + nEmerType + ", Emer State : " + nEmerState);

		if(nTestFlag == 0) {
			/* Emergency Type */
			m_Config.m_nEmergencyType = nEmerType;
			/* Emergency State */
			m_Config.m_nEmergencyState = nEmerState;

			try {
				Intent inEmer = new Intent();
				inEmer.setClassName(NameSpace.EMERGENCY_PACKAGE, NameSpace.EMERGENCY_ACTIVITY);
				inEmer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SecurityService.getInstance().startActivity(inEmer);
				inEmer = null;
			}
			catch(ActivityNotFoundException e) {
				Log.e(NameSpace.DEBUG_TAG, "Emergency Activity Not Found Exception. !!!" + e);
			}
		}

		/* Emergency Broadcast Message */
		Intent intent = new Intent(NameSpace.EMERGENCY_ACTION);
		intent.putExtra("test", nTestFlag);
		intent.putExtra("masterPort", nMasterPort);
		intent.putExtra("slavePort", nSlavePort);
		intent.putExtra("emerType", nEmerType);
		intent.putExtra("emerStaus", nEmerState);
		m_Context.sendBroadcast(intent);
		intent = null;

		return 0;
	}

	/* Method Post Check Event Signal */
	public static int methodPostCheckEventSignal(int nType, String strDummy) {
		Log.i(NameSpace.DEBUG_TAG, "Post Check Event => Type : " + nType + ", Dummy : " + strDummy);

		/* Register Check Event Broadcast Message */
		Intent intent = new Intent(NameSpace.REG_CHECK_EVENT_ACTION);
		intent.putExtra("type", nType);
		intent.putExtra("dummy", strDummy);
		m_Context.sendBroadcast(intent);
		intent = null;

		return 0;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    static {
   		System.loadLibrary("secsvc");
	}

    /* Set CDX Service Subcribe Request */
    public static native int SetCdxSubcribeRequest(String strRemoteIPAddr);

    /* Media Service Control */
    public static native int MediaServiceControl(String strParams[]);

    /* Get Register Profile String */
    public static native String getRegProfileString(String strParams[]);
    /* Get Profile String */
    public static native String getProfileString(String strParams[]);
    /* Get Profile Inverse String */
    public static native String getProfileInverseString(String strParams[]);
    /* Write Profile String */
    public static native int writeProfileString(String strParams[]);
}
