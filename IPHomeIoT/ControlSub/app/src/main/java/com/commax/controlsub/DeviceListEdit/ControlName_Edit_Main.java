package com.commax.controlsub.DeviceListEdit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;
import com.commax.pam.db.interfaces.MySQLConnection;

import java.util.ArrayList;

/**
 * Created by OWNER on 2016-12-15.
 */
public class ControlName_Edit_Main extends LinearLayout implements View.OnClickListener {

    public String TAG = ControlName_Edit_Main.class.getSimpleName();

    Context mContext;

    public ArrayList<com.commax.control.Common.DeviceInfoSimple> mArrayList;

    // layout
    LayoutInflater inflater;
    View rootView;
    public static ControlName_Edit_Main _instance;
    public static ControlName_Edit_Main getInstance()  {
        return _instance;
    }

    // ListView
    ListView mListViewLight;
    ListView mListViewIndoor;
    ListView mListViewEnergy;
    ListView mListViewSafety;

    //ControlName_Edit_List_Adapter
    ControlName_Edit_List_Adapter mAdapterLight;
    ControlName_Edit_List_Adapter mAdapterIndoor;
    ControlName_Edit_List_Adapter mAdapterEnergy;
    ControlName_Edit_List_Adapter mAdapterSafety;
    // ArrayList
    ArrayList<ControlName_Edit_ListCell> mListLight;
    ArrayList<ControlName_Edit_ListCell> mListIndoor;
    ArrayList<ControlName_Edit_ListCell> mListEnergy;
    ArrayList<ControlName_Edit_ListCell> mLIstSafety;
    /*UI*/
    public ImageView backbutton;
    public ImageView savebutton;
    public ImageView deletebutton;

    public RelativeLayout mRelativeLayout;

    TextView title_text;
    LinearLayout guid_text_layout;
    TextView guide_text;
    TextView okay_text;
    TextView cancle_text;

    EditText for_keyborad_gone_edittext;

    //TextView
    TextView light_textview;
    TextView indoor_textview;
    TextView energy_textview;
    TextView safety_textview;

    // Handler
    Handler handler;
    //device delete mode flag , handler
    LinearLayout select_all_checkbox;
    CheckBox all_checkbox;

    public boolean m_bmonitor_start = false;
    String device_delete_count = null;
    int checkbox_count =0;


    public boolean mNickName_success_flag = false;
    Customdialog_small_two_button cutoff_dialog;
    Customdialog_small_one_button nickname_null_dialog;


    //custom toast pxd
    public LayoutInflater toastInflater;
    public View toastLayout;
    public TextView toastTextView;


    public ControlName_Edit_Main(Context context)
    {
        super(context);
        mContext = context;

    }

    public ControlName_Edit_Main(Context context , ArrayList<com.commax.control.Common.DeviceInfoSimple> arrayList)
    {
        super(context);
        mContext = context;
        mArrayList = arrayList;
        init(context);
    }

    public void init(Context context)
    {
        _instance = this;

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.activity_device_nickname_edit, this);

        //pxd custom toast
        try {
            toastInflater = MainActivity.getInstance().getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        }catch (Exception e){
            e.printStackTrace();
        }

        mListViewLight = (ListView)rootView.findViewById(R.id.listViewLight);
        mListViewIndoor = (ListView)rootView.findViewById(R.id.listViewIndoor);
        mListViewEnergy = (ListView)rootView.findViewById(R.id.listViewEnergy);
        mListViewSafety = (ListView)rootView.findViewById(R.id.listViewSafety);

        mRelativeLayout = (RelativeLayout)rootView.findViewById(R.id.title_bar);

        title_text = (TextView)rootView.findViewById(R.id.title_text);
        guid_text_layout = (LinearLayout)rootView.findViewById(R.id.guide_text_layout);
        cancle_text = (TextView)findViewById(R.id.cancle_text);
        okay_text= (TextView)findViewById(R.id.ok_text);

