package com.commax.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.login.Common.AboutFile;
import com.commax.login.Common.BuyerID;
import com.commax.login.Common.FileEx;
import com.commax.login.Common.GetMacaddress;
import com.commax.login.Common.Network_state;
import com.commax.login.Common.ServerIPLocal;
import com.commax.login.Common.SharedPreference_file;
import com.commax.login.Common.TypeDef;
import com.commax.login.UC.Utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /* 선언  */
    JSONHelper_main jsonHelper_main = new JSONHelper_main();
    GetMacaddress getMacaddress;
    Utility utility = new Utility();
    Customdialog_exit_one_button customdialog_exit_one_button;
    Network_state network_state = new Network_state();
    SharedPreference_file sharedPreference_file;
    BuyerID mBuyerID = new BuyerID();
    AboutFile aboutFile = new AboutFile();

    //singleton pattern
    private static MainActivity instance;
    public static MainActivity getInstance() {
        return instance;
    }

    //uracle servrer ip cloud_svr.i
    public String cloud_ip = readCloudDNSfile(TypeDef.AuthServer_DNS);
    public String client_id = readCloudDNSfile(TypeDef.Client_ID);
    public String client_secret = readCloudDNSfile(TypeDef.Client_Secret);
    public String model_name = readCloudDNSfile(TypeDef.ProductModel);

    private Context context;
    public boolean id_overlap_check_flag = true;
    public ToastMessageHandler toastHandler = null;

    /*UI*/
    EditText name_edit;
    EditText id_edit;
    EditText pwd_edit;
    EditText pwd_check_edit;
    Button register_btn;
    Button id_overlap_check_btn;

    /*custom toast pxd*/
    public LayoutInflater toastInflater;
    public View toastLayout;
    public TextView toastTextView;

    /*Strings */
    String name;
    String id;
    String password;
    String password_check;
    public String before_id;
    String mac_address;
    String nation_code;
    Spinner nation_spinner;

    //가입 할때 WorkType ( 통합인증 인터페이스 문서 참조)
    String new_string = TypeDef.new_string;
    String map_string = TypeDef.map;

    //Local Server
    String LocalserverIP;
    String site_code;

    //resource workType check flag  => true : map , flase : new
    public boolean resource_check;

    //nation code, name
    public ArrayList<String> mListData = new ArrayList<String>();
    public ArrayList<String> mListCode = new ArrayList<String>();

    // progress loading view
    private View mLoadingContainer;
    LinearLayout all_view;

    /* 70ux 이전 서버 계정 ID/ pwd */
    String before_server_id ;
    String before_server_password ;
    public boolean before_server_exist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        context = this;
        getMacaddress = new GetMacaddress(context);
        sharedPreference_file = new SharedPreference_file(context);

        //make properties 파일
        try {
            aboutFile.makefile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(TypeDef.nexel_activity_70UX)
        {
            setContentView(R.layout.activity_main_70ux_nexel);
        } else if (TypeDef.ip_home_iot_new_ui) {
            setContentView(R.layout.activity_main_new_ui);
        } else {
            setContentView(R.layout.activity_main);
        }

        instance = this; //Activity 속성 다른곳에서 사용하도록

        /* custom toast pxd */
        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //file read string
        mac_address = getMacaddress.getMacAddress();
        Log.d(TAG, "mac address : " + mac_address);
        if (TextUtils.isEmpty(mac_address)) {
            showToastOnWorking(getString(R.string.mac_error));
        }

        Log.d(TAG, "client_id : " + client_id + " , client_secret : " + client_secret + " , model_name : " + model_name);
        //loading view
        mLoadingContainer = findViewById(R.id.loading_container);
        all_view = (LinearLayout) findViewById(R.id.all_view);

        /*utf-8 encode*/
        try {
            client_id = URLEncoder.encode(client_id, "UTF-8");
            client_secret = URLEncoder.encode(client_secret, "UTF-8");
//            model_name = URLEncoder.encode(model_name, "UTF-8");
//            cloud_ip = URLEncoder.encode(cloud_ip, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //TODO onResume?
        if(TypeDef.Connect_Local_Server)
        {
            //Local Server IP
            try
            {
                ServerIPLocal serverIPLocal = new ServerIPLocal(context);
                LocalserverIP = serverIPLocal.getValue();
                aboutFile.writeFile(TypeDef.LocalServerIP,LocalserverIP);
                Log.d(TAG, "LocalServer IP : " + LocalserverIP);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.d(TAG, " local Server not Use ");
        }

        //toast handler
        toastHandler = new ToastMessageHandler();

        //70UX 일 경우만 동작
        if(TypeDef.compile_70UX)
        {
            //get intent
            try{
                Intent myintent = getIntent();
                String action_name = myintent.getAction();

                Log.d(TAG, " myintetn = " + action_name);
                if(myintent != null)
                {
                    if(TextUtils.isEmpty(myintent.getAction()))
                    {
                        before_server_id = myintent.getStringExtra("beforeID");
                        before_server_password = myintent.getStringExtra("beforePassword");
                        before_server_exist = true;
                        Log.d(TAG ,"before server id/ password = " + before_server_id + "/" + before_server_password);
                    }
                    else
                    {
                        Log.d(TAG, "그냥 메인 실행됨");
                    }
                }
                myintent = null;

                //70UX 루비에 서버에서 계정이 존재하면 다이얼로그를 띄우고
                if (TextUtils.isEmpty(aboutFile.readFile("create_account"))) {
                    String[] get_id_pwd = {"before_server","http://www.ruvie.co.kr/member_info.html?method=memberInfo&mac=", mac_address , "main"};
                    startTask(get_id_pwd);
                    Log.d(TAG, "working");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //70UX 일경우만 사용
    public void main_70ux()
    {
        /* 2016-11-09
        * 앱이 실행되면 루비에 계정이 있는지를 먼저 체크하고 해당 ID, pwd  정보를 확인하고 가져오면 다이얼로그를 띄운다. */
        if(!(TextUtils.isEmpty(before_server_id)))
        {
            Log.d(TAG, "before server id");
            id_edit.setText(before_server_id);
        }
        else
        {
            Log.d(TAG, "nothing in before server ID");
        }

        if(!(TextUtils.isEmpty(before_server_password)))
        {
            Log.d(TAG, "before server password");
            pwd_edit.setText(before_server_password);
            pwd_check_edit.setText(before_server_password);
        }
        else
        {
            Log.d(TAG, "nothing in before server Password");
        }

        if(before_server_exist)
        {
            try
            {
                String[] ux_dialog_guide = {getString(R.string.notification) , getString(R.string.ux_server_guide_message) , TypeDef.MainActivity , TypeDef.ux_dialog};
                customdialog_exit_one_button = new Customdialog_exit_one_button(context , ux_dialog_guide );
                customdialog_exit_one_button.show();
                Log.d(TAG, " one button 다이얼로그 띄움");
            }catch (Exception e)
            {
                Log.d(TAG, "App 종료로 dialog 안띄움");
                e.printStackTrace();
            }
        }
        else
        {
            Log.d(TAG, "루비에 계정이 존재하지 않습니다.");
        }
        //exist 초기화
        before_server_exist = false;

    }


    //Custom Toast
    private void showToastOnWorking(String txt)
    {
        Toast toast = new Toast(getApplicationContext());
        toastTextView.setText(txt);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public void initData() {
        Log.d(TAG, " init Data ");

        name_edit = (EditText) findViewById(R.id.name_edit);
        //id
        id_edit = (EditText) findViewById(R.id.id_input_register);
        id_edit.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num()});   //숫자와 알파벳만 입력
        id_edit.setPrivateImeOptions("defaultInputmode=english;");               //영어 키패드 먼저 나오기
        //pwd
        pwd_edit = (EditText) findViewById(R.id.password_input_register);
        pwd_edit.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num_ect()}); //숫자와 알파벳 특수기호 입력
        pwd_edit.setPrivateImeOptions("defaultInputmode=english;");                 //영어 키패드 먼저 나오기
        //pwd_check
        pwd_check_edit = (EditText) findViewById(R.id.password_input_register_check);//숫자와 알파벳 특수기호 입력
        pwd_check_edit.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num_ect()});//영어 키패드 먼저 나오기
        pwd_check_edit.setPrivateImeOptions("defaultInputmode=english;");
        //button , spinner
        register_btn = (Button) findViewById(R.id.register_btn);
        id_overlap_check_btn = (Button) findViewById(R.id.id_overlap_check);

        nation_spinner = (Spinner) findViewById(R.id.nationcode);
        nation_spinner.setOnItemSelectedListener(this);
        nation_spinner.setOnTouchListener(spinnerOnTouch);
        //cloud_svr.i 파일에 필요한 정보가 다 있는지 사전 체크
        if (TextUtils.isEmpty(client_id) || TextUtils.isEmpty(client_secret) || TextUtils.isEmpty(cloud_ip) ) {
            showToastOnWorking(getString(R.string.file_error));
        }
    }

    private View.OnTouchListener spinnerOnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(TAG, " spinner clicked");
                //키보드 숨기기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pwd_check_edit.getWindowToken(), 0);
            }
            return false;
        }
    };

    public void spinnerAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter;
                if(TypeDef.ip_home_iot_new_ui)
                {
                    //spinner 색상 변경
                    adapter = new ArrayAdapter<String>(context,
                            R.layout.spinner_center_item_new_ui, mListData);
                }
                else
                {
                    adapter = new ArrayAdapter<String>(context,
                            R.layout.spinner_center_item, mListData);
                }

                Log.d(TAG, "mListData.length() = " + mListData.size() + " , mListCode.length() = " + mListCode.size());
                //spinner item 색상
                adapter.setDropDownViewResource(R.layout.spinner_center_item);
                nation_spinner.setAdapter(adapter);
                //Spinner 의 높이를 맞추기 위하여 해당 View의 pixel 값을 받아와서 적용
                DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
                int width = dm.widthPixels;
                int height = dm.heightPixels;
                Log.e(TAG, "width: " + width + " , height : " + height);

                if(!TypeDef.nexel_activity_70UX)
                {
                    //Spinner sync
                    if (width == 1920) {
                        //1920 * 1080
                        nation_spinner.setDropDownVerticalOffset(-77);
                    }else if(width == 1024)
                    {
                        //1024 * 600
                        nation_spinner.setDropDownVerticalOffset(+7);
                    }
                    else
                    {
                        //800 * 480
                        nation_spinner.setDropDownVerticalOffset(-43);
                    }
                }
            }
        });
    }

    public void onClick(View view) {
        Log.d(TAG, "onClick");
        name = name_edit.getText().toString();
        id = id_edit.getText().toString();
        password = pwd_edit.getText().toString();
        password_check = pwd_check_edit.getText().toString();
        int id_length = id_edit.length();
        int password_length = password.length();
        //키보드 숨기기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pwd_check_edit.getWindowToken(), 0);
        //등록 버튼
        if (view.equals(register_btn)) {
            // ture : ID overlap , 중복check 한 ID와 현재 등록하는 ID가 같은지 check
            try
            {
                if(TextUtils.isEmpty(mac_address))
                {
                    //맥정보가 없을 경우 가입이 되어서는 안된다.
                    showToastOnWorking(getString(R.string.mac_error));
                }
                //로컬 서버 사용 유무 및 sitecode 가져왔는지 판단
                else if(TypeDef.Connect_Local_Server && TextUtils.isEmpty(site_code))
                {
                    //로컬 서버에서 사이트 코드를 가져오지 못했을때 가입이 되면 안된다.
                    showToastOnWorking(getString(R.string.localserver_status_check));
                }
                else if (id_length == 0) {
                    showToastOnWorking(getString(R.string.id_hint));
                } else if (nation_code.equals(TypeDef.not)) {
                    showToastOnWorking(getString(R.string.select_nationCode));
                } else if (password_length == 0) {
                    showToastOnWorking(getString(R.string.password_hint));
                } else if (password_check.length() == 0) {
                    showToastOnWorking(getString(R.string.password_check_hint));
                } else if (id_overlap_check_flag || (!before_id.equals(id))) {
                    showToastOnWorking(getString(R.string.please_check_id_overlap));
                }
                //false : ID use available
                else {
                    if (name.length() > 20) {
                        showToastOnWorking(getString(R.string.name_length_check));
                    } else if (password_length > 20 || password_length < 6) {
                        showToastOnWorking(getString(R.string.password_length_check));
                    } else {
                        //pwd  , pwd 확인 비교
                        if (password.equals(password_check)) {
                            if (resource_check) {
                                //urlEncording 처리
                                try {
                                    try{
                                        password = URLEncoder.encode(password, "UTF-8");
                                        name = URLEncoder.encode(name, "UTF-8");
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                        name = null;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                new AppTask().execute("cmx", "register", name, id, password, mac_address, model_name, nation_code, map_string);
                                Log.d(TAG, "map startTask");
                            } else {
                                try {
                                    password = URLEncoder.encode(password, "UTF-8");
                                    name = URLEncoder.encode(name, "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                new AppTask().execute("cmx", "register", name, id, password, mac_address, model_name, nation_code, new_string);
                                Log.d(TAG, "new startTask");
                            }
                        } else {
                            showToastOnWorking(getString(R.string.password_passwordcheck_different));
                        }
                    }
                    Log.d(TAG, " name : " + name);
                }
            }catch (Exception e)
            {
                showToastOnWorking(getString(R.string.check_internet_connect));
            }
        }
        //num : 13 ID중복 확인 버튼
        else if (view.equals(id_overlap_check_btn)) {
            if (id_length == 0) {
                showToastOnWorking(getString(R.string.id_hint));
            } else if (id_length < 4 || id_length > 20) {
                showToastOnWorking(getString(R.string.id_length_check));
            } else {
                new AppTask().execute("cmx", "user", id);
            }
        }
        //back button
        else if (view.equals(findViewById(R.id.back_button))) {
            Log.d(TAG, "x button clicked");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        id_overlap_check_flag = true;
        Log.d(TAG, "create_account  : " + aboutFile.readFile(TypeDef.create_account));
        resource_check = false;

        //file 에 create_account=yes 이면 다른페이지 보이도록 하고 아니면 현재 페이지 보이고
        if (TextUtils.isEmpty(aboutFile.readFile(TypeDef.create_account))) {
            initData();
            //nation code array clear
            mListData.clear();
            mListCode.clear();

            // /mnt/sdcard/networkstate.i 파일에서 true : network is working , false : networ is not working 값을 읽어온다
            String networkstate = network_state.getNetworkState();
            if(networkstate.equals(TypeDef.fasle) && TypeDef.Network_i_status)
            {
                //network is not working dialog
                Log.d(TAG, "network is not working ");
                show_no_network_dialog();
            }
            else
            {
                //network is working
                //국가코드는 MAX 한글로 15글자이다.
                MainActivity.getInstance().mListData.add(getString(R.string.select_nation));
                MainActivity.getInstance().mListCode.add("not");
                //Progress bar loading
                setLoadingView(true);
                //API Call
                new AppTask().execute("cmx", "countries", client_id, model_name, client_secret);
                new AppTask().execute("oauth", "authorize", mac_address, model_name, client_id, client_secret , TypeDef.MainActivity);

                //for IP Home IoT
                //로컬 서버가 연결되어 있으면 로컬서버에서 무조건 사이트 코드를 가져와야만 회원가입 되게 동작 해야한다.(talk with 권희훈책임님)
                if(TypeDef.Connect_Local_Server)
                {
                    //get Site Code
                    String[] ip_parsing  = LocalserverIP.split("[.]");
                    if(ip_parsing[0].equals("10") || ip_parsing[0].equals("192") || ip_parsing[0].equals("220"))
                    {
                        //10.x.x.x 가 로컬 서버 IP인 경우에만 로컬 서버에서 사이트코드 가져온다
                        //아닌 경우는 version.i 에서 사이트 코드 읽어온다.
                        new AppTask().execute("sitecode", mac_address , LocalserverIP );
                    }
                    else
                    {
                        site_code = utility.getSiteCode();
                        Log.d(TAG, "From version.i = >  site code : " + site_code);
                        aboutFile.writeFile(TypeDef.SiteCode, site_code);

                        Log.e(TAG, "site_code : " + site_code);
                    }
                }
                else
                {
                    site_code = utility.getSiteCode();
                    Log.d(TAG, "From version.i = >  site code : " + site_code);
                    aboutFile.writeFile(TypeDef.SiteCode, site_code);

                    Log.e(TAG, "site_code : " + site_code);
                }

                if(TypeDef.Lotte_navigation)
                {
                    //하단 네비게이션 키 바 Broadcast
                    sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_SHOW_ACTION);
                    Log.i(TAG, "system key show");
                }else if(TypeDef.IP_Home_IoT_navigation)
                {
                    Log.d(TAG, " IP Home IoT navigation Hide ");
                    //hide navigation bar made by Diana Kim
                    hideNavigationBar();
                }
            }
        } else {
            //SubActivity 호출
            Intent intentSubActivity = new Intent(MainActivity.this, SubActivity.class);
            startActivity(intentSubActivity);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        if(TypeDef.Lotte_navigation)
        {
            sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //TODO for test
        Log.d(TAG, "code : " + mListCode.get(position));
        nation_code = mListCode.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        showToastOnWorking(getString(R.string.select_nationCode));
        nation_code = "not";
    }
    // ID 중복 체크 에러 Toast message 핸들러
    class ToastMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showToastOnWorking(String.valueOf(msg.obj));
                    break;
                case 1:
                    setLoadingView(false);
                    break;
                case 2:
                    if(TypeDef.Network_i_status)
                    {
                        show_no_network_dialog();
                    }
                    break;
                case 3:
                    if(TypeDef.compile_70UX) {
                        main_70ux();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 로딩뷰 표시 설정
     *
     * @param isView 표시 유무
     */
    public void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            Log.d(TAG, "Loading View display");
            mLoadingContainer.setVisibility(View.VISIBLE);
            all_view.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            Log.d(TAG, "List View display");
            mLoadingContainer.setVisibility(View.GONE);
            all_view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 작업 시작
     */
    void startTask(String[] params) {
        Log.d(TAG, "startTask");
        Log.d(TAG, "AppTask exe");
        new AppTask().execute(params);
    }

    /**
     * 작업 태스크
     *
     * @author nohhs
     */
    private class AppTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "AppTask : onPreExecute");
        }

        @Override
        protected Void doInBackground(String... params) {
            // 어플리스트 작업시작
            try {

                jsonHelper_main.restCall(context, "127.0.0.1", cloud_ip, params[0], params[1], params, toastHandler);
                Log.d(TAG, "Jsonhelper_main.restCall()");

            } catch (Exception e) {

                Message msg = MainActivity.getInstance().toastHandler.obtainMessage();
                msg.what = 0;
                msg.obj = String.valueOf(getString(R.string.network_error));
                MainActivity.getInstance().toastHandler.sendMessage(msg);
                Log.e(TAG, "restCall error");

                e.printStackTrace();
            }
            Log.d(TAG, "AppTask : doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "AppTask : onPostExecute");
            setLoadingView(false);
        }
    }

    public void show_no_network_dialog()
    {
        String[] custom_dialog = { getString(R.string.notification) ,getString(R.string.no_network_response) , TypeDef.MainActivity , TypeDef.network_dialog};
        customdialog_exit_one_button = new Customdialog_exit_one_button(context, custom_dialog);
        customdialog_exit_one_button.show();
    }
    // 숫자 , 영문 만 허용
    protected InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //ID
    protected class CustomInputFilter_alpha_num implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (source.equals("") || ps.matcher(source).matches()) {

                return source;
            } else {

                if(TextUtils.isEmpty(source))
                {
                    Log.d(TAG, " back space ");
                }
                else
                {
                    showToastOnWorking(getString(R.string.edit_alpha_num_only));
                }
                return "";
            }
        }
    }

    // pWD 특정 특수문자 , 숫자, 영문
    protected class CustomInputFilter_alpha_num_ect implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[!@#$%^&*+=-_a-zA-Z0-9]+$");

            if (source.equals("") || ps.matcher(source).matches()) {

                return source;
            } else {
                if(TextUtils.isEmpty(source))
                {
                    Log.d(TAG, " back space ");
                }
                else {
                    showToastOnWorking(getString(R.string.edit_alpha_num_etc_only));
                }
                return "";
            }
        }
    }

    //cloud_svr.i 파일 read
    private String readCloudDNSfile(String value) {

        FileEx io = new FileEx();
        String[] files = null;
        String server_dns = "";

        try {
            files = io.readFile("/user/app/bin/cloud_svr.i");
//            int i = changePermissons(files,0777);
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (files == null) {
            return null;
        }

        if (files.length > 0) {
            // ���� üũ
            if (files == null) {
                return null;
            }
            if ("".equals(files[0])) {
                return null;
            }
            if ("-1".equals(files[0])) {
                return null;
            }
        }

        for (int i = 0; i < files.length; i++) {
            String line = files[i];
            if (line.contains("#")) {
                continue;
            }
            if (line.contains(value)) {

                server_dns = line.replace(value + "=", "");
            }
        }
        return server_dns;

    }

    //made by dianna Kim for IP Home IoT navigation bar hide
    public void hideNavigationBar()
    {
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

    public void clearNavigationBar(){

        try {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            // This work only for android 4.4+
            ((Activity)context).getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Send Custom Broadcast Message
    public void sendCustomBroadcastMessage(String strBroadcastAction) {
        try
        {
            Intent intent = new Intent(strBroadcastAction);
            sendBroadcast(intent);
            intent = null;
            Log.i(TAG, "sendCustomBroadcastMessage : " + strBroadcastAction);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

   /*
      public int changePermissons(String[] path, int mode) throws Exception {
        Class<?> fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions = fileUtils.getMethod("setPermissions",
                String.class, int.class, int.class, int.class);

        return (Integer) setPermissions.invoke(null, path.getAbsolutePath(),
                mode, -1, -1);
    }

  //TODO 안드로이드 6.0 권한 대응 하기 위한 test
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    public void request_permission()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.getInstance(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    //TODO sdk 버전 체크하는 로직이 필요하다다
   public void executeButton(){
        Log.d(TAG, "executeButton()");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d(TAG, "permissionChekch : " + permissionCheck);

        //TODO 퍼미션이 있는지를 먼저 체크해서 해당 퍼미션에 관한 권한을 받아야 한다.
        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // 권한 없음
            Log.i(TAG, "Permission denied");
        }else{
            // 권한 있음
            Log.i(TAG, " Permission granted");
        }

        final String[] PERMISSIONS_STORAGE = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        //Asking request Permissions
        ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE, 9);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult()");
        boolean writeAccepted = false;
        switch(requestCode){
            case 9:
                writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "switch case 9:");
                break;
        }

        if(writeAccepted)
        {
            String state = Environment.getExternalStorageState();
            Log.d(TAG, "state :  " + state);
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File dir = new File(Environment.getExternalStorageDirectory()+"/"+"JiyoungTest");
                Log.d(TAG," directory : " + Environment.getExternalStorageDirectory()+"/"+"JiyoungTest");
                if(!dir.exists())
                {
                    boolean b = dir.mkdirs();
                    if(b){
                        Log.i("TAG", "WOW! "+dir+" created!");
                    }else{
                        Log.e("TAG", "OPS! "+dir+" NOT created! To be sure: new dir exist? "+dir.exists());
                    }
                }
                else
                {
                    Log.d(TAG, "directory is already exist");
                }
            }
        }
    }*/
}
