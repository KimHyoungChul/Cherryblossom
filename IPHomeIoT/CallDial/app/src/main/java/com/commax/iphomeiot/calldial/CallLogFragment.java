package com.commax.iphomeiot.calldial;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.commax.iphomeiot.common.provider.CallLogInfo;
import com.commax.iphomeiot.common.provider.CallLogProviderClient;
import com.commax.iphomeiot.common.ui.dialog.PopUpDialog;
import com.commax.nubbyj.base.TimeUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class CallLogFragment extends BaseFragment implements OnClickListener, OnTouchListener, CallLogFragmentAdapter.RemovableLog {
	private CallLogProviderClient provider_ = new CallLogProviderClient();
	private IFragmentListener listener_;
	private LinearLayout layoutAllSelect_ = null;
	private ImageView imgAllSelect_;
	private ListView loglistView_;
	private CallLogFragmentAdapter logFragmentAdapter_;
	private boolean isAllChecked_ = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		m_Context = inflater.getContext();
		View rootView = inflater.inflate(R.layout.fragment_calllog, container, false);

		provider_.removeMissedcallNewValue(getActivity());
		layoutAllSelect_ = (LinearLayout)rootView.findViewById(R.id.allSelect_layout);
		layoutAllSelect_.setVisibility(View.GONE);
		layoutAllSelect_.setOnClickListener(this);
		imgAllSelect_ = (ImageView) rootView.findViewById(R.id.cbAllCheck);
		logFragmentAdapter_ = new CallLogFragmentAdapter(this, getActivity());
		loglistView_ = (ListView) rootView.findViewById(R.id.lvLog);
		loglistView_.setAdapter(logFragmentAdapter_);
		ArrayList<CallLogInfo> logInfo = provider_.getAllLog(getActivity());
		logFragmentAdapter_.updateLogInfo(logInfo, getActivity());
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (provider_ == null)
			return;

		ArrayList<CallLogInfo> logInfo = provider_.getAllLog(getActivity());
		if (logInfo == null)
			return;

		logFragmentAdapter_.updateLogInfo(logInfo, getActivity());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
        case MotionEvent.ACTION_DOWN: v.setAlpha(0.6f); break;
        case MotionEvent.ACTION_MOVE: v.setAlpha(0.6f); break;
        case MotionEvent.ACTION_UP: v.setAlpha(1.0f); break;
        default: v.setAlpha(1.0f); break;
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.allSelect_layout) {
			if (!isAllChecked_) {
				logFragmentAdapter_.removeSelectAll();
				imgAllSelect_.setImageResource(R.drawable.btn_checkbox_s);
				isAllChecked_ = true;
			}
			else {
				logFragmentAdapter_.removeUnselectAll();
				imgAllSelect_.setImageResource(R.drawable.btn_checkbox_n);
				isAllChecked_ = false;
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void setFragmentListener(IFragmentListener listener) {

		listener_ = listener;
	}

	public void changeUiToDeleteMode() {
		logFragmentAdapter_.setRemoveMode(true);
		layoutAllSelect_.setVisibility(View.VISIBLE);
		((TextView) getActivity().findViewById(R.id.tvTitle)).setText(getText(R.string.title_selectitem));
		((TextView) getActivity().findViewById(R.id.tvTitle)).setTextColor(Color.argb(255, 68, 73, 107));
	}

	public void changeUiToNormalMode() {
		logFragmentAdapter_.setRemoveMode(false);
		layoutAllSelect_.setVisibility(View.GONE);
		isAllChecked_ = false;
		((TextView) getActivity().findViewById(R.id.tvTitle)).setText(getText(R.string.app_name));
		((TextView) getActivity().findViewById(R.id.tvTitle)).setTextColor(Color.argb(255, 255, 255, 255));
		getActivity().findViewById(R.id.fragmentTabView).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.topMenu).setBackground(getActivity().getDrawable(R.mipmap.bg_title_1depth));
		getActivity().findViewById(R.id.imgChangeRemoveMode).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.btnCancelRemove).setVisibility(View.GONE);
		getActivity().findViewById(R.id.imgBackButton).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.btnRemove).setVisibility(View.GONE);
	}


	public void removeSelectLog() {
		final PopUpDialog confirmDlg = new PopUpDialog(getActivity());
		confirmDlg.setText(getString(R.string.popup_msg_title), getString(R.string.popup_msg_content), getString(R.string.popup_msg_ok), getString(R.string.popup_msg_cancel));
		confirmDlg.setListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logFragmentAdapter_.removeSelectedIgtem();
				changeUiToNormalMode();
				confirmDlg.cancel();
				onResume();
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmDlg.cancel();
			}
		});
		confirmDlg.show();

	}

	@Override
	public void removeLog(int key) {
		provider_.deleteLog(key, getActivity().getApplicationContext());
	}

	@Override
	public void clickRecordImg(CallLogFragmentAdapter.CallLogSection log) {
		if (log == null)
			return;

		if (log.callDate_ == null)
			return;

		long diffInDays = TimeUtil.diffFromTodayInDays(Integer.parseInt(log.callDate_) * 1000L);
		File recordFile = new File(log.recordFilePath_);
		String fileName = recordFile.getName();

		Intent videoPlay = new Intent();
		videoPlay.setClassName("commax.wallpad.videoplayer", "commax.wallpad.videoplayer.VideoPlayerPreview");
		videoPlay.putExtra("fileName", fileName);
		videoPlay.putExtra("isToday", (diffInDays == 0));
		startActivity(videoPlay);
	}

	@Override
	public void clickRemoveCheckImg(int selectedCount) {
		if (selectedCount > 0) {
			((TextView) getActivity().findViewById(R.id.tvTitle)).setText(String.format(getString(R.string.title_selecteditem_count), selectedCount));
			((Button) getActivity().findViewById(R.id.btnRemove)).setVisibility(View.VISIBLE);
		}
		else {
			((TextView) getActivity().findViewById(R.id.tvTitle)).setText(getString(R.string.title_selectitem));
			((Button) getActivity().findViewById(R.id.btnRemove)).setVisibility(View.GONE);
		}

		if (selectedCount == logFragmentAdapter_.logSize()){
            imgAllSelect_.setImageResource(R.drawable.btn_checkbox_s);
            isAllChecked_ = true;
        }
		else {
            imgAllSelect_.setImageResource(R.drawable.btn_checkbox_n);
            isAllChecked_ = false;
        }
	}
	/* Send Place Call */
	private void sendPlaceCall(CallLogEntry itemCallLog, int outgoing) {
		/* Make Outgoing Call */
		makeOutgoingCall(m_Context, itemCallLog.m_strNumber, outgoing);
	}

	/* Get Contact Name */
	public String getContactName(String strPhoneNumber) {
		String strContactName = "";

		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(strPhoneNumber));
		Cursor cursor = m_Context.getContentResolver().query(uri, new String[] {PhoneLookup.DISPLAY_NAME}, null, null, null);

		if(cursor == null) {
			return strContactName;
		}

		if(cursor.moveToFirst()) {
			strContactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}

		if(cursor != null) cursor.close();
		cursor = null;

		return strContactName;
	}

	/* Set Call Duration */
	private String setCallDuration(String nValue) {
		String strTime = "";
		int nSecondValue = 0;

		try {
			nSecondValue = Integer.parseInt(nValue);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		strTime = String.format(Locale.US, "%d:%02d", nSecondValue / 60, nSecondValue % 60);

		return strTime;
	}

}
