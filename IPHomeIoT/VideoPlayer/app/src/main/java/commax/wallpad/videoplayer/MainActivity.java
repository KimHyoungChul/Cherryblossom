package commax.wallpad.videoplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Environment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.commax.iphomeiot.common.ui.dialog.PopUpDialog;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.File;

public class MainActivity extends Activity implements VideoListAdapterSelectState, ITerminatedThread{

    private final static int TODAYS = 0;
    private final static int LAST_SEVEN_DAYS = 1;
    private final static int OLD_DAYS = 2;
    private ExpandableGridView todays_;
    private ExpandableGridView lastSevenDays_;
    private ExpandableGridView oldDays_;
    private VideoListAdapter todayAdapter_;
    private VideoListAdapter lastSevenDaysAdapter_;
    private VideoListAdapter oldDaysAdapter_;
    private int selectTodayCount_ = 0;
    private int selectSevenDaysCount_ = 0;
    private int selectAfterCount_ = 0;
    private int totalCount_ = 0;
    private LinearLayout allSelect_;
    private FrameLayout mainTitle_;
    private TextView mainTitleText_;
    private ImageView back_;
    private ImageView deleteFile_;
    private ImageView allDelete_;
    private TextView deleteIcon_;
    private TextView cancelIcon_;
    private boolean isDelete_;
    private String storageRoot_;
    private View mainDivider1_;
    private View mainDivider2_;
    private PopUpDialog popUpDialog_;
    private View decorView_;
    private int uiOptions_ = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private DisplayMetrics displayMetrics_;
    private BroadcastReceiver receiver_;

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
        isDelete_ = false;

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            storageRoot_ = ""+Environment.getExternalStorageDirectory().getAbsolutePath() + "/cmxMediaGallery";
        else
            storageRoot_ = ""+Environment.getExternalStorageDirectory().getAbsolutePath() +"/cmxMediaGallery";

        File directory = new File(storageRoot_);
        if (!directory.exists()) {
            directory.mkdir();
        }

        decorView_ = getWindow().getDecorView();
        decorView_.setSystemUiVisibility(uiOptions_);

