package com.commax.settings.device;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.util.PlusClickGuard;

/**
 * InitData 확인 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class InitDevicePopup extends Dialog {


    private final Context mContext;
    private final InitDeviceListener mListener;


    public InitDevicePopup(Context context, InitDeviceListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_reset_device);

        setGuideMessage();
        addButtonListener();
    }

    /**
     * 버튼에 리스너 추가
     */
    private void addButtonListener() {
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                dismiss();
                mListener.onInitDevice(false);
            }
        });

        Button confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                dismiss();
                mListener.onInitDevice(true);

            }
        });
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public InitDevicePopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


    /**
     * 삭제 메시지 설정
     */
    public void setGuideMessage() {
        TextView deleteMessage = (TextView) findViewById(R.id.deleteMessage);
        String message = getContext().getResources().getString(R.string.dataInitconfirm);
        deleteMessage.setText(message);
    }

    /**
     * FocusChanged 될때 NavigationBar 숨김
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            try {
                // 액티비티 아래의 네비게이션 바가 안보이게
                final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                getWindow().getDecorView().setSystemUiVisibility(flags);
                final View decorView = getWindow().getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
