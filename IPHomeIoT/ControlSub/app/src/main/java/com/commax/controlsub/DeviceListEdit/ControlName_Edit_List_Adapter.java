package com.commax.controlsub.DeviceListEdit;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ControlName_Edit_List_Adapter extends ArrayAdapter<ControlName_Edit_ListCell> {
	private static final String TAG = ControlName_Edit_List_Adapter.class.getSimpleName();
	DeviceNameEdit deviceNameEdit;
	ControlName_Edit_Main controlNameEdit;

	LayoutInflater inflater;
	Context mContext;

	ArrayList<ControlName_Edit_ListCell> mListData;
	EditText NickName;
//	ImageButton delete_button;
	CheckBox delete_checkbox;
	ArrayList<ControlName_Edit_ListCell> mDelete_List;

	public boolean delete_mode = false;

	public ControlName_Edit_List_Adapter(Context context, ArrayList<ControlName_Edit_ListCell> items) {
		super(context, 0, items);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mListData = items;
		mDelete_List = new ArrayList<ControlName_Edit_ListCell>();
	}
	
	@Override
	public View getView(final int position, final View convertView, ViewGroup parent) {
		View v = convertView;
		final ControlName_Edit_ListCell cell = getItem(position);

		v = inflater.inflate(R.layout.list_item_layout, null);
		NickName = (EditText) v.findViewById(R.id.nickname_edit);

		delete_checkbox = (CheckBox) v.findViewById(R.id.delete_checkbox);

		if(delete_mode)
		{
			delete_checkbox.setVisibility(View.VISIBLE);
			NickName.setClickable(false);
			NickName.setFocusable(false);
//			setFocusableInTouchMode(false);
			NickName.setEnabled(cell.isable);
		}
		else {
			NickName.setClickable(true);
			NickName.setFocusable(true);
			NickName.setEnabled(cell.isable);
			//TODO 닉네임 모드로 변환되어서는 check box flag 를 초기화시킨다.
			cell.is_all_selected = false;
		}

		//delete check box click
		delete_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//체크박스가 선택 체크 되었을때
					Log.d(TAG, cell.getNickName() +" check box is checked");
					//중복되어 들어있는값은 list에 넣지 않기
					if(cell.is_all_selected == true)
					{
						Log.d(TAG, "이미선택되어 있습니다.");
					}
					else
					{
						mDelete_List.add(cell);
					}
					cell.is_all_selected = true;
				} else {
					//체크박스가 선택 해제 되었을때
					Log.d(TAG, cell.getNickName() + " check box is not checked");
					cell.is_all_selected = false;
					mDelete_List.remove(cell);
					//여기서 해당 디바이스 사이즈 체크해서 able , disable 처리?
				}
			}
		});

		// 체크박스 선택되었는지 체크
		delete_checkbox.setChecked(cell.is_all_selected);

		//edittext lenght MAX 30
		InputFilter[] inputFilter = new InputFilter[2];
		inputFilter[0] = new InputFilter.LengthFilter(30);
		inputFilter[1] = new CustomInputFilter_language_num_ect();

		NickName.setFilters(inputFilter);
