package com.commax.securityservice;

import android.content.ContentResolver;

public class Config {
	private ContentResolver m_ContentResolver = null;				/* Content Resolver Object */

	public int m_nEmergencyType = 0;								/* Emergency Type */
	public int m_nEmergencyState = NameSpace.EmerEventReturnType;	/* Emergency State(복귀) */

	public int m_nNotifySecurityStatus = 0;							/* Notify Security Status */

	public Config(ContentResolver cr) {
		/* Content Resolver Object */
		m_ContentResolver = cr;
	}
}
