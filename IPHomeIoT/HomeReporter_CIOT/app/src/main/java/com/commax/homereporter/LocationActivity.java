package com.commax.homereporter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class LocationActivity extends AppCompatActivity implements View.OnClickListener {

    static final String TAG = "LocationActivity";
    EditText et_search;
    TextView tv_ok;
    ProgressBar progressBar;
    TextView tv_search;
    TextView tv_no_result;
    ListView listView;
    WoeidListAdapter adapter;
    String info_sort = "";

    LayoutInflater toastInflater;
    View toastLayout;
    TextView toastTextView;
//    ProgressDialog progressDialog;

    boolean searching = false;
    BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getSupportActionBar().hide();
        hideNavigationBar();

        listView = (ListView) findViewById(R.id.listView);
        ImageButton bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        et_search = (EditText) findViewById(R.id.et_search);
        RelativeLayout lay_location_top = (RelativeLayout) findViewById(R.id.lay_location_top);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        TextView tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_no_result = (TextView) findViewById(R.id.tv_no_result);

        Intent intent = getIntent();
        info_sort = intent.getExtras().getString(NameSpace.INFO_SORT);
        Log.d(TAG, "info_sort "+info_sort);

        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        }catch (Exception e){
            e.printStackTrace();
        }

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                switch (actionId) {

                    case EditorInfo.IME_ACTION_SEARCH:
                        doSearch();
                        break;

                }
                return false;
            }
        });


