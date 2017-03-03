package com.commax.controlsub.Connection_Guide;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

import java.util.ArrayList;

/**
 * Created by OWNER on 2017-01-06.
 */
public class Connection_Guide_Main extends LinearLayout implements View.OnClickListener{
    public String TAG = Connection_Guide_Main.class.getSimpleName();

    Context mContext;
    // layout
    LayoutInflater inflater;
    View rootView;
    LinearLayout container;
    LinearLayout guide_list_layout;
    //UI
    TextView title_text;
    ImageView exitButton;
    ImageView exitButton_detail;

    //상세 레이아웃
    TextView device_name;
    TextView device_guide;
    ImageView device_image_detail;
    Button connectButton;

    // ListView
    ListView mListViewLight;
    ListView mListViewIndoor;
    ListView mListViewEnergy;
    ListView mListViewSafety;

    //ControlName_Edit_List_Adapter
    Connection_Guide_List_Adapter mAdapterLight;
    Connection_Guide_List_Adapter mAdapterIndoor;
    Connection_Guide_List_Adapter mAdapterEnergy;
    Connection_Guide_List_Adapter mAdapterSafety;

    // ArrayList
    ArrayList<Connection_Guide_ListCell> mListLight;
    ArrayList<Connection_Guide_ListCell> mListIndoor;
    ArrayList<Connection_Guide_ListCell> mListEnergy;
    ArrayList<Connection_Guide_ListCell> mLIstSafety;

    //TextView
    TextView light_textview;
    TextView indoor_textview;
    TextView energy_textview;
    TextView safety_textview;


    public Connection_Guide_Main(Context context)
    {
        super(context);
        mContext = context;
        init();
    }

    public void init()
    {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.activity_connect_guide, this);

        container = (LinearLayout) rootView.findViewById(R.id.container_detail);
        guide_list_layout = (LinearLayout)rootView.findViewById(R.id.guide_list_layout);

        title_text = (TextView)rootView.findViewById(R.id.title_text);
        exitButton = (ImageView) rootView.findViewById(R.id.title_back_button);

        mListViewLight = (ListView)rootView.findViewById(R.id.listViewLight);
        mListViewIndoor = (ListView)rootView.findViewById(R.id.listViewIndoor);
        mListViewEnergy = (ListView)rootView.findViewById(R.id.listViewEnergy);
        mListViewSafety = (ListView)rootView.findViewById(R.id.listViewSafety);

        light_textview = (TextView)rootView.findViewById(R.id.light_textview);
        indoor_textview = (TextView)rootView.findViewById(R.id.indoor_textview);
        energy_textview = (TextView)rootView.findViewById(R.id.energy_textview);
        safety_textview = (TextView)rootView.findViewById(R.id.safety_textview);

        /* device detail UI */
        exitButton_detail = (ImageView)rootView.findViewById(R.id.title_back_button_detail);
        device_name = (TextView) rootView.findViewById(R.id.device_name_detail);
        device_guide = (TextView) rootView.findViewById(R.id.device_guide_detail);
        device_image_detail = (ImageView) rootView.findViewById(R.id.device_image_detail);
        connectButton =(Button)rootView.findViewById(R.id.btn_connect);

        mListLight = new ArrayList<Connection_Guide_ListCell>();
        mListIndoor = new ArrayList<Connection_Guide_ListCell>();
        mListEnergy = new ArrayList<Connection_Guide_ListCell>();
        mLIstSafety = new ArrayList<Connection_Guide_ListCell>();

        exitButton.setOnClickListener(this);
        exitButton_detail.setOnClickListener(this);
        connectButton.setOnClickListener(this);

