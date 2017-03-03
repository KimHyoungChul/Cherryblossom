package com.commax.settings.user_account;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.util.PlusClickGuard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 사용자 계정 설정 화면
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class UserAccountFragment extends Fragment {

    private String LOG_TAG = UserAccountFragment.class.getSimpleName();

    final String ACCOUNT_FILE_PATH = "mnt/sdcard/CMXdata/CreateAccount.properties";

    public UserAccountFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showUserId();
        addButtonListener();

    }

    private void addButtonListener() {

        Button signUp = (Button) getActivity().findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                launchJoinApp();

            }
        });
    }

    /**
     * 회원가입 앱 실행
     */
    private void launchJoinApp() {

        Intent intent = new Intent();
        intent.setClassName("com.commax.login", "com.commax.login.MainActivity");

        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 앱이 실행 가능한지 체크
     *
     * @param intent
     * @return
     */
    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getActivity().getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    /**
     * 사용자 계정 아이디 표시
     */
    private void showUserId() {
        TextView userIdView = (TextView) getActivity().findViewById(R.id.userId);

        String userId = getUserId();
        if (userId == null || userId.equals("")) {
            Toast.makeText(getActivity(), R.string.no_signup_id, Toast.LENGTH_SHORT).show();
            setButtonText(getString(R.string.signUp));
            return;
        }

        userIdView.setText(userId);
        setButtonText(getString(R.string.changeUserInfo));
    }

    private void setButtonText(String label) {
        Button signUp = (Button) getActivity().findViewById(R.id.signUp);
        signUp.setText(label);
    }

    /**
     * 사용자 계정 아이디 가져옴
     *
     * @return
     */
    private String getUserId() {

        //로그인 아이디가 아니라 회원가입 아이디 표시하기로 함

        return readAccountFile();

        //테스트용
        //return "goodsogi";
    }




    /**
     * 원래 코드
     * 사용자 계정 파일을 읽음
     *
     * @return
     */
    private String readAccountFile() {

        try {
            //openFileInput 메소드를 사용하면 path separator가 포함되어 있다는 오류 발생
            //openFileInput의 앱의 사적인 영역에 있는 파일만 오픈
            InputStream inputStream = new FileInputStream(new File(ACCOUNT_FILE_PATH));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";


                while ((receiveString = bufferedReader.readLine()) != null) {
                    receiveString = receiveString.trim();
                    String[] tokens = receiveString.split("=");
                    if (tokens[0].contains("id")) {

                        return tokens[1];
                    }


                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Can not read file: " + e.toString());
        }
        return "";
    }


}
