package com.commax.basictheme;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 앱 이름, 패키지명, 리소스 아이콘 관리
 * Created by bagjeong-gyu on 2016. 9. 7..
 */
public class AppInfoMap {

    //패키지명
    private static String PACKAGE_DELIVERY = "com.commax.webappbase";
    private static String PACKAGE_EMERGENCY_LOG = "com.commax.webappbase";
    private static String PACKAGE_CAR_LOG = "com.commax.webappbase";
    private static String PACKAGE_NOTICE = "com.commax.webappbase";
    private static String PACKAGE_VOTE = "com.commax.webappbase";
    private static String PACKAGE_LOBBY_PASSWORD = "com.commax.webappbase";
    private static String PACKAGE_HOME_IOT = "com.commax.homeiot";
    private static String PACKAGE_ONE_PASS = "com.commax.webappbase";
    private static String PACKAGE_PARKING = "com.commax.webappbase";
    private static String PACKAGE_AFTER_SERVICE = "com.commax.webappbase";
    private static String PACKAGE_MANUAL = "com.commax.webappbase";
    private static String PACKAGE_SURROUNDINGS = "com.commax.webappbase";
    private static String PACKAGE_LOGIN = "com.commax.login";
    private static String PACKAGE_ELEVATOR = "com.commax.elevator";
    private static String PACKAGE_CCTV = "com.commax.CCTVViewer";
    private static String PACKAGE_VISITOR = "com.commax.pxdsamplegallery";
    private static String PACKAGE_SETTINGS = "com.commax.settings";
    private static String PACKAGE_ENERGY_MONITORING = "com.commax.pxdemsmonitor";
    private static String PACKAGE_SECURITY = "com.commax.security";
    private static String PACKAGE_PHONE = "com.commax.dialer";
    private static String PACKAGE_CONTACTS = "com.commax.dialer";
    private static String PACKAGE_CALLLOG = "com.commax.dialer";
    private static String PACKAGE_FRONT_DOOR = "com.commax.phonedoor";
    private static String PACKAGE_GUARD = "com.commax.phoneguard";
    private static String PACKAGE_SMART_DOOR_PHONE = "com.commax.smartdoorphone.lottetower";
    private static String PACKAGE_CALCULATOR = "com.android.calculator2";
    private static String PACKAGE_RECORDER = "com.commax.soundrecorder";
    private static String PACKAGE_DESKCLOCK = "com.android.deskclock";
    private static String PACKAGE_VIDEO_RECORD = "com.commax.videorecord";
    private static String PACKAGE_EVACUATION = "com.commax.webappbase";
    private static String PACKAGE_NEW_SETTING = "com.commax.newsetting";
    private static String PACKAGE_NEW_CCTV = "com.commax.yuri.cctv";
    private static String PACKAGE_A64_CCTV = "commax.wallpad.cctvview";
    private static String PACKAGE_A64_VISITOR = "commax.wallpad.videoplayer";
    private static String PACKAGE_A64_DOOR = "com.commax.yuri.onvifservice";
    private static String PACKAGE_A64_CALLLOG = "com.commax.iphomiot.calllogview";
    private static String PACKAGE_A64_EMERLOG = "com.commax.recentemerlog";
    private static String PACKAGE_A64_SETTINGS = "com.commax.settings";
    private static String PACKAGE_A64_OS_SETTINGS = "com.android.settings";


