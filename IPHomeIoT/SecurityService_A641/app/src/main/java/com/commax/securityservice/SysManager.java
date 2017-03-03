package com.commax.securityservice;

import android.content.Context;

public class SysManager {
	private Context m_Context = null;						/* Application Context Object */
	private static SysManager m_hInstance = null;			/* System Manager Object Instance */

	public SysManager(Context context) {
		/* Application Context Object */
		m_Context = context;

		/* System Manager Object Instance */
		m_hInstance = this;
	}
}
