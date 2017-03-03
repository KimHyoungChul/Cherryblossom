package com.commax.settings.data;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.content_provider.CommaxConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 저장 데이터 초기화
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class InitDataFragment extends Fragment implements InitDataListener {


    public InitDataFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_init_data, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addButtonListener();
    }

    private void addButtonListener() {
        Button init = (Button) getActivity().findViewById(R.id.init);
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlusClickGuard.doIt(v);
                //init();
                v.setEnabled(false);

                showDeletePopup(); //2017-01-06,yslee::데이터 초기화 확인팝업 추가
                //v.setEnabled(true);
            }
        });
    }


    // 확인 팝업 표시
    private void showDeletePopup() {
        InitDataPopup popup = new InitDataPopup(getActivity(), this);
        popup.show();
    }


    //저장 데이터 초기화 콜백
    @Override
    public void onInitData(boolean bconfirm)  {

        if(bconfirm) {
            init();
        }

        Button init = (Button) getActivity().findViewById(R.id.init);
        init.setEnabled(true);
    }


    //저장 데이터 초기화
    private void init() {
        //수정할 수 있음!!
        sendInitDataBroadcast();

        //통화기록 삭제
        delCallLog();

        //저장영상 삭제(방문객기록, 방문객이미지, 저장영상 데이터)
        delVisitorLog();

        //비상기록 삭제
        delEmergencyLog();

        Toast.makeText(getActivity(), R.string.dataInitResult, Toast.LENGTH_SHORT).show();
    }

    /**
     * 저장 데이터 초기화 브로드캐스트 전송
     */
    private void sendInitDataBroadcast() {
        Intent intent = new Intent(CommaxConstants.BROADCAST_INIT_DATA);
        getActivity().sendBroadcast(intent);
    }

    /**
     * 저장영상 삭제
     */
    private void delCallLog() {
        CallLogProviderClient calllogProvider = new CallLogProviderClient();
        calllogProvider.deleteLogAll(getActivity());
    }

    /**
     * 저장영상 삭제
     */
    private void delVisitorLog() {
        removeDir("cmxMediaGallery");
    }


    //파일 & 폴더 삭제
    public static void removeDir(String dirName) {
        String mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath()  + File.separator + dirName;

        File file = new File(mRootPath);
        File[] childFileList = file.listFiles();
        if(childFileList != null) {
            for (File childFile : childFileList) {
                if (childFile.isDirectory()) {
                    //removeDir(childFile.getAbsolutePath());    //하위 디렉토리
                } else {
                    childFile.delete();    //하위 파일
                }
            }
        }
        file.delete();    //root 삭제
    }

    /**
     * 비상기록 삭제
     */
    private void delEmergencyLog() {

        //비상기록 앱으로 삭제명령 보냄
        Intent intent = new Intent();
        intent.setClassName(CommaxConstants.PACKAGE_EMERGENCY, CommaxConstants.ACTIVITY_EMERGENCY);
        intent.putExtra(CommaxConstants.KEY_RESET, CommaxConstants.SEND_COMMAND_EMERGENCY_DEL);

        if (isAvailable(intent)) {
            //getActivity().startActivity(intent);
            String commandstr = "am start --user 0 -a android.intent.action.MAIN -n com.commax.recentemerlog/.MainActivity --es reset deleteEmergencyLog";

            try {
                Runtime runtime = Runtime.getRuntime();
                Process execute = runtime.getRuntime().exec(commandstr);

            } catch (IOException e) {
                e.printStackTrace();
            }

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
                mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}
