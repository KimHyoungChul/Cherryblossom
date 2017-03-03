package com.commax.iphomeiot.calldial;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, BaseFragment.IFragmentListener {
    public static boolean m_bReCreateFlag = false;				/* Recreate Flag */

    public static DialerFragment m_DialerFragment = null;		/* Dialer Fragment */
    public static CallLogFragment m_CallLogFragment = null;		/* Call Log Fragment */
    public static ContactsFragment m_ContactsFragment = null;	/* Contacts Fragment */

    private TextView tvDiarerFragment_;
    private TextView tvCallLogFragment_;
    private TextView tvContactsFragment_;
    private FrameLayout layoutTopMenu_;
    private ImageView imgChanegeRemoveMode_;
    private ImageView imgContactsAddUser_;
    private ImageView imgBack_;
    private Button btnCancelRemove_;
    private Button btnRemove_;
    private LinearLayout fragmentTabView_;
    private TextView tvTitle_;

    private int m_nCurrentTabFragment = 0;						/* Current Tab Fragment */
    private int uiOptions_ = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    | View.SYSTEM_UI_FLAG_FULLSCREEN
    | View.SYSTEM_UI_FLAG_LOW_PROFILE
    | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private void createCrashlytics () {
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier("Woohj");
        Crashlytics.setUserName("Woohj");
        Crashlytics.setUserEmail("Woohj0623@commax.com");
    }

    /* Set Dialer Option Migration */
    private void setDialerOptionMigration() {
        //old name, new name, default
        final String keys[][] = {{NameSpace.KEY_PSTN, "false"},
                {NameSpace.KEY_NEIG, "false"},
                {NameSpace.KEY_EXT, "false"},
                {NameSpace.KEY_DOOR, "false"},
                {NameSpace.KEY_GUARD, "false"},
                {NameSpace.KEY_OFFICE, "false"},
                {NameSpace.KEY_TYPE, NameSpace.KEY_PSTN}};

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(NameSpace.SETTING_FILE_PATH);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Properties prop = new Properties();
        try {
            prop.load(inputStream);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        for(int i = 0; i < keys.length; i++) {
            String key = keys[i][0];
            String value = prefs.getString(key, "");
            if(!(value.length() > 0)) {
                value = prop.getProperty(key);
                if(value == null)
                    value = keys[i][1];

                editor.putString(key, value);
            }
        }
        editor.commit();

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        createCrashlytics ();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions_);
        getWindow().setUiOptions(uiOptions_);

		/* Set Dialer Option Migration */
        setDialerOptionMigration();

		/* Dialer Fragment */
        if(m_DialerFragment == null)
            m_DialerFragment = new DialerFragment();
		/* Call Log Fragment */
        if(m_CallLogFragment == null){
            m_CallLogFragment = new CallLogFragment();
            m_CallLogFragment.setFragmentListener(this);
        }
		/* Contacts Fragment */
        if(m_ContactsFragment == null) {
            m_ContactsFragment = new ContactsFragment();
        }

        ((ImageView)findViewById(R.id.imgBackButton)).setOnClickListener(this);
        ((ImageView)findViewById(R.id.imgBackButton)).setOnTouchListener(this);

        tvDiarerFragment_ = (TextView) findViewById(R.id.txtDialerButton);
        tvCallLogFragment_ = (TextView) findViewById(R.id.txtCallLogButton);
        tvContactsFragment_ = (TextView) findViewById(R.id.txtContactButton);

        tvDiarerFragment_.setOnClickListener(this);
        tvCallLogFragment_.setOnClickListener(this);
        tvContactsFragment_.setOnClickListener(this);
        tvContactsFragment_.setVisibility(View.GONE);

        layoutTopMenu_ = (FrameLayout) findViewById(R.id.topMenu);
        fragmentTabView_ = (LinearLayout) findViewById(R.id.fragmentTabView);
        imgBack_ = (ImageView) findViewById(R.id.imgBackButton);
        imgBack_.setOnClickListener(this);
        imgChanegeRemoveMode_ = (ImageView) findViewById(R.id.imgChangeRemoveMode);
        imgChanegeRemoveMode_.setOnClickListener(this);
        imgChanegeRemoveMode_.setVisibility(View.GONE);
        imgContactsAddUser_ = (ImageView) findViewById(R.id.imgContactsAddUser);
        imgContactsAddUser_.setVisibility(View.GONE);
        imgContactsAddUser_.setOnClickListener(this);
        btnCancelRemove_ = (Button) findViewById(R.id.btnCancelRemove);
        btnCancelRemove_.setOnClickListener(this);
        btnCancelRemove_.setVisibility(View.GONE);
        btnRemove_ = (Button) findViewById(R.id.btnRemove);
        btnRemove_.setOnClickListener(this);
        btnRemove_.setVisibility(View.GONE);
        tvTitle_ = (TextView) findViewById(R.id.tvTitle);

        Intent intent = getIntent();
        if((intent != null) && (intent.getAction() != null) && (savedInstanceState == null)) {
            if(intent.getAction().equals(NameSpace.STRING_TAB_CALLLOG_FRAGMENT)) {
                setSelectTabFragment(NameSpace.TAB_CALLLOG_FRAGMENT);
            }
            else if(intent.getAction().equals(NameSpace.STRING_TAB_CONTACTS_FRAGMENT)) {
                setSelectTabFragment(NameSpace.TAB_CONTACTS_FRAGMENT);
            }
            else {
                if (intent.getExtras() != null) {
                    String callLogFragment = intent.getExtras().getString("setFragment");
                    if (callLogFragment != null) {
                        if (callLogFragment.equals("CallLog"))
                            setSelectTabFragment(NameSpace.TAB_CALLLOG_FRAGMENT);
                    }
                    else
                        setSelectTabFragment(NameSpace.TAB_DIALER_FRAGMENT);
                }else
                    setSelectTabFragment(NameSpace.TAB_DIALER_FRAGMENT);
            }
        }
        else {
            if (intent.getExtras() != null) {
                String callLogFragment = intent.getExtras().getString("setFragment");
                if (callLogFragment != null) {
                    if (callLogFragment.equals("CallLog"))
                        setSelectTabFragment(NameSpace.TAB_CALLLOG_FRAGMENT);
                }
                else
                    setSelectTabFragment(NameSpace.TAB_DIALER_FRAGMENT);
            }else
                setSelectTabFragment(NameSpace.TAB_DIALER_FRAGMENT);
        }
		/* Recreate Flag */
        m_bReCreateFlag = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions_);
        getWindow().setUiOptions(uiOptions_);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: v.setAlpha(0.6f); break;
            case MotionEvent.ACTION_MOVE: v.setAlpha(0.6f); break;
            case MotionEvent.ACTION_UP: v.setAlpha(1.0f); break;
            default: v.setAlpha(1.0f); break;
        }

        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgBackButton :
                finish();
                break;
            case R.id.txtDialerButton :
                setSelectTabFragment(NameSpace.TAB_DIALER_FRAGMENT);
                break;
            case R.id.txtCallLogButton :
                setSelectTabFragment(NameSpace.TAB_CALLLOG_FRAGMENT);
                break;
            case R.id.txtContactButton :
                setSelectTabFragment(NameSpace.TAB_CONTACTS_FRAGMENT);
                break;
            case R.id.imgChangeRemoveMode :
                if (m_nCurrentTabFragment == NameSpace.TAB_CALLLOG_FRAGMENT) {
                    m_CallLogFragment.changeUiToDeleteMode();

                    fragmentTabView_.setVisibility(View.GONE);
                    layoutTopMenu_.setBackgroundColor(Color.WHITE);
                    imgChanegeRemoveMode_.setVisibility(View.GONE);
                    btnCancelRemove_.setVisibility(View.VISIBLE);
                    imgBack_.setVisibility(View.GONE);
                }
                else if (m_nCurrentTabFragment == NameSpace.TAB_CONTACTS_FRAGMENT) {
                    m_ContactsFragment.changeUiToDeleteMode();
                    layoutTopMenu_.setBackgroundColor(Color.WHITE);
                }
                break;
            case R.id.btnCancelRemove :
                if (m_nCurrentTabFragment == NameSpace.TAB_CALLLOG_FRAGMENT) {
                    m_CallLogFragment.changeUiToNormalMode();
                    fragmentTabView_.setVisibility(View.VISIBLE);
                    layoutTopMenu_.setBackground(getDrawable(R.mipmap.bg_title_1depth));
                    imgChanegeRemoveMode_.setVisibility(View.VISIBLE);
                    btnCancelRemove_.setVisibility(View.GONE);
                    imgBack_.setVisibility(View.VISIBLE);
                    btnRemove_.setVisibility(View.GONE);
                }
                else if (m_nCurrentTabFragment == NameSpace.TAB_CONTACTS_FRAGMENT) {
                    m_ContactsFragment.changeUiToNormalMode();
                    layoutTopMenu_.setBackground(getDrawable(R.mipmap.bg_title_1depth));
                    imgChanegeRemoveMode_.setVisibility(View.VISIBLE);
                    btnCancelRemove_.setVisibility(View.GONE);
                    imgBack_.setVisibility(View.VISIBLE);
                    btnRemove_.setVisibility(View.GONE);
                }
                break;
            case R.id.btnRemove :
                if (m_nCurrentTabFragment == NameSpace.TAB_CALLLOG_FRAGMENT) {
                    m_CallLogFragment.removeSelectLog();
                }
                else if (m_nCurrentTabFragment == NameSpace.TAB_CONTACTS_FRAGMENT) {
                }
                break;
            case R.id.imgContactsAddUser :
                if (m_nCurrentTabFragment == NameSpace.TAB_CONTACTS_FRAGMENT) {

                }
        }
    }

    private void setSelectTabFragment(int nSelectTabFragment) {
		/* Current Tab Fragment */
        if(m_nCurrentTabFragment == nSelectTabFragment)
            return;

		/* Get Missed Call Count */
        if(getMissedCallCount() > 0) {
			/* Current Tab Fragment(Tab Call Log Fragment) */
            m_nCurrentTabFragment = NameSpace.TAB_CALLLOG_FRAGMENT;
        }

		/* Current Tab Fragment */
        switch(nSelectTabFragment) {
		/* Tab Dialer Fragment */
            case NameSpace.TAB_DIALER_FRAGMENT:
                tvDiarerFragment_.setSelected(true);
                tvCallLogFragment_.setSelected(false);
                tvContactsFragment_.setSelected(false);

                tvDiarerFragment_.setTextColor(0xFFFFFFFF);
                tvCallLogFragment_.setTextColor(0x80FFFFFF);
                tvContactsFragment_.setTextColor(0x80FFFFFF);

                imgChanegeRemoveMode_.setVisibility(View.GONE);
                imgContactsAddUser_.setVisibility(View.GONE);
			/* Dialer Fragment */
                getSupportFragmentManager().beginTransaction().replace(R.id.layContextFrame, m_DialerFragment).commit();
                break;
		/* Tab Call Log Fragment */
            case NameSpace.TAB_CALLLOG_FRAGMENT:
                tvDiarerFragment_.setSelected(false);
                tvCallLogFragment_.setSelected(true);
                tvContactsFragment_.setSelected(false);

                tvDiarerFragment_.setTextColor(0x80FFFFFF);
                tvCallLogFragment_.setTextColor(0xFFFFFFFF);
                tvContactsFragment_.setTextColor(0x80FFFFFF);

                imgChanegeRemoveMode_.setVisibility(View.VISIBLE);
                imgContactsAddUser_.setVisibility(View.GONE);
			/* Call Log Fragment */
                getSupportFragmentManager().beginTransaction().replace(R.id.layContextFrame, m_CallLogFragment).commit();
                break;
		/* Tab Contacts Fragment */
            case NameSpace.TAB_CONTACTS_FRAGMENT:
                tvDiarerFragment_.setSelected(false);
                tvCallLogFragment_.setSelected(false);
                tvContactsFragment_.setSelected(true);

                tvDiarerFragment_.setTextColor(0x80FFFFFF);
                tvCallLogFragment_.setTextColor(0x80FFFFFF);
                tvContactsFragment_.setTextColor(0xFFFFFFFF);

                imgChanegeRemoveMode_.setVisibility(View.VISIBLE);
                imgContactsAddUser_.setVisibility(View.VISIBLE);
			/* Contacts Fragment */
                getSupportFragmentManager().beginTransaction().replace(R.id.layContextFrame, m_ContactsFragment).commit();
                break;
        }

		/* Current Tab Fragment */
        m_nCurrentTabFragment = nSelectTabFragment;
    }

    /* Get Missed Call Count */
    public int getMissedCallCount() {
        final Cursor missedCallCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.NEW + " = ?",
                new String[] { Integer.toString(CallLog.Calls.MISSED_TYPE), "1" }, CallLog.Calls.DATE + " DESC ");

        final int count = missedCallCursor.getCount();
        missedCallCursor.close();

        return count;
    }

    @Override
    public void confirmDeleteItem(int fragmentId) {
        switch (fragmentId) {
            case NameSpace.TAB_CALLLOG_FRAGMENT :
                m_CallLogFragment.changeUiToNormalMode();
                fragmentTabView_.setVisibility(View.VISIBLE);
                layoutTopMenu_.setBackground(getDrawable(R.mipmap.bg_title_1depth));
                imgChanegeRemoveMode_.setVisibility(View.VISIBLE);
                btnCancelRemove_.setVisibility(View.GONE);
                imgBack_.setVisibility(View.VISIBLE);
                btnRemove_.setVisibility(View.GONE);
                break;
            case NameSpace.TAB_CONTACTS_FRAGMENT:
                m_ContactsFragment.changeUiToNormalMode();
                layoutTopMenu_.setBackground(getDrawable(R.mipmap.bg_title_1depth));
                imgChanegeRemoveMode_.setVisibility(View.VISIBLE);
                btnCancelRemove_.setVisibility(View.GONE);
                imgBack_.setVisibility(View.VISIBLE);
                btnRemove_.setVisibility(View.GONE);
                break;
        }
    }
}