    //화면 하단에 있는 앱이라서 제외
    //private static String PACKAGE_HOME_COACH = "com.commax.homecoach";
    //private static String PACKAGE_APP_LIST = "com.commax.applist";
    //private static String PACKAGE_CONTROL = "com.commax.control";
    //private static String PACKAGE_HOME_REPORTER = "com.commax.homereporter";
    //private static String PACKAGE_MODE = "com.commax.homeiot";

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
    private static String ACTIVITY_SETTINGS = "com.commax.settings.Settings";
    private static String ACTIVITY_ENERGY_MONITORING = "com.commax.pxdemsmonitor.MainActivity";
    private static String ACTIVITY_SECURITY = "com.commax.security.EmergencyOccurView";
    private static String ACTIVITY_PHONE = "com.commax.dialer.MainActivity";
    private static String ACTIVITY_CONTACTS = "com.commax.dialer.ContactsActivity";
    private static String ACTIVITY_CALLLOG = "com.commax.dialer.CallLogActivity";
    private static String ACTIVITY_FRONT_DOOR = "com.commax.phonedoor.PhoneDoor";
    private static String ACTIVITY_GUARD = "com.commax.phoneguard.PhoneGuard";
    private static String ACTIVITY_SMART_DOOR_PHONE = "com.commax.smartdoorphone.lottetower.MainActivity";
    private static String ACTIVITY__CALCULATOR = "com.android.calculator2.Calculator";
    private static String ACTIVITY_RECORDER = "com.commax.soundrecorder.MainActivity";
    private static String ACTIVITY_DESKCLOCK = "com.android.deskclock.DeskClock";
    private static String ACTIVITY_VIDEO_RECORD = "com.commax.videorecord.MainActivity";
    private static String ACTIVITY_EVACUATION = "com.commax.webappbase.MainActivityEvacuation";
    private static String ACTIVITY_NEW_SETTING = "com.commax.newsetting.MainActivity";
    private static String ACTIVITY_NEW_CCTV = "com.commax.yuri.cctv.MainActivity";
    private static String ACTIVITY_A64_CCTV = "commax.wallpad.cctvview.MainActivity";
    private static String ACTIVITY_A64_VISITOR = "commax.wallpad.videoplayer.MainActivity";
    private static String ACTIVITY_A64_DOOR = "com.commax.yuri.onvifservice.MainActivity";
    private static String ACTIVITY_A64_CALLLOG = "com.commax.iphomiot.calllogview.MainActivity";
    private static String ACTIVITY_A64_EMERLOG = "com.commax.recentemerlog.MainActivity";
    private static String ACTIVITY_A64_SETTINGS = "com.commax.settings.MainActivity";
    private static String ACTIVITY_A64_OS_SETTINGS = "com.android.settings.Settings";



     //화면 하단에 있는 앱이라서 제외
    //private static String ACTIVITY_MODE = "com.commax.homeiot.ui.MainActivity";
    //private static String ACTIVITY_HOME_COACH = "com.commax.homecoach.TipActivity";
    //private static String ACTIVITY_APP_LIST = "com.commax.applist.V2_MainActivity";
    //private static String ACTIVITY_CONTROL = "com.commax.control.MainActivity";
    //private static String ACTIVITY_HOME_REPORTER = "com.commax.homereporter.MainActivity";

    private static ArrayList<String> activities;
    private static HashMap<String, Integer> icons;
    private static HashMap<String, String> activityNames;
    private static HashMap<String, String> packages;
    private static Context mContext;


    static void init(Context context) {
        mContext = context;

        createActivityList();
        createPackageMap();
        createActivityNameMap();
        createAppLauncherIconMap();

    }