        List_component_add();
    }

    public void change_mode(String mode , Connection_Guide_ListCell listCell)
    {
        Log.d(TAG, "change mode : " + mode);
        //TODO for test
        if(mode.equals(TypeDef.CONNECT_DEVICE_DETAIL_MODE))
        {
            container.setVisibility(VISIBLE);
            guide_list_layout.setVisibility(GONE);
            //device guide detail UI 변경
            device_name.setText(listCell.DeviceName);
            device_guide.setText(listCell.guide_text);
            device_image_detail.setImageDrawable(listCell.mDeviceImage);

        }
        else
        {
            container.setVisibility(GONE);
            guide_list_layout.setVisibility(VISIBLE);
        }
    }


    public void List_component_add()
    {
        //Light category
        mListLight.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_4switch),
                mContext.getString(R.string.switch_of_4_light_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.switch_of_4_light_guide_text) , getResources().getDrawable(R.mipmap.img_device_4switch)));
        mListLight.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_2switch),
                mContext.getString(R.string.switch_of_2_light_device_name) ,mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.switch_of_2_light_guide_text), getResources().getDrawable(R.mipmap.img_device_2switch)));

        //Indoor category
        mListIndoor.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_pirm),
                mContext.getString(R.string.multisensor_pir_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.multisensor_pir_guide_text) , getResources().getDrawable(R.mipmap.img_device_pirm)));
        mListIndoor.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_leak),
                mContext.getString(R.string.water_sensor_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.water_sensor_guide_text) , getResources().getDrawable(R.mipmap.img_device_leak)));

        //energy category
        mListEnergy.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_sp),
                mContext.getString(R.string.smart_plug_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.smart_plug_guide_text) , getResources().getDrawable(R.mipmap.img_device_sp)));
        mListEnergy.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_stp),
                mContext.getString(R.string.standby_power_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.standby_power_guide_text) , getResources().getDrawable(R.mipmap.img_device_stp)));

        //safe category
        mLIstSafety.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_mgnt),
                mContext.getString(R.string.magnetic_sensor_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.magnetic_sensor_guide_text) , getResources().getDrawable(R.mipmap.img_device_mgnt)));
        mLIstSafety.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_gasd),
                mContext.getString(R.string.gas_detect_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.gas_detect_guide_text) , getResources().getDrawable(R.mipmap.img_device_gasd)));
        mLIstSafety.add(new Connection_Guide_ListCell(getResources().getDrawable(R.mipmap.ic_device_gasl),
                mContext.getString(R.string.gas_valve_switch_device_name), mContext.getString(R.string.commax_company_name) , mContext.getString(R.string.gas_valve_switch_guide_text) , getResources().getDrawable(R.mipmap.img_device_gasl)));


        mAdapterLight = new Connection_Guide_List_Adapter(mContext, mListLight);
        mAdapterLight.setDynamicHeight(mListViewLight);
        mListViewLight.setAdapter(mAdapterLight);

        mAdapterIndoor = new Connection_Guide_List_Adapter(mContext, mListIndoor);
        mAdapterIndoor.setDynamicHeight(mListViewIndoor);
        mListViewIndoor.setAdapter(mAdapterIndoor);

        mAdapterEnergy = new Connection_Guide_List_Adapter(mContext, mListEnergy);
        mAdapterEnergy.setDynamicHeight(mListViewEnergy);
        mListViewEnergy.setAdapter(mAdapterEnergy);

        mAdapterSafety = new Connection_Guide_List_Adapter(mContext, mLIstSafety);
        mAdapterSafety.setDynamicHeight(mListViewSafety);
        mListViewSafety.setAdapter(mAdapterSafety);

        device_header_show();
    }

    public void onClick(View view)
    {
        if(view.equals(exitButton))
        {
            MainActivity.getInstance().finish();
        }
        else if(view.equals(exitButton_detail))
        {
            Log.d(TAG, " onclick exitButton_detail");
            change_mode(TypeDef.CONNECT_DEVICE_LIST_MODE  , null);
        }
        else if(view.equals(connectButton))
        {
            try {
                //TODO for test tap id  = 11
                Intent intent = new Intent();
                intent.setClassName("com.commax.control" , "com.commax.control.DialogActivity");
                intent.putExtra("tapid",11);
                MainActivity.getInstance().startActivityForResult(intent ,0);
                //send add command
                MainActivity.getInstance().sendDeviceAddCommand();
            }
            catch (Exception e)
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

    //사용 안함
    public void more_device_guide(Connection_Guide_ListCell listCell)
    {
        Log.d(TAG, "more_device_guide");
        try {
//            guide_list_layout.setVisibility(View.GONE);
//            container.setVisibility(View.VISIBLE);

         /*   guide_list_layout.removeAllViews();
            guide_list_layout.setVisibility(GONE);
            container.removeAllViews();
            container.setVisibility(VISIBLE);

            Connection_Guide_Component connection_guide_component = new Connection_Guide_Component(mContext , listCell);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(connection_guide_component, layoutParams);*/

            MainActivity.getInstance().container.removeAllViews();
            MainActivity.getInstance().check_more_device("guide_detail_page", "ffffffff-ffff-ffff-ffff-ffffffffffff", null , listCell);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
