package com.commax.controlsub.MoreActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.DeviceInfo;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

import java.util.ArrayList;

/**
 * Created by OWNER on 2016-08-19.
 */
public class More_StandybyPower_Activity extends Activity {
    String TAG = More_StandybyPower_Activity.class.getSimpleName();
    ArrayList<DeviceInfo> StandybyPower;
    DeviceInfo deviceInfo;

    Context mContext;

    TextView title_device;
    TextView data_number;
    TextView data_scale;

    ImageView title_power_button;
    Button auto_binary_button;
    Button manual_binary_button;
    Button maximun_button;

    String value;
    String value2;
    String value3;
    String value4;
    String scale;
    String sort;
    String CommaxDevice;
    String RootDevice;
    String nickname;
    String rootUuid;
    String subUuid;
    String rootdevice;


    boolean set_command_ok=false;
    boolean doing_control=false;
    static final int control_time_out=10;

    /*
    *  value : on/ off  , value1 : data number , value2 : maximun number , value4 : auto / manual
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.more_standbypower);
        Log.d(TAG ,"onCreate()");
        mContext = this;
        StandybyPower = (ArrayList<DeviceInfo>) getIntent().getSerializableExtra("array");


   /*     Log.d(TAG, "arraylist : " + StandybyPower);
        Log.d(TAG, "arraylist : " + StandybyPower.get(0));
        Log.d(TAG, " NicName : " + StandybyPower.get(0).getNickName());
        Log.d(TAG, " getCommaxDevice : " + StandybyPower.get(0).getCommaxDevice());
        Log.d(TAG, " getSort : " + StandybyPower.get(0).getSort());
        Log.d(TAG, " getValue : " + StandybyPower.get(0).getValue());*/

