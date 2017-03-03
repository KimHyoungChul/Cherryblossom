package com.commax.applist;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class V2_SelectFavoriteActivity extends Activity {//AppCompat

    LinearLayout mListLayout;
    static V2_SelectFavoriteActivity mInstance = null;

    static class SelectFavoriteCell {

        Activity mActivity = null;
        View mCheckbox;
        boolean mChecked = false;
        boolean mEnabled = true;

        static final int MAX_SELECT = 5;

        SelectFavoriteCell(Activity activity, View checkbox)  {

            mActivity = activity;
            mCheckbox = checkbox;

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if( mEnabled == false  ) {
                        return;
                    }

                    if(   mChecked == false ) {
                        int count = V2_SelectFavoriteActivity.mInstance.countSelected();
                        if( count >= MAX_SELECT) {
                            mInstance.RefreshEnableList();
                            return;
                        }
                    }

                    SelectFavoriteCell.this.SetCheck(!mChecked);
                    mInstance.RefreshEnableList();

                }
            });

            SetCheck(false);
        }

        public void SetCheck(boolean checked){

            mChecked = checked;

            ImageView checkBox = (ImageView) mCheckbox.findViewById(R.id.checkBox);
            Drawable dr = null;

            if( checked) {
                dr = mActivity.getResources().getDrawable(R.drawable.btn_checkbox_s);
            }else   {
                dr = mActivity.getResources().getDrawable(R.drawable.btn_checkbox_n);
            }

            checkBox.setImageDrawable(dr);

        }

        public boolean GetCheck(){
            return mChecked;
        }


        public void SetEnable(boolean enabled){

            mEnabled = enabled;
            ImageView checkBox = (ImageView) mCheckbox.findViewById(R.id.checkBox);

            if(enabled) {
                checkBox.setAlpha(1.0f);
            }else {
                checkBox.setAlpha(0.3f);
            }

            TextView textViewName = (TextView) mCheckbox.findViewById(R.id.textViewName);

            if(enabled) {
                textViewName.setAlpha(1.0f);
            }else {
                textViewName.setAlpha(0.5f);
            }

        }

        public boolean GetEnable(){

            return mEnabled ;
        }
    };


    ArrayList< SelectFavoriteCell> mListFavoriteCell = new ArrayList< SelectFavoriteCell> ();

    void RefreshEnableList(){

        int countSelected = V2_SelectFavoriteActivity.mInstance.countSelected();

        if (countSelected >= SelectFavoriteCell.MAX_SELECT) {

            for( int i = 0; i < mInstance.mListFavoriteCell.size(); ++i ) {

                SelectFavoriteCell iterCell = mInstance.mListFavoriteCell.get(i);

                if( iterCell.GetCheck()){
                    iterCell.SetEnable(true);
                    continue;
                }
                iterCell.SetEnable(false);
            }
        }else{

            for( int i = 0; i < mInstance.mListFavoriteCell.size(); ++i ) {

                SelectFavoriteCell iterCell = mInstance.mListFavoriteCell.get(i);
                iterCell.SetEnable(true);

            }
        }
    }


    void refreshAppList(){

        mListFavoriteCell.clear();
        mListLayout.removeAllViews();

        for(V2_AppData v2Data : V2_AppData.msAppDatas) {
            String name = v2Data.mName;//V2_Function.getStringByName(this, v2Data.mNameID);

            Drawable drawable =v2Data.mIcon;// V2_Function.getDrawableName(this, v2Data.mIconID);

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View cell = (View) inflater.inflate(R.layout.v2_select_favorite_cell, null);
            View cell_dummy = (View) inflater.inflate(R.layout.v2_select_favorite_cell_dummy, null);
            cell.setTag(v2Data.mPackageName+":"+ v2Data.mActivityName);


            SelectFavoriteCell temp = new SelectFavoriteCell(this, cell );

            if( V2_FavoriteData.hasApp(v2Data.mPackageName, v2Data.mActivityName)){
                temp.SetCheck(true);
            }
            else{
                temp.SetCheck(false);
            }
            mListFavoriteCell.add(temp);

            TextView textViewName = (TextView) cell.findViewById(R.id.textViewName);

            if (textViewName != null) {
                textViewName.setText(name);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "can't Find textViewName", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            mListLayout.addView(cell);
            mListLayout.addView(cell_dummy);
        }


    }

    int countSelected()   {

        int result = 0;

       int count =  mListFavoriteCell.size();

        for(int i=0; i<count; i++) {
            SelectFavoriteCell cell = mListFavoriteCell.get(i);
            //do something with your child element


            boolean checked = cell.GetCheck();

            if (checked) {
                ++result;
            }
        }
        return result;
    }

    void onOK()   {

        if( countSelected() <= 0 )        {

            String ttt = getString(R.string.alert_select_one_favorite);
            V2_CommonToast commonToast = new V2_CommonToast(this);
            commonToast.showToast( ttt, Toast.LENGTH_SHORT);
            return;
        }

        V2_FavoriteData.msFavoriteDatas.clear();

        int count =  mListFavoriteCell.size();

        for(int i=0; i<count; i++) {
            SelectFavoriteCell cell = mListFavoriteCell.get(i);

            //do something with your child element

            boolean checked = cell.GetCheck();//.isChecked();

            if( checked ){
                String tagAll =  (String) cell.mCheckbox.getTag();

                String[] array = tagAll.split(":");;

                String packageName = array[0];
                String activityName = array[1];

                V2_AppData appData = V2_AppData.findApp(packageName, activityName);

                if( appData != null) {

                    V2_FavoriteData favData = new V2_FavoriteData();

                    favData.mCategory      = appData.mCategory;
                    favData.mName           = appData.mName;
                    favData.mIcon           = appData.mIcon;
                    favData.mPackageName   = appData.mPackageName;
                    favData.mActivityName  = appData.mActivityName;

                    V2_FavoriteData.msFavoriteDatas.add(favData);

                }
            }
        }

        V2_FavoriteData.writeFavoriteData(V2_SelectFavoriteActivity.this);

        setResult(RESULT_OK);
        finish();
        overridePendingTransition(0, 0);

    }

    void onCANCEL(){
        // Intent intent = new Intent();
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstance = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.v2_activity_select_favorite);

        mInstance = this;
        hideNavigationBar();




        getWindow().setWindowAnimations(0);

        //layout = (LinearLayout) findViewById(R.id.frame);

        //getSupportActionBar().hide();




        {
             mListLayout = (LinearLayout) findViewById(R.id.layoutApps);
        }

        {
            Button buttonEdit = (Button) findViewById(R.id.buttonOK);
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // your handler code here
                    onOK();
                }
            });
        }


        {

            Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // your handler code here
                    onCANCEL();
                }
            });
            //  categoryMain1 = tempView;
        }


        refreshAppList();

        RefreshEnableList();
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
}
