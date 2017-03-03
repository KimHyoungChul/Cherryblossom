package com.commax.settings.content_provider;

/**
 * Created by OWNER on 2016-11-23.
 */

public class CommaxConstants {
    public static final String ENABLED = "enabled";
    public static final String DISABLED = "disabled";

    //동, 호
    public static final String BROADCAST_DONGHO = "com.commax.settings.DONGHO";
    public static final String KEY_DONG = "dong";
    public static final String KEY_HO = "ho";

    //월패드 비밀번호
    public static final String BROADCAST_WALLPAD_PASSWORD = "com.commax.settings.WALLPAD_PASSWORD";
    public static final String KEY_WALLPAD_PASSWORD = "wallpadPassword";

    //연속통화시간
    public static final String BROADCAST_CALLTIME = "com.commax.settings.CALLTIME";
    public static final String KEY_CALLTIME = "callTime";

    //영상녹화시간
    public static final String BROADCAST_MOVIE_RECORD_TIME = "com.commax.settings.MOVIE_RECORD_TIME";
    public static final String KEY_MOVIE_RECORD_TIME = "movieRecordTime";

    //영상자동저장
    public static final String BROADCAST_MOVIE_AUTO_SAVE = "com.commax.settings.MOVIE_AUTO_SAVE";
    public static final String KEY_MOVIE_AUTO_SAVE = "movieAutoSave";

    //스마트폰 수신
    public static final String BROADCAST_SMARTPHONE_CALL = "com.commax.settings.SMARTPHONE_CALL";
    public static final String KEY_SMARTPHONE_CALL = "smartphoneCall";

    //현관 모니터링 서비스
    public static final String BROADCAST_DOOR_MONITOR = "com.commax.app.DOOR_MONITOR"; //현관 모니터링 서비스를 호출하는 브로드캐스트
    public static final String KEY_FROM = "from"; //현관 모니터링 서비스를 호출한 위치 구분
    public static final String BASIC_THEME = "basicTheme"; //베이직 테마
//    public static final String PACKAGE_DOOR = "com.commax.yuri.onvifservice";
//    public static final String ACTIVITY_DOOR = "com.commax.yuri.onvifservice.MainActivity";
    public static final String PACKAGE_DOOR = "com.commax.iphomiot.doorcall";
    public static final String ACTIVITY_DOOR = "com.commax.iphomiot.doorcall.MainActivity";
    public static final String PREVIEW = "preview"; //미리보기
    public static final String KEY_IP = "ip";
    public static final String KEY_MODE = "mode";
    public static final String FROM_DOORPHONE_SETTING = "doorphoneSetting"; //2017-01-09,yslee::설정앱의 도어폰 설정탭으로 바로 진입기능 추가

    //가치정보 사용여부
    public static final String BROADCAST_USE_VALUE_INFO = "com.commax.settings.USE_VALUE_INFO";
    public static final String KEY_USE_VALUE_INFO = "useValueInfo";

    //제어기기 연결 초기화
    public static final String BROADCAST_INIT_DEVICE ="com.commax.settings.INIT_DEVICE";

    //저장 데이터 초기화
    public static final String BROADCAST_INIT_DATA = "com.commax.settings.INIT_DATA";
    public static final String PACKAGE_EMERGENCY = "com.commax.recentemerlog";
    public static final String ACTIVITY_EMERGENCY = "com.commax.recentemerlog.MainActivity";
    public static final String KEY_RESET = "reset";
    public static final String SEND_COMMAND_EMERGENCY_DEL = "deleteEmergencyLog";

    //CCTV 앱
    public static final String PACKAGE_CCTV = "commax.wallpad.cctvview";
    public static final String ACTIVITY_CCTV = "commax.wallpad.cctvview.CCTVPreview";

    public static final String KEY_PORT = "port";
    public static final String KEY_ID = "id";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_RTSP_URL = "rtspUrl";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String FROM_CCTV_SETTING = "cctvSetting";

    public static final String KEY_IS_REGISTERED = "isRegistered";

    public static final String DOOR_DEFAULT_SIPNO = "201";

    public static final String CCTV_DEFAULT_ID = "admin";
    public static final String CCTV_DEFAULT_PW = "1234";

    //홈스크린도움말 사용여부
    public static final String BROADCAST_USE_VALUE_TIP = "com.commax.settings.USE_VALUE_TIP";
    public static final String KEY_USE_VALUE_TIP = "useValueTip";

}