    /**
     * 패키지 맵 생성
     */
    private static void createPackageMap() {
        packages = new HashMap<>();
        packages.put(ACTIVITY_CCTV, PACKAGE_CCTV);
        packages.put(ACTIVITY_VISITOR, PACKAGE_VISITOR);
        packages.put(ACTIVITY_SETTINGS, PACKAGE_SETTINGS);
        packages.put(ACTIVITY_ENERGY_MONITORING, PACKAGE_ENERGY_MONITORING);
        packages.put(ACTIVITY_SECURITY, PACKAGE_SECURITY);
        packages.put(ACTIVITY_PHONE, PACKAGE_PHONE);
        packages.put(ACTIVITY_CONTACTS, PACKAGE_CONTACTS);
        packages.put(ACTIVITY_CALLLOG, PACKAGE_CALLLOG);
        packages.put(ACTIVITY_FRONT_DOOR, PACKAGE_FRONT_DOOR);
        packages.put(ACTIVITY_GUARD, PACKAGE_GUARD);
        packages.put(ACTIVITY_SMART_DOOR_PHONE, PACKAGE_SMART_DOOR_PHONE);
        packages.put(ACTIVITY_DELIVERY, PACKAGE_DELIVERY);
        packages.put(ACTIVITY_EMERGENCY_LOG, PACKAGE_EMERGENCY_LOG);
        packages.put(ACTIVITY_CAR_LOG, PACKAGE_CAR_LOG);
        packages.put(ACTIVITY_NOTICE, PACKAGE_NOTICE);
        packages.put(ACTIVITY_VOTE, PACKAGE_VOTE);
        packages.put(ACTIVITY_LOBBY_PASSWORD, PACKAGE_LOBBY_PASSWORD);
        packages.put(ACTIVITY_HOME_IOT, PACKAGE_HOME_IOT);
        packages.put(ACTIVITY_ONE_PASS, PACKAGE_ONE_PASS);
        packages.put(ACTIVITY_PARKING, PACKAGE_PARKING);
        packages.put(ACTIVITY_AFTER_SERVICE, PACKAGE_AFTER_SERVICE);
        packages.put(ACTIVITY_MANUAL, PACKAGE_MANUAL);
        packages.put(ACTIVITY_SURROUNDINGS, PACKAGE_SURROUNDINGS);
        packages.put(ACTIVITY_LOGIN, PACKAGE_LOGIN);
        packages.put(ACTIVITY_ELEVATOR, PACKAGE_ELEVATOR);
        packages.put(ACTIVITY__CALCULATOR, PACKAGE_CALCULATOR);
        packages.put(ACTIVITY_RECORDER, PACKAGE_RECORDER);
        packages.put(ACTIVITY_DESKCLOCK, PACKAGE_DESKCLOCK);
        packages.put(ACTIVITY_VIDEO_RECORD, PACKAGE_VIDEO_RECORD);
        packages.put(ACTIVITY_EVACUATION, PACKAGE_EVACUATION);
        packages.put(ACTIVITY_NEW_SETTING, PACKAGE_NEW_SETTING);
        packages.put(ACTIVITY_NEW_CCTV, PACKAGE_NEW_CCTV);
        packages.put(ACTIVITY_A64_CCTV, PACKAGE_A64_CCTV);
        packages.put(ACTIVITY_A64_VISITOR, PACKAGE_A64_VISITOR);
        packages.put(ACTIVITY_A64_DOOR, PACKAGE_A64_DOOR);
        packages.put(ACTIVITY_A64_CALLLOG, PACKAGE_A64_CALLLOG);
        packages.put(ACTIVITY_A64_EMERLOG, PACKAGE_A64_EMERLOG);
        packages.put(ACTIVITY_A64_SETTINGS, PACKAGE_A64_SETTINGS);
        packages.put(ACTIVITY_A64_OS_SETTINGS, PACKAGE_A64_OS_SETTINGS);

        //화면 하단에 있는 앱이라서 제외
        //packages.put(ACTIVITY_MODE , PACKAGE_MODE);
        //packages.put(ACTIVITY_HOME_COACH , PACKAGE_HOME_COACH);
        //packages.put(ACTIVITY_APP_LIST , PACKAGE_APP_LIST);
        //packages.put(ACTIVITY_CONTROL , PACKAGE_CONTROL);
        //packages.put(ACTIVITY_HOME_REPORTER , PACKAGE_HOME_REPORTER);
    }

