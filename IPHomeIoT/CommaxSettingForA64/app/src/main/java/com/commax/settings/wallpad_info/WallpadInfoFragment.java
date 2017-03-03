package com.commax.settings.wallpad_info;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.setting_provider.Product;
import com.commax.settings.util.PlusClickGuard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 세대기 정보 설정화면
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class WallpadInfoFragment extends Fragment {


    private static final String LOG_TAG = WallpadInfoFragment.class.getSimpleName();
    private static final String FILENAME_PROC_VERSION = "/proc/version";

    public WallpadInfoFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallpad_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addMenuItemListener();
        showDeviceInfo();
    }

    private void showDeviceInfo() {

        //시스템 버전
        TextView SystemVersion = (TextView) getActivity().findViewById(R.id.systemVersionValue);
        SystemVersion.setText(getFormattedSystemVersion()); //todo

        //부트로더버전(todo)
        TextView loaderVersion = (TextView) getActivity().findViewById(R.id.bootloaderVersionValue);
        loaderVersion.setText(Product.getLoaderValue());

        //커널 버전
        TextView kernelVersion = (TextView) getActivity().findViewById(R.id.kernelVersionValue);
        kernelVersion.setText(getFormattedKernelVersion());

        //빌드번호
        TextView buildNoValue = (TextView) getActivity().findViewById(R.id.buildNoValue);
        buildNoValue.setText(Build.DISPLAY);

        //안드로이드 버전
        TextView androidVersionValue = (TextView) getActivity().findViewById(R.id.androidVersionValue);
        androidVersionValue.setText(Build.VERSION.RELEASE); //2.3.3과 같은 값 반환
    }

    /**
     * 메뉴 항목 클릭 리스너 추가
     */
    private void addMenuItemListener() {
        //애플리케이션
        RelativeLayout application = (RelativeLayout) getActivity().findViewById(R.id.application);
        application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                launchCommaxApplication();
            }
        });

        //태블릿 정보
        RelativeLayout tabletInfo = (RelativeLayout) getActivity().findViewById(R.id.tabletInfo);
        if(TypeDef.DISPLAY_TABLET_INFO_ENABLE) {
            tabletInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchTabletInfo();
                }
            });
        } else {
            tabletInfo.setVisibility(View.GONE); //숨김
        }

        //월패드 업데이트
        RelativeLayout wallpadUpdate = (RelativeLayout) getActivity().findViewById(R.id.wallpadUpdate);
        if(TypeDef.DISPLAY_UPDATE_INFO_ENABLE) {
            wallpadUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    Toast.makeText(getActivity(), "wallpadUpdate 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            wallpadUpdate.setVisibility(View.GONE); //숨김
        }


    }

    /**
     * 네이티브 환경설정 앱 태블릿 정보 표시
     */
    private void launchTabletInfo() {
        Intent intent = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
        startActivity(intent);
    }


    private void launchCommaxApplication() {

        Intent intent = new Intent(getActivity(), CommaxApplicationActivity.class);
        startActivity(intent);
    }

    public static String getFormattedSystemVersion() {

        //2017.01-06,yslee::시스템 정보 추가
        String SystemVersion = "CIOT-700M"; //todo
        String prodCode= "PROD=" + Product.getProdCode();
        String verCode= "VER=" + Product.getVerCode();

        try {
            SystemVersion = prodCode + "," + verCode;
        } catch (Exception e) {

        }

        return SystemVersion;
    }

    public static String getFormattedKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));

        } catch (IOException e) {
            Log.e(LOG_TAG,
                    "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "Unavailable";
        }
    }

    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
                "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                        "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                        "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                        "(#\\d+) " +              /* group 3: "#1" */
                        "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(LOG_TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(LOG_TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }


}
