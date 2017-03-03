package com.commax.settings.wallpad_info;

import android.content.Context;

import java.util.ArrayList;

/**
 * 코맥스 앱 Map => 불필요(추후삭제)
 * Created by bagjeong-gyu on 2016. 9. 7..
 */
public class CommaxAppMap {


    //액티비티 명
    private static String ACTIVITY_DELIVERY = "com.commax.webappbase.MainActivityDelivery";
    private static String ACTIVITY_EMERGENCY_LOG = "com.commax.webappbase.MainActivityEmergencylog";
    private static String ACTIVITY_CAR_LOG = "com.commax.webappbase.MainActivityCarlog";
    private static String ACTIVITY_NOTICE = "com.commax.webappbase.MainActivityNotice";
    private static String ACTIVITY_VOTE = "com.commax.webappbase.MainActivityVote";
    private static String ACTIVITY_LOBBY_PASSWORD = "com.commax.webappbase.MainActivityLobbypw";
    private static String ACTIVITY_HOME_IOT = "com.commax.homeiot.IfRunActivity";
    private static String ACTIVITY_ONE_PASS = "com.commax.webappbase.MainActivityOnepass";
    private static String ACTIVITY_PARKING = "com.commax.webappbase.MainActivityParking";
    private static String ACTIVITY_AFTER_SERVICE = "com.commax.webappbase.MainActivityAfterservice";
    private static String ACTIVITY_MANUAL = "com.commax.webappbase.MainActivityManual";
    private static String ACTIVITY_SURROUNDINGS = "com.commax.webappbase.MainActivitySurroundings";
    private static String ACTIVITY_LOGIN = "com.commax.login.MainActivity";
    private static String ACTIVITY_ELEVATOR = "com.commax.elevator.Main";
    private static String ACTIVITY_CCTV = "com.commax.CCTVViewer.CCTVViewer";
    private static String ACTIVITY_VISITOR = "com.commax.pxdsamplegallery.GalleryActivity";
    private static String ACTIVITY_SETTINGS = "com.commax.settings.MainActivity";//"com.commax.settings.Settings";
    private static String ACTIVITY_ENERGY_MONITORING = "com.commax.pxdemsmonitor.MainActivity";
    private static String ACTIVITY_SECURITY = "com.commax.security.EmergencyOccurView";
    private static String ACTIVITY_PHONE = "com.commax.dialer.MainActivity";
    private static String ACTIVITY_CONTACTS = "com.commax.dialer.ContactsActivity";
    private static String ACTIVITY_CALLLOG = "com.commax.dialer.CallLogActivity";
    private static String ACTIVITY_FRONT_DOOR = "com.commax.phonedoor.PhoneDoor";
    private static String ACTIVITY_GUARD = "com.commax.phoneguard.PhoneGuard";
    private static String ACTIVITY_SMART_DOOR_PHONE = "com.commax.smartdoorphone.lottetower.MainActivity";
    // private static String ACTIVITY__CALCULATOR = "com.android.calculator2.Calculator";
    private static String ACTIVITY_RECORDER = "com.commax.soundrecorder.MainActivity";
    // private static String ACTIVITY_DESKCLOCK = "com.android.deskclock.DeskClock";
    private static String ACTIVITY_VIDEO_RECORD = "com.commax.videorecord.MainActivity";
    private static String ACTIVITY_EVACUATION = "com.commax.webappbase.MainActivityEvacuation";
    private static String ACTIVITY_NEW_SETTING = "com.commax.newsetting.MainActivity";
    private static String ACTIVITY_NEW_CCTV = "com.commax.yuri.cctv.MainActivity";


    private static ArrayList<String> activities;

    private static Context mContext;


    static void init(Context context) {
        mContext = context;

        createActivityList();

    }


    /**
     * 액티비티명 리스트 생성
     */
    static private void createActivityList() {
        activities = new ArrayList<String>();
        activities.add(ACTIVITY_CCTV);
        activities.add(ACTIVITY_VISITOR);
        activities.add(ACTIVITY_SETTINGS);
        activities.add(ACTIVITY_ENERGY_MONITORING);
        activities.add(ACTIVITY_SECURITY);
        activities.add(ACTIVITY_PHONE);
        activities.add(ACTIVITY_CONTACTS);
        activities.add(ACTIVITY_CALLLOG);
        activities.add(ACTIVITY_FRONT_DOOR);
        activities.add(ACTIVITY_GUARD);
        activities.add(ACTIVITY_SMART_DOOR_PHONE);
        activities.add(ACTIVITY_DELIVERY);
        activities.add(ACTIVITY_EMERGENCY_LOG);
        activities.add(ACTIVITY_CAR_LOG);
        activities.add(ACTIVITY_NOTICE);
        activities.add(ACTIVITY_VOTE);
        activities.add(ACTIVITY_LOBBY_PASSWORD);
        activities.add(ACTIVITY_HOME_IOT);
        activities.add(ACTIVITY_ONE_PASS);
        activities.add(ACTIVITY_PARKING);
        activities.add(ACTIVITY_AFTER_SERVICE);
        activities.add(ACTIVITY_MANUAL);
        activities.add(ACTIVITY_SURROUNDINGS);
        activities.add(ACTIVITY_LOGIN);
        activities.add(ACTIVITY_ELEVATOR);
        //activities.add(ACTIVITY__CALCULATOR);
        activities.add(ACTIVITY_RECORDER);
        //activities.add(ACTIVITY_DESKCLOCK);
        activities.add(ACTIVITY_VIDEO_RECORD);
        activities.add(ACTIVITY_EVACUATION);
        activities.add(ACTIVITY_NEW_SETTING);
        activities.add(ACTIVITY_NEW_CCTV);


    }


    /**
     * 액티비티 명을 포함하는지 체크
     *
     * @param activityName
     * @return
     */
    public static boolean containsActivity(String activityName) {
        return activities.contains(activityName);
    }


}
