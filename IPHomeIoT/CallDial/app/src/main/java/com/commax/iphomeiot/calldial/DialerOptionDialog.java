package com.commax.iphomeiot.calldial;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

public class DialerOptionDialog extends Dialog implements OnClickListener {
	private Context m_Context = null;

	/* Dialer Option Event Listener */
	private OnDialerOptionEventListener onDialerOptionEventListener;

	private CheckBox m_chkCallOptonPstn = null;
	private CheckBox m_chkCallOptonGener = null;
	private CheckBox m_chkCallOptonInside = null;
	private CheckBox m_chkCallOptonDoor = null;
	private CheckBox m_chkCallOptonGuard = null;
	private CheckBox m_chkCallOptonOffice = null;

	/* Interface Dialer Option Event Listener */
	public interface OnDialerOptionEventListener {
		/* Dialer Option Event */
		public void onDialerOptionEvent(boolean bResponse);
	}

	public DialerOptionDialog(Context context, OnDialerOptionEventListener onDialerOptionEventListener) {
		super(context);

		m_Context = context;

		/* Dialer Option Event Listener */
		this.onDialerOptionEventListener = onDialerOptionEventListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialer_option_dialog);

		m_chkCallOptonPstn = (CheckBox)findViewById(R.id.chkCallOptonPstn);
		m_chkCallOptonGener = (CheckBox)findViewById(R.id.chkCallOptonGener);
		m_chkCallOptonInside = (CheckBox)findViewById(R.id.chkCallOptonInside);
		m_chkCallOptonDoor = (CheckBox)findViewById(R.id.chkCallOptonDoor);
		m_chkCallOptonGuard = (CheckBox)findViewById(R.id.chkCallOptonGuard);
		m_chkCallOptonOffice = (CheckBox)findViewById(R.id.chkCallOptonOffice);

		((Button)findViewById(R.id.btnDialerOptionDialogCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.btnDialerOptionDialogOk)).setOnClickListener(this);

		/* Call Hot Keys Control */
		setCallHotKeysControl();
	}

	/* Call Hot Keys Control */
	private void setCallHotKeysControl() {
		final Object objHotKeys[][] = {{NameSpace.KEY_PSTN, m_chkCallOptonPstn},
								{NameSpace.KEY_NEIG, m_chkCallOptonGener},
								{NameSpace.KEY_EXT, m_chkCallOptonInside},
								{NameSpace.KEY_DOOR, m_chkCallOptonDoor},
								{NameSpace.KEY_GUARD, m_chkCallOptonGuard},
								{NameSpace.KEY_OFFICE, m_chkCallOptonOffice}};

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Context);

		for(int i = 0; i < objHotKeys.length; i++)
			((CheckBox)objHotKeys[i][1]).setChecked(((prefs.getString((String)objHotKeys[i][0], "").equals("true")) == true) ? true : false);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnDialerOptionDialogCancel))) {
			/* Dialer Option Event */
			onDialerOptionEventListener.onDialerOptionEvent(false);

			cancel();
		}
		else if(v.equals(findViewById(R.id.btnDialerOptionDialogOk))) {
			/* Save Call Hot Keys Status */
			saveCallHotKeysStatus();

			/* Dialer Option Event */
			onDialerOptionEventListener.onDialerOptionEvent(true);

			dismiss();
		}
	}

	/* Save Call Hot Keys Status */
	private void saveCallHotKeysStatus() {
		final Object objHotKeys[][] = {{NameSpace.KEY_PSTN, m_chkCallOptonPstn},
								{NameSpace.KEY_NEIG, m_chkCallOptonGener},
								{NameSpace.KEY_EXT, m_chkCallOptonInside},
								{NameSpace.KEY_DOOR, m_chkCallOptonDoor},
								{NameSpace.KEY_GUARD, m_chkCallOptonGuard},
								{NameSpace.KEY_OFFICE, m_chkCallOptonOffice}};

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Context);
		SharedPreferences.Editor editor = prefs.edit();

		for(int i = 0; i < objHotKeys.length; i++)
			editor.putString((String)objHotKeys[i][0], String.valueOf(((CheckBox)objHotKeys[i][1]).isChecked()));

		editor.commit();
	}
}
