package com.commax.controlsub.DeviceListEdit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;
import com.commax.pam.db.interfaces.MySQLConnection;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by OWNER on 2016-09-07.
 */

public class DeviceNameEdit extends LinearLayout implements View.OnClickListener {

    /*
    * 2016-12-16 Ip Home IoT 월패드에서 ClearEditText 의 del 아이콘이 정상 동작 하지 않는다.
    * 아이콘이 보이지 않는데 동작은 하는 상태로 해결점을 찾이 못해서 ControlNameEdit 를 다시만들고
    * 실행 시키니까 ControlNameEdit 은 정상 동작함
    * 해당파일은 badkup 으로 가지고 있음*/

    public String TAG = DeviceNameEdit.class.getSimpleName();

    private static DeviceNameEdit instance;
    public static DeviceNameEdit getInstance(){
        return instance;
    }

    Context mContext;
    // layout
    LayoutInflater inflater;
    View rootView;
    // Handler
    ArrayList<ControlName_Edit_ListCell> mListLight;
    ArrayList<ControlName_Edit_ListCell> mListIndoor;
    ArrayList<ControlName_Edit_ListCell> mListEnergy;
    ArrayList<ControlName_Edit_ListCell> mLIstSafety;
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
    //TextView
    TextView light_textview;
    TextView indoor_textview;
    TextView energy_textview;
    TextView safety_textview;

    //All deivce List
    ArrayList<com.commax.control.Common.DeviceInfoSimple> mListData;

    /*UI*/
    ImageView backbutton;
    ImageView savebutton;
    TextView guide_text;
    EditText test_edit;

    public boolean mNickName_success_flag = false;

    public DeviceNameEdit(Context context) {
        super(context);
        init(context);
        mContext = context;
    }

    public DeviceNameEdit(Context context, ArrayList<com.commax.control.Common.DeviceInfoSimple> arrayList) {
        super(context);
        Log.d(TAG ,"DeviceListEdit()");
        mListData = arrayList;
        mContext = context;
        init(context);
    }

