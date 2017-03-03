package com.commax.iphomiot.doorcall.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.doorphone_custom.CustomDevice;
import com.commax.iphomiot.doorcall.R;

public class ChooseDoorActivity extends Activity implements View.OnClickListener {
    private AdapterView.OnItemClickListener itemClickListener_ = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            finish();
            CustomDevice device = (CustomDevice)parent.getAdapter().getItem(position);
            DoorCallApplication.getInstance().createMonitoringActivity(device.getIpv4());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choosedoor);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point windowSize = new Point();
        display.getRealSize(windowSize);
        getWindow().getAttributes().width = (int)(windowSize.x * 0.5);
        getWindow().getAttributes().height = (int)(windowSize.y * 0.5);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setGravity(Gravity.CENTER);

        ListView lvDoorList = (ListView)findViewById(R.id.lvDoorList);
        lvDoorList.setAdapter(new ChooseDoorAdapter(ContentProviderManager.getAllCustomDoorCamera(this)));
        lvDoorList.setOnItemClickListener(itemClickListener_);
        lvDoorList.setDivider(new ColorDrawable(0xebecf0));
        lvDoorList.setDividerHeight(1);
        Button btnCloseDoorPopup = (Button)findViewById(R.id.btnCloseDoorPopup);
        btnCloseDoorPopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseDoorPopup:
                finish();
                break;
        }
    }
}
