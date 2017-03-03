package com.commax.ipdoorcamerasetting.dongho;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.ipdoorcamerasetting.CommonActivity;
import com.commax.ipdoorcamerasetting.R;
import com.commax.ipdoorcamerasetting.content_provider.ContentProviderConstants;
import com.commax.ipdoorcamerasetting.content_provider.ContentProviderManager;
import com.commax.ipdoorcamerasetting.registration.CameraRegistrationActivity;
import com.commax.ipdoorcamerasetting.util.PlusClickGuard;

import java.util.ArrayList;
import java.util.List;

/**
 * 동호 등록
 */
public class DonghoActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongho);

    }



    /**
     * CameraRegistrationActivity 실행
     * @param view
     */
    public void launchCameraRegistrationActivity(View view) {

        PlusClickGuard.doIt(view);


        if(!isDonghoSaved()) {
            return;
        }


        Intent intent = new Intent(this, CameraRegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * 동호 저장
     * @return
     */
    private boolean isDonghoSaved() {

        EditText dongInput = (EditText) findViewById(R.id.dongInput);
        EditText hoInput = (EditText) findViewById(R.id.hoInput);

        String dong = dongInput.getText().toString();
        String ho = hoInput.getText().toString();

        if(dong.equals("")) {
            Toast.makeText(this, R.string.put_in_dong, Toast.LENGTH_SHORT).show();
            return false;
        }



        if(ho.equals("")) {
            Toast.makeText(this, R.string.put_in_ho, Toast.LENGTH_SHORT).show();
            return false;
        }




        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG, dong);
        contentValues.put(ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO, ho);

        ContentProviderManager.deletePreviousDongho(this);
        ContentProviderManager.saveDongho(this, contentValues);



        return true;
    }


}