        backbutton = (ImageView) rootView.findViewById(R.id.title_back_button);
        savebutton =(ImageView)rootView.findViewById(R.id.compelete_button);
        deletebutton =(ImageView)rootView.findViewById(R.id.device_del_button);
        guide_text = (TextView)rootView.findViewById(R.id.guide_text);
        for_keyborad_gone_edittext = (EditText)rootView.findViewById(R.id.text_edit);


        light_textview = (TextView)rootView.findViewById(R.id.light_textview);
        indoor_textview = (TextView)rootView.findViewById(R.id.indoor_textview);
        energy_textview = (TextView)rootView.findViewById(R.id.energy_textview);
        safety_textview = (TextView)rootView.findViewById(R.id.safety_textview);

        //select all checkbox
        select_all_checkbox = (LinearLayout)findViewById(R.id.all_checkbox_linear);
        all_checkbox = (CheckBox)findViewById(R.id.checkbox_all_select);


        backbutton.setOnClickListener(this);
        savebutton.setOnClickListener(this);
        deletebutton.setOnClickListener(this);
        okay_text.setOnClickListener(this);
        cancle_text.setOnClickListener(this);

        mListLight = new ArrayList<ControlName_Edit_ListCell>();
        mListIndoor = new ArrayList<ControlName_Edit_ListCell>();
        mListEnergy = new ArrayList<ControlName_Edit_ListCell>();
        mLIstSafety = new ArrayList<ControlName_Edit_ListCell>();

        if(TypeDef.OPTION_DELETE_MODE)
        {
            deletebutton.setVisibility(VISIBLE);
        }
        else
        {
            deletebutton.setVisibility(GONE);
        }

        //List 분류
        List_divide();

        try {
            //  open DB
            MainActivity.getInstance().OpenDB();
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        Log.d(TAG, "save button setAlpha 0.2F");
        //TODO sava button able/ disable 에서 hide and show 로 변경
        savebutton.setVisibility(GONE);
        if(savebutton.getVisibility() == GONE)
        {
            Log.d(TAG, "save button is gone");
            mRelativeLayout.removeView(deletebutton);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_END , R.id.delete_button);
            deletebutton.setLayoutParams(rlParams);
            mRelativeLayout.addView(deletebutton);
        }

    /*    savebutton.setAlpha(0.2F);
        savebutton.setEnabled(false);*/

        //전체 선택 해제 체크 박스
        all_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //체크박스가 선택 체크 되었을때
                    mAdapterLight.mDelete_List.clear();
                    mAdapterLight.mDelete_List.addAll(mListLight);
                    mAdapterLight.show_all_checkbox(true);
                    mListViewLight.setAdapter(mAdapterLight);

                    mAdapterIndoor.mDelete_List.clear();
                    mAdapterIndoor.mDelete_List.addAll(mListIndoor);
                    mAdapterIndoor.show_all_checkbox(true);
                    mListViewIndoor.setAdapter(mAdapterIndoor);

                    mAdapterEnergy.mDelete_List.clear();
                    mAdapterEnergy.mDelete_List.addAll(mListEnergy);
                    mAdapterEnergy.show_all_checkbox(true);
                    mListViewEnergy.setAdapter(mAdapterEnergy);