    /**
     * 앱 론처 아이콘 맵 생성
     */
    private static void createAppLauncherIconMap() {
        icons = new HashMap<>();
        icons.put(ACTIVITY_CCTV, R.mipmap.ic_basic_cctv);
        icons.put(ACTIVITY_VISITOR, R.mipmap.ic_basic_visitor);
        icons.put(ACTIVITY_SETTINGS, R.mipmap.ic_basic_setting);
        icons.put(ACTIVITY_ENERGY_MONITORING, R.mipmap.ic_basic_energy);
        icons.put(ACTIVITY_SECURITY, R.mipmap.ic_basic_emergency);
        icons.put(ACTIVITY_PHONE, R.mipmap.ic_basic_call);
        icons.put(ACTIVITY_CONTACTS, R.mipmap.ic_basic_adress);
        icons.put(ACTIVITY_CALLLOG, R.mipmap.ic_basic_call_list);
        icons.put(ACTIVITY_FRONT_DOOR, R.mipmap.ic_basic_door);
        icons.put(ACTIVITY_GUARD, R.mipmap.ic_basic_guardroom);
        icons.put(ACTIVITY_SMART_DOOR_PHONE, R.mipmap.ic_basic_smartdoor);
        icons.put(ACTIVITY_DELIVERY, R.mipmap.ic_basic_box);
        icons.put(ACTIVITY_EMERGENCY_LOG, R.mipmap.ic_basic_emergency_list);
        icons.put(ACTIVITY_CAR_LOG, R.mipmap.ic_basic_parking);
        icons.put(ACTIVITY_NOTICE, R.mipmap.ic_basic_notice);
        icons.put(ACTIVITY_VOTE, R.mipmap.ic_basic_vote);
        icons.put(ACTIVITY_LOBBY_PASSWORD, R.mipmap.ic_basic_lobbypw);
        icons.put(ACTIVITY_HOME_IOT, R.mipmap.ic_basic_ifrun);
        icons.put(ACTIVITY_ONE_PASS, R.mipmap.ic_basic_onepass_tag);
        icons.put(ACTIVITY_PARKING, R.mipmap.ic_basic_parking_list);
        icons.put(ACTIVITY_AFTER_SERVICE, R.mipmap.ic_basic_as);
        icons.put(ACTIVITY_MANUAL, R.mipmap.ic_basic_manual);
        icons.put(ACTIVITY_SURROUNDINGS, R.mipmap.ic_basic_around_info);
        icons.put(ACTIVITY_LOGIN, R.mipmap.ic_basic_join);
        icons.put(ACTIVITY_ELEVATOR, R.mipmap.ic_basic_elevator);
        icons.put(ACTIVITY__CALCULATOR, R.mipmap.ic_basic_calculator);
        icons.put(ACTIVITY_RECORDER, R.mipmap.ic_basic_recorder);
        icons.put(ACTIVITY_DESKCLOCK, R.mipmap.ic_basic_alarm);
        icons.put(ACTIVITY_VIDEO_RECORD, R.mipmap.ic_basic_videomemo);
        icons.put(ACTIVITY_EVACUATION, R.mipmap.ic_basic_emergency_evacuation);
        icons.put(ACTIVITY_NEW_SETTING, R.mipmap.ic_basic_setting);
        icons.put(ACTIVITY_NEW_CCTV, R.mipmap.ic_basic_cctv);
        icons.put(ACTIVITY_A64_CCTV, R.mipmap.ic_basic_cctv);
        icons.put(ACTIVITY_A64_VISITOR, R.mipmap.ic_basic_visitor);
        icons.put(ACTIVITY_A64_DOOR, R.mipmap.ic_basic_door);
        icons.put(ACTIVITY_A64_CALLLOG, R.mipmap.ic_basic_call_list);
        icons.put(ACTIVITY_A64_EMERLOG, R.mipmap.ic_basic_emergency_list);
        icons.put(ACTIVITY_A64_SETTINGS, R.mipmap.ic_basic_setting);
        icons.put(ACTIVITY_A64_OS_SETTINGS, R.mipmap.ic_basic_setting);



        //화면 하단에 있는 앱이라서 제외
        //icons.put(ACTIVITY_MODE , R.mipmap.ic_basic_energy);
        //icons.put(ACTIVITY_HOME_COACH , R.mipmap.ic_basic_energy);
        //icons.put(ACTIVITY_APP_LIST , R.mipmap.ic_basic_energy);
        //icons.put(ACTIVITY_CONTROL , R.mipmap.ic_basic_energy);
        //icons.put(ACTIVITY_HOME_REPORTER , R.mipmap.ic_basic_energy);

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
        activities.add(ACTIVITY__CALCULATOR);
        activities.add(ACTIVITY_RECORDER);
        activities.add(ACTIVITY_DESKCLOCK);
        activities.add(ACTIVITY_VIDEO_RECORD);
        activities.add(ACTIVITY_EVACUATION);
        activities.add(ACTIVITY_NEW_SETTING);
        activities.add(ACTIVITY_NEW_CCTV);
        activities.add(ACTIVITY_A64_CCTV);
        activities.add(ACTIVITY_A64_VISITOR);
        activities.add(ACTIVITY_A64_DOOR);
        activities.add(ACTIVITY_A64_CALLLOG);
        activities.add(ACTIVITY_A64_EMERLOG);
        activities.add(ACTIVITY_A64_SETTINGS);
        activities.add(ACTIVITY_A64_OS_SETTINGS);


        //화면 하단에 있는 앱이라서 제외
        //activities.add(ACTIVITY_MODE);
        //activities.add(ACTIVITY_HOME_COACH);
        //activities.add(ACTIVITY_APP_LIST);
        //activities.add(ACTIVITY_CONTROL);
        //activities.add(ACTIVITY_HOME_REPORTER);

    }

