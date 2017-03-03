package com.commax.settings.doorphone_onvif;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;

import java.util.Locale;

/**
 * 도어폰 카메라 삭제 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class OnvifDoorphoneCameraDeletePopup extends Dialog {


    private final Context mContext;
    private final OnvifDeleteDoorphoneCameraListener mListener;
    private OnvifDevice mDevice;


    public OnvifDoorphoneCameraDeletePopup(Context context, OnvifDeleteDoorphoneCameraListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_delete_doorphone_camera);

        addButtonListener();

        if(TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
            setDeleteMessage();
        }
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
                dismiss();
                if(!TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
                    deleteDoorphoneCameraOnContentProvider();
                }
                mListener.onDelete(mDevice);


            }
        });
    }

    /**
     * Content provider에서 도어폰 카메라 삭제
     */
    private void deleteDoorphoneCameraOnContentProvider() {
        ContentProviderManager.deleteOnvifDoorCamera(mContext, mDevice.getIpAddress());

    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public OnvifDoorphoneCameraDeletePopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


    /**
     * 삭제 메시지 설정
     */
    public void setDeleteMessage() {
        TextView deleteMessage = (TextView) findViewById(R.id.deleteMessage);

        String message = null;

        if(TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
            message = ((Activity)mContext).getResources().getString(R.string.check_delete);
        } else {
            String language = Locale.getDefault().getDisplayLanguage();

            if (language.equals("한국어")) {
                message = mDevice.getName() + "을 삭제하시겠습니까?";
            } else {
                message = "delete" + " " + mDevice.getName() + "?";
            }
        }

        deleteMessage.setText(message);
    }

    /**
     * Device 설정
     * @param onvifDevice
     */
    public void setDevice(OnvifDevice onvifDevice) {
        mDevice = onvifDevice;
        setDeleteMessage();
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