                    mAdapterSafety.mDelete_List.clear();
                    mAdapterSafety.mDelete_List.addAll(mLIstSafety);
                    mAdapterSafety.show_all_checkbox(true);
                    mListViewSafety.setAdapter(mAdapterSafety);

                }
                else
                {
                    if((mListLight.size() + mListIndoor.size() + mListEnergy.size() + mLIstSafety.size()) != (mAdapterLight.mDelete_List.size() + mAdapterIndoor.mDelete_List.size() + mAdapterEnergy.mDelete_List.size() + mAdapterSafety.mDelete_List.size()))
                    {
                        Log.d(TAG, "전체 선택 만 해제");
                    }
                    else
                    {
                        //체크박스가 선택 해제 되었을때
                        mAdapterLight.mDelete_List.clear();
                        mAdapterLight.show_all_checkbox(false);
                        mListViewLight.setAdapter(mAdapterLight);

                        mAdapterIndoor.mDelete_List.clear();
                        mAdapterIndoor.show_all_checkbox(false);
                        mListViewIndoor.setAdapter(mAdapterIndoor);

                        mAdapterEnergy.mDelete_List.clear();
                        mAdapterEnergy.show_all_checkbox(false);
                        mListViewEnergy.setAdapter(mAdapterEnergy);

                        mAdapterSafety.mDelete_List.clear();
                        mAdapterSafety.show_all_checkbox(false);
                        mListViewSafety.setAdapter(mAdapterSafety);
                    }
                }
            }
        });
    }

    // category별 list 를 분리시킨다.
    public void List_divide()
    {
        com.commax.control.Common.DeviceInfoSimple simple_data;

        try {
            if (mArrayList.size() <= 0) {
                MainActivity.getInstance().showToastOnWorking(MainActivity.getInstance().getString(R.string.no_device));
            }
            else
            {
                for (int i = 0; i < mArrayList.size(); i++)
                {
                    simple_data = mArrayList.get(i);
                    //categry Light
                    if (simple_data.nCategory.equals("2"))
                    {
                        mListLight.add(new ControlName_Edit_ListCell(simple_data.nickName, "Light", simple_data.rootUuid));
                    }
                    //category Indoor
                    else if (simple_data.nCategory.equals("3"))
                    {
                        mListIndoor.add(new ControlName_Edit_ListCell(simple_data.nickName, "Indoor", simple_data.rootUuid));
                    }
                    //category Energy
                    else if (simple_data.nCategory.equals("4"))
                    {
                        mListEnergy.add(new ControlName_Edit_ListCell(simple_data.nickName ,"Energy", simple_data.rootUuid));
                    }
                    //category Safety
                    else if (simple_data.nCategory.equals("5")) {
                        mLIstSafety.add(new ControlName_Edit_ListCell(simple_data.nickName, "Safety", simple_data.rootUuid));
                    }
                }
            }

            mAdapterLight = new ControlName_Edit_List_Adapter(mContext, mListLight);
            mAdapterLight.setDynamicHeight(mListViewLight);
            mListViewLight.setAdapter(mAdapterLight);

            mAdapterIndoor = new ControlName_Edit_List_Adapter(mContext, mListIndoor);
            mAdapterIndoor.setDynamicHeight(mListViewIndoor);
            mListViewIndoor.setAdapter(mAdapterIndoor);

            mAdapterEnergy = new ControlName_Edit_List_Adapter(mContext, mListEnergy);
            mAdapterEnergy.setDynamicHeight(mListViewEnergy);
            mListViewEnergy.setAdapter(mAdapterEnergy);

            mAdapterSafety = new ControlName_Edit_List_Adapter(mContext, mLIstSafety);
            mAdapterSafety.setDynamicHeight(mListViewSafety);
            mListViewSafety.setAdapter(mAdapterSafety);

            device_header_show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        Log.d(TAG , "onClick");
        //키보드 숨기기
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(for_keyborad_gone_edittext.getWindowToken(), 0);
        for_keyborad_gone_edittext.clearFocus();

        if(view.equals(backbutton))
        {
            //닉네임 arraylist 초기화
            mArrayList.clear();
            MainActivity.getInstance().finish();
        }
        else if(view.equals(savebutton))
        {
            if(mAdapterLight.nickname_null_check() || mAdapterEnergy.nickname_null_check() || mAdapterIndoor.nickname_null_check() || mAdapterSafety.nickname_null_check())
            {
                MainActivity.getInstance().hideNavigationBar();
                MainActivity.getInstance().clearNavigationBar();
                //custom dialog tow button : [1] : title , [2] : text
                String[] dialog= {MainActivity.getInstance().getString(R.string.notification), MainActivity.getInstance().getString(R.string.no_nickname)};
                nickname_null_dialog = new Customdialog_small_one_button(mContext,dialog );
                nickname_null_dialog.show();
            }
            else
            {
                try{
                    mAdapterLight.save_button();
                    mAdapterIndoor.save_button();
                    mAdapterEnergy.save_button();
                    mAdapterSafety.save_button();

                    Log.d(TAG, "nick name save");
                    MainActivity.getInstance().finish();
                }
                catch(Exception e)
                {
                    Log.d(TAG, "nick name change fail");
                    e.printStackTrace();
                    MainActivity.getInstance().showToastOnWorking(MainActivity.getInstance().getString(R.string.do_it_again));
                }
            }


        }
        else if(view.equals(deletebutton))
        {
            try {
                m_bmonitor_start = true;
                //mDeleteList 초기화
                mAdapterLight.mDelete_List.clear();
                mAdapterIndoor.mDelete_List.clear();
                mAdapterEnergy.mDelete_List.clear();
                mAdapterSafety.mDelete_List.clear();

                startMonitor();
                change_mode("delete_mode");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(view.equals(okay_text))
        {
            try {
                MainActivity.getInstance().hideNavigationBar();
                MainActivity.getInstance().clearNavigationBar();
                //custom dialog tow button : [1] : title , [2] : text
                String[] dialog= {MainActivity.getInstance().getString(R.string.device_delete), MainActivity.getInstance().getString(R.string.device_delete_dialog_text)};
                cutoff_dialog = new Customdialog_small_two_button(mContext,dialog );
                cutoff_dialog.show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(view.equals(cancle_text))
        {
            try {
                change_mode("nickname_mode");
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void device_header_show()
    {
        if(mListLight.size() == 0)
        {
            light_textview.setVisibility(GONE);
        }
        if(mListIndoor.size() == 0)
        {
            indoor_textview.setVisibility(GONE);
        }
        if(mListEnergy.size() == 0)
        {
            energy_textview.setVisibility(GONE);
        }
        if(mLIstSafety.size() == 0)
        {
            safety_textview.setVisibility(GONE);
        }
    }

    public void change_mode(String mode)
    {
        //check box count 초기화
        checkbox_count = 0;
        mAdapterIndoor.mDelete_List.clear();

        if(mode.equals("delete_mode"))
        {
            //change the delete mode
            select_all_checkbox.setVisibility(VISIBLE);

            backbutton.setVisibility(GONE);
            guid_text_layout.setVisibility(GONE);
            deletebutton.setVisibility(GONE);
            savebutton.setVisibility(GONE);

            okay_text.setVisibility(GONE);
            cancle_text.setVisibility(VISIBLE);
            title_text.setText(R.string.device_delete);


            mAdapterLight.delete_mode = true;
            mListViewLight.setAdapter(mAdapterLight);

            mAdapterIndoor.delete_mode = true;
            mListViewIndoor.setAdapter(mAdapterIndoor);

            mAdapterEnergy.delete_mode = true;
            mListViewEnergy.setAdapter(mAdapterEnergy);

            mAdapterSafety.delete_mode = true;
            mListViewSafety.setAdapter(mAdapterSafety);
        }
        else if(mode.equals("nickname_mode"))
        {
            //change the delete mode
            select_all_checkbox.setVisibility(GONE);

            m_bmonitor_start = false;   //check box count 체크 하는 monitorThread flag
            device_delete_count = null; // check box count 갯수 초기화

            backbutton.setVisibility(VISIBLE);
            guid_text_layout.setVisibility(VISIBLE);
            deletebutton.setVisibility(VISIBLE);
//            savebutton.setVisibility(VISIBLE);

            savebutton.setEnabled(false);
            savebutton.setVisibility(GONE);

            if(savebutton.getVisibility() == GONE)
            {
                Log.d(TAG, "save button is gone");
                mRelativeLayout.removeView(deletebutton);
                RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_END , R.id.delete_button);
                deletebutton.setLayoutParams(rlParams);
                mRelativeLayout.addView(deletebutton);
            }

            okay_text.setVisibility(GONE);
            cancle_text.setVisibility(GONE);
            title_text.setText(R.string.edit_device);


            mAdapterLight.delete_mode = false;
            mListViewLight.setAdapter(mAdapterLight);

            mAdapterIndoor.delete_mode = false;
            mListViewIndoor.setAdapter(mAdapterIndoor);

            mAdapterEnergy.delete_mode = false;
            mListViewEnergy.setAdapter(mAdapterEnergy);

            mAdapterSafety.delete_mode = false;
            mListViewSafety.setAdapter(mAdapterSafety);
        }
    }

    //카테고리별 list업데이트
    public void updateDevice(String category)
    {
        if(category.equals("Light"))
        {
            mAdapterLight.setDynamicHeight(mListViewLight);
            mListViewLight.setAdapter(mAdapterLight);
//            mListViewLight.deferNotifyDataSetChanged();
        }
        else if(category.equals("Indoor"))
        {
            mAdapterIndoor.setDynamicHeight(mListViewIndoor);
            mListViewIndoor.setAdapter(mAdapterIndoor);
//            mListViewIndoor.deferNotifyDataSetChanged();
        }
        else if(category.equals("Energy"))
        {
            mAdapterEnergy.setDynamicHeight(mListViewEnergy);
            mListViewEnergy.setAdapter(mAdapterEnergy);
//            mListViewEnergy.deferNotifyDataSetChanged();
        }
        else if(category.equals("Safety"))
        {
            mAdapterSafety.setDynamicHeight(mListViewSafety);
            mListViewSafety.setAdapter(mAdapterSafety);
        }
        device_header_show();
    }

    public void remove_report (String remove_raw)
    {
        String rootUuid = "";
        String nickName = "";
        String category = null;
        //get device listCell
        rootUuid = MainActivity.getInstance().dataManager.mysql_getRootUuid(remove_raw);
        Log.d(TAG, "removeReport : " + rootUuid);
        if (!TextUtils.isEmpty(rootUuid))
        {
            nickName = removeDeviceLoop(mListLight, rootUuid);
            category = "Light";
            if (TextUtils.isEmpty(nickName)) {
                nickName = removeDeviceLoop(mListIndoor, rootUuid);
                category = "Indoor";
                if (TextUtils.isEmpty(nickName)) {
                    nickName = removeDeviceLoop(mListEnergy, rootUuid);
                    category = "Energy";
                    if (TextUtils.isEmpty(nickName)) {
                        nickName = removeDeviceLoop(mLIstSafety, rootUuid);
                        category = "Safety";
                    }
                }
            }
            if (!TextUtils.isEmpty(nickName)){
                //TODO category list별 업데이트를 진행해야 한다.
                updateDevice(category);
            }
        }
        //remove value 과정 : device list remove ->  UI Update
        Log.e(TAG, "removeReport : " + nickName);
    }

    public void add_updateReport(String add_raw)
    {
        //TODO 모바일에서도 디바이스 연동이 가능하기 때문에 addreport 오는 로직이 필요 할듯
        //개발 버전
        com.commax.control.Common.DeviceInfoSimple deviceInfoSimple = null;
        Log.d(TAG,"raw : " + add_raw);
        String rootUuid = MainActivity.getInstance().dataManager.mysql_getRootUuid(add_raw);
        String commax_device = MainActivity.getInstance().dataManager.mysql_getCommaxDevice(add_raw);
        String nickName = MainActivity.getInstance().dataManager.mysql_getNickName(rootUuid);
        Log.d(TAG, "commax_device : " + commax_device + "rootUuid : " + rootUuid + " nickName : " + nickName);
        String category = MainActivity.getInstance().divide_category_by_commaxdevice(commax_device);
        Log.d(TAG, "category : " + category);

        if(category.equalsIgnoreCase("Light"))
            category = "2";
        else if(category.equalsIgnoreCase("Indoor"))
            category = "3";
        else if(category.equalsIgnoreCase("Endoor"))
            category = "4";
        else if(category.equalsIgnoreCase("Safety"))
            category = "5";

        if (deviceInfoSimple != null) {
            deviceInfoSimple.nCategory = category;
            deviceInfoSimple.nickName = nickName;
            deviceInfoSimple.rootUuid = rootUuid;
            mArrayList.add(deviceInfoSimple);
        }
        List_divide();
    }

    public String removeDeviceLoop(ArrayList<ControlName_Edit_ListCell> deviceList, String rootUuid )
    {
        String nickName= "";
        ControlName_Edit_ListCell listCell= null;

        for (int i = 0; i < deviceList.size() ; i++) {
            listCell = deviceList.get(i);
            if (rootUuid.equalsIgnoreCase(listCell.getRootUuid())) {
                //rootUuid 만 비교해도 됨
                nickName = listCell.getNickName();
                deviceList.remove(i);
                break;
            }
        }

        return nickName;
    }

    public void nickname_update_report(String rootuuid , String nickname)
    {
        //TODO 업데이트 처리 진행 해야한다.
        for(int i = 0 ; i <mListLight.size() ; i++) {
            ControlName_Edit_ListCell listCell = mListLight.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                android.util.Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mListLight.get(i).nickname = nickname;
                updateDevice("Light");
                break;
            }
        }
        for(int i = 0 ; i <mListIndoor.size() ; i++) {
            ControlName_Edit_ListCell listCell = mListIndoor.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                android.util.Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mListIndoor.get(i).nickname = nickname;
                updateDevice("Indoor");
                break;
            }
        }
        for(int i = 0 ; i <mListEnergy.size() ; i++) {
            ControlName_Edit_ListCell listCell = mListEnergy.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                android.util.Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mListEnergy.get(i).nickname = nickname;
                updateDevice("Energy");
                break;
            }
        }

        for(int i = 0 ; i <mLIstSafety.size() ; i++) {
            ControlName_Edit_ListCell listCell = mLIstSafety.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                android.util.Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mLIstSafety.get(i).nickname = nickname;
                updateDevice("Safety");
                break;
            }
        }

    }

    public void startTask(String changeNickname , ControlName_Edit_ListCell cell)
    {
        String rootuuid = cell.getRootUuid();
        new AppTask().execute(changeNickname , rootuuid );
    }

    /**
     * 작업 태스크
     * @author nohhs
     */
    private class AppTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩뷰 시작
            android.util.Log.d(TAG, "AppTask : onPreExecute");
        }
        @Override
        protected Void doInBackground(String... params) {
            // 어플리스트 작업시작
            try {
                android.util.Log.d(TAG , "rootUuid : " + params[1]  + ", nickname : " + params[0]);
                MySQLConnection.getInstance().updateDeviceNickName(params[1], params[0]);
                android.util.Log.d(TAG,"JSONHelper.restCall()");

                Intent intent = new Intent("com.commax.controlsub.NickName_ACTION");
                intent.putExtra("rootUuidStr",params[1]);
                intent.putExtra("nickNameStr", params[0]);
                mContext.sendBroadcast(intent);
                mNickName_success_flag = true;

            } catch (Exception e) {
                android.util.Log.e(TAG,"restCall error . Server can't connect");
                e.printStackTrace();
            }
            android.util.Log.d(TAG, "AppTask : doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            android.util.Log.d(TAG, "AppTask : onPostExecute result = " + result);
        }
    };


    //listAdapter 에 mDeleteList 에 체크된 디바이스들이 있는지를 체크하여 "완료" 텍스트를 disable , enable 처리
    private void startMonitor() {

        if(m_bmonitor_start == true){
            //handler init
            handler = new Handler(){

                public void handleMessage(android.os.Message msg) {
                    if(msg.what == 0)
                    {
                        //no data in mDeleteList
                        //TODO for test
//                        okay_text.setEnabled(false);
                        okay_text.setVisibility(GONE);
                        title_text.setText(R.string.device_delete);
                        device_delete_count = "0";
                        all_checkbox.setChecked(false);
                    }
                    else if(msg.what == 1)
                    {
                        //data in mDeleteList
                        //체크박스에 체크된 카운트가 있을시 해당 개수를 타이틀에 보여준다.
                        if(TextUtils.isEmpty(device_delete_count))
                        {
                            device_delete_count = (String)msg.obj;
                            title_text.setText(device_delete_count + MainActivity.getInstance().getString(R.string.device_selected));
                            all_checkbox.setChecked(false);
                        }
                        else
                        {
                            try {
                                if(!(device_delete_count.equals(String.valueOf(msg.obj))))
                                {
                                    device_delete_count = (String)msg.obj;
                                    Log.e(TAG, "selected device count was changed => count : " + device_delete_count);
                                    title_text.setText(device_delete_count + MainActivity.getInstance().getString(R.string.device_selected));

                                    if(mListLight.size()+ mListIndoor.size() + mListEnergy.size() + mLIstSafety.size() == Integer.valueOf(device_delete_count))
                                    {
                                        //전체 선택 갯수가 선택 된 경우
                                        all_checkbox.setChecked(true);
                                    }
                                    else
                                    {
                                        //전체 선택된 후에 하나라도 선택을 해제한 경우 전체선택 checkbox 를 해제해야한다.
                                        if(all_checkbox.isChecked())
                                        {
                                            all_checkbox.setChecked(false);
                                            Log.d(TAG, " 전체 선택이 아닌데도 전체 선택이 체크되어 있는 경우");
                                        }
                                        else
                                        {
                                            Log.d(TAG, " 전체선택된 경우도 아니고 전체 선택 체크박스도 표시안되어 있는 경우");
                                        }
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        //TODO save button able/ disable 에서 hide and show 로 변경
//                        okay_text.setEnabled(true);
                        okay_text.setVisibility(VISIBLE);
                    }
                }
            };

            //start Monitor
            MonitorThread monitor_thread;
            monitor_thread = new MonitorThread();
            if(monitor_thread != null) {
                try {
                    monitor_thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //TODO Thread 하나 돌려서 해당 adapter 의 mDelete_List 사이즈가 모두 0 이면 완료 text 를 disable 처리 해야한다.
    class MonitorThread extends Thread {
        public void run() {
            try {
                android.util.Log.d(TAG, "MonitorThread ... Start");

                while(m_bmonitor_start) {

                    if(mAdapterSafety.mDelete_List.size() == 0 && mAdapterIndoor.mDelete_List.size() == 0 &&
                            mAdapterEnergy.mDelete_List.size() == 0 && mAdapterLight.mDelete_List.size() == 0)
                    {
                        handler.sendEmptyMessage(0);
                    }
                    else
                    {
                        //해당 디바이스들에서 체크된 카운트의 갯수를 확인해야한다.
                        int count = mAdapterSafety.mDelete_List.size() + mAdapterLight.mDelete_List.size() +
                                mAdapterIndoor.mDelete_List.size() + mAdapterEnergy.mDelete_List.size();
//                        handler.sendEmptyMessage(1);

                        //handler 처리
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = String.valueOf(count);
                        handler.sendMessage(msg);
                    }

                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //pxd custom toast
    public void showToastOnWorking(String txt){
        try
        {
                Toast toast = new Toast(mContext);
                toastTextView.setText(txt);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.show();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
