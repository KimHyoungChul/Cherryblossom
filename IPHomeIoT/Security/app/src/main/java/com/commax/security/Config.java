package com.commax.security;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class Config {
	private ContentResolver m_ContentResolver = null;			/* Content Resolver Object */
	
	public static String m_strHomeSvrIPAddr = "";						/* Home Server IP Address */

	public static int nAgentType = -1;
	
	public static int nSensorUsed1State = -1;
	public static int nSensorUsed2State = -1;
	public static int nSensorUsed3State = -1;
	public static int nSensorUsed4State = -1;
	public static int nSensorUsed5State = -1;
	
	public static int nSensorUsed1PrevState = -1;
    public static int nSensorUsed2PrevState = -1;
    public static int nSensorUsed3PrevState = -1;
    public static int nSensorUsed4PrevState = -1;
    public static int nSensorUsed5PrevState = -1;
	
	public static int nPreventSensorUsed1State = -1;
	public static int nPreventSensorUsed2State = -1;
	public static int nPreventSensorUsed3State = -1;
	public static int nPreventSensorUsed4State = -1;
	public static int nPreventSensorUsed5State = -1;
	
	public static int nOutingVisitorCatpure = -1;
    public static int nOutingLightAllOff = -1;
    public static int nOutingGasClose = -1;
    public static int nOutingBypassCall = -1;
    public static int nOutingSensorUsed1State = -1;
    public static int nOutingSensorUsed2State = -1;
    public static int nOutingSensorUsed3State = -1;
    public static int nOutingSensorUsed4State = -1;
    public static int nOutingSensorUsed5State = -1;
    public static int nOutingDelayTime = 0;
    public static int nOutingReturnTime = 0;
    
    public static String strOutingDelayTime = "";
    public static String strOutingReturnTime = "";
    
    public static int SoundType = -1;
    
    public static int GetMode = 0;
    
    public static boolean bOK_btn_click = true;
    
    public static boolean bSecurity_Check_1 = false;
    public static boolean bSecurity_Check_2 = false;
    public static boolean bSecurity_Check_3 = false;
    public static boolean bSecurity_Check_4 = false;
    public static boolean bSecurity_Check_5 = false;
    public static boolean bGoOut_Check_1 = false;
    public static boolean bGoOut_Check_2 = false;
    public static boolean bGoOut_Check_3 = false;
    public static boolean bGoOut_Check_4 = false;
    public static boolean bGoOut_Check_5 = false;
    public static boolean bGoOut_Check_Capture = false;
    public static boolean bGoOut_Check_TurnoffLight = false;
    public static boolean bGoOut_Check_CloseGas = false;
    public static boolean bGoOut_Check_BypassCall = false;
    
	public Config(ContentResolver cr) {
		/* Content Resolver Object */
		this.m_ContentResolver = cr;

		/* Get System Information */
		getSystemInformation();
	}

	/* Get System Information */
	private void getSystemInformation() {
		/* Get Settings Database Query(Home Server IP Context URI) */
		this.m_strHomeSvrIPAddr = getSettingsDatabaseQuery(NameSpace.HOME_SERVER_IP_CONTEXT_URI);
	}

	/* Get Settings Database Query */
	public String getSettingsDatabaseQuery(Uri uiContextUri) {
		String strContext = "";
		Cursor InfoCursor = null;

		if((InfoCursor = m_ContentResolver.query(uiContextUri, null, null, null, null)) != null) {
			while(InfoCursor.moveToNext()) {
				strContext = InfoCursor.getString(InfoCursor.getColumnIndex("value"));
			}

			if(InfoCursor != null) InfoCursor.close();
			InfoCursor = null;
		}

		return strContext;
	}
}