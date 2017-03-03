package com.commax.iphomiot.doorcall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.iphomiot.doorcall.view.ChooseDoorActivity;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.doorphone_custom.CustomDevice;

import java.util.List;

public class MainActivity extends Activity {
    private static final String intent_Key_AppMode_ = "mode";
    private static final String intent_Value_AppMode_ = "preview";
    private static final String intent_Key_CameraIp_ = "ip";
    private static final int reqcode_preview_ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appMode = getIntent().getStringExtra(intent_Key_AppMode_);
        if (appMode == null || !appMode.equals(intent_Value_AppMode_)) {
            List<CustomDevice> doorList = ContentProviderManager.getAllCustomDoorCamera(this);
            int doorCount = doorList.size();
            if (doorCount == 0) {
                DoorCallApplication.getInstance().goToConfigurationActivity();
                Toast.makeText(getApplicationContext(), getString(R.string.STR_REGISTER_DOOR), Toast.LENGTH_SHORT).show();
                finish();
            } else if (doorCount == 1) {
                DoorCallApplication.getInstance().createMonitoringActivity(doorList.get(0).getIpv4());
                finish();
            } else {
                Intent doorChooseIntent = new Intent(this, ChooseDoorActivity.class);
                startActivity(doorChooseIntent);
                finish();
            }
        }
        else {
            String cameraIp = getIntent().getStringExtra(intent_Key_CameraIp_);
            DoorCallApplication.getInstance().createPreviewActivity(cameraIp, this, reqcode_preview_);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case reqcode_preview_:
                setResult(Activity.RESULT_OK, data);
                finish();
                break;
        }
    }
}