//        progressDialog = new ProgressDialog(getApplicationContext());
        adapter = new WoeidListAdapter(this);
        listView.setAdapter(adapter);

        bt_clear.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_search.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NameSpace.SCREEN_OFF_ACTION);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(NameSpace.SCREEN_OFF_ACTION)){

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

    private void showToastRetry(){

        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.retry));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();

            tv_no_result.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showToastSavedSucceed(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.save_success));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastInputCity(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.input_city));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
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

            tv_no_result.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastSelectLocation(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.select_location));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastFailedSave(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.save_fail));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastNotSupportedText(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.no_support_text));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dismissProgressDialog(){
        try {
            Log.d(TAG, "dismissProgress");
            progressBar.setVisibility(View.INVISIBLE);
            tv_search.setClickable(true);

        }catch (Exception e){
            e.printStackTrace();
        }
            searching = false;
//        if (progressDialog != null) {
//            progressDialog.dismiss();
//        }
    }

    @Override
    public void onClick(View v) {

        try {
            switch (v.getId()) {

                case R.id.bt_clear:
                    et_search.setText("");
                    break;

                case R.id.tv_ok:
                    save();
                    break;

                case R.id.tv_search:
                    doSearch();
                    break;

                case R.id.tv_cancel:
                    finish();
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HandlerEvent.EVENT_HANDLE_UPDATE_SEARCH_RESULT:
                        updateSearchResult(msg);
                        break;

                    case HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY:
                        showToastRetry();
                        break;

                    case HandlerEvent.EVENT_HANDLE_SHOW_NETWORK_ERR:
                        showToastNetworkErr();
                        break;

                    case HandlerEvent.EVENT_HANDLE_DISMISS_PROGRESS_DIALOG:
                        dismissProgressDialog();
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void updateSearchResult(Message msg){

        try{
            Log.d(TAG, "updateSearchResult");
            Bundle bundle = msg.getData();
            if (bundle.containsKey(NameSpace.WOEID_SIZE)) {
                drawWOEID(bundle);
            }else if(bundle.containsKey(NameSpace.TM_SIZE)){
                drawTMXTMY(bundle);
            }else if(bundle.containsKey(NameSpace.AREA_SIZE)){
                drawAreaCode(bundle);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void drawAreaCode(Bundle bundle){

        int area_size = 0;

        try {
            if (bundle.containsKey("area_size")) {

                area_size = bundle.getInt("area_size");
                Log.d(TAG, "drawAreaCode : area_size : " + area_size);

                if (area_size > 0) {

                    // MAX item count 100.
                    if (area_size > NameSpace.MAX_RESULT_SIZE) {
                        area_size = NameSpace.MAX_RESULT_SIZE;
                    }

                    for (int i = 0; i < area_size; i++) {
                        if ((bundle.containsKey("h_dong" + i)) && (bundle.containsKey("areaCode" + i))) {

                            String loc = "";
                            String h_sido = bundle.getString("h_sido" + i);
                            String h_sgg = bundle.getString("h_sgg" + i);
                            String h_dong = bundle.getString("h_dong" + i);
                            String b_dong = bundle.getString("b_dong" + i);
                            loc = h_sido + " " + h_sgg + " " + h_dong + "(" + b_dong + ")";
                            String areaCode = bundle.getString("areaCode" + i);

//                        try {
//                            loc = jsonTools.reduceSpace(loc);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                            adapter.addItem(new WoeidItem(loc, areaCode, "", "", ""));
                            adapter.notifyDataSetChanged();

                        }
                    }
                } else {

                    showToastRetry();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void drawWOEID(Bundle bundle){

        int woeid_size=0;

        try {
            if (bundle.containsKey("woeid_size")) {

                woeid_size = bundle.getInt("woeid_size");

                Log.d(TAG, "drawWOEID woeid_size = " + woeid_size);
                if (woeid_size > 0) {

                    // MAX item count 100.
                    if (woeid_size > NameSpace.MAX_RESULT_SIZE) {
                        woeid_size = NameSpace.MAX_RESULT_SIZE;
                    }

                    for (int i = 0; i < woeid_size; i++) {
                        if ((bundle.containsKey("location" + i)) && (bundle.containsKey("woeid" + i))) {

                            String loc = bundle.getString("location" + i);
                            String woeid = bundle.getString("woeid" + i);

                            try {
                                JsonTools jsonTools = new JsonTools();
                                loc = jsonTools.reduceSpace(loc);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            adapter.addItem(new WoeidItem(loc, woeid, "", "", ""));
                            adapter.notifyDataSetChanged();

                        }
                    }
                } else {

                    showToastRetry();

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void drawTMXTMY(Bundle bundle){

        int tm_size=0;

        try {
            if (bundle.containsKey("tm_size")) {

                tm_size = bundle.getInt("tm_size");

                if (tm_size > 0) {

                    // MAX item count 100.
                    if (tm_size > NameSpace.MAX_RESULT_SIZE) {
                        tm_size = NameSpace.MAX_RESULT_SIZE;
                    }

                    for (int i = 0; i < tm_size; i++) {
                        if ((bundle.containsKey("location" + i)) && (bundle.containsKey("tmX" + i)) && (bundle.containsKey("tmY" + i))) {

                            String location = bundle.getString("location" + i);
                            String tmX = bundle.getString("tmX" + i);
                            String tmY = bundle.getString("tmY" + i);

                            adapter.addItem(new WoeidItem(location, tmX, tmY, "", ""));
                            adapter.notifyDataSetChanged();

                        }
                    }
                } else {

                    showToastRetry();

                }
            } else {
                Log.d(TAG, "no tm_size");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void save(){

        try {

            String loc = "";
            String code = "";
            String code2 = "";
            boolean selected = false;

            for(int i=0;i<adapter.getCount();i++){

                try {
                    WoeidItem woeidItem = (WoeidItem) adapter.getItem(i);
                    Log.d(TAG, "item " + woeidItem.isChecked());
                    if(woeidItem.isChecked()) {
                        selected = true;

                        if(info_sort.equalsIgnoreCase(NameSpace.INFO_WEATHER)) {
                            loc = woeidItem.getData(0);
                            Log.d(TAG, "save " + woeidItem.getData(0));
                            code = woeidItem.getData(1);
                        }else if(info_sort.equalsIgnoreCase(NameSpace.INFO_AIR)){
                            loc = woeidItem.getData(0);
                            code = woeidItem.getData(1);
                            code2 = woeidItem.getData(2);
                        }else if(info_sort.equalsIgnoreCase(NameSpace.INFO_HEALTH_LIFE)){
                            loc = woeidItem.getData(0);
                            code = woeidItem.getData(1);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            //getting selected location and woeid
            Log.d("onItemClick", "selected location : " + loc + " / code : " + code+ " / code2 : " + code2);

            if(selected) {
                if (info_sort.equalsIgnoreCase(NameSpace.INFO_WEATHER)) {
                    saveInfo(NameSpace.WOEID, loc, code, code2);
                } else if (info_sort.equalsIgnoreCase(NameSpace.INFO_AIR)) {
                    saveInfo(NameSpace.TMXTMY, loc, code, code2);
                } else if (info_sort.equalsIgnoreCase(NameSpace.INFO_HEALTH_LIFE)) {
                    saveInfo(NameSpace.AREACODE, loc, code, code2);
                }
            }else {
                showToastSelectLocation();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveInfo(String save_what, String loc, String woeid, String code2){                   //Saving WOEID to /sdcard/WOEID/woeid.properties

        String location = "";
        JsonTools jsonTools = new JsonTools();
        ArrayList<String> temp = jsonTools.split(loc, " ");
        String temp2 = "";

        try {
            if (temp != null) {

                if (temp.size() > 1) {

                    temp2=loc;
                    location = temp2;
                    Log.d(TAG, "saveWOEID location = " + temp2);

                } else {
                    location = loc;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/WOEID/");
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            String filename = "woeid.properties";
            File file = new File(Environment.getExternalStorageDirectory() + "/WOEID/" + filename);
            Properties mProperty = new Properties();

            try {
                if (!file.exists()) {
                    file.createNewFile();
                    Log.e("Property", " Create profile ");
                }

                FileInputStream fis = new FileInputStream(file);
                mProperty.load(fis);

                FileOutputStream fos = new FileOutputStream(file);

                if((location!=null)&&(!TextUtils.isEmpty(location))) {
                    if (save_what.equalsIgnoreCase(NameSpace.WOEID)) {
                        mProperty.setProperty("location", location);
                    }else if(save_what.equalsIgnoreCase(NameSpace.TMXTMY)){
                        mProperty.setProperty("location_tm", location);
                    }else if(save_what.equalsIgnoreCase(NameSpace.AREACODE)){
                        mProperty.setProperty("location_area", location);
                    }
                }

                if(save_what.equalsIgnoreCase(NameSpace.AREACODE)) {
                    mProperty.setProperty(save_what, woeid);
                }else if(save_what.equalsIgnoreCase(NameSpace.TMX)){
                    mProperty.setProperty(save_what, woeid);
                }else if(save_what.equalsIgnoreCase(NameSpace.TMY)){
                    mProperty.setProperty(save_what, woeid);
                }else if(save_what.equalsIgnoreCase(NameSpace.WOEID)){
                    mProperty.setProperty(save_what, woeid);
                }else if(save_what.equalsIgnoreCase(NameSpace.TMXTMY)){
                    mProperty.setProperty(NameSpace.TMX, woeid);
                    mProperty.setProperty(NameSpace.TMY, code2);
                }
                mProperty.store(fos, "area info");

                fos.close();
                fis.close();
                Log.e("Property", " File Saved");

                showToastSavedSucceed();

//                editor = mPref.edit();
//                if (save_what.equalsIgnoreCase(NameSpace.WOEID)) {
//                    editor.putString("Location", txtFromEditBox);
//                }else if(save_what.equalsIgnoreCase(NameSpace.TMXTMY)){
//                    editor.putString("Location_tm", txtFromEditBox);
//                }else if(save_what.equalsIgnoreCase(NameSpace.AREACODE)){
//                    editor.putString("Location_area", txtFromEditBox);
//                }
//                editor.commit();

                try {
                    Process process = Runtime.getRuntime().exec("sync");
                    process.getErrorStream().close();
                    process.getInputStream().close();
                    process.getOutputStream().close();
                    process.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //명시적 송신

                if (save_what.equalsIgnoreCase(NameSpace.WOEID)) {
                    Intent intent = new Intent("com.commax.com.settingwoeid.WOEID_SAVED_ACTION");
                    sendBroadcast(intent);
                }else if(save_what.equalsIgnoreCase(NameSpace.TMXTMY)){
                    Intent intent = new Intent("com.commax.com.settingwoeid.TM_SAVED_ACTION");
                    sendBroadcast(intent);
                }else if(save_what.equalsIgnoreCase(NameSpace.AREACODE)){
                    Intent intent = new Intent("com.commax.com.settingwoeid.AREA_SAVED_ACTION");
                    sendBroadcast(intent);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void doSearch(){
        try {
            if(!searching) {
                searching = true;
                //hide keyboard

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(et_search.getWindowToken(), 0);

                //clear previous result
                adapter.clearItems();
                adapter.notifyDataSetChanged();

//            resetPropertyString();

                String txtFromEditBox = et_search.getText().toString().trim();
                Log.d(TAG, "search_btn : " + txtFromEditBox);

                if (!TextUtils.isEmpty(txtFromEditBox)) {
//                        Toast.makeText(mContext, getString(R.string.searching), Toast.LENGTH_SHORT).show();
                    //show progress dialog
//                if(!progressDialog.isShowing()) {
//                    progressDialog = ProgressDialog.show(LocationActivity.this, "", getString(R.string.searching), true);

                    tv_no_result.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    tv_search.setClickable(false);

                    WOEIDThread wThread;
                    Thread thread;
                    wThread = new WOEIDThread(getApplicationContext(), mHandler, txtFromEditBox, info_sort);
                    thread = new Thread(wThread);
                    thread.start();

//                }else {
//                    Log.d(TAG, "progressDialog.isShowing");
//                }
                } else {

                    showToastInputCity();
                    searching = false;

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
