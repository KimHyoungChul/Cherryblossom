package com.commax.homereporter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class SupportView extends LinearLayout {

    TextView tv_type;
    TextView tv_value;
    TextView tv_status;
    TextView tv_unit;
    ImageView iv_status;

    String mType = "";
    Context mContext;

    public SupportView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.support, this);

        tv_value = (TextView) rootView.findViewById(R.id.tv_value);
        tv_type = (TextView) rootView.findViewById(R.id.tv_type);
        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_unit = (TextView) rootView.findViewById(R.id.tv_unit);
        iv_status = (ImageView) rootView.findViewById(R.id.iv_status);

        //TODO iv_status icon setting is needed
        tv_status.setText(getResources().getString(R.string.info_support));
        iv_status.setImageResource(R.mipmap.ic_rp_support);

    }

}
