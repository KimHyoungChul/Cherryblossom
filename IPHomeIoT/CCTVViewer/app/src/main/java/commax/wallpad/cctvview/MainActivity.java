package commax.wallpad.cctvview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.commax.nubbyj.device.camera.CameraClient;
import com.commax.nubbyj.device.camera.CameraClientReceivable;
import com.commax.nubbyj.network.rtsp.client.RTSPReceivable;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.content_provider.OnvifDevice;
import com.crashlytics.android.Crashlytics;
import java.util.ArrayList;
import java.util.List;
import commax.wallpad.cctvview.uitis.TOAST;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity implements SurfaceHolder.Callback, CameraClientReceivable, View.OnClickListener{

    private ListView cameraListView_;
    private LinearLayout listViewLayout_;
    private LinearLayout subtitle_;
    private FrameLayout actionBarLayout_;
    private FrameLayout disconnectView_;
    private SurfaceView surfaceView_;
    private ImageView previousButton_;
    private ImageView nextButton_;
    private ImageView back_;
    private ImageView snapShot_;
    private ImageView goToEnvironmentApp_;
    private ImageView disconnectLogo_;
    private Button subtitleFavorites_;
    private Button subtitleCCTVList_;
    private List<OnvifDevice> onvifDevices_;
    private ArrayList<CameraInfo> cameraList_ = null;
    private CameraListAdapter cameraListAdapter_;
    public int cameraId_;
    private int currentSelectedPosition_ = 0;
    private String selectCameraIp_;
    private boolean isFullScreen_;
    private BroadcastReceiver receiver_;
    private int uiOptions_;

    private void createCrashlytics () {
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier("Woohj");
        Crashlytics.setUserName("Woohj");
        Crashlytics.setUserEmail("Woohj0623@commax.com");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createCrashlytics ();
        View decorView = getWindow().getDecorView();

        uiOptions_ = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions_);
        getWindow().setUiOptions(uiOptions_);

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(uiOptions_);
                } else {
                    // TODO: The system bars are NOT visible. Make any desired
                }
            }
        });

        cameraId_ = 0;
        listViewLayout_ = (LinearLayout) this.findViewById(R.id.listviewLayout);
        actionBarLayout_ = (FrameLayout) this.findViewById(R.id.actionBarLayout);
        cameraListView_ = (ListView) this.findViewById(R.id.cameraList);
        previousButton_ = (ImageView) this.findViewById(R.id.previousButton);
        previousButton_.setOnClickListener(this);
        nextButton_ = (ImageView) this.findViewById(R.id.nextButton);
        nextButton_.setOnClickListener(this);
        disconnectView_ = (FrameLayout) this.findViewById(R.id.disconnectView);
        back_ = (ImageView) this.findViewById(R.id.back);
        back_.setOnClickListener(this);
        subtitle_ = (LinearLayout) findViewById(R.id.subtitle);
        subtitle_.setVisibility(View.GONE);
        subtitleFavorites_ = (Button) this.findViewById(R.id.subtitleFavorites);
        subtitleFavorites_.setOnClickListener(this);
        subtitleCCTVList_ = (Button) this.findViewById(R.id.subtitleCCTVList);
        subtitleCCTVList_.setOnClickListener(this);
        setSelectedFavorites(false);
        snapShot_ = (ImageView) this.findViewById(R.id.snapshot);
        snapShot_.setOnClickListener(this);
        snapShot_.setVisibility(View.GONE);
        goToEnvironmentApp_ = (ImageView) this.findViewById(R.id.goToEnvironmentApp);
        goToEnvironmentApp_.setOnClickListener(this);
        disconnectLogo_ = (ImageView) this.findViewById(R.id.disconnectLogo);
        onvifDevices_ = ContentProviderManager.getAllOnvifCctv(getApplicationContext());

        CCTVDBAccessManager cctvdbAccessManager = new CCTVDBAccessManager(getApplicationContext(), "cameraInfo.db", null, 1);
        ArrayList<CameraInfo> cameraInfos = cctvdbAccessManager.getFavoritesList();
        for (int i=0; i< cameraInfos.size(); i++) {
            if (!ContentProviderManager.isOnvifCctvIpExistOnContentProvider(getApplicationContext(), cameraInfos.get(i).getIp()))
                cctvdbAccessManager.delete(cameraInfos.get(i).getIp());
        }

        if (cameraList_ == null)
            cameraList_ = new ArrayList<CameraInfo>();


        for (int i=0; i<onvifDevices_.size(); i++) {
            CameraClient cameraClient = new CameraClient(i);
            cameraClient.setCameraClientReceivable(this);
            CameraInfo cameraInfo = new CameraInfo(cameraClient, i, onvifDevices_.get(i).getIpAddress(), onvifDevices_.get(i).getName(), onvifDevices_.get(i).getId(), onvifDevices_.get(i).getPassword(), onvifDevices_.get(i).getStreamUrl(), 0 );
            cameraList_.add(cameraInfo);
            cctvdbAccessManager.onUpdateData(onvifDevices_.get(i).getIpAddress(), i, onvifDevices_.get(i).getName(), onvifDevices_.get(i).getId(), onvifDevices_.get(i).getPassword(), onvifDevices_.get(i).getStreamUrl(), 0);
        }

        selectCameraIp_ = null;
        cameraListAdapter_ = new CameraListAdapter(this, cctvdbAccessManager);
        cameraListAdapter_.setCCTVList(cameraList_, selectCameraIp_);
        cameraListView_.setAdapter(cameraListAdapter_);

        if(cameraList_.size() < 2) {
            previousButton_.setVisibility(View.GONE);
            nextButton_.setVisibility(View.GONE);
        }
        else {
            previousButton_.setVisibility(View.VISIBLE);
            nextButton_.setVisibility(View.VISIBLE);
        }

        fullScreenSurfaceView(false);

        if (cameraList_.size() != 0) {
            disconnectLogo_.setVisibility(View.GONE);
            disconnectView_.setBackground(getDrawable(R.mipmap.bg_cctv_camera_off));
        }

        surfaceView_.setEnabled(false);

        surfaceView_.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    fullScreenSurfaceView(isFullScreen_);
                }
                return false;
            }
        });

        cameraListView_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (surfaceView_.getHolder().getSurface() == null) {
                    TOAST.show(MainActivity.this, " The surface view was not created. Please restart the application");
                    return;
                }
                currentSelectedPosition_ = i;
                Log.e("tag", "onItemClickListener " + i);
                startSelectedLiveVideo();
            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    if (cameraList_.size() == 0)
                        return;

                    for (int num = 0; num <cameraList_.size(); num++) {
                        CameraInfo cameraInfo = cameraList_.get(num);
                        cameraInfo.getCameraClient().stopLiveVideo(cameraInfo.getStreamNo());
                    }

                    finish();
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                }
            }
        }, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraList_.size() == 0)
            return;

        for (int i=0; i<cameraList_.size(); i++) {
            CameraInfo stopCamera = cameraList_.get(i);
            stopCamera.getCameraClient().stopLiveVideo(stopCamera.getStreamNo());
        }

        CameraInfo cameraInfo = cameraList_.get(cameraId_);
        cameraInfo.getCameraClient().setRenderSurface(surfaceView_.getHolder().getSurface());
        cameraInfo.getCameraClient().startLiveVideo(cameraInfo.getId(), cameraInfo.getPassword(), cameraInfo.getRtspUrl(), cameraInfo.getStreamNo(), true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraList_.size() == 0)
            return;

        for (int num = 0; num <cameraList_.size(); num++) {
            CameraInfo cameraInfo = cameraList_.get(num);
            cameraInfo.getCameraClient().stopLiveVideo(cameraInfo.getStreamNo());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraList_.size() == 0)
            return;

        for (int num = 0; num <cameraList_.size(); num++) {
            CameraInfo cameraInfo = cameraList_.get(num);
            cameraInfo.getCameraClient().stopLiveVideo(cameraInfo.getStreamNo());
        }
        if (receiver_ != null)
            unregisterReceiver(receiver_);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    private void fullScreenSurfaceView(boolean isFullScreen) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int statusBarHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

        if (!isFullScreen) {
            int width = displayMetrics.widthPixels * 13/20;
            int height = displayMetrics.heightPixels - statusBarHeight;

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            surfaceView_ = (SurfaceView) this.findViewById(R.id.videoSurfaceView);
            surfaceView_.getHolder().addCallback(this);
            surfaceView_.setLayoutParams(layoutParams);
            actionBarLayout_.setVisibility(View.VISIBLE);
            cameraListView_.setVisibility(View.VISIBLE);
            listViewLayout_.setVisibility(View.VISIBLE);
            isFullScreen_ = true;
        }
        else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            surfaceView_ = (SurfaceView) this.findViewById(R.id.videoSurfaceView);
            surfaceView_.getHolder().addCallback(this);
            surfaceView_.setLayoutParams(layoutParams);
            actionBarLayout_.setVisibility(View.GONE);
            cameraListView_.setVisibility(View.GONE);
            listViewLayout_.setVisibility(View.GONE);
            isFullScreen_ = false;
        }
    }

    private void startSelectedLiveVideo() {
        cameraListAdapter_.setPositionSelected(currentSelectedPosition_);
        int cameraId = -1;

        if (cameraListAdapter_.getListViewItemSize() != 0)
            cameraId = cameraList_.get(cameraListAdapter_.getCameraId()).getCameraId();

        if (cameraListAdapter_.getCameraId() == -1) {
            cameraId_ = cameraId;
            return;
        }

        if (cameraId_ == cameraId)
            return;

        for (int i=0; i<cameraList_.size(); i++) {
            CameraInfo stopCamera = cameraList_.get(i);
            stopCamera.getCameraClient().stopLiveVideo(stopCamera.getStreamNo());
        }

        cameraId_ = cameraId;
        surfaceView_.setEnabled(false);
        CameraInfo cameraInfo = cameraList_.get(cameraId);
        cameraInfo.getCameraClient().setRenderSurface(surfaceView_.getHolder().getSurface());
        cameraInfo.getCameraClient().startLiveVideo(cameraInfo.getId(), cameraInfo.getPassword(), cameraInfo.getRtspUrl(), cameraInfo.getStreamNo(), true, false);
    }

    private void setSelectedFavorites(boolean isSelected) {
        Drawable backgroundCCTV = subtitleCCTVList_.getBackground();
        Drawable backgroundFavorite = subtitleFavorites_.getBackground();
        if (isSelected)
        {
            if (backgroundCCTV instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) backgroundCCTV;
                gradientDrawable.setColor(Color.WHITE);
                subtitleCCTVList_.setTextColor(Color.BLACK);
            }
            if (backgroundFavorite instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) backgroundFavorite;
                gradientDrawable.setColor(Color.argb(255, 107, 117, 199));
                subtitleFavorites_.setTextColor(Color.WHITE);
            }
        }else
        {
            if (backgroundCCTV instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) backgroundCCTV;
                gradientDrawable.setColor(Color.argb(255, 107, 117, 199));
                subtitleCCTVList_.setTextColor(Color.WHITE);

            }
            if (backgroundFavorite instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) backgroundFavorite;
                gradientDrawable.setColor(Color.WHITE);
                subtitleFavorites_.setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back :
                if(cameraList_.size() != 0) {
                    for (int i=0; i< cameraList_.size(); i++) {
                        CameraInfo cameraInfo = cameraList_.get(i);
                        cameraInfo.getCameraClient().stopLiveVideo(cameraInfo.getStreamNo());
                    }
                }
                finish();
                break;
            case R.id.previousButton :
                if (cameraListAdapter_.getListViewItemSize() <= 1)
                    return;

                currentSelectedPosition_ = currentSelectedPosition_ -1;
                if (currentSelectedPosition_ < 0)
                    currentSelectedPosition_ = cameraListAdapter_.getListViewItemSize() -1;

                startSelectedLiveVideo();
                break;
            case R.id.nextButton :
                if (cameraListAdapter_.getListViewItemSize() <= 1)
                    return;

                if (cameraListAdapter_.getListViewItemSize() == 0)
                    return;

                currentSelectedPosition_ = currentSelectedPosition_ +1;
                if (currentSelectedPosition_ == cameraListAdapter_.getListViewItemSize())
                    currentSelectedPosition_ = 0;

                startSelectedLiveVideo();
                break;
            case R.id.subtitleFavorites :
                selectCameraIp_ = cameraListAdapter_.getSelectedIp(currentSelectedPosition_);
                setSelectedFavorites(true);
                currentSelectedPosition_ =  cameraListAdapter_.setFavoritesList(selectCameraIp_);

                if (cameraListAdapter_.getListViewItemSize() == 0){
                    disconnectView_.setVisibility(View.VISIBLE);
                }
                startSelectedLiveVideo();
                break;
            case R.id.subtitleCCTVList :
                selectCameraIp_ = cameraListAdapter_.getSelectedIp(currentSelectedPosition_);
                setSelectedFavorites(false);
                currentSelectedPosition_ = cameraListAdapter_.setCCTVList(cameraList_, selectCameraIp_);

                if (cameraListAdapter_.getListViewItemSize() == 0) {
                    disconnectView_.setVisibility(View.VISIBLE);
                }
                startSelectedLiveVideo();
                break;
            case R.id.snapshot :
                snapShot_.setVisibility(View.GONE);
                break;
            case R.id.goToEnvironmentApp:
                Intent intent = new Intent();
                intent.setClassName("com.commax.settings","com.commax.settings.MainActivity");
                intent.putExtra("from","cctvSetting");
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (cameraList_.size() == 0)
            return;

        cameraList_.get(cameraId_).getCameraClient().setRenderSurface(surfaceHolder.getSurface());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {    }

    @Override
    public void onCommandResult(int cameraId, int streamNo, RTSPReceivable.RTSPClientCommand rtspClientCommand, boolean cmdResult) {
        Log.e("tag", "onCammandResult " + rtspClientCommand + " " + cmdResult + " " + cameraId_);
        if (rtspClientCommand == RTSPReceivable.RTSPClientCommand.StartLiveVideo) {
            if (!cmdResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectView_.setVisibility(View.VISIBLE);
                        surfaceView_.setEnabled(false);
                        fullScreenSurfaceView(false);

                    }
                });
            }
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectView_.setVisibility(View.GONE);
                        surfaceView_.setEnabled(true);
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
