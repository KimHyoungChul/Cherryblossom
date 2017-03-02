package com.commax.controlsub.MoreActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.DeviceInfo;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

import java.util.Arrays;

/**
 * Created by OWNER on 2016-08-02.
 */
public class Custom_standy_cutoff_dialog extends Dialog implements  View.OnClickListener{
    String TAG = Custom_standy_cutoff_dialog.class.getSimpleName();

    Context mContext;
    Button mLeftButton;
    Button mRightButton;

    LinearLayout all_layout;
    EditText cutoff_edit_value;
    ImageView minusButton;
    ImageView plusButton;

    DeviceInfo deviceInfo;

     /*
    *  value : on/ off  , value2 : data number , value3 : cutoff number , value4 : auto / manual
    *
    * */

    private static final int LENGTH = 20;

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public Custom_standy_cutoff_dialog(Context context , DeviceInfo devicedata) {
        super(context);
        mContext = context;
        deviceInfo = devicedata;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.custom_dialog_standbypower_cutoff);
        mLeftButton = (Button) findViewById(R.id.btn_cancel);
        mRightButton = (Button) findViewById(R.id.btn_ok);
        cutoff_edit_value = (EditText)findViewById(R.id.cutoff_edittext);
        minusButton = (ImageView) findViewById(R.id.cutoff_minus_button);
        plusButton = (ImageView) findViewById(R.id.cutoff_plus_button);
        all_layout = (LinearLayout)findViewById(R.id.all_layout);

        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        init_data();

        minusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(cutoff_edit_value.isFocused())
                {
                    //edittext set not focused
                    Rect outRect = new Rect();
                    cutoff_edit_value.getGlobalVisibleRect(outRect);
                    cutoff_edit_value.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if(MotionEvent.ACTION_DOWN == action){
                    Log.d(TAG, "minusButton ActionDown");
                    onHandler1("minus");
                }else if(MotionEvent.ACTION_UP == action){
                    Log.d(TAG, "minusButton Actionup");
                    setminusButton();
                    mHandler.removeCallbacks(r);
                }
                return false;
            }
        });

        plusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(cutoff_edit_value.isFocused())
                {
                    //edittext set not focused
                    Rect outRect = new Rect();
                    cutoff_edit_value.getGlobalVisibleRect(outRect);
                    cutoff_edit_value.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                if(MotionEvent.ACTION_DOWN == action){
                    Log.d(TAG, "plusButton ActionDown");
                    onHandler1("plus");
                }else if(MotionEvent.ACTION_UP == action){
                    Log.d(TAG, "plusButton Actionup");
                    setplusButton();
                    mHandler.removeCallbacks(r);
                }
                return false;
            }
        });

        cutoff_edit_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged");
                cutoff_edit_value.setSelection(s.length());
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged");
                try {
                    if (s.length() != 0) {
                        if (s.charAt(0) == '0' && s.length() != 0) {
                            Log.d(TAG, "  첫번째 자리수 0");
                            Log.d(TAG, "s = " + s);
                            if(s.charAt(0) =='0' && s.charAt(1) == '0')
                            {
                                Log.d(TAG, " 00 입력 ");
                                cutoff_edit_value.setText("0");
                                cutoff_edit_value.setSelection(1);
                            }
                            else
                            {
                                String[] change_int = String.valueOf(s).split("0");
                                Log.e(TAG, "chage_int : " + Arrays.toString(change_int));
                                cutoff_edit_value.setText(change_int[1]);
                                cutoff_edit_value.setSelection(cutoff_edit_value.length());
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                if(s.length() > 2)
                {
                    MainActivity.getInstance().showToastOnWorking(mContext.getString(R.string.cutoff_value_min_max));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged");
            }
        });
    }
    //onTouch listener
    private Handler mHandler;
    private Runnable r;
    private void onHandler1(final String minus_plus) {
        mHandler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                //TODO minus button
                int int_value = 0;
                if(minus_plus.equalsIgnoreCase("minus"))
                {
                   setminusButton();
                }
                else if(minus_plus.equalsIgnoreCase("plus"))
                {
                    setplusButton();
                }
                mHandler.postDelayed(r, 500);
            }
        };
        mHandler.postDelayed(r, 500);
    }

    public void setplusButton()
    {
        int int_value = 0;
        Log.d(TAG, "plusButton clicked");
        String cutoff_value = cutoff_edit_value.getText().toString();
        try
        {
            if(cutoff_value.length() == 0)
            {
                cutoff_value = "0";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            cutoff_value = "0";
        }

        String[] cutoff= cutoff_value.split("w");
        double value = Double.valueOf(cutoff[0]);
        int_value = (int)value;
        if(int_value >= 99)
        {
            MainActivity.getInstance().showToastOnWorking(mContext.getString(R.string.fcu_number_limit));
        }
        else
        {
            int_value = int_value + 1 ;
        }
        Log.d(TAG, "value : " + int_value);
        cutoff_edit_value.setText(String.valueOf(int_value));
        cutoff_edit_value.setSelection(cutoff_edit_value.length());
    }

    public void setminusButton()
    {
        int int_value =0;
        Log.d(TAG , "minusButton clicked");
        String cutoff_value = cutoff_edit_value.getText().toString();
        Log.d(TAG,"cut_off_vlaue : " + cutoff_value);
        try
        {
            if(cutoff_value.length() == 0)
            {
                cutoff_value = "0";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            cutoff_value = "0";
        }
        String[] cutoff= cutoff_value.split("w");
        double value = Double.valueOf(cutoff[0]);
        int_value = (int)value ;
        if(int_value <= 0)
        {
            MainActivity.getInstance().showToastOnWorking(mContext.getString(R.string.fcu_number_limit));
        }
        else
        {
            int_value = int_value - 1;
        }
        Log.d(TAG, "value : " + int_value);
        cutoff_edit_value.setText(String.valueOf(int_value));
        cutoff_edit_value.setSelection(cutoff_edit_value.length());
    }

    public void init_data()
    {
        double value3 = Double.valueOf(deviceInfo.getValue3());
        int int_value = (int)value3;
        Log.d(TAG, "int_value :" + int_value);
        cutoff_edit_value.setText(String.valueOf(int_value));
        cutoff_edit_value.setSelection(cutoff_edit_value.length());
    }

    @Override
    public void onClick(View v) {
        if (v == mLeftButton) {
            Log.d(TAG ,"cancle");
            cancel();
        }
        else if(v.equals(mRightButton)){
            Log.d(TAG , "Okay");
            int return_value;

            try
            {
                return_value = Integer.valueOf(cutoff_edit_value.getText().toString());
            }
            catch (Exception e)
            {
                return_value = 0;
            }

            if(return_value > 99 || return_value < 0 )
            {
                MainActivity.getInstance().showToastOnWorking(mContext.getString(R.string.fcu_number_limit));
            }
            else
            {
                String value = cutoff_edit_value.getText().toString().split("w")[0];
                String subuuid = deviceInfo.getSubUuid3();
                String sort = TypeDef.SUB_DEVICE_METERSETTING;
                Log.d(TAG, "value : " + value + " , subuuid : " + subuuid + " , sort : " +sort);
                MainActivity.getInstance().more_standbyPower.controlDevice(value ,subuuid , sort);
//            More_StandbyPower more_standbyPower = new More_StandbyPower(mContext , deviceInfo);
//            more_standbyPower.controlDevice(value ,subuuid , sort);
                dismiss();
            }
        }
    }
}
