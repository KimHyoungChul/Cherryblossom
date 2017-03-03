package com.commax.settings.ringtone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.util.PlusClickGuard;

import java.util.List;

/**
 * 코맥스 벨소리 선택 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class CommaxRingtoneSelectPopup extends Dialog {


    private final Context mContext;
    private final CommaxRingtoneSelectListener mRingtoneSelectListener;
    private SoundPool mSoundPool;
    private int mSoundType;


    private List<CommaxRingtone> mDatas;
    private String mDefaultRingtoneFile;
    private int mStreamId;

    public CommaxRingtoneSelectPopup(Context context, CommaxRingtoneSelectListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mRingtoneSelectListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_sound_select);

        mStreamId = -1;

        addButtonListener();
    }

    /**
     * 팝업창 타이틀 설정
     *
     * @param title
     */
    public void setPopupTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.progress_title);
        titleView.setText(title);
    }

    /**
     * 디폴트 벨소리 파일명 설정
     *
     * @param fileName
     */
    public void setDefaultRingtoneFile(String fileName) {
        mDefaultRingtoneFile = fileName;
    }

    /**
     * 리스트 초기화
     *
     * @param context
     */
    public void initList(Context context) {
        ListView list = (ListView) findViewById(R.id.ringtoneList);

        CommaxRingtoneListAdapter adapter = new CommaxRingtoneListAdapter(context, R.layout.list_item_ringtone, mDatas);

        list.setAdapter(adapter);

        //Adapter에서 CheckableLinearLayout의 setChecked를 호출하면 효과가 없음. ListView.getCheckedItemPosition이 없어서 초기화됨
        //ListView의 setItemChecked사용해야 함
        list.setItemChecked(getDefaultRingtonePosition(), true);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //벨소리 재생
                //메모리 부족 문제(?)로 계속해서 mp3 파일을 재생하지 못하는 이슈 발생
                //재생이 끝난 다음에는 release하고 그 다음 SoundPool의 객체를 새로 생성하는 방법으로 해결
                //release는 resource만 해당되는 것이 아니라 SoundPool 객체를 제거함

                stopSoundPool();
                //SoundPool.Builder를 사용해야 하는데 이것은 sdk 21이상을 요구하므로 deprecated 생성자 사용하는 것이 좋음
                mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                final int soundId = mSoundPool.load("/user/app/bin/sound/" + mDatas.get(position).getFile(), 1);
                //setOnLoadCompleteListener 사용해야 sound가 재생됨
                mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int sampleId,
                                               int status) {
                        //볼륨 조절 파라미터는 없음
                        //1이 Max 볼륨인데 음악 스트림 볼륨이 작으면 똑같이 작음
                        mStreamId = soundPool.play(soundId, 1F, 1F, 1, -1, 1.0F);
                    }
                });


            }
        });


        //xml에서 지정해서 필요없음
        //list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    /**
     * 벨소리 재생 정지
     */
    private void stopSoundPool() {
        if (mStreamId != -1) {
            mSoundPool.stop(mStreamId);
            mSoundPool.release();
        }
    }

    /**
     * 리스트상에서 벨소리 위치 가져옴
     *
     * @return
     */
    private int getDefaultRingtonePosition() {
        int position = 0;
        CommaxRingtone ringtone = null;

        for (int i = 0; i < mDatas.size(); i++) {
            ringtone = mDatas.get(i);

            if (ringtone.getName() == null) {
                continue;
            }

            if (mDefaultRingtoneFile == null) {
                break;
            }

            if (mDefaultRingtoneFile.contains(ringtone.getName())) {
                position = i;
            }
        }


        return position;
    }


    /**
     * 벨소리 타입 설정
     *
     * @param soundType
     */
    public void setSoundType(int soundType) {
        mSoundType = soundType;
    }


    /**
     * 벨소리 데이터 설정
     *
     * @param datas
     */
    public void setSoundData(List<CommaxRingtone> datas) {
        mDatas = datas;

    }


    private void addButtonListener() {
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                stopSoundPool();

                dismiss();
            }
        });

        Button confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                stopSoundPool();
                String selectedFile = getSelectedRingtoneFile();

                if ((selectedFile==null) || selectedFile.equals("")) { //2017-01-17,yslee::벨소리 부재시 예외처리
                    Toast.makeText(mContext, R.string.fail_change_ringtone, Toast.LENGTH_SHORT).show();
                } else {
                    mRingtoneSelectListener.onCommaxRingtoneSelected(mSoundType, selectedFile);
                }
                dismiss();
            }
        });
    }


    /**
     * 사용자가 선택한 벨소리 파일명 가져옴
     *
     * @return
     */
    private String getSelectedRingtoneFile() {
        try {
            ListView list = (ListView) findViewById(R.id.ringtoneList);
            CommaxRingtoneListAdapter adapter = (CommaxRingtoneListAdapter) list.getAdapter();
            int checkedItemPosition = list.getCheckedItemPosition();

            CommaxRingtone ringtone = adapter.getItem(checkedItemPosition);
            return ringtone.getFile();
        } catch (Exception e) {
            String setstr = "";
            return setstr;
        }
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public CommaxRingtoneSelectPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


    /**
     * FocusChanged 될때 NavigationBar 숨김
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            try {
                // 액티비티 아래의 네비게이션 바가 안보이게
                final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                getWindow().getDecorView().setSystemUiVisibility(flags);
                final View decorView = getWindow().getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
