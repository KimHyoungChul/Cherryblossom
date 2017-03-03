package com.commax.iphomeiot.calldial;

public class CallLogEntry {
	public String m_ID = "";
	public String m_strName = "";
	public String m_strNumber = "";
	public int m_nType = 0;
	public String m_strDay = "";
	public String m_strDuration = "";

	public CallLogEntry(String _ID, String strName, String strNumber, int nType, String strDay, String strDuration) {
		this.m_ID = _ID;
		this.m_strName = strName;
		this.m_strNumber = strNumber;
		this.m_nType = nType;
		this.m_strDay = strDay;
		this.m_strDuration = strDuration;
	}
}