    //made by sim 심책임님 하단 키바
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
		 //Send Custom Broadcast Message(System Key Show/Hide Action)
        sendCustomBroadcastMessage((hasFocus == true) ? TypeDef.SYSTEM_KEY_SHOW_ACTION : TypeDef.SYSTEM_KEY_HIDE_ACTION);
    }

     //Send Custom Broadcast Message
    private void sendCustomBroadcastMessage(String strBroadcastAction) {
        Intent intent = new Intent(strBroadcastAction);
        mContext.sendBroadcast(intent);
        intent = null;
    }

    public  void init(Context context)
    {
        Log.d(TAG , " init()");
        mContext = context.getApplicationContext();
        instance = this;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.activity_device_nickname_edit, this); // from : 가져올 layout 지정


        mListViewLight = (ListView) rootView.findViewById(R.id.listViewLight);
        mListViewIndoor = (ListView)rootView.findViewById(R.id.listViewIndoor);
        mListViewEnergy = (ListView)rootView.findViewById(R.id.listViewEnergy);
        mListViewSafety = (ListView)rootView.findViewById(R.id.listViewSafety);

        backbutton = (ImageView) rootView.findViewById(R.id.title_back_button);
        savebutton =(ImageView)rootView.findViewById(R.id.compelete_button);
        guide_text = (TextView)rootView.findViewById(R.id.guide_text);
        test_edit = (EditText)rootView.findViewById(R.id.text_edit);

        backbutton.setOnClickListener(this);
        savebutton.setOnClickListener(this);

        mListLight = new ArrayList<ControlName_Edit_ListCell>();
        mListIndoor = new ArrayList<ControlName_Edit_ListCell>();
        mListEnergy = new ArrayList<ControlName_Edit_ListCell>();
        mLIstSafety = new ArrayList<ControlName_Edit_ListCell>();

        mListLight.clear();
        mListIndoor.clear();
        mListEnergy.clear();
        mLIstSafety.clear();

        light_textview = (TextView)rootView.findViewById(R.id.light_textview);
        indoor_textview = (TextView)rootView.findViewById(R.id.indoor_textview);
        energy_textview = (TextView)rootView.findViewById(R.id.energy_textview);
        safety_textview = (TextView)rootView.findViewById(R.id.safety_textview);

        com.commax.control.Common.DeviceInfoSimple simple_data;
        try {
            if(mListData.size() == 0)
            {
                Log.d(TAG, "list size = 0");
            }
            else
            {
                for(int i = 0 ; i < mListData.size() ; i++)
                {
                    simple_data = mListData.get(i);
                    if(simple_data.nCategory.equals("2"))
                    {
                        mListLight.add(new ControlName_Edit_ListCell(simple_data.nickName ,"Light", simple_data.rootUuid));
                    }
                    else if(simple_data.nCategory.equals("3"))
                    {
                        mListIndoor.add(new ControlName_Edit_ListCell(simple_data.nickName ,"Indoor", simple_data.rootUuid));
                    }
                    else if(simple_data.nCategory.equals("4"))
                    {
                        mListEnergy.add(new ControlName_Edit_ListCell(simple_data.nickName ,"Energy", simple_data.rootUuid));
                    }
                    else if(simple_data.nCategory.equals("5"))
                    {
                        mLIstSafety.add(new ControlName_Edit_ListCell(simple_data.nickName ,"Safety", simple_data.rootUuid));
                    }
                }

                mAdapterLight = new ControlName_Edit_List_Adapter(mContext , mListLight);
                mAdapterLight.setDynamicHeight(mListViewLight);
                mListViewLight.setAdapter(mAdapterLight);

                mAdapterEnergy = new ControlName_Edit_List_Adapter(mContext , mListEnergy);
                mAdapterEnergy.setDynamicHeight(mListViewEnergy);
                mListViewEnergy.setAdapter(mAdapterEnergy);

                mAdapterIndoor = new ControlName_Edit_List_Adapter(mContext , mListIndoor);
                mAdapterIndoor.setDynamicHeight(mListViewIndoor);
                mListViewIndoor.setAdapter(mAdapterIndoor);

                mAdapterSafety = new ControlName_Edit_List_Adapter(mContext , mLIstSafety);
                mAdapterSafety.setDynamicHeight(mListViewSafety);
                mListViewSafety.setAdapter(mAdapterSafety);

                //TODO size 0이면 text GONE 처리
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
//            DataManager dataManager = new DataManager(MainActivity.getInstance().mContext);
               //  open DB
            MainActivity.getInstance().OpenDB();
        }catch (Exception e)
        {
           e.printStackTrace();
        }
    }

    //clear edit text 사용하면 맨 밑에 있는  list 3개의  x버튼이 오작동 한다.
    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = MainActivity.getInstance().getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }*/

    public  void onClick(View view)
    {
        //키보드 숨기기
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(test_edit.getWindowToken(), 0);
        test_edit.clearFocus();

        if(view.equals(backbutton))
        {
            MainActivity.getInstance().finish();
        }
        else if(view.equals(savebutton))
        {
            try{
                mAdapterLight.save_button();
                mAdapterIndoor.save_button();
                mAdapterEnergy.save_button();
                mAdapterSafety.save_button();

                Log.d(TAG, "nick name save");
                Log.d(TAG ,"device name edit finish");
//                MainActivity.getInstance().finish();
            }
            catch(Exception e)
            {
                Log.d(TAG, "nick name change fail");
                e.printStackTrace();
//                MainActivity.getInstance().showToastOnWorking("닉네임 변경이 실패하였습니다.");
            }
        }
    }

    public void updateDevice(String category)
    {
        if(category.equals("Light"))
        {
            mListViewLight.setAdapter(mAdapterLight);
//            mListViewLight.deferNotifyDataSetChanged();
        }
        else if(category.equals("Indoor"))
        {
            mListViewIndoor.setAdapter(mAdapterIndoor);
//            mListViewIndoor.deferNotifyDataSetChanged();
        }
        else if(category.equals("Energy"))
        {
            mListViewEnergy.setAdapter(mAdapterEnergy);
//            mListViewEnergy.deferNotifyDataSetChanged();
        }
        else if(category.equals("Safety"))
        {
            mListViewSafety.setAdapter(mAdapterSafety);
        }

    }

    private ArrayList<ControlName_Edit_ListCell> sortAndAddSections(ArrayList<ControlName_Edit_ListCell> itemList)
    {
        ArrayList<ControlName_Edit_ListCell> tempList = new ArrayList<ControlName_Edit_ListCell>();
        //First we sort the array
        Collections.sort(itemList);

        //Loops thorugh the list and add a section before each sectioncell start
        String header = "";
        String install = "eIndoorEnv";
        String update = "eLight";
        String newest = "eEnergy";

        Log.d(TAG,install);
        for(int i = 0; i < itemList.size(); i++)
        {
            //If it is the start of a new section we create a new listcell and add it to our array
            if(header != itemList.get(i).getCategory()){
                ControlName_Edit_ListCell sectionCell = new ControlName_Edit_ListCell(null,itemList.get(i).getCategory(), null);
                sectionCell.setToSectionHeader();
                //update header index = 0
                if(itemList.get(i).getCategory().equals(update))
                {
                    tempList.add(0,sectionCell);
                }
                else{
                    tempList.add(sectionCell);
                }
                header = itemList.get(i).getCategory();
            }
            //update category index = 0
            if(header.equals(update))
            {
                tempList.add(1,itemList.get(i));
            }
            else{
                tempList.add(itemList.get(i));
            }
        }
        return tempList;
    }

    public void startTask(String changeNickname , ControlName_Edit_ListCell cell)
    {
        String rootuuid = cell.getRootUuid();
        new AppTask().execute(changeNickname , rootuuid );
    }

    public void nickname_update_report(String rootuuid , String nickname)
    {
        //TODO 업데이트 처리 진행 해야한다.
        for(int i = 0 ; i <mListLight.size() ; i++) {
            ControlName_Edit_ListCell listCell = mListLight.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mListLight.get(i).nickname = nickname;
                updateDevice("Light");
                break;
            }
        }
        for(int i = 0 ; i <mListIndoor.size() ; i++) {
            ControlName_Edit_ListCell listCell = mListIndoor.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mListIndoor.get(i).nickname = nickname;
                updateDevice("Indoor");
                break;
            }
        }
        for(int i = 0 ; i <mListEnergy.size() ; i++) {
            ControlName_Edit_ListCell listCell = mListEnergy.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mListEnergy.get(i).nickname = nickname;
                updateDevice("Energy");
                break;
            }
        }

        for(int i = 0 ; i <mLIstSafety.size() ; i++) {
            ControlName_Edit_ListCell listCell = mLIstSafety.get(i);
            if (rootuuid.equals(listCell.getRootUuid())) {
                Log.d(TAG, "rootuuid :" + listCell.getRootUuid()  + ", nickname : " + listCell.nickname + "category : " + listCell.category);
                mLIstSafety.get(i).nickname = nickname;
                updateDevice("Safety");
                break;
            }
        }

    }

    /**
     * 작업 태스크
     * @author nohhs
     */
    private class AppTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩뷰 시작
            Log.d(TAG, "AppTask : onPreExecute");
        }
        @Override
        protected Void doInBackground(String... params) {
            // 어플리스트 작업시작
            try {
                Log.d(TAG , "rootUuid : " + params[1]  + ", nickname : " + params[0]);
                MySQLConnection.getInstance().updateDeviceNickName(params[1], params[0]);
                Log.d(TAG,"JSONHelper.restCall()");

                Intent intent = new Intent("com.commax.controlsub.NickName_ACTION");
                intent.putExtra("rootUuidStr",params[1]);
                intent.putExtra("nickNameStr", params[0]);
                mContext.sendBroadcast(intent);
                mNickName_success_flag = true;

            } catch (Exception e) {
                Log.e(TAG,"restCall error . Server can't connect");
                e.printStackTrace();
            }
            Log.d(TAG, "AppTask : doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            Log.d(TAG, "AppTask : onPostExecute result = " + result);
        }
    };

}
