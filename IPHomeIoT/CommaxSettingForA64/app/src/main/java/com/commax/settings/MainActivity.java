package com.commax.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.settings.call.CallSettingFragment;
import com.commax.settings.cctv.CctvRegistrationByOnVifFragment2;
import com.commax.settings.cctv.CctvRegistrationByOnvifFragment;
import com.commax.settings.common.BuyerID;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.data.InitDataFragment;
import com.commax.settings.device.InitDeviceFragment;
import com.commax.settings.doorphone_custom.CustomDoorCameraRegistrationFragment;
import com.commax.settings.doorphone_onvif.OnvifDoorphoneCameraRegistrationFragment;
import com.commax.settings.home_screen.HomeScreenFragment;
import com.commax.settings.ringtone.RingtoneFragment;
import com.commax.settings.screensaver.ScreensaverFragment;
import com.commax.settings.setting_provider.Symbol;
import com.commax.settings.user_account.UserAccountFragment;
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.wallpad.PasswordHandler;
import com.commax.settings.wallpad.WallpadSettingFragment;
import com.commax.settings.wallpad_info.WallpadInfoFragment;

import java.util.List;

import static com.commax.settings.R.id.fragmentContainer;

/**
 * 메인 액티비티
 */
public class MainActivity extends CommonActivity {

    private String LOG_TAG = this.getClass().getSimpleName();
    private BuyerID mBuyerID;

    View mPreviousSelectedView;
    int mLeftMenuDepth;

    static MainActivity _instance;
    public Button mDeleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Select Buyer ID */
        mBuyerID = new BuyerID();

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) { //2017-02-03,yslee::시스템용 2차 PXD UI적용
            setContentView(R.layout.activity_full_main);
        } else {
            setContentView(R.layout.activity_main);
        }

        /* init Screen */
        setFullScreen();
        setScreenTitle(getString(R.string.settingTitle));

        _instance = this;
        mLeftMenuDepth = 0;
        mDeleteBtn = (Button) findViewById(R.id.deleteBtn);
        addButtonListener();


        if(!TypeDef.DISPLAY_LOGIN_ENABLE) { //2017-01-13,yslee::메인메뉴에 회원가입 표시여부
            TextView menuuseraccount = (TextView) findViewById(R.id.menuUserAccount);
            menuuseraccount.setVisibility(View.GONE);

            if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
                RelativeLayout menuuseraccount_layout = (RelativeLayout) findViewById(R.id.menuUserAccount_layout);
                menuuseraccount_layout.setVisibility(View.GONE);
            }

        }

        if(!TypeDef.DISPLAY_TFR_ENABLE) { //2017-01-02,yslee::가치정보인트로 옵션처리
            TextView menuscreensaver = (TextView) findViewById(R.id.menuValueInfo);
            menuscreensaver.setVisibility(View.GONE);

            if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
                RelativeLayout menuscreensaver_layout = (RelativeLayout) findViewById(R.id.menuValueInfo_layout);
                menuscreensaver_layout.setVisibility(View.GONE);
            }
        }

        if(TypeDef.OP_PASSWORD_CHECK_ENABLE) {
            launchPasscodeCheckActivity(); //2017-01-10,yslee::패스워드 체크
        }

        if(getIntent().getStringExtra(CommaxConstants.KEY_FROM) == null) {

            if(!TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
                if (TypeDef.DISPLAY_LOGIN_ENABLE) { //2017-01-13,yslee::메인메뉴에 회원가입 표시여부
                    setUserAccountMenuSelected(); //default 탭
                } else {
                    setWallpadSettinMenuSelected(); //default 탭
                }
            }

        } else {

            //외부앱에서 필요한 설정탭으로 이동
            if( getIntent().getStringExtra(CommaxConstants.KEY_FROM).equals(CommaxConstants.FROM_DOORPHONE_SETTING) ) {

                //2017-01-09,yslee::설정앱의 도어폰 설정탭으로 바로 진입기능 추가
                TextView doorphoneRegistration = (TextView) findViewById(R.id.menuDoorphoneRegistration);
                handleDoorCameraRegistration(doorphoneRegistration);
                if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
                    mLeftMenuDepth = 0; //바로 종료
                }

            } else if( getIntent().getStringExtra(CommaxConstants.KEY_FROM).equals(CommaxConstants.FROM_CCTV_SETTING) ) {

                //CCTV 앱에서 설정을 누른 경우 CCTV 등록 탭으로 이동
                TextView cctvRegistration = (TextView) findViewById(R.id.menuCctvRegistration);
                handleCctvRegistration(cctvRegistration);
                if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
                    mLeftMenuDepth = 0; //바로 종료
                }

            } else {

            }
        }

    }

    /**
     * launchMode가 "singleTop"이고 해당 액티비티가 task의 맨상단에 있는 경우 새로운 instance를 생성하지 않고 onNewIntent가 호출됨
     * launchMode로 "singleTask"와 "singleInstance"는 사용하지 않기를 권고함
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        //CCTV 앱에서 설정을 누른 경우 CCTV 등록 탭으로 이동
        if(intent.getStringExtra(CommaxConstants.KEY_FROM).equals(CommaxConstants.FROM_CCTV_SETTING)) {

            TextView cctvRegistration = (TextView) findViewById(R.id.menuCctvRegistration);
            handleCctvRegistration(cctvRegistration);
        }
    }


    /*
     * 2017-01-10,yslee::패스워드 체크 결과 수신
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(TypeDef.OP_PASSWORD_CHECK_ENABLE) {

            if (requestCode == PasswordHandler.PROVIDER_INDEX) {
                if (resultCode == RESULT_OK) {
                    //enter = true;
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        int condition = data.getExtras().getInt(
                                PasscodeCheckActivity.KEY, -1);
                        if (condition == Symbol.VALID) {
                            return;
                        } else if (condition == PasscodeCheckActivity.ADMIN) {
                            //관리자 모드 진입
                            return;
                        } else {

                        }
                    }
                }

                finish(); //종료
                return;
            }

        }
    }

    public static MainActivity getInstance() {
        return _instance;
    }

    /**
     /* button에 대한 이벤트 처리
     */
    private void addButtonListener() {

        Button gobackbtn = (Button) findViewById(R.id.goBackBtn);
        gobackbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                PlusClickGuard.doIt(v);

                if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {

                    if(mLeftMenuDepth == 0) {
                        finish();
                    } else {
                        //메인메뉴로 이동함
                        showLeftMenuBar();
                    }

                } else {
                    finish();
                }
            }
        });


