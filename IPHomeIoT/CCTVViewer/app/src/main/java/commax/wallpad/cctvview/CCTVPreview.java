package commax.wallpad.cctvview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.nubbyj.device.camera.CameraClient;
import com.commax.nubbyj.device.camera.CameraClientReceivable;
import com.commax.nubbyj.network.rtsp.client.RTSPReceivable;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.content_provider.OnvifDevice;

import java.util.List;

public class CCTVPreview extends Activity implements SurfaceHolder.Callback, CameraClientReceivable {

    private CameraClient cameraClient_;
    private BroadcastReceiver receiver_;
    private TextView previewComment_;
    private FrameLayout disconnectPreview_;
    private SurfaceView previewSurfaceView_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cctv_preview);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        Intent intent = getIntent();
        String id = intent.getExtras().getString("id");
        String pass = intent.getExtras().getString("password");
        String rtspUrl = intent.getExtras().getString("rtspUrl");
        int streamNo = 0;
        boolean useTcp = false;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int statusBarHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

        final FrameLayout titleBar = (FrameLayout) findViewById(R.id.previewTitleBar);
        ImageView back = (ImageView) findViewById(R.id.backToEnvirontment);
        Button registerCCTV = (Button) findViewById(R.id.registerCCTV);
        previewComment_ = (TextView) findViewById(R.id.previewComment);
        previewComment_.setVisibility(View.GONE);
        disconnectPreview_ = (FrameLayout) findViewById(R.id.disconnectPreview);
        disconnectPreview_.setVisibility(View.GONE);

        List<OnvifDevice> onvifDevices = ContentProviderManager.getAllOnvifCctv(getApplicationContext());
        for (int i=0; i<onvifDevices.size(); i++) {
            if (rtspUrl.equals(onvifDevices.get(i).getStreamUrl())) {
                registerCCTV.setVisibility(View.GONE);
            }
        }

        int width = displayMetrics.widthPixels;
        int height = (int) (displayMetrics.heightPixels - statusBarHeight - 78.75);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        previewSurfaceView_ = (SurfaceView) this.findViewById(R.id.previewSurface);
        previewSurfaceView_.getHolder().addCallback(this);
        previewSurfaceView_.setLayoutParams(layoutParams);

        if (rtspUrl != null) {
            cameraClient_ = new CameraClient(0);
            cameraClient_.setCameraClientReceivable(this);
            cameraClient_.startLiveVideo(id, pass, rtspUrl, streamNo, useTcp, false);
        }
        else {
            registerCCTV.setEnabled(false);
            registerCCTV.setVisibility(View.GONE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraClient_ != null)
                    cameraClient_.stopLiveVideo(0);
                finish();
            }
        });

        registerCCTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("isRegistered","true");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        previewSurfaceView_.setOnTouchListener(new View.OnTouchListener() {
            boolean isFull_ = false;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isFull_) {
                        titleBar.setVisibility(View.GONE);
                        isFull_ = true;
                    }
                    else {
                        titleBar.setVisibility(View.VISIBLE);
                        isFull_ = false;
                    }
                }
                return false;
            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
                    finish();
            }
        }, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (cameraClient_ != null) {
            cameraClient_.stopLiveVideo(0);
            cameraClient_ = null;
        }

        if (receiver_ != null) {
            unregisterReceiver(receiver_);
            receiver_ = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (cameraClient_ != null)
            cameraClient_.setRenderSurface(holder.getSurface());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onCommandResult(int cameraId, int streamNo, RTSPReceivable.RTSPClientCommand cmd, boolean cmdResult) {
        if (cmd == RTSPReceivable.RTSPClientCommand.StartLiveVideo) {
            if (!cmdResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPreview_.setVisibility(View.VISIBLE);
                        previewComment_.setVisibility(View.VISIBLE);
                        previewSurfaceView_.setEnabled(false);

                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPreview_.setVisibility(View.GONE);
                        previewComment_.setVisibility(View.GONE);
                        previewSurfaceView_.setEnabled(true);
                    }
                });
            }
        }

    }

    @Override
    public void onRecvVideoEncodeStream(int cameraId, int streamNo, RTSPReceivable.MediaCodec mediaCodecId, byte[] mediaData, int mediaSize, int timesec) {

    }

    @Override
    public void onRecvVideoDecodeStream(int cameraId, int streamNo, byte[] yBuf, byte[] uBuf, byte[] vBuf, int videoWidth, int videoHeight) {

    }
}
