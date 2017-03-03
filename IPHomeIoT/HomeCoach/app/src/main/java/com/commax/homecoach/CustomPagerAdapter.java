package com.commax.homecoach;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.homecoach.CustomAlertsDialog;

/**
 * Created by YOUNG on 2016-10-07.
 */

public class CustomPagerAdapter extends PagerAdapter implements View.OnClickListener {
    LayoutInflater mInflater;

    Context m_Context;

    /* Tip Names */
    private String ELEC = "elec";
    private String INDOOR = "indoor";

    private TextView text_Subtitle;
    private TextView text_Docu;
    private LinearLayout button_1;
    private TextView btn_name1;
    private LinearLayout button_2;
    private TextView btn_name2;

    private static CustomAlertsDialog alertsDialog;

    public CustomPagerAdapter(Context context, LayoutInflater inflater) {
        //전달 받은 LayoutInflater를 멤버변수로 전달
        m_Context = context;
        this.mInflater = inflater;
    }

    //PagerAdapter가 가지고 잇는 View의 개수를 리턴
    //보통 보여줘야하는 이미지 배열 데이터의 길이를 리턴
    @Override
    public int getCount() {
        return 4;
    }

    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(가장 처음부터 0,1,2,3...)
    @Override
    public Object instantiateItem(ViewGroup pager, int position) {
        View view = null;
        view = mInflater.inflate(R.layout.pager_layout, null);

        text_Subtitle = (TextView) view.findViewById(R.id.text_Subtitle);
        text_Docu = (TextView) view.findViewById(R.id.text_Docu);
        button_1 = (LinearLayout) view.findViewById(R.id.button_1);
        btn_name1 = (TextView) view.findViewById(R.id.btn_name1);
        button_2 = (LinearLayout) view.findViewById(R.id.button_2);
        btn_name2 = (TextView) view.findViewById(R.id.btn_name2);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);

        if (Config.TIPS_NAME.equals(ELEC)) {
            if (position == 0) {    // 청소할 때
                text_Subtitle.setText(m_Context.getResources().getText(R.string.elec_cleaner_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.elec_cleaner_docu));
                button_1.setVisibility(View.GONE);
                btn_name2.setText(m_Context.getResources().getText(R.string.btn_ems_elec));
            } else if (position == 1) {     // 세탁할 때
                text_Subtitle.setText(m_Context.getResources().getText(R.string.elec_washer_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.elec_washer_docu));
                button_1.setVisibility(View.GONE);
                btn_name2.setText(m_Context.getResources().getText(R.string.btn_ems_elec));
            } else if (position == 2) {     // TV 셋톱박스
                text_Subtitle.setText(m_Context.getResources().getText(R.string.elec_tv_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.elec_tv_docu));
                button_1.setVisibility(View.VISIBLE);
                btn_name1.setText(m_Context.getResources().getText(R.string.btn_readypower));
                btn_name2.setText(m_Context.getResources().getText(R.string.btn_ems_elec));
            } else {    // 에어컨 사용할 때
                text_Subtitle.setText(m_Context.getResources().getText(R.string.elec_aircon_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.elec_aircon_docu));
                button_1.setVisibility(View.GONE);
                btn_name2.setText(m_Context.getResources().getText(R.string.btn_ems_elec));
            }
        }
        else if (Config.TIPS_NAME.equals(INDOOR)) {
            button_2.setVisibility(View.GONE); // EMS 불러오지 않음

            if (position == 0) {    // 바닥 습기 제거
                text_Subtitle.setText(m_Context.getResources().getText(R.string.indoor_removehumidity_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.indoor_removehumidity_docu));
                button_1.setVisibility(View.VISIBLE);
                btn_name1.setText(m_Context.getResources().getText(R.string.btn_heater));
            } else if (position == 1) {     // 화장실 환기
                text_Subtitle.setText(m_Context.getResources().getText(R.string.indoor_restroom_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.indoor_restroom_docu));
                button_1.setVisibility(View.GONE);
            } else if (position == 2) {     // 결로 방지
                text_Subtitle.setText(m_Context.getResources().getText(R.string.indoor_condensation_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.indoor_condensation_docu));
                button_1.setVisibility(View.VISIBLE);
                btn_name1.setText(m_Context.getResources().getText(R.string.btn_heater));
            } else {    // 습도 유지
                text_Subtitle.setText(m_Context.getResources().getText(R.string.indoor_maintainhumidity_subtitle));
                text_Docu.setText(m_Context.getResources().getText(R.string.indoor_maintainhumidity_docu));
                button_1.setVisibility(View.GONE);
            }
        }

        ((ViewPager) pager).addView(view, 0);

        return view;
    }

    //화면에 보이지 않은 View는파쾨를 해서 메모리를 관리함.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : 파괴될 View의 인덱스(가장 처음부터 0,1,2,3...)
    //세번째 파라미터 : 파괴될 객체(더 이상 보이지 않은 View 객체)
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View) object);

    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
        return v == obj;
    }

    @Override
    public void onClick(View v) {
        Intent intent_btn1 = new Intent();
        Intent intent_btn2 = new Intent();

        switch (v.getId()) {
            case R.id.button_1:
                if (Config.TIPS_NAME.equals(ELEC)) {
                    intent_btn1.setClassName("com.commax.control", "com.commax.control.MainActivity");
                    intent_btn1.putExtra("tapid", 4);
                    intent_btn1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    m_Context.startActivity(intent_btn1);
                } else if (Config.TIPS_NAME.equals(INDOOR)) {
                    intent_btn1.setClassName("com.commax.control", "com.commax.control.MainActivity");
                    intent_btn1.putExtra("tapid", 3);
                    intent_btn1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    m_Context.startActivity(intent_btn1);
                }
                break;

            case R.id.button_2:
                if (Config.TIPS_NAME.equals(ELEC)) {
                    try {
                        intent_btn2.putExtra("PERIOD", "ThisMonth");
                        intent_btn2.putExtra("ENERGY", "0");
                        intent_btn2.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        m_Context.startActivity(intent_btn2);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }
                break;

            default:
                break;
        }
    }
}