        init_data(StandybyPower);

    }

    void init_data( ArrayList<DeviceInfo> StandybyPower)
    {

        CommaxDevice = StandybyPower.get(0).getCommaxDevice();
        sort = StandybyPower.get(0).getSort();
        value = StandybyPower.get(0).getValue();
        value2 = StandybyPower.get(0).getValue2();
        value3 = StandybyPower.get(0).getValue3();
        value4 = StandybyPower.get(0).getValue4();
        scale = StandybyPower.get(0).getScale();
        RootDevice = StandybyPower.get(0).getRootDevice();
        nickname = StandybyPower.get(0).getNickName();
        rootUuid = StandybyPower.get(0).getRootUuid();
        subUuid = StandybyPower.get(0).getSubUuid();
        rootdevice = StandybyPower.get(0).getRootDevice();

        Log.d(TAG, " getCommaxDevice : " + CommaxDevice);
        Log.d(TAG, " getSort : " + StandybyPower.get(0).getSort());
        Log.d(TAG, " getValue : " + StandybyPower.get(0).getValue());
        Log.d(TAG, " getValue2 : " + StandybyPower.get(0).getValue2());
        Log.d(TAG, " getValue3: " + StandybyPower.get(0).getValue3());
        Log.d(TAG, " getValue4: " + StandybyPower.get(0).getValue4());
        Log.d(TAG, " getType : " + StandybyPower.get(0).getType());
        Log.d(TAG, " getPrecision : " + StandybyPower.get(0).getPrecision());
        Log.d(TAG, " getScale : " + StandybyPower.get(0).getScale());
        Log.d(TAG, " getOption1 : " + StandybyPower.get(0).getOption1());
        Log.d(TAG, " getRootDevice : " + StandybyPower.get(0).getRootDevice());

        title_device = (TextView)findViewById(R.id.title_text);
        title_device.setText(nickname);

        title_power_button = (ImageView)findViewById(R.id.title_power_button);

        data_number = (TextView)findViewById(R.id.data_number);
        data_number.setText(value2);

        data_scale = (TextView)findViewById(R.id.data_scale);
        String[] scales = scale.split("\"");
        scale = scales[1];
        data_scale.setText(scale);

        auto_binary_button = (Button)findViewById(R.id.auto_button);
        manual_binary_button = (Button)findViewById(R.id.manual_button);
        maximun_button = (Button)findViewById(R.id.cutoff_button);

        maximun_button.setText(value3);

        if(value.equals(TypeDef.SWITCH_ON))
        {
            title_power_button.setSelected(true);
        }
        else if(value.equals(TypeDef.SWITCH_OFF))
        {
            title_power_button.setSelected(false);
        }

        if(value4.equalsIgnoreCase(TypeDef.OP_MODE_MANUAL))
        {
            manual_binary_button.setSelected(true);
            auto_binary_button.setSelected(false);
            Log.d(TAG, "binary button : " + value4 );
        }else if (value4.equals(TypeDef.OP_MODE_AUTO))
        {
            manual_binary_button.setSelected(false);
            auto_binary_button.setSelected(true);
            Log.d(TAG, "binary button : " + value4 );
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG ,"onStop()");
        MainActivity.getInstance().finish();

    }

    public void onClick(View view)
    {
        Log.d(TAG , "onclick()");
        String setting_on = null;

        if(view.equals(findViewById(R.id.title_back_button)))
        {
            Log.d(TAG,"click back button");
            finish();
        }
        else if(view.equals(findViewById(R.id.title_power_button)))
        {
            Log.d(TAG,"click power button");
            if(value.equals(TypeDef.SWITCH_OFF))
            {
                title_power_button.setSelected(true);
                setting_on = "on";
            }
            else if(value.equals(TypeDef.SWITCH_ON))
            {
                title_power_button.setSelected(false);
                setting_on = "off";
            }
            setStatus(setting_on);
        }
        else if(view.equals(findViewById(R.id.auto_button)))
        {
            // on, off 상태 전환
            try {
                if (value.equalsIgnoreCase(TypeDef.OP_MODE_AUTO)) {
                    Log.d(TAG, "clicked MANUAL");
                    setting_on = TypeDef.OP_MODE_MANUAL;
                    auto_binary_button.setSelected(false);
                    manual_binary_button.setSelected(true);
                    controlDevice(setting_on);
                }
                else if (value.equalsIgnoreCase(TypeDef.OP_MODE_MANUAL)) {
                    Log.d(TAG, "clicked AUTO");
                    setting_on = TypeDef.OP_MODE_AUTO;
                    auto_binary_button.setSelected(true);
                    manual_binary_button.setSelected(false);
                    controlDevice(setting_on);
                } else {
                    setting_on = TypeDef.STATUS_UNKNOWN;
                }
//                setStatus(setting_on);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else if(view.equals(findViewById(R.id.manual_button)))
        {
            // on, off 상태 전환
            try {
                if (value.equalsIgnoreCase(TypeDef.SWITCH_ON)) {
                    setting_on = TypeDef.SWITCH_OFF;
                } else if (value.equalsIgnoreCase(TypeDef.SWITCH_OFF)) {
                    setting_on = TypeDef.SWITCH_ON;
                } else {
                    setting_on = TypeDef.STATUS_UNKNOWN;
                }
                setStatus(setting_on);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public void controlDevice(String value) {
        //AIDL 을 통한 Device 제어
        //power on/off
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                rootUuid,
                rootdevice,
                subUuid,
                value,
                sort
        );
    }

    protected void setStatus(String on_off){

        String rootDevice = "";
        set_command_ok=false;

        if(!doing_control) {
            Log.d(TAG, "setStatus : "+ sort +" setting to : " + on_off);
            doing_control=true;
            try {

                switch (sort) {
                    case TypeDef.SUB_DEVICE_SWITCHBINARY:
                        rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        try {
                            Log.d(TAG, "rootDevice : " +rootDevice );
                            if (on_off.equalsIgnoreCase(TypeDef.SWITCH_ON)) {
                                MainActivity.getInstance().dataManager.setOnOrOffAddSort(rootUuid, rootDevice, subUuid, "on", sort);
                                checkResult();

                            } else if (on_off.equalsIgnoreCase(TypeDef.SWITCH_OFF)) {
                                MainActivity.getInstance().dataManager.setOnOrOffAddSort(rootUuid, rootDevice, subUuid, "off", sort);
                                checkResult();
                            } else {
                                doing_control=false;
                                Toast.makeText(mContext, "need sync", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void checkResult(){

     /*   try {
//            doing_control=true;
//            Toast.makeText(mContext, "on working", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < (control_time_out*10); i++) {
                        if (set_command_ok) {
                            doing_control=false;
                            return;
                        }
                        if (!MainActivity.getInstance().activity_on){
                            Log.d(TAG, "activity is not foreground");
                            return;
                        }

                        if ((i % 10) == 0) {
                            Log.d(TAG, "Try " + i / 10);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    MainActivity.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!set_command_ok) {
                                if ((MainActivity.getInstance().activity_on)) {
                                    Toast.makeText(mContext, "control failed", Toast.LENGTH_SHORT).show();

                                    Log.d(TAG, "status : unknown");
                                    temp_status = on_unlock;
                                }
                            }
                        }
                    });
                    doing_control=false;
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

}
