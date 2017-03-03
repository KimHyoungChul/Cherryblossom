package com.commax.security;

import android.content.Context;

public class SysManager {
	public static Context m_Context = null;						/* Application Context Object */
	public static SysManager m_hInstance = null;			/* System Manager Object Instance */

	/* Get System Manager Instance */
	public static SysManager getInstance() {
		/* Sound Manager Object Instance */
		return m_hInstance;
	}

	public SysManager(Context context) {
		/* Application Context Object */
		setM_Context(context);

		/* System Manager Object Instance */
		m_hInstance = this;
	}

    public Context getM_Context() {
        return m_Context;
    }

    private void setM_Context(Context m_Context) {
        this.m_Context = m_Context;
    }
}
