package com.commax.settings.doorphone_custom;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.commax.settings.CommonActivity;
import com.commax.settings.R;

/**
 * IP 설정 액티비티
 */
public class IpConfigActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_config);
        setFullScreen();


        init();

    }

    /**
     * 초기화
     */
    private void init() {
        RadioGroup mode = (RadioGroup) findViewById(R.id.mode);
        mode.check(R.id.autoMode);

//수정 필요할 듯!!
//        EditText ipaddressInput = (EditText) findViewById(R.id.ipaddressInput);
//        ipaddressInput.setText("192.168.1.200");
//        ipaddressInput.setEnabled(false);
//
//        EditText subnetmaskInput = (EditText) findViewById(R.id.subnetmaskInput);
//        subnetmaskInput.setText("255.255.255.0");
//        subnetmaskInput.setEnabled(false);
//
//        EditText gatewayInput = (EditText) findViewById(R.id.gatewayInput);
//        gatewayInput.setText("10.1.1.254");
//        gatewayInput.setEnabled(false);
//
//        EditText dnsInput = (EditText) findViewById(R.id.dnsInput);
//        dnsInput.setText("168.126.63.1");
//        dnsInput.setEnabled(false);


    }

    /**
     * 액티비티 종료
     *
     * @param view
     */
    public void closeActivity(View view) {
        finish();
    }


    /**
     * IP 설정 완료
     *
     * @param view
     */
    public void completeIpConfig(View view) {
        //도어폰 sdk와 연동 필요!!


    }
}
