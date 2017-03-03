package com.commax.settings.call;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static com.commax.settings.R.string.callTimeUnit_min;
import static com.commax.settings.R.string.callTimeUnit_sec;

/**
 * 코맥스 벨소리 설정 관리
 * Created by bagjeong-gyu on 2016. 10. 4..
 */


public class CallTimeSettingManager {

    public static final int INDEX_CALLTIME = 1; //연속통화시간
    public static final int INDEX_MOVIE_RECORDTIME = 2; //영상녹화시간

    private final Context mContext;

    private static CallTimeSettingManager instance;

    private String callTime_Table[][] = {
            // table name, value
            {"30", "30"},
            {"1", "60"},
            {"2", "120"},
            {"3", "180"}
    };

    private String moveRecodeTime_Table[][] = {
            // table name, value
            {"10", "10"},
            {"30", "30"},
            {"60", "60"}
    };


    private CallTimeSettingManager(Context context) {
        mContext = context;
    }

    public static CallTimeSettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new CallTimeSettingManager(context);
        }
        return instance;
    }


    /**
     * Calltime 리스트 데이터 생성
     *
     * @return
     */
    public List<CallTimeData> getCallTimeList() {
        List<CallTimeData> datas = new ArrayList<>();
        String itemname;
        CallTimeData timedata = null;

        for (int i = 0; i < callTime_Table.length; i++) {
            timedata = new CallTimeData();
            if(i==0) {
                itemname = String.format("%s %s", callTime_Table[i][0],mContext.getString(callTimeUnit_sec));
            } else {
                itemname = String.format("%s %s", callTime_Table[i][0],mContext.getString(callTimeUnit_min));
            }
            timedata.setName(itemname);
            timedata.setValue(callTime_Table[i][1]);
            datas.add(timedata);
        }

        return datas;
    }

    /**
     * VideoRecordTime 리스트 데이터 생성
     *
     * @return
     */
    public List<CallTimeData> getVideoRecordTimeList() {
        List<CallTimeData> datas = new ArrayList<>();
        String itemname;
        CallTimeData timedata = null;

        for (int i = 0; i < moveRecodeTime_Table.length; i++) {
            timedata = new CallTimeData();
            itemname = String.format("%s %s", moveRecodeTime_Table[i][0],mContext.getString(callTimeUnit_sec));
            timedata.setName(itemname);
            timedata.setValue(moveRecodeTime_Table[i][1]);
            datas.add(timedata);
        }

        return datas;
    }

    /**
     * 리스트에서 선택 데이터 검색
     */
    public int getListIndex(String value, int callType) {

        int index= 0;

        if(callType == INDEX_CALLTIME) {
            for (int i = 0; i < callTime_Table.length; i++) {
                if(value.equalsIgnoreCase(callTime_Table[i][1])) {
                    index = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < moveRecodeTime_Table.length; i++) {
                if(value.equalsIgnoreCase(moveRecodeTime_Table[i][1])) {
                    index = i;
                    break;
                }
            }
        }

        return index;
    }

    /**
     * 리스트에서 선택 데이터 문자열 변환
     */
    public String getListString(String value, int callType) {

        String getTime=null;

        if(callType == INDEX_CALLTIME) {
            for (int i = 0; i < callTime_Table.length; i++) {
                if(value.equalsIgnoreCase(callTime_Table[i][1])) {
                    getTime = String.format("%s %s", callTime_Table[i][0], i==0 ? mContext.getString(callTimeUnit_sec):mContext.getString(callTimeUnit_min));
                    break;
                }
            }
        } else {
            for (int i = 0; i < moveRecodeTime_Table.length; i++) {
                if(value.equalsIgnoreCase(moveRecodeTime_Table[i][1])) {
                    getTime = String.format("%s %s", moveRecodeTime_Table[i][0], mContext.getString(callTimeUnit_sec));
                    break;
                }
            }
        }

        return getTime;
    }

}