        displayMetrics_ = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics_);

        mainTitle_ = (FrameLayout) this.findViewById(R.id.mainTitle);
        mainTitleText_ = (TextView) this.findViewById(R.id.mainTitleText);
        allSelect_ = (LinearLayout) this.findViewById(R.id.allSelect);
        allSelect_.setVisibility(View.GONE);
        allDelete_ = (ImageView) this.findViewById(R.id.allDelete);
        back_ = (ImageView) this.findViewById(R.id.backToHome);
        deleteFile_ = (ImageView)  this.findViewById(R.id.deleteFile);
        deleteIcon_ = (TextView) this.findViewById(R.id.deleteIcon);
        deleteIcon_.setVisibility(View.GONE);
        cancelIcon_ = (TextView) this.findViewById(R.id.cancelIcon);
        cancelIcon_.setVisibility(View.GONE);
        todays_ = (ExpandableGridView) this.findViewById(R.id.today);
        todays_.setExpanded(true);
        lastSevenDays_ = (ExpandableGridView) this.findViewById(R.id.lastSevenDays);
        lastSevenDays_.setExpanded(true);
        oldDays_ = (ExpandableGridView) this.findViewById(R.id.oldDays);
        oldDays_.setExpanded(true);
        MediaFileManager.getInstance().setStorageRoot(storageRoot_);

        mainDivider1_ = (View) this.findViewById(R.id.mainDivider1);
        mainDivider2_ = (View) this.findViewById(R.id.mainDivider2);
        mainDivider1_.setVisibility(View.GONE);
        mainDivider2_.setVisibility(View.GONE);

        if (MediaFileManager.getInstance().size() == 0) {
            MediaFileManager.getInstance().getFileList(storageRoot_, MainActivity.this);
        }
        else {
            if (todayAdapter_ == null)
                todayAdapter_ = new VideoListAdapter(TODAYS, MainActivity.this, MainActivity.this, storageRoot_, MediaFileManager.getInstance().getTodayFileInfoList(), displayMetrics_);
            todayAdapter_.setIsToday(true);
            todays_.setAdapter(todayAdapter_);
            todays_.setNumColumns(3);
            if (lastSevenDaysAdapter_ == null)
                lastSevenDaysAdapter_ = new VideoListAdapter(LAST_SEVEN_DAYS, MainActivity.this, MainActivity.this, storageRoot_, MediaFileManager.getInstance().getSevenDaysFileInfoList(), displayMetrics_);
            lastSevenDays_.setAdapter(lastSevenDaysAdapter_);
            lastSevenDays_.setNumColumns(3);
            if (oldDaysAdapter_ == null)
                oldDaysAdapter_ = new VideoListAdapter(OLD_DAYS, MainActivity.this, MainActivity.this, storageRoot_, MediaFileManager.getInstance().getOldDaysFileInfoList(), displayMetrics_);
            oldDays_.setAdapter(oldDaysAdapter_);
            oldDays_.setNumColumns(3);
        }

        deleteFile_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MediaFileManager.getInstance().size() != 0)
                    setVisibleMainView(false);
            }
        });

        cancelIcon_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDeleteFlag(false);
                setVisibleMainView(true);
            }
        });

        deleteIcon_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpDialog_ = new PopUpDialog(MainActivity.this);
                popUpDialog_.setText(getString(R.string.deletePopTitle), getString(R.string.deletePopContents), getString(R.string.yes), getString(R.string.no));
                popUpDialog_.setListener(okClickListener, cancelClickListener);
                popUpDialog_.setFullScreen();
                popUpDialog_.setCancelable(false);
                popUpDialog_.show();
            }
        });

        allSelect_.setOnClickListener(new View.OnClickListener() {
            boolean select_ = true;
            @Override
            public void onClick(View v) {
                if (select_) {
                    todayAdapter_.setAllDelete(true);
                    lastSevenDaysAdapter_.setAllDelete(true);
                    oldDaysAdapter_.setAllDelete(true);
                    todays_.invalidateViews();
                    lastSevenDays_.invalidateViews();
                    oldDays_.invalidateViews();
                    select_ = false;
                }
                else {
                    todayAdapter_.setAllDelete(false);
                    lastSevenDaysAdapter_.setAllDelete(false);
                    oldDaysAdapter_.setAllDelete(false);
                    todays_.invalidateViews();
                    lastSevenDays_.invalidateViews();
                    oldDays_.invalidateViews();
                    select_ = true;
                }
            }
        });

        back_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDelete_) {
                    MediaFileManager.getInstance().clearFile();
                    finish();
                }
                else
                    setVisibleMainView(true);
            }
        });

        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MediaFileManager.getInstance().clearFile();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver_ != null) {
            unregisterReceiver(receiver_);
            receiver_ = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isDelete_) {
                finish();
            }
            else
                setVisibleMainView(true);
        }
        return false;
    }

    private void registerReceiver() {
        if (receiver_ != null) {
            return;
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    if (popUpDialog_ != null) {
                        popUpDialog_.dismiss();
                        popUpDialog_ = null;
                    }
                    finish();
                } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                }
            }
        }, intentFilter);
    }

    private View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            todayAdapter_.deleteFile(0);
            lastSevenDaysAdapter_.deleteFile(1);
            oldDaysAdapter_.deleteFile(2);

            todayAdapter_.updateData(MediaFileManager.getInstance().getTodayFileInfoList());
            lastSevenDaysAdapter_.updateData(MediaFileManager.getInstance().getSevenDaysFileInfoList());
            oldDaysAdapter_.updateData(MediaFileManager.getInstance().getOldDaysFileInfoList());
            todays_.invalidateViews();
            lastSevenDays_.invalidateViews();
            oldDays_.invalidateViews();
            updateDeleteFlag(false);
            setVisibleMainView(true);
            popUpDialog_.dismiss();
        }
    };

    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popUpDialog_.dismiss();
            decorView_ = getWindow().getDecorView();
            decorView_.setSystemUiVisibility(uiOptions_);
        }
    };

    private void updateDeleteFlag(boolean select) {
        if (select) {
            todayAdapter_.setAllDelete(true);
            lastSevenDaysAdapter_.setAllDelete(true);
            oldDaysAdapter_.setAllDelete(true);
            todays_.invalidateViews();
            lastSevenDays_.invalidateViews();
            oldDays_.invalidateViews();
        }
        else {
            todayAdapter_.setAllDelete(false);
            lastSevenDaysAdapter_.setAllDelete(false);
            oldDaysAdapter_.setAllDelete(false);
            todays_.invalidateViews();
            lastSevenDays_.invalidateViews();
            oldDays_.invalidateViews();
        }
    }

    private void updateDeleteView(boolean isDelete) {
        if (isDelete)
            isDelete_ = false;
        else
            isDelete_ = true;

        todayAdapter_.setDeleteState(isDelete_);
        todays_.invalidateViews();
        lastSevenDaysAdapter_.setDeleteState(isDelete_);
        lastSevenDays_.invalidateViews();
        oldDaysAdapter_.setDeleteState(isDelete_);
        oldDays_.invalidateViews();
    }

    private void setVisibleMainView (boolean isMain) {
        decorView_ = getWindow().getDecorView();
        decorView_.setSystemUiVisibility(uiOptions_);

        if (isMain) {
            updateDeleteView(isDelete_);
            allDelete_.setImageDrawable(getDrawable(R.mipmap.btn_checkbox_n));
            deleteFile_.setVisibility(View.VISIBLE);
            deleteIcon_.setVisibility(View.GONE);
            back_.setVisibility(View.VISIBLE);
            cancelIcon_.setVisibility(View.GONE);
            mainTitle_.setBackground(getDrawable(R.mipmap.bg_title_1depth));
            mainTitleText_.setTextColor(Color.WHITE);
            allSelect_.setVisibility(View.GONE);
            mainDivider1_.setVisibility(View.GONE);
            mainDivider2_.setVisibility(View.GONE);
            totalCount_ = 0;
            mainTitleText_.setText(getString(R.string.title));
        }
        else {
            updateDeleteView(isDelete_);
            mainTitle_.setBackgroundColor(Color.argb(255, 237, 237, 237));
            deleteFile_.setVisibility(View.GONE);
            back_.setVisibility(View.GONE);
            cancelIcon_.setVisibility(View.VISIBLE);
            mainTitleText_.setTextColor(Color.BLACK);
            allSelect_.setVisibility(View.VISIBLE);
            mainDivider1_.setVisibility(View.VISIBLE);
            mainDivider2_.setVisibility(View.VISIBLE);
            mainTitleText_.setText(getString(R.string.selectItem));
        }
    }

    @Override
    public void selectItmeCount(int adapterId, int count) {
        if (adapterId == 0)
            selectTodayCount_ = count;
        if (adapterId == 1)
            selectSevenDaysCount_ = count;
        if (adapterId == 2)
            selectAfterCount_ = count;

        totalCount_ = selectTodayCount_ + selectSevenDaysCount_ + selectAfterCount_;

        if (totalCount_ == MediaFileManager.getInstance().size())
            allDelete_.setImageDrawable(getDrawable(R.mipmap.btn_checkbox_s));
        else
            allDelete_.setImageDrawable(getDrawable(R.mipmap.btn_checkbox_n));

        mainTitleText_.setText(totalCount_ + " " + getString(R.string.selected));

        if(totalCount_ == 0) {
            mainTitleText_.setText(getString(R.string.selectItem));
            deleteIcon_.setVisibility(View.GONE);
            deleteIcon_.setEnabled(false);
        }
        else {
            deleteIcon_.setVisibility(View.VISIBLE);
            deleteIcon_.setEnabled(true);
        }
    }

    @Override
    public void isTerminate() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                todayAdapter_ = new VideoListAdapter(TODAYS, MainActivity.this, MainActivity.this, storageRoot_, MediaFileManager.getInstance().getTodayFileInfoList(), displayMetrics_);
                todayAdapter_.setIsToday(true);
                todays_.setAdapter(todayAdapter_);
                todays_.setNumColumns(3);
                lastSevenDaysAdapter_ = new VideoListAdapter(LAST_SEVEN_DAYS, MainActivity.this, MainActivity.this, storageRoot_, MediaFileManager.getInstance().getSevenDaysFileInfoList(), displayMetrics_);
                lastSevenDays_.setAdapter(lastSevenDaysAdapter_);
                lastSevenDays_.setNumColumns(3);
                oldDaysAdapter_ = new VideoListAdapter(OLD_DAYS, MainActivity.this, MainActivity.this, storageRoot_, MediaFileManager.getInstance().getOldDaysFileInfoList(), displayMetrics_);
                oldDays_.setAdapter(oldDaysAdapter_);
                oldDays_.setNumColumns(3);
            }
        });
    }
}
