package com.commax.homereporter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    public static boolean screen_on = true;

    InfoLoader mInfoLoader;
    Thread thread;
    LinearLayout lay_scroll;
    ProgressBar progressBar;
    BroadcastReceiver mBroadcastReceiver;

    LayoutInflater toastInflater;
    View toastLayout;
    TextView toastTextView;
    TextView tv_title;
    ProjectOptions projectOptions;

    static boolean need_call_refresh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "rep lifecycle onCreate");

        if(getSupportActionBar()!=null) {
            getSupportActionBar().hide();
        }

        hideNavigationBar();

        lay_scroll = (LinearLayout) findViewById(R.id.lay_scroll);
        RelativeLayout lay_main_top = (RelativeLayout) findViewById(R.id.lay_main_top);
        ImageBT ib_edit = (ImageBT) findViewById(R.id.ib_edit);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ImageBT ib_home = (ImageBT) findViewById(R.id.ib_home);
        ListView listView = (ListView)findViewById(R.id.listView);
        progressBar = (ProgressBar)findViewById(R.id.progress);

        ib_home.setOnClickListener(mClick);
        ib_edit.setOnClickListener(mClick);

//        mInfoLoader = new InfoLoader(getApplicationContext(), mHandler);   //Draw view
//        thread = new Thread(mInfoLoader);
//        thread.start();

        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        }catch (Exception e){
            e.printStackTrace();
        }

        projectOptions = new ProjectOptions();
        getProjectOptions();
        setAppConfig();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NameSpace.WOEID_ACTION);
        intentFilter.addAction(NameSpace.TM_ACTION);
        intentFilter.addAction(NameSpace.AREA_ACTION);
        intentFilter.addAction(NameSpace.SCREEN_OFF_ACTION);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(NameSpace.WOEID_ACTION)){

                    Log.d(TAG, "WOEID_ACTION event caught");

                    ArrayList<String> list_unselected_info = new ArrayList<>();
                    ArrayList<String> list_support_info = new ArrayList<>();
                    InfoTools infoTools = new InfoTools();
                    list_unselected_info = infoTools.getUnSelectedInfo();
                    list_support_info = infoTools.getSupportInfo();

                    try {
                        if(lay_scroll.getChildCount()>0) {
                            mInfoLoader.updateWeather();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else if (action.equalsIgnoreCase(NameSpace.TM_ACTION)){

                    ArrayList<String> list_unselected_info = new ArrayList<>();
                    ArrayList<String> list_support_info = new ArrayList<>();
                    InfoTools infoTools = new InfoTools();
                    list_unselected_info = infoTools.getUnSelectedInfo();
                    list_support_info = infoTools.getSupportInfo();

                    try {
                        if (list_support_info.contains(NameSpace.INFO_AIR)&&(!list_unselected_info.contains(NameSpace.INFO_AIR))) {

                            if(lay_scroll.getChildCount()>0) {
                                mInfoLoader.updateAir();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(action.equals(NameSpace.AREA_ACTION)){

                    Log.d(TAG, "AREA_ACTION event caught");

                    ArrayList<String> list_unselected_info = new ArrayList<>();
                    ArrayList<String> list_support_info = new ArrayList<>();
                    InfoTools infoTools = new InfoTools();
                    list_unselected_info = infoTools.getUnSelectedInfo();
                    list_support_info = infoTools.getSupportInfo();

                    try {
                        if (list_support_info.contains(NameSpace.INFO_HEALTH_LIFE)&&(!list_unselected_info.contains(NameSpace.INFO_HEALTH_LIFE))) {
                            if(lay_scroll.getChildCount()>0) {
                                mInfoLoader.updateHealthLife();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else if(action.equals(NameSpace.SCREEN_OFF_ACTION)){

                    Log.d(TAG, "SCREEN_OFF_ACTION event caught");
                    finish();
                }
            }
        };

        try {
            registerReceiver(mBroadcastReceiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.ib_home:
                    try{
                        Log.d(TAG, "BtnHome_Clicked()");

                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.MAIN");
                        intent.addCategory("android.intent.category.HOME");
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                                | Intent.FLAG_ACTIVITY_FORWARD_RESULT
                                | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        startActivity(intent);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    finish();
                    break;

                case R.id.ib_edit:
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivityForResult(intent, 0);
                    break;
            }

        }
    };

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW:
                        addInfoView(msg.obj);
                        break;

                    case HandlerEvent.EVENT_HANDLE_FILL_VIEW:
                        fillView();
                        break;

                    case HandlerEvent.EVENT_HANDLE_REMOVE_INFO_VIEW:
                        removeInfoView(msg.obj);
                        break;

                    case HandlerEvent.EVENT_HANDLE_SHOW_PROGRESS:
                        progressBar.setVisibility(View.VISIBLE);
                        break;

                    case HandlerEvent.EVENT_HANDLE_HIDE_PROGRESS:
                        progressBar.setVisibility(View.GONE);
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_WEATHER:
                        updateWeatherData(msg);
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_AIR:
                        updateAirData(msg);
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_HEALTH_LIFE:
                        updateHealthLifeData(msg);
                        break;

                    case HandlerEvent.EVENT_HANDLE_SHOW_NETWORK_ERR:
                        showToastNetworkErr();
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void getProjectOptions(){
        try{

            try {
                FileEx io = new FileEx();
                String[] files = null;

                try {
                    files = io.readFile(NameSpace.PXD_CONFIG_PATH);
                } catch (FileNotFoundException e) {
                    // e.printStackTrace();
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                if (files == null) {
                    return;
                }

                if (files.length > 0) {
                    // ���� üũ
                    if (files == null) {
                        return;
                    }
                    if ("".equals(files[0])) {
                        return;
                    }
                    if ("-1".equals(files[0])) {
                        return;
                    }
                }

                try {
                    for (int i = 0; i < files.length; i++) {
                        String line = files[i];
                        if (line.contains(NameSpace.CONFIG_APP4_NAME)) {
                            projectOptions.app4_name = line.replace(NameSpace.CONFIG_APP4_NAME + "=", "");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                Log.d(TAG, "getProjectOptions : "+projectOptions.app4_name);

            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setAppConfig(){
        try{
            if (projectOptions!=null){
                if (projectOptions.app4_name.equalsIgnoreCase("myhome")){

                    Locale locale;
                    String lang = "";

                    try {
                        locale = getResources().getConfiguration().locale;
                        lang = locale.getLanguage();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.d(TAG, "setAppConfig country "+lang);
                    if (lang.equalsIgnoreCase("en")) {
                        tv_title.setText("My home");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastNetworkErr(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.connection_err));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateWeatherData(Message msg){
        try{
            Log.d(TAG, "updateWeatherData");
            Bundle bundle = msg.getData();

            if (bundle.containsKey(NameSpace.WEATHER_VALUE)) {
                double[] weatherValue = bundle.getDoubleArray(NameSpace.WEATHER_VALUE);

                if (lay_scroll.getChildCount()>0){

                    boolean found = false;

                    for (int i=0;i<lay_scroll.getChildCount();i++){
                        if (lay_scroll.getChildAt(i) instanceof WeatherView){
                            WeatherView weatherView = (WeatherView) lay_scroll.getChildAt(i);
                            found = true;

                            if ((weatherValue!=null)&&(weatherValue[0] < 9999) && (weatherValue[1] != 3200) && (weatherValue[2] != -1)) {
                                weatherView.updateWeather(weatherValue);
                            }else {
                                removeInfoView(weatherView);
                            }
                        }
                    }

                    if (!found){
                        if ((weatherValue!=null)&&(weatherValue[0] < 9999) && (weatherValue[1] != 3200) && (weatherValue[2] != -1)) {
                            WeatherView weatherView = new WeatherView(getApplicationContext(), weatherValue);
                            lay_scroll.addView(weatherView);

                            VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                            lay_scroll.addView(verticalBar);
                            refillView();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateAirData(Message msg){
        try{
            Log.d(TAG, "updateAirData");
            Bundle bundle = msg.getData();

            if (bundle.containsKey(NameSpace.AIR_VALUE)) {
                double[] airValue = bundle.getDoubleArray(NameSpace.AIR_VALUE);

                boolean[] found = {false, false, false};

                if (lay_scroll.getChildCount()>0){

                    int removed = 0;

                    for (int i=0;i<lay_scroll.getChildCount();i++){
                        try {
                            if (lay_scroll.getChildAt(i-removed) instanceof AirView) {
                                AirView airView = (AirView) lay_scroll.getChildAt(i-removed);

                                if (airView.getType().equalsIgnoreCase(NameSpace.AIR_O3)) {
                                    found[0] = true;
                                    Log.d(TAG, "updateAirData found AIR_O3");
                                    if ((airValue!=null)&&(airValue[0]!=-1)) {
                                        airView.updateAir(airValue[0]);
                                    }else {
                                        removeInfoView(airView);
                                        removed++;
                                    }
                                } else if (airView.getType().equalsIgnoreCase(NameSpace.AIR_DUST)) {
                                    found[1] = true;
                                    Log.d(TAG, "updateAirData found AIR_DUST");
                                    if ((airValue!=null)&&(airValue[1]!=-1)) {
                                        airView.updateAir(airValue[1]);
                                    }else {
                                        removeInfoView(airView);
                                        removed++;
                                    }
                                } else if (airView.getType().equalsIgnoreCase(NameSpace.AIR_TOTAL)) {
                                    found[2] = true;
                                    Log.d(TAG, "updateAirData found AIR_TOTAL");
                                    if ((airValue!=null)&&(airValue[2]!=-1)) {
                                        airView.updateAir(airValue[2]);
                                    }else {
                                        removeInfoView(airView);
                                        removed++;
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    for (int a=0;a<found.length;a++){

                        if(!found[a]){
                            String airType[] = {NameSpace.AIR_O3, NameSpace.AIR_DUST, NameSpace.AIR_TOTAL};

                            if ((airValue!=null)&&(airValue[a]!=-1)) {
                                AirView airView = new AirView(getApplicationContext(), airType[a], airValue[a]);

                                int index = -1;
                                index = getViewPosition(airType[a]);
                                lay_scroll.addView(airView);

                                VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                                lay_scroll.addView(verticalBar);
                            }

                            refillView();
                        }

                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int getViewPosition(String type){

        int position = -1;

        for (int i=0;i<lay_scroll.getChildCount();i++){

            if (lay_scroll.getChildAt(i) instanceof WeatherView){

            }

        }

        return position;
    }

    private void updateHealthLifeData(Message msg){
        try{
            Log.d(TAG, "updateHealthLifeData");
            Bundle bundle = msg.getData();

            if (bundle.containsKey(NameSpace.HEALTH_LIFE_VALUE)) {
                double hlValue = bundle.getDouble(NameSpace.HEALTH_LIFE_VALUE);
                boolean found = false;

                if (lay_scroll.getChildCount()>0){

                    for (int i=0;i<lay_scroll.getChildCount();i++){
                        try {
                            if (lay_scroll.getChildAt(i) instanceof AirView) {
                                AirView airView = (AirView) lay_scroll.getChildAt(i);

                                if (airView.getType().equalsIgnoreCase(NameSpace.LIFE_ULTRV)) {
                                    found = true;
                                    if (hlValue!=-1) {
                                        airView.updateAir(hlValue);
                                        Log.d(TAG, "updateHealthLifeData update LIFE_ULTRV");
                                    }else {
                                        removeInfoView(airView);
                                        Log.d(TAG, "updateHealthLifeData removeInfoView LIFE_ULTRV");
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    if (!found){
                        if (hlValue!=-1) {
                            AirView airView = new AirView(getApplicationContext(), NameSpace.LIFE_ULTRV, hlValue);
                            lay_scroll.addView(airView);

                            VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                            lay_scroll.addView(verticalBar);
                            refillView();
                        }
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addInfoView(Object object){

        try {

            boolean hasLocalInfo = false;
            int missed_index = 0;
            boolean add_fail = false;

            try {
                int child_count = lay_scroll.getChildCount();

                for (int i = 0; i < child_count; i++) {
                    if (lay_scroll.getChildAt(i) instanceof MissedView) {
                        hasLocalInfo = true;
                        missed_index = i;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (hasLocalInfo){
                try {
                    Log.d(TAG, "addInfoView " + missed_index);
                    lay_scroll.addView((View) object, missed_index);

                    VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                    lay_scroll.addView(verticalBar, missed_index+1);
                }catch (Exception e){
                    e.printStackTrace();
                    add_fail = true;
                    Log.d(TAG, "addInfoView failed");
                }
            }else {
                lay_scroll.addView((View) object);

                VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                lay_scroll.addView(verticalBar);
            }

            if (add_fail){
                lay_scroll.addView((View) object);

                VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                lay_scroll.addView(verticalBar);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void removeInfoView(Object object){

        int same = -1;

        try {
            for (int i = 0; i < lay_scroll.getChildCount(); i++) {
                if (lay_scroll.getChildAt(i) == (View) object) {
                    Log.d(TAG, "removeInfoView " + i + " same");
                    same = i;
                }
            }

            if (((same + 1) <= lay_scroll.getChildCount()) && (same != -1)) {
                lay_scroll.removeViewAt(same + 1);
            }

            lay_scroll.removeView((View) object);

            refillView();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void refillView(){

        int info_view_count = 0;
        int filled_count = 0;

        try {
            for (int i = 0; i < lay_scroll.getChildCount(); i++) {

                if ((lay_scroll.getChildAt(i) instanceof WeatherView)
                        || (lay_scroll.getChildAt(i) instanceof AirView)
                        || (lay_scroll.getChildAt(i) instanceof TodayEnergyView)
                        || (lay_scroll.getChildAt(i) instanceof MonthEnergyView)
                        || (lay_scroll.getChildAt(i) instanceof ParkingView)
                        || (lay_scroll.getChildAt(i) instanceof NoticeView)
                        || (lay_scroll.getChildAt(i) instanceof MissedView)
                        || (lay_scroll.getChildAt(i) instanceof SupportView)
                        || (lay_scroll.getChildAt(i) instanceof TempView)
                        || (lay_scroll.getChildAt(i) instanceof HumidView)
                        || (lay_scroll.getChildAt(i) instanceof MeterView)) {
                    info_view_count++;
                }

                if ((lay_scroll.getChildAt(i) instanceof EmptyView)) {
                    filled_count++;
                }

            }

            Log.d(TAG, "refillView info_view_count " + info_view_count + " filled_count " + filled_count);

            int make = 4 - info_view_count;
            int refill_count = make - filled_count;

            if (refill_count >= 0) {

                for (int i = 0; i < refill_count; i++) {

                    EmptyView emptyView = new EmptyView(getApplicationContext());
                    lay_scroll.addView(emptyView);
                    VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                    lay_scroll.addView(verticalBar);

                }
            }

            if ((info_view_count >= 4) && (filled_count > 0)) {


                int same = -1;

                for (int i = 0; i < lay_scroll.getChildCount(); i++) {
                    if (lay_scroll.getChildAt(i) instanceof EmptyView) {
                        Log.d(TAG, "refillView " + i + " same");

                        same = i;
                        if (((same + 1) <= lay_scroll.getChildCount()) && (same != -1)) {
                            if (lay_scroll.getChildAt(same + 1) instanceof VerticalBar) {
                                lay_scroll.removeViewAt(same + 1);
                            }
                        }

                        lay_scroll.removeViewAt(i);
                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void fillView(){

        int info_view_count = 0;

        try {
            for (int i = 0; i < lay_scroll.getChildCount(); i++) {

                if ((lay_scroll.getChildAt(i) instanceof WeatherView)
                        || (lay_scroll.getChildAt(i) instanceof AirView)
                        || (lay_scroll.getChildAt(i) instanceof TodayEnergyView)
                        || (lay_scroll.getChildAt(i) instanceof MonthEnergyView)
                        || (lay_scroll.getChildAt(i) instanceof ParkingView)
                        || (lay_scroll.getChildAt(i) instanceof NoticeView)
                        || (lay_scroll.getChildAt(i) instanceof MissedView)
                        || (lay_scroll.getChildAt(i) instanceof SupportView)
                        || (lay_scroll.getChildAt(i) instanceof TempView)
                        || (lay_scroll.getChildAt(i) instanceof HumidView)
                        || (lay_scroll.getChildAt(i) instanceof MeterView)) {
                    info_view_count++;
                }

            }

            Log.d(TAG, "fillView info_view_count " + info_view_count);

            int make = 4 - info_view_count;

            for (int i = 0; i < make; i++) {

                EmptyView emptyView = new EmptyView(getApplicationContext());
                lay_scroll.addView(emptyView);
                VerticalBar verticalBar = new VerticalBar(getApplicationContext());
                lay_scroll.addView(verticalBar);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case 100:
                redrawInfo();
            case 101:
                Log.d(TAG, "rep lifecycle onActivityResult");
                need_call_refresh = false;
                break;
        }
    }

    private void redrawInfo(){
        try {
//            if (mInfoLoader.end_thread_running) {
                interruptThread();

                removeAll();
                mInfoLoader = new InfoLoader(getApplicationContext(), mHandler);   //Draw view
                thread = new Thread(mInfoLoader);
                thread.start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeAll(){

        try {
            lay_scroll.removeAllViews();
            lay_scroll.removeAllViewsInLayout();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void interruptThread(){
        try {
            if (thread != null) {
                if (thread.isAlive()) {
                    thread.interrupt();
                    Log.d(TAG, "Thread interrupted");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void drawEmptyView(){

        EmptyView emptyView = new EmptyView(getApplicationContext());
        lay_scroll.addView(emptyView);
        VerticalBar verticalBar = new VerticalBar(getApplicationContext());
        lay_scroll.addView(verticalBar);

        EmptyView emptyView2 = new EmptyView(getApplicationContext());
        lay_scroll.addView(emptyView2);
        VerticalBar verticalBar2 = new VerticalBar(getApplicationContext());
        lay_scroll.addView(verticalBar2);

        EmptyView emptyView3 = new EmptyView(getApplicationContext());
        lay_scroll.addView(emptyView3);
        VerticalBar verticalBar3 = new VerticalBar(getApplicationContext());
        lay_scroll.addView(verticalBar3);

        EmptyView emptyView4 = new EmptyView(getApplicationContext());
        lay_scroll.addView(emptyView4);

    }

    private void hideNavigationBar(){

        try {
            // 액티비티 아래의 네비게이션 바가 안보이게
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "rep lifecycle onResume");
        screen_on = true;

        if (need_call_refresh){
            need_call_refresh = false;
            redrawInfo();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "rep lifecycle onPause");
        super.onPause();
        screen_on = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "rep lifecycle onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        need_call_refresh = true;
        Log.d(TAG, "rep lifecycle onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "rep lifecycle onDestroy");

        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
