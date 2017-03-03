package com.commax.iphomiot.doorcall.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.commax.iphomeiot.common.network.HttpUtil;
import com.commax.iphomeiot.common.ui.activity.ActivityUtil;
import com.commax.iphomiot.doorcall.R;
import com.commax.nubbyj.network.NetworkUtil;
import com.commax.settings.content_provider.ContentProviderManagerEx;

public class PreviewActivity extends BaseVideoActivity implements View.OnClickListener {
    private String ip_ = "";
    private RegisterDoorTask registerDoorTask_ = null;

    public static final String INTENT_KEY_IP = "ip";

    private class RegisterDoorTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog progressDialog_ = null;

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog_ = new ProgressDialog(PreviewActivity.this);
            progressDialog_.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog_.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog_.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String recvContent = HttpUtil.requestGet(params[0], 2000 * 10);
            int sipNo = Integer.parseInt(params[1]);
            if (recvContent == null)
                return -1;
            else
                return sipNo;
        }

        protected void onPostExecute(Integer ret) {
            progressDialog_.dismiss();
            Intent intent = new Intent();
            if (ret == -1)
                intent.putExtra("isRegistered", "false");
            else {
                intent.putExtra("isRegistered", "true");
                intent.putExtra("sipNo", ret.toString());
            }
            PreviewActivity.this.setResult(Activity.RESULT_OK, intent);
            PreviewActivity.this.finish();
        }
    }

    private void initView() {
        addCameraDisplayView((FrameLayout)findViewById(R.id.layoutVideo));
        ImageView imgChangeIp = (ImageView)findViewById(R.id.imgChangeIp);
        imgChangeIp.setVisibility(View.GONE);
        TextView tvComment = (TextView)findViewById(R.id.tvComment);
        tvComment.setVisibility(View.GONE);
        if (ContentProviderManagerEx.isCustomDoorCameraIpExistOnContentProvider(this, ip_)) {
            Button btnRegistration = (Button)findViewById(R.id.btnRegistration);
            btnRegistration.setVisibility(View.GONE);
        }
        else {
        }
        ActivityUtil.registerClickListener(this, R.id.imgClose, R.id.btnRegistration, R.id.imgChangeIp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_preview);

        ip_ = getIntent().getStringExtra(INTENT_KEY_IP);
        initView();
        startRtspLiveVideo(ip_);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgClose:
                finish();
                break;
            case R.id.btnRegistration:
                if (registerDoorTask_ != null)
                    return;
                String localIp = NetworkUtil.getLocalIpAddress();
                Integer sipNo = ContentProviderManagerEx.getUsableSipNo(this);
                String httpReq = String.format("http://%s/settings/platform/&enable=1&server=%s&port=5060&user=%d,0000&password=%d", ip_, localIp, sipNo, sipNo);
                registerDoorTask_ = new RegisterDoorTask();
                registerDoorTask_.execute(httpReq, sipNo.toString());
                break;
            case R.id.imgChangeIp:
                Intent ipSettingIntent = new Intent(this, IpSettingActivity.class);
                startActivity(ipSettingIntent);
                break;
        }
    }
}