//		NickName.setFilters(new InputFilter[]{new CustomInputFilter_no_emoticon()});
		//edit text changed listener
		NickName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if(count!=0)
				{
					Log.d(TAG, "count : " + count);
				}
				/*if(NickName.length() >= 30)
				{
					MainActivity.getInstance().showToastOnWorking("30자 이내로 입력가능합니다.");
				}*/
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				ControlNameEdit.getInstance().devicename_edittext_changed = true;
			}
			@Override
			public void afterTextChanged(Editable s) {
//					cell.update_nickname = NickName.getText().toString();
				if(!(cell.nickname.equals(s.toString())))
				{
					Log.d(TAG , " cell = " + s);
					Log.d(TAG, "cell.nickname = " + cell.nickname);
					cell.updated=true;
					cell.update_nickname = s.toString();
					//TODO for test
					ControlName_Edit_Main edit_main = ControlName_Edit_Main.getInstance();
					edit_main.savebutton.setVisibility(View.VISIBLE);
					edit_main.savebutton.setAlpha(1.0F);
					edit_main.savebutton.setEnabled(true);

					edit_main.mRelativeLayout.removeView(edit_main.deletebutton);
					RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					rlParams.addRule(RelativeLayout.START_OF , R.id.compelete_button);
					edit_main.deletebutton.setLayoutParams(rlParams);
					edit_main.mRelativeLayout.addView(edit_main.deletebutton);
				}
			}
		});

		//edittext click listener
		NickName.setText(cell.getNickName());

	/*	//키보드 엔터 버튼으로 넥네임 변경
		NickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.d(TAG , " onEditorAction ");
				try {
					String changeNickname = NickName.getText().toString();

					DeviceNameEdit.getInstance().startTask(changeNickname,  cell);

					if(DeviceNameEdit.getInstance().mNickName_success_flag)
					{
						//바뀐 nickname 에 대해서도 반영을 해야한다.
						cell.nickname = changeNickname;
						DeviceNameEdit.getInstance().mNickName_success_flag = false;
//							MainActivity.getInstance().showToastOnWorking(getContext().getString(R.string.nickname_change_success));
					}
					else
					{
						Log.e(TAG, "nick name change not success");
						DeviceNameEdit.getInstance().mNickName_success_flag = false;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return false;
			}
		});*/

	/*	//TODO list click listitem
		v.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				Log.d(TAG,"List Item clicked : " + cell.getNickName());
			}
		});*/


		return v;
	}

	//닉네임 일관 변경 전송
	public void save_button()
	{
		Log.d(TAG, " save_button()");

		controlNameEdit = new ControlName_Edit_Main(mContext);

		int i;
		for(i = 0 ; i < mListData.size() ; i++ )
		{
			if(mListData.get(i).updated == true)
			{
				Log.d(TAG, "updated == "+ mListData.get(i).updated);

					try {
						Log.i(TAG, "NickName : " + mListData.get(i).nickname);
						Log.i(TAG, "NickName changed : " + mListData.get(i).update_nickname);
						mListData.get(i).updated = false;

						controlNameEdit.startTask(mListData.get(i).update_nickname,  mListData.get(i));


					// 2016-10-13 현재 변경 요청 보내고 응답 오는것까지 1초가량 걸린다.
					// 즉 3개 보내면 적어도 1.5초 이상 갯수 가 많은 만큼 길어진다.
					//현재는 명령어를 보내고 응답을 받고 앱을 종료하지만 응답을 올바르게 받앗는지에 대한 체크는 하지 않고 잇다.
					/*if(DeviceNameEdit.getInstance().mNickName_success_flag)
					{
						//바뀐 nickname 에 대해서도 반영을 해야한다.
						mListData.get(i).nickname = mListData.get(i).update_nickname;
						mListData.get(i).update_nickname = null;
						DeviceNameEdit.getInstance().mNickName_success_flag = false;
//							MainActivity.getInstance().showToastOnWorking(getContext().getString(R.string.nickname_change_success));
						Log.d(TAG , "nickname changed success");
					}
					else
					{
						Log.e(TAG, "nick name change not success");
						DeviceNameEdit.getInstance().mNickName_success_flag = false;
					}*/
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			}
		}
	}

	//no emoticon
	protected class CustomInputFilter_no_emoticon implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start,
								   int end, Spanned dest, int dstart, int dend) {
			Pattern ps = Pattern.compile("^[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]*$");
			if (source.equals("") || !ps.matcher(source).matches())
				return source;
			else{
				ControlName_Edit_Main.getInstance().showToastOnWorking(mContext.getString(R.string.no_support_text));
			}
			return "";
		}
	}

	//한글 영문 특수문자 일부 지원
	protected class CustomInputFilter_language_num_ect implements InputFilter {
		@Override
		public CharSequence filter(CharSequence source, int start,
								   int end, Spanned dest, int dstart, int dend) {
			Pattern ps = Pattern.compile("^[~!@#$%^&*()_+=?/\":;.,a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ ]+$");

			if (source.equals("") || ps.matcher(source).matches()) {
				return source;
			} else {
				if(TextUtils.isEmpty(source))
				{
					Log.d(TAG, " back space ");
				}
				else
				{
					MainActivity.getInstance().showToastOnWorking(mContext.getString(R.string.no_support_text));
				}
				return "";
			}
		}
	}

	public boolean nickname_null_check()
	{
		boolean nickname_null = false;
		//닉네임 null 체크
		for (int i = 0 ; i < mListData.size() ; i++)
		{
			if(TextUtils.isEmpty(mListData.get(i).update_nickname) && mListData.get(i).updated)
			{
				Log.d(TAG, " mListData : " + mListData.get(i).nickname);
				Log.d(TAG, " update_nickname : " + mListData.get(i).update_nickname );
				nickname_null = true;
				break;
			}
		}
		return nickname_null;
	}

	public void device_delete_completeButton()
	{
		for(int i = 0 ; i < mDelete_List.size() ; i ++)
		{
			ControlName_Edit_ListCell cell = mDelete_List.get(i);
			Log.d(TAG, "mDelete_Lis : " + mDelete_List.get(i).getNickName());

			JSONObject jsonObject = new JSONObject();
			JSONObject jsonObject1 = new JSONObject();

			try {
				jsonObject1 .put("rootUuid", cell.getRootUuid());
				jsonObject.put("command", "remove");
				jsonObject.put("object", jsonObject1);
				Log.d(TAG, "json :  " + String.valueOf(jsonObject));

				MainActivity.getInstance().sendCommand(String.valueOf(jsonObject));
				//TODO 해당 삭제 리스트에 있던 글자들 disalbe처리 해주어야 한다.
				cell.isable = false;

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mDelete_List.clear();
	}

	public void show_all_checkbox(boolean ischecked)
	{
		if(ischecked)
		{
			for(int i = 0 ; i<mListData.size() ; i++)
			{
				mListData.get(i).is_all_selected = true;
			}
		}
		else
		{
			for(int i = 0 ; i<mListData.size() ; i++)
			{
				mListData.get(i).is_all_selected = false;
			}
		}

	}

	public void setDynamicHeight(ListView mListView) {
		int height = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
		for (int i = 0; i < getCount(); i++) {
			View listItem = getView(i, null, mListView);
			listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			height += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = mListView.getLayoutParams();
		params.height = height + (mListView.getDividerHeight() * (getCount() - 1));
		mListView.setLayoutParams(params);
		mListView.requestLayout();
	}
}
