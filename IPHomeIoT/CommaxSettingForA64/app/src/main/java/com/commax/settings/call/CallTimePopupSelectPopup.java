package com.commax.settings.call;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.util.PlusClickGuard;

import java.util.List;

/**
 * 연속통화시간 설정 팝업(리스트에서 선택)
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class CallTimePopupSelectPopup extends Dialog {


    private final Context mContext;
    private final CallTimeConfirmListener mListener;
    private List<CallTimeData> mDatas;


    public CallTimePopupSelectPopup(Context context, CallTimeConfirmListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_call_time_select);


        addButtonListener();

    }

    /**
     * 팝업창 타이틀 설정
     *
     * @param title
     */
    public void setPopupTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.progress_title);
        titleView.setText(title);
    }


    /**
     * 리스트 데이터 설정
     *
     * @param datas
     */
    public void setPopupData(List<CallTimeData> datas) {
        mDatas = datas;

    }


    /**
     * 리스트 초기화
     *
     * @param context
     */
    public void initList(Context context, int selectedItem) {
        ListView list = (ListView) findViewById(R.id.calltimeList);

        CallTimeListAdapter adapter = new CallTimeListAdapter(context, R.layout.list_item_calltime, mDatas);

        list.setAdapter(adapter);

        //Adapter에서 CheckableLinearLayout의 setChecked를 호출하면 효과가 없음. ListView.getCheckedItemPosition이 없어서 초기화됨
        //ListView의 setItemChecked사용해야 함
        list.setItemChecked(selectedItem, true);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        //xml에서 지정해서 필요없음
        //list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void addButtonListener() {
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                dismiss();
            }
        });

        Button confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                if (isCallTimeSaved()) {
                    mListener.onCallTimeConfirm();
                    dismiss();
                }
            }
        });
    }


    /**
     * 연속통화시간 저장
     */
    private boolean isCallTimeSaved() {

        String callTime = "0";
        int ncallTime = 0;
        try {
            ListView list = (ListView) findViewById(R.id.calltimeList);
            CallTimeListAdapter adapter = (CallTimeListAdapter) list.getAdapter();
            int checkedItemPosition = list.getCheckedItemPosition();
            CallTimeData adaptor_calltime = adapter.getItem(checkedItemPosition);
            callTime = adaptor_calltime.getValue();
        } catch (Exception e) {

        }

        if (callTime.equals("")) {
            Toast.makeText(mContext, R.string.put_in_time, Toast.LENGTH_SHORT).show();
            return false;
        }

        //2017-01-12,yslee::callTime 범위체크
        try {
            ncallTime = Integer.parseInt(callTime);
        }catch (Exception e) {

        }

        if (ncallTime < 30 || ncallTime > 180) {
            String message = ((Activity)mContext).getResources().getString(R.string.put_in_time) + "(" + ((Activity)mContext).getResources().getString(R.string.callTimeHint) +  ")";
            Toast.makeText(mContext, message , Toast.LENGTH_SHORT).show();
            return false;
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.CallTimeEntry.COLUMN_NAME_CALLTIME, callTime);


        ContentProviderManager.deletePreviousCallTime(mContext);
        ContentProviderManager.saveCallTime(mContext, contentValues);

        return true;
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public CallTimePopupSelectPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
