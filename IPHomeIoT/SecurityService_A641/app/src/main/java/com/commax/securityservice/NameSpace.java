package com.commax.securityservice;

public class NameSpace {
	static final String DEBUG_TAG						= "SecSvc";

	static final int EmerEventOccurType					= 1;				/* 발생 */
	static final int EmerEventStopType					= 2;				/* 정지 */
	static final int EmerEventReturnType				= 3;				/* 복귀 */

	/* Register Check Event Broadcast Message */
	static final String REG_CHECK_EVENT_ACTION					= "com.commax.service.system.REG_CHECK_EVENT";
	/* Emergency Broadcast Message */
	static final String EMERGENCY_ACTION						= "com.commax.service.system.EMERGENCY";

	/* Emergency Package Name */
	static final String EMERGENCY_PACKAGE						= "com.commax.security";
	/* Emergency Activity Name */
	static final String EMERGENCY_ACTIVITY						= "com.commax.security.EmergencyView";
}
