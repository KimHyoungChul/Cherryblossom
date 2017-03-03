package com.commax.iphomeiot.calldial;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DialerFragment extends BaseFragment implements OnClickListener, OnTouchListener, OnLongClickListener {
	private DialerOptionDialog m_dlgDialerOptionDialog = null;			/* Dialer Option Dialog */
	private ContactsAddUserDialog m_dlgContactsAddUserDialog = null;	/* Contacts Add User Dialog */
	private FrameLayout layoutFragmentDialCallKindsLayout_ = null;
	private FrameLayout layoutDial_ = null;

	private ImageView m_imgAddContactButton = null;			/* Input Number Add Contact Button */
	private TextView m_txtInputDialNumber = null;			/* Input Number Display */
	private ImageView m_imgDeleteButton = null;				/* Input Number Delete Button */

	private TextView m_txtCallPstnButton = null;			/* Call Place PSTN Select Button */
	private TextView m_txtCallGenerButton = null;			/* Call Place Generation Select Button */
	private TextView m_txtCallInsideButton = null;			/* Call Place Inside Select Button */

	private TextView m_txtDialOneButton = null;				/* Input Dial Button : 1 */
	private TextView m_txtDialTwoButton = null;				/* Input Dial Button : 2 */
	private TextView m_txtDialThreeButton = null;			/* Input Dial Button : 3 */
	private TextView m_txtDialFourButton = null;			/* Input Dial Button : 4 */
	private TextView m_txtDialFiveButton = null;			/* Input Dial Button : 5 */
	private TextView m_txtDialSixButton = null;				/* Input Dial Button : 6 */
	private TextView m_txtDialSevenButton = null;			/* Input Dial Button : 7 */
	private TextView m_txtDialEightButton = null;			/* Input Dial Button : 8 */
	private TextView m_txtDialNineButton = null;			/* Input Dial Button : 9 */
	private TextView m_txtDialZeroButton = null;			/* Input Dial Button : 0 */
	private TextView m_txtDialStarButton = null;			/* Input Dial Button : * */
	private TextView m_txtDialPoundButton = null;			/* Input Dial Button : # */

	private ImageView m_imgDialCallButton = null;			/* Dial Call Request Button */

	private Button m_btnDoorCallRequest = null;				/* Door Call Request Button */
	private Button m_btnGuardCallRequest = null;			/* Guard Call Request Button */
	private Button m_btnOfficeCallRequest = null;			/* Admin Office Call Request Button */

	private PhoneNumberFormattingTextWatcherEx m_txtFormatWatcherEx = null;

	private String m_strAdminInputString = "";				/* Dialer Option Input Administrator Password */
	private String m_strCallTypeCheckedKey = "";			/* Call Place Type Checked Key */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		m_Context = inflater.getContext();

		View rootView = inflater.inflate(R.layout.fragment_dialer, container, false);

		/* Input Telephone Number Formatting Text Watcher */
		if(m_txtFormatWatcherEx == null)
			m_txtFormatWatcherEx = new PhoneNumberFormattingTextWatcherEx();

		layoutFragmentDialCallKindsLayout_ = (FrameLayout) rootView.findViewById(R.id.layoutFragmentDialCallKindsLayout);
		layoutDial_ = (FrameLayout) rootView.findViewById(R.id.layoutDial);

		/* Input Number Add Contact Button */
		m_imgAddContactButton = (ImageView) rootView.findViewById(R.id.imgAddContactButton);
		m_imgAddContactButton.setVisibility(View.GONE);
		/* Input Number Display */
		m_txtInputDialNumber = (TextView) rootView.findViewById(R.id.txtInputDialNumber);
		/* Input Number Delete Button */
		m_imgDeleteButton = (ImageView) rootView.findViewById(R.id.imgDeleteButton);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton = (TextView) rootView.findViewById(R.id.txtCallPstnButton);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton = (TextView) rootView.findViewById(R.id.txtCallGenerButton);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton = (TextView) rootView.findViewById(R.id.txtCallInsideButton);

		/* Input Dial Button : 1 */
		m_txtDialOneButton = (TextView) rootView.findViewById(R.id.txtDialOneButton);
		/* Input Dial Button : 2 */
		m_txtDialTwoButton = (TextView) rootView.findViewById(R.id.txtDialTwoButton);
		/* Input Dial Button : 3 */
		m_txtDialThreeButton = (TextView) rootView.findViewById(R.id.txtDialThreeButton);
		/* Input Dial Button : 4 */
		m_txtDialFourButton = (TextView) rootView.findViewById(R.id.txtDialFourButton);
		/* Input Dial Button : 5 */
		m_txtDialFiveButton = (TextView) rootView.findViewById(R.id.txtDialFiveButton);
		/* Input Dial Button : 6 */
		m_txtDialSixButton = (TextView) rootView.findViewById(R.id.txtDialSixButton);
		/* Input Dial Button : 7 */
		m_txtDialSevenButton = (TextView) rootView.findViewById(R.id.txtDialSevenButton);
		/* Input Dial Button : 8 */
		m_txtDialEightButton = (TextView) rootView.findViewById(R.id.txtDialEightButton);
		/* Input Dial Button : 9 */
		m_txtDialNineButton = (TextView) rootView.findViewById(R.id.txtDialNineButton);
		/* Input Dial Button : 0 */
		m_txtDialZeroButton = (TextView) rootView.findViewById(R.id.txtDialZeroButton);
		/* Input Dial Button : * */
		m_txtDialStarButton = (TextView) rootView.findViewById(R.id.txtDialStarButton);
		/* Input Dial Button : # */
		m_txtDialPoundButton = (TextView) rootView.findViewById(R.id.txtDialPoundButton);

		/* Dial Call Request Button */
		m_imgDialCallButton = (ImageView) rootView.findViewById(R.id.imgDialCallButton);

		/* Door Call Request Button */
		m_btnDoorCallRequest = (Button) rootView.findViewById(R.id.btnDoorCallButton);
		/* Guard Call Request Button */
		m_btnGuardCallRequest = (Button) rootView.findViewById(R.id.btnGuardCallButton);
		/* Admin Office Call Request Button */
		m_btnOfficeCallRequest = (Button) rootView.findViewById(R.id.btnOfficeCallButton);

		/* Input Number Add Contact Button */ 
		m_imgAddContactButton.setOnClickListener(this);
		m_imgAddContactButton.setOnTouchListener(this);
		/* Input Number Delete Button */
		m_imgDeleteButton.setOnClickListener(this);
		m_imgDeleteButton.setOnTouchListener(this);
		m_imgDeleteButton.setOnLongClickListener(this);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setOnClickListener(this);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setOnClickListener(this);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setOnClickListener(this);

		/* Input Dial Button : 1 */
		m_txtDialOneButton.setOnClickListener(this);
		m_txtDialOneButton.setOnTouchListener(this);
		/* Input Dial Button : 2 */
		m_txtDialTwoButton.setOnClickListener(this);
		m_txtDialTwoButton.setOnTouchListener(this);
		/* Input Dial Button : 3 */
		m_txtDialThreeButton.setOnClickListener(this);
		m_txtDialThreeButton.setOnTouchListener(this);
		/* Input Dial Button : 4 */
		m_txtDialFourButton.setOnClickListener(this);
		m_txtDialFourButton.setOnTouchListener(this);
		/* Input Dial Button : 5 */
		m_txtDialFiveButton.setOnClickListener(this);
		m_txtDialFiveButton.setOnTouchListener(this);
		/* Input Dial Button : 6 */
		m_txtDialSixButton.setOnClickListener(this);
		m_txtDialSixButton.setOnTouchListener(this);
		/* Input Dial Button : 7 */
		m_txtDialSevenButton.setOnClickListener(this);
		m_txtDialSevenButton.setOnTouchListener(this);
		/* Input Dial Button : 8 */
		m_txtDialEightButton.setOnClickListener(this);
		m_txtDialEightButton.setOnTouchListener(this);
		/* Input Dial Button : 9 */
		m_txtDialNineButton.setOnClickListener(this);
		m_txtDialNineButton.setOnTouchListener(this);
		/* Input Dial Button : 0 */
		m_txtDialZeroButton.setOnClickListener(this);
		m_txtDialZeroButton.setOnTouchListener(this);
		/* Input Dial Button : * */
		m_txtDialStarButton.setOnClickListener(this);
		m_txtDialStarButton.setOnTouchListener(this);
		/* Input Dial Button : # */
		m_txtDialPoundButton.setOnClickListener(this);
		m_txtDialPoundButton.setOnTouchListener(this);

		/* Dial Call Request Button */
		m_imgDialCallButton.setOnClickListener(this);
		m_imgDialCallButton.setOnTouchListener(this);

		/* Door Call Request Button */
		m_btnDoorCallRequest.setOnClickListener(this);
		m_btnDoorCallRequest.setOnTouchListener(this);
		/* Guard Call Request Button */
		m_btnGuardCallRequest.setOnClickListener(this);
		m_btnGuardCallRequest.setOnTouchListener(this);
		/* Admin Office Call Request Button */
		m_btnOfficeCallRequest.setOnClickListener(this);
		m_btnOfficeCallRequest.setOnTouchListener(this);

		/* Input Number Display */
		m_txtInputDialNumber.setInputType(android.text.InputType.TYPE_NULL);
		m_txtInputDialNumber.setCursorVisible(false);

		initCallTypeButtons();
		initHotKeyButtons();

		return rootView;
	}

	private boolean isVisibleKindsLayout_ = false;

	private void initHotKeyButtons() {
		final Object objHotKeys[][] = {{NameSpace.KEY_DOOR, m_btnDoorCallRequest},			/* Door Call Request Button */
									{NameSpace.KEY_GUARD, m_btnGuardCallRequest},			/* Guard Call Request Button */
									{NameSpace.KEY_OFFICE, m_btnOfficeCallRequest}};		/* Admin Office Call Request Button */

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Context);

		for(int i = 0; i < objHotKeys.length; i++) {
			if ((prefs.getString((String)objHotKeys[i][0], "").equals("true")) == true) {
				((Button) objHotKeys[i][1]).setVisibility(View.VISIBLE);
			}
			else
				((Button) objHotKeys[i][1]).setVisibility(View.GONE);
		}
	}

	private void initCallTypeButtons() {
		final Object objCallTypes[][] = {{NameSpace.KEY_PSTN, m_txtCallPstnButton},			/* Call Place PSTN Select Button */
									{NameSpace.KEY_NEIG, m_txtCallGenerButton},				/* Call Place Generation Select Button */
									{NameSpace.KEY_EXT, m_txtCallInsideButton}};			/* Call Place Inside Select Button */

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Context);
		/* Call Place Type Checked Key */
		m_strCallTypeCheckedKey = prefs.getString(NameSpace.KEY_TYPE, NameSpace.KEY_PSTN);

		for(int i = 0; i < objCallTypes.length; i++) {
			if (((prefs.getString((String)objCallTypes[i][0], "").equals("true")) == true)) {
				((TextView) objCallTypes[i][1]).setVisibility(View.VISIBLE);
				isVisibleKindsLayout_ = true;
			}
			else {
				((TextView) objCallTypes[i][1]).setVisibility(View.GONE);
			}
		}

		if (!isVisibleKindsLayout_) {
			layoutFragmentDialCallKindsLayout_.setVisibility(View.GONE);
		}

		/* Call Place Type Checked Key(PSTN) */
		if(m_strCallTypeCheckedKey.equals(NameSpace.KEY_PSTN) == true) {
			/* Set Telephone(PSTN) View */
			setTelephoneView();
		}
		/* Call Place Type Checked Key(Generation) */
		else if(m_strCallTypeCheckedKey.equals(NameSpace.KEY_NEIG) == true) {
			/* Set Generation View */
			setGenerationView();
		}
		/* Call Place Type Checked Key(Inside) */
		else if(m_strCallTypeCheckedKey.equals(NameSpace.KEY_EXT) == true) {
			/* Set External View */
			setExternalView();
		}
		else {
			/* Call Place PSTN Select Button */
			if(m_txtCallPstnButton.getVisibility() == View.VISIBLE) {
				/* Set Telephone(PSTN) View */
				setTelephoneView();
			}
			/* Call Place Generation Select Button */
			else if(m_txtCallGenerButton.getVisibility() == View.VISIBLE) {
				/* Set Generation View */
				setGenerationView();
			}
			else {
				/* Call Place Type Checked Key */
				m_strCallTypeCheckedKey = "";
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();

		/* Dialer Option Dialog */
		if(m_dlgDialerOptionDialog != null) m_dlgDialerOptionDialog.cancel();
		m_dlgDialerOptionDialog = null;

		/* Contacts Add User Dialog */
		if(m_dlgContactsAddUserDialog != null) m_dlgContactsAddUserDialog.cancel();
		m_dlgContactsAddUserDialog = null;
	}

	// for hardware keyboard
	public void onKeyDown(Integer keyCode) {
		/* Dialer Option Input Administrator Password */
		if(m_strAdminInputString.length() < NameSpace.ADMIN_CODE.length()) {
			m_strAdminInputString += "" + keyCode;
			if(NameSpace.ADMIN_CODE.equals(m_strAdminInputString)) {
				/* Dialer Option Input Administrator Password */
				m_strAdminInputString = "";

				/* Input Number Display */
				m_txtInputDialNumber.setText("");

				/* Show Dialer Option Select */
				showDialerOptionSelect();
				return;
			}
		}

		/* Input Number Display */
		m_txtInputDialNumber.onKeyDown(keyCode, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));

		return;
	}

	/* Show Dialer Option Select */
	public void showDialerOptionSelect() {
		/* Dialer Option Dialog */
		if(m_dlgDialerOptionDialog == null) {
			m_dlgDialerOptionDialog = new DialerOptionDialog(getContext(), new DialerOptionDialog.OnDialerOptionEventListener() {
				@Override
				public void onDialerOptionEvent(boolean bResponse) {
					if(bResponse == true) {
						/* Recreate Flag */
						MainActivity.m_bReCreateFlag = true;

						((Activity) m_Context).recreate();
					}

					/* Dialer Option Dialog */
					if(m_dlgDialerOptionDialog != null)
						m_dlgDialerOptionDialog = null;
				}
			});
			
			/* Dialer Option Dialog */
			m_dlgDialerOptionDialog.show();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/* Door/Guard/Admin Office Call Request Button */
		if((m_btnDoorCallRequest == v) || (m_btnGuardCallRequest == v) || (m_btnOfficeCallRequest == v)) {
			switch(event.getAction()){
	        case MotionEvent.ACTION_DOWN:
	        	v.setAlpha(0.6f);
	        	v.setBackgroundColor(0x80D4D4D9);
	        	break;
	        case MotionEvent.ACTION_MOVE:
	        	v.setAlpha(0.6f);
	        	v.setBackgroundColor(0x80D4D4D9);
	        	break;
	        case MotionEvent.ACTION_UP:
	        	v.setAlpha(1.0f);
	        	v.setBackgroundColor(0xFFECECF1);
	        	break;
	        default:
	        	v.setAlpha(1.0f);
	        	v.setBackgroundColor(0xFFECECF1);
	        	break;
			}
		}
		else {
			switch(event.getAction()){
	        case MotionEvent.ACTION_DOWN: v.setAlpha(0.6f); break;
	        case MotionEvent.ACTION_MOVE: v.setAlpha(0.6f); break;
	        case MotionEvent.ACTION_UP: v.setAlpha(1.0f); break;
	        default: v.setAlpha(1.0f); break;
			}
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		String strNumber = "";

		/* Input Number Add Contact Button */ 
		if(m_imgAddContactButton == v) {
			/* Show Contacts Add User Dialog */
			showContactsAddUserDialog();
		}
		/* Input Number Delete Button */
		else if(m_imgDeleteButton == v) {
			onKeyDown(KeyEvent.KEYCODE_DEL);
		}
		/* Call Place PSTN Select Button */
		else if(m_txtCallPstnButton == v) {
			/* Set Telephone(PSTN) View */
			setTelephoneView();
		}
		/* Call Place Generation Select Button */
		else if(m_txtCallGenerButton == v) {
			/* Set Generation View */
			setGenerationView();
		}
		/* Call Place Inside Select Button */
		else if(m_txtCallInsideButton == v) {
			/* Set External View */
			setExternalView();
		}
		/* Input Dial Button : 1 */
		else if(m_txtDialOneButton == v) {
			onKeyDown(KeyEvent.KEYCODE_1);
		}
		/* Input Dial Button : 2 */
		else if(m_txtDialTwoButton == v) {
			onKeyDown(KeyEvent.KEYCODE_2);
		}
		/* Input Dial Button : 3 */
		else if(m_txtDialThreeButton == v) {
			onKeyDown(KeyEvent.KEYCODE_3);
		}
		/* Input Dial Button : 4 */
		else if(m_txtDialFourButton == v) {
			onKeyDown(KeyEvent.KEYCODE_4);
		}
		/* Input Dial Button : 5 */
		else if(m_txtDialFiveButton == v) {
			onKeyDown(KeyEvent.KEYCODE_5);
		}
		/* Input Dial Button : 6 */
		else if(m_txtDialSixButton == v) {
			onKeyDown(KeyEvent.KEYCODE_6);
		}
		/* Input Dial Button : 7 */
		else if(m_txtDialSevenButton == v) {
			onKeyDown(KeyEvent.KEYCODE_7);
		}
		/* Input Dial Button : 8 */
		else if(m_txtDialEightButton == v) {
			onKeyDown(KeyEvent.KEYCODE_8);
		}
		/* Input Dial Button : 9 */
		else if(m_txtDialNineButton == v) {
			onKeyDown(KeyEvent.KEYCODE_9);
		}
		/* Input Dial Button : 0 */
		else if(m_txtDialZeroButton == v) {
			onKeyDown(KeyEvent.KEYCODE_0);
		}
		/* Input Dial Button : * */
		else if(m_txtDialStarButton == v) {
			/* Call Place Type Checked Key */
			onKeyDown(((NameSpace.KEY_NEIG.equals(m_strCallTypeCheckedKey)) == true) ? KeyEvent.KEYCODE_MINUS : KeyEvent.KEYCODE_STAR);
		}
		/* Input Dial Button : # */
		else if(m_txtDialPoundButton == v) {
			onKeyDown(KeyEvent.KEYCODE_POUND);
		}
		/* Dial Call Request Button */
		else if(m_imgDialCallButton == v) {
			/* Dialer Option Input Administrator Password */
			m_strAdminInputString = "";

			/* Set Number Dialing */
			setNumberDialing();
		}
		/* Door Call Request Button */
		else if(m_btnDoorCallRequest == v) {
			strNumber = new PhoneNumberFormater().getDoorNum();
			/* Send Place Call */
			sendPlaceCall(strNumber, NameSpace.OUTGOING_DOOR);
			strNumber = null;
		}
		/* Guard Call Request Button */
		else if(m_btnGuardCallRequest == v) {
			strNumber = new PhoneNumberFormater().getGuardNum(m_Context);
			/* Send Place Call */
			sendPlaceCall(strNumber, NameSpace.OUTGOING_GUARD);
			strNumber = null;
		}
		/* Admin Office Call Request Button */
		else if(m_btnOfficeCallRequest == v) {
			strNumber = new PhoneNumberFormater().getMngOfficeNum(m_Context);
			/* Send Place Call */
			sendPlaceCall(strNumber, NameSpace.OUTGOING_OFFICE);
			strNumber = null;
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		/* Input Number Display */
		m_txtInputDialNumber.setText("");

		return false;
	}

	/* Show Contacts Add User Dialog */
	private void showContactsAddUserDialog() {
		/* Input Number Display */
		String strNumber = m_txtInputDialNumber.getText().toString();

		strNumber = strNumber.replace("_", "");
		if(strNumber.length() <= 0) {
			/* Invalid Input Dial Number */
			invalidInputDialNumber();
			return;
		}

		/* Contacts Add User Dialog */
		if(m_dlgContactsAddUserDialog == null) {
			m_dlgContactsAddUserDialog = new ContactsAddUserDialog(getContext(), NameSpace.TAB_DIALER_FRAGMENT, "", "", strNumber, new ContactsAddUserDialog.OnContactsAddUserEventListener() {
				@Override
				public void onContactsAddUserEvent(boolean bResponse) {
					/* Contacts Add User Dialog */
					if(m_dlgContactsAddUserDialog != null)
						m_dlgContactsAddUserDialog = null;
				}
			});

			m_dlgContactsAddUserDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					/* Contacts Add User Dialog */
					if(m_dlgContactsAddUserDialog != null)
						m_dlgContactsAddUserDialog = null;

					/* Send Custom Broadcast Message(System Key Hide Action) */
					sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);
				}
			});

			/* Contacts Add User Dialog */
			m_dlgContactsAddUserDialog.show();
		}
	}

	/* Set Telephone(PSTN) View */
	private void setTelephoneView() {
		/* Set Preference Call Type */
		setPreferenceCallType(NameSpace.KEY_PSTN);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setBackgroundResource(R.drawable.call_enable_btn_shape);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setBackgroundResource(R.drawable.call_disable_btn_shape);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setBackgroundResource(R.drawable.call_disable_btn_shape);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setTextColor(0xFFFFFFFF);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setTextColor(0xFF534E7F);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setTextColor(0xFF534E7F);

		/* Input Number Display */
		m_txtInputDialNumber.addTextChangedListener(m_txtFormatWatcherEx);
		m_txtInputDialNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(NameSpace.DIAL_MAX_PSTN) });

		/* Input Number Add Contact Button */ 
		m_imgAddContactButton.setVisibility(View.VISIBLE);

		m_txtDialStarButton.setVisibility(View.VISIBLE);
		m_txtDialPoundButton.setVisibility(View.VISIBLE);

		m_txtDialStarButton.setText("*");
		m_txtDialPoundButton.setText("#");

		/* Input Number Display */
		m_txtInputDialNumber.setText("");
	}

	/* Set Generation View */
	private void setGenerationView() {
		/* Set Preference Call Type */
		setPreferenceCallType(NameSpace.KEY_NEIG);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setBackgroundResource(R.drawable.call_disable_btn_shape);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setBackgroundResource(R.drawable.call_enable_btn_shape);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setBackgroundResource(R.drawable.call_disable_btn_shape);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setTextColor(0xFF534E7F);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setTextColor(0xFFFFFFFF);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setTextColor(0xFF534E7F);

		/* Input Number Display */
		m_txtInputDialNumber.removeTextChangedListener(m_txtFormatWatcherEx);
		m_txtInputDialNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(NameSpace.DIAL_MAX_NEIG) });

		/* Input Number Add Contact Button */ 
		//m_imgAddContactButton.setVisibility(View.INVISIBLE);

		m_txtDialStarButton.setVisibility(View.VISIBLE);
		m_txtDialPoundButton.setVisibility(View.VISIBLE);

		/* STR : Korean language, ETC(-) */
		m_txtDialStarButton.setText(R.string.STR_DONG);

		/* Input Number Display */
		m_txtInputDialNumber.setText("");
	}

	/* Set External View */
	private void setExternalView() {
		/* Set Preference Call Type */
		setPreferenceCallType(NameSpace.KEY_EXT);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setBackgroundResource(R.drawable.call_disable_btn_shape);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setBackgroundResource(R.drawable.call_disable_btn_shape);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setBackgroundResource(R.drawable.call_enable_btn_shape);

		/* Call Place PSTN Select Button */
		m_txtCallPstnButton.setTextColor(0xFF534E7F);
		/* Call Place Generation Select Button */
		m_txtCallGenerButton.setTextColor(0xFF534E7F);
		/* Call Place Inside Select Button */
		m_txtCallInsideButton.setTextColor(0xFFFFFFFF);

		/* Input Number Display */
		m_txtInputDialNumber.removeTextChangedListener(m_txtFormatWatcherEx);
		m_txtInputDialNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(NameSpace.DIAL_MAX_EXT) });

		/* Input Number Add Contact Button */ 
		m_imgAddContactButton.setVisibility(View.GONE);

		m_txtDialStarButton.setVisibility(View.GONE);
		m_txtDialPoundButton.setVisibility(View.GONE);

		/* Input Number Display */
		m_txtInputDialNumber.setText("");
	}

	/* Set Preference Call Type */
	private void setPreferenceCallType(String strCallType) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(m_Context);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(NameSpace.KEY_TYPE, strCallType);

		/* Call Place Type Checked Key */
		m_strCallTypeCheckedKey = strCallType;

		editor.commit();
	}

	/* Set Number Dialing */
	private void setNumberDialing() {
		/* Input Number Display */
		String strNumber = m_txtInputDialNumber.getText().toString();

		if(strNumber.length() <= 0) {
			/* Invalid Input Dial Number */
			invalidInputDialNumber();
			return;
		}

		/* Call Place Type Checked Key(Generation) */
		if(NameSpace.KEY_NEIG.equals(m_strCallTypeCheckedKey)) {
			strNumber = new PhoneNumberFormater().convertNeightbor(strNumber);
			if(strNumber.length() <= 0) {
				/* Invalid Input Dial Number */
				invalidInputDialNumber();
				return;
			}
		}
		/* Call Place Type Checked Key(Inside) */
		else if(NameSpace.KEY_EXT.equals(m_strCallTypeCheckedKey)) {
			strNumber = new PhoneNumberFormater().convertExt(strNumber);
			if(strNumber.length() <= 0) {
				/* Invalid Input Dial Number */
				invalidInputDialNumber();
				return;
			}
		}
		/* Call Place Type Checked Key(PSTN) */
		else {
			strNumber = strNumber.replace("-", "");
			if(strNumber.length() <= 0) {
				/* Invalid Input Dial Number */
				invalidInputDialNumber();
				return;
			}
		}

		/* Send Place Call */
		sendPlaceCall(strNumber, NameSpace.OUTGOING_NEIGHBORHOOD);
	}

	/* Invalid Input Dial Number */
	private void invalidInputDialNumber() {
		/* animations */
	//	Animation shake = AnimationUtils.loadAnimation(m_Context, R.anim.phone_blink);
		/* Input Number Display */
	/*	m_txtInputDialNumber.startAnimation(shake);

		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.frmCustomToastMainFrame));

		TextView txtCustomToastMessage = (TextView) layout.findViewById(R.id.txtCustomToastMessage);
	*/	/* STR : Please ented the saving number */
	/*	txtCustomToastMessage.setText(R.string.STR_SAVE_INPUT_NUMBER_MESSAGE);

		Toast toast = new Toast(m_Context);
		toast.setGravity(Gravity.BOTTOM, 0, 46);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show(); */
	}

	/* Send Place Call */
	private void sendPlaceCall(String strNumber, int outgoing) {

		makeOutgoingCall(m_Context, strNumber, outgoing);
	}
}