//        MainActivity.getInstance().mDeleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                Toast.makeText(getApplicationContext(), "버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    /**
     * 2017-01-10,yslee::패스워드 체크 액티비티로 이동
     */
    private void launchPasscodeCheckActivity() {
        Intent intent = new Intent(this, PasscodeCheckActivity.class);
        startActivityForResult(intent, PasswordHandler.PROVIDER_INDEX);
    }

    /**
     * 앱 처음 실행시 사용자 계정 항목이 디폴트로 선택되게 함
     */
    private void setUserAccountMenuSelected() {
        TextView menuUserAccount = (TextView) findViewById(R.id.menuUserAccount);
        handleUserAccount(menuUserAccount);

    }

    /**
     * 앱 처음 실행시 사용자 계정 항목이 디폴트로 선택되게 함
     */
    private void setWallpadSettinMenuSelected() {
        TextView menuUserAccount = (TextView) findViewById(R.id.menuWallpadSetting);
        handleWallpadSetting(menuUserAccount);

    }

    /**
     * Fragment 실행
     *
     * @param fragment
     */
    private void launchFragment(Fragment fragment) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(fragmentContainer, fragment);
        fragmentTransaction.commit();

    }

    /**
     * 메뉴바 표시
     *
     */
    private void showLeftMenuBar() {

        setScreenTitle(getString(R.string.settingTitle));

        LinearLayout fragmentcontainer = (LinearLayout) findViewById(R.id.fragmentContainer);
        fragmentcontainer.setVisibility(View.INVISIBLE);
        fragmentcontainer.removeAllViews();

        LinearLayout leftmenubar = (LinearLayout) findViewById(R.id.leftMenuBarBody);
        leftmenubar.setVisibility(View.VISIBLE);


        Button editBtn = (Button) findViewById(R.id.editBtn);
        editBtn.setVisibility(View.INVISIBLE);
        Button deleteBtn = (Button) findViewById(R.id.deleteBtn);
        deleteBtn.setVisibility(View.INVISIBLE);

        //int mSelectMenuItem = 0;
        mLeftMenuDepth = 0;

    }

    /**
     * 메뉴바 숨김
     *
     */
    private void hideLeftMenuBar() {

        LinearLayout leftmenubar = (LinearLayout) findViewById(R.id.leftMenuBarBody);
        leftmenubar.setVisibility(View.INVISIBLE);

        LinearLayout fragmentcontainer = (LinearLayout) findViewById(R.id.fragmentContainer);
        fragmentcontainer.setVisibility(View.VISIBLE);

        mLeftMenuDepth++;
    }



    /**
     * 이전 선택된 항목 비활성화
     *
     * @param view
     */
    private void handleViewSelect(View view) {

        if (mPreviousSelectedView != null) {
            mPreviousSelectedView.setSelected(false);
        }
        view.setSelected(true);

        mPreviousSelectedView = view;
    }


    /**
     * 앱이 실행 가능한지 체크
     *
     * @param intent
     * @return
     */
    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    /**
     * 홈스크린 설정화면
     *
     * @param view
     */
    public void setHomeScreen(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.homeSetting));
            hideLeftMenuBar();
            //handleViewSelect(view);

        } else {
            handleViewSelect(view);
            //홈 화면 지정은 안드로이드 내부 api사용
//        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
//        if (isAvailable(intent)) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "해당 설정 기능이 없습니다.", Toast.LENGTH_SHORT).show();
//        }

        }

        HomeScreenFragment fragment = new HomeScreenFragment();
        launchFragment(fragment);
    }

    /**
     * 월패드 설정
     *
     * @param view
     */
    public void handleWallpadSetting(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.wallpadSetting));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        WallpadSettingFragment fragment = new WallpadSettingFragment();
        launchFragment(fragment);
    }

    /**
     * 사용자 계정
     *
     * @param view
     */
    public void handleUserAccount(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.userAccount));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        UserAccountFragment fragment = new UserAccountFragment();
        launchFragment(fragment);

    }

    /**
     * 제어기기 연결 초기화
     *
     * @param view
     */
    public void initDevice(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.deviceInit));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        InitDeviceFragment fragment = new InitDeviceFragment();
        launchFragment(fragment);

    }

    /**
     * 저장 데이터 초기화
     *
     * @param view
     */
    public void initData(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.dataInit));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        InitDataFragment fragment = new InitDataFragment();
        launchFragment(fragment);

    }

    /**
     * 현관카메라 등록
     *
     * @param view
     */
    public void handleDoorCameraRegistration(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.doorphoneRegistration));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        if (TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE) {
            //커스텀 프로토콜 사용하여 도어폰 카메라 등록
            CustomDoorCameraRegistrationFragment fragment = new CustomDoorCameraRegistrationFragment();
            launchFragment(fragment);

        } else {
            //Onvif 프로토콜 사용하여 도어폰 카메라 등록
            OnvifDoorphoneCameraRegistrationFragment fragment = new OnvifDoorphoneCameraRegistrationFragment();
            launchFragment(fragment);

        }

    }

    /**
     * CCTV 등록
     *
     * @param view
     */
    public void handleCctvRegistration(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.cctvRegistration));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        if(TypeDef.OP_NEW_CCTV_SCANMODE_ENABLE) { //2017-02-07,yslee::CCTV Scan방법을 현관과 동일하게 변경
            CctvRegistrationByOnVifFragment2 fragment = new CctvRegistrationByOnVifFragment2();
            launchFragment(fragment);
        } else {
            CctvRegistrationByOnvifFragment fragment = new CctvRegistrationByOnvifFragment();
            launchFragment(fragment);
        }

    }

    /**
     * 통화설정
     *
     * @param view
     */
    public void handleCallSetting(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.callSetting));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        CallSettingFragment fragment = new CallSettingFragment();
        launchFragment(fragment);

    }

    /**
     * 가치정보
     *
     * @param view
     */
    public void setScreensaver(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.screensaverSetting));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        ScreensaverFragment fragment = new ScreensaverFragment();
        launchFragment(fragment);

    }

    /**
     * 벨소리
     *
     * @param view
     */
    public void setRingtone(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.ringtoneSetting));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        RingtoneFragment fragment = new RingtoneFragment();
        launchFragment(fragment);

    }

    /**
     * 세대기 정보
     *
     * @param view
     */
    public void showWallpadInfo(View view) {
        PlusClickGuard.doIt(view);

        if(TypeDef.OP_FULLSCREEN_TYPE_ENABLE) {
            setScreenTitle(getString(R.string.wallpadInfo));
            hideLeftMenuBar();
            //handleViewSelect(view);
        } else {
            handleViewSelect(view);
        }

        WallpadInfoFragment fragment = new WallpadInfoFragment();
        launchFragment(fragment);

    }


}