    /**
     * 각 앱에 해댱하는 아이콘 아이디 가져옴
     *
     * @param activityName
     * @return
     */
    public static int getIconId(String activityName) {
        return icons.get(activityName);
    }


    /**
     * 해당 앱의 이름 가져옴(정확히 액티비티 이름)
     *
     * @param context
     * @param activityName 액티비티 명(패키지 명 포함)
     * @return
     */
    public static String getAppName(Context context, String activityName) {

        return activityNames.get(activityName);


        //아래는 이전 코드
        //패키지가 중복되는 앱은 같은 이름을 반환하는 이슈있음


//        //"com.commax.security.EmergencyOccurView"는 이상하게 getApplicationLabel 호출하면
//        //패키지명을 리턴해서 아래와 같이 편법으로 처리
//        if(activityName.equals(AppInfoMap.ACTIVITY_SECURITY)) {
//            return "비상발생";
//        }
//
//
//
//        Log.d(BasicThemeConstants.LOG_TAG, "activityName name: " + activityName);
//        final PackageManager pm = context.getPackageManager();
//        ApplicationInfo ai;
//        try {
//            ai = pm.getApplicationInfo(getPackageName(activityName), 0);
//
//            Log.d(BasicThemeConstants.LOG_TAG, "packageName name: " + getPackageName(activityName));
//        } catch (final PackageManager.NameNotFoundException e) {
//            ai = null;
//        }
//        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
//        Log.d(BasicThemeConstants.LOG_TAG, "app name: " + applicationName);
//        return applicationName;
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

    /**
     * 패키지 명 가져옴
     *
     * @param activityName
     * @return
     */
    public static String getPackageName(String activityName) {
        return packages.get(activityName);
    }

    /**
     * 액티비티 명 맵 생성
     */
    public static void createActivityNameMap() {
        activityNames = new HashMap<>();

        PackageManager manager = mContext.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);
        final int count = apps.size();

        String title = null;
        String packageAndActivityName = null;

        for (int i = 0; i < count; i++) {

            ResolveInfo info = apps.get(i);

            title = info.loadLabel(manager).toString();
            packageAndActivityName = info.activityInfo.name;
            activityNames.put(packageAndActivityName, title);
        }

    }
}
