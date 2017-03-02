package com.commax.controlsub.Connection_Guide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.controlsub.R;


/**
 * Created by OWNER on 2017-01-06.
 */
public class Connection_Guide_Component extends LinearLayout {
    // layout
    LayoutInflater inflater;
    View rootView;

    Context mContext;

    //UI
    TextView title_text;
    ImageView backbutton;

    public Connection_Guide_Component(Context context , Connection_Guide_ListCell listCell)
    {
        super(context);
        mContext = context;
        init();
    }

    public void init()
    {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.activity_connect_guide_component, this);


    }

}
