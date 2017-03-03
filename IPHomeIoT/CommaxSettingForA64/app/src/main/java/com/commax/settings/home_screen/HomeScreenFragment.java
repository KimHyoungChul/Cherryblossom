package com.commax.settings.home_screen;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.call.CallSettingFragment;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.util.PlusClickGuard;

/**
 * 홈 선택 화면
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class HomeScreenFragment extends Fragment {

    private static final String LOG_TAG = CallSettingFragment.class.getSimpleName();

    private static final String BASICTHEME_LAUNCHER = "com.commax.basictheme"; //베이직테마
    private static final String SETCONTROL_LAUNCHER = "com.commax.wirelesssetcontrol"; //SetControl 테마


    public HomeScreenFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addMenuItemListener();

        addSwitchListener();
        initSwitch();

    }

    @Override
    public void onResume() {
        super.onResume();
        showDefaultLauncher();
    }

    /**
     * 메뉴 항목 클릭 리스너 추가
     */
    private void addMenuItemListener() {
        //런처 설정
        RelativeLayout homeScreenTheme = (RelativeLayout) getActivity().findViewById(R.id.homeScreenTheme);
        homeScreenTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                //기본 환경설정 홈테마 선택화면을 띄우는 것이 바람직해보임
                showChooseLauncherNativeScreen();

                //"홈 앱으로 사용 항상"을 선택해야 해당 런처 패키지명을 가져옴
                //런처를 선택하면 해당 런처가 실행되는 구조임
                //showChooseLauncherPopup();
            }
        });

    }

    private void initSwitch() {

        if(TypeDef.DISPLAY_HOMESCREEN_TIP_ENABLE) {
            String useValueTip = ContentProviderManager.getUseValueTip(getActivity());
            CheckBox useValueTipSwitch = (CheckBox) getActivity().findViewById(R.id.helpTipSwitch);
            useValueTipSwitch.setChecked(useValueTip != null ? useValueTip.equals(CommaxConstants.TRUE) ? true : false : true);
        }
    }


    private void addSwitchListener() {

        //홈스크린도움말 사용여부
            if(TypeDef.DISPLAY_HOMESCREEN_TIP_ENABLE) {
            CheckBox helpTipSwitch = (CheckBox) getActivity().findViewById(R.id.helpTipSwitch);

                helpTipSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    handleHomescreenTipSwitch(isChecked);
                    sendHomescreenTipBroadcast();
                }
            });
        } else {
            RelativeLayout item_deviceServerIp = (RelativeLayout) getActivity().findViewById(R.id.homescreenTip);
            item_deviceServerIp.removeAllViews();
            item_deviceServerIp.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.homescreenTip_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }
    }

    /**
     * 홈스크린도움말 체크 처리
     *
     * @param isChecked
     */
    private void handleHomescreenTipSwitch(boolean isChecked) {

        String value = isChecked ? CommaxConstants.TRUE : CommaxConstants.FALSE;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.UseValueTipEntry.COLUMN_NAME_USE_VALUE_TIP, value);

        //Log.d(LOG_TAG, "handleHomescreenTipSwitch " + value);
        ContentProviderManager.deletePreviousUseValueTip(getActivity());
        ContentProviderManager.saveUseValueTip(getActivity(), contentValues);

    }

    /**
     * 홈스크린도움말 브로드캐스트 전송
     */
    private void sendHomescreenTipBroadcast() {
        String useValueTip = ContentProviderManager.getUseValueTip(getActivity());

        Log.d(LOG_TAG, "sendHomescreenTipBroadcast " + useValueTip);

        Intent intent = new Intent(CommaxConstants.BROADCAST_USE_VALUE_TIP);
        intent.putExtra(CommaxConstants.KEY_USE_VALUE_TIP, useValueTip);
        getActivity().sendBroadcast(intent);
    }

    /**
     * 기본 안드로이드 설정앱의 홈 테마 선택화면 표시
     */
    private void showChooseLauncherNativeScreen() {
        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
        startActivity(intent);

    }

    /**
     * 런처 선택 팝업창 표시
     */
    private void showChooseLauncherPopup() {

        PackageManager p = getActivity().getPackageManager();
        ComponentName cN = new ComponentName(getActivity(), FakeHome.class);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(selector);

        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }


    /**
     * 디폴트 런처 표시
     */
    private void showDefaultLauncher() {
        //론처를 항상사용하기로 선택해야 패키지명이 표시됨
        //디폴트값은 "android"

        String launcherPackageName = getLauncherPackageName();

        if (launcherPackageName == null) {
            return;
        }

        String launcherName = null;
        if (launcherPackageName.equals(BASICTHEME_LAUNCHER)) {
            launcherName = "Basic Theme";
        } else if (launcherPackageName.equals(SETCONTROL_LAUNCHER)) {
            launcherName = "SetControl";
        } else {
            launcherName = "Android";
        }

        TextView launcherValue = (TextView) getActivity().findViewById(R.id.homeScreenThemeValue);
        launcherValue.setText(launcherName);

    }

    /**
     * 디폴트 런처 패키지 명 가져옴
     *
     * @return
     */
    private String getLauncherPackageName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getActivity().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }


}
