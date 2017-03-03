package com.commax.applist;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class V2_MainActivity extends Activity/*AppCompatActivity*/ {

    static int MAX_APPS_PER_LINE = 0;

    class CategoryAppsManager {

        int mCurrentCount = 0;
        LinearLayout mLineLayout;

        View                    mCategory= null;
        View                    mCategoryDummy= null;
        ArrayList<LinearLayout> mChildLayout = new ArrayList<LinearLayout>();

        public int getAppCount() {
            return mCurrentCount;
        }

        public void SetVisible(boolean bVisible)
        {
            if( bVisible == false ) {

                mCategory.setVisibility(View.GONE);

                if( mCategoryDummy!= null )
                {
                    mCategoryDummy.setVisibility(View.GONE);
                }

            }else {

                mCategory.setVisibility(View.VISIBLE);

                if( mCategoryDummy!= null )
                {
                    mCategoryDummy.setVisibility(View.VISIBLE);
                }

            }
        }

        public CategoryAppsManager(View category, View  categoryDummy, LinearLayout  lineLayout){

            mCategory = category;
            mCategoryDummy = categoryDummy;
            mLineLayout = lineLayout;
            removeAllViews();
        }

        public void removeAllViews(){

            mCurrentCount = 0;

            mLineLayout.removeAllViews();
            mChildLayout.clear();

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            LinearLayout line = (LinearLayout) inflater.inflate(R.layout.v2_apps_line, null);
            line.removeAllViews();
            View cell_dummy = (View) inflater.inflate(R.layout.v2_item_cell_dummy, null);
            line.addView(cell_dummy);

            mLineLayout.addView(line);
            mChildLayout.add(line);
        }

        public void addView(View app)   {

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cell_dummy = (View) inflater.inflate(R.layout.v2_item_cell_dummy, null);

            int newLineIndex = (mCurrentCount ) / MAX_APPS_PER_LINE;
            int newRowIndex = (mCurrentCount ) % MAX_APPS_PER_LINE;

            if( newLineIndex >= mChildLayout.size())  {

                if( newLineIndex > 0)    {
                    LinearLayout line_dummy = (LinearLayout) inflater.inflate(R.layout.v2_apps_line_dummy, null);
                    //line_dummy.removeAllViews();
                    mLineLayout.addView(line_dummy);
                }

                LinearLayout line = (LinearLayout) inflater.inflate(R.layout.v2_apps_line, null);
                line.removeAllViews();
                mLineLayout.addView(line);
                mChildLayout.add(line);
            }



            int lastIndex = mChildLayout.size() -1;

            LinearLayout child = mChildLayout.get(lastIndex);

            if( newRowIndex == 0) {
                child.removeAllViews();//.addView(cell_dummy);
                child.addView(app);

            }else
            {
                child.addView(cell_dummy);
                child.addView(app);
            }
            ++mCurrentCount;
        }
    }

    CategoryAppsManager category1 = null;
    CategoryAppsManager category2 = null;
    CategoryAppsManager category3 = null;
    CategoryAppsManager category4 = null;
    CategoryAppsManager category5 = null;

    View categoryMain1;
    View categoryMain2;
    View categoryMain3;
    View categoryMain4;
    View categoryMain5;

    class UIFavorite {
        public LinearLayout mListLayout =null;
    }

    UIFavorite mUIFavorite;

    private void refreshFavorite() {

        try {

            if (mUIFavorite.mListLayout != null) {
                mUIFavorite.mListLayout.removeAllViews();
            }


            for (final V2_FavoriteData v2Data : V2_FavoriteData.msFavoriteDatas) {

                String name = v2Data.mName;//V2_Function.getStringByName(this, v2Data.mNameID);
                Drawable drawable = v2Data.mIcon;// V2_Function.getDrawableName(this, v2Data.mIconID );
                int badgeCount = V2_BadgeInterface.GetBadgeCount(v2Data.mPackageName);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View cell = (View) inflater.inflate(R.layout.v2_item_cell, null);
                View cell_dummy = (View) inflater.inflate(R.layout.v2_item_cell_dummy, null);

                TextView textViewName = (TextView) cell.findViewById(R.id.textViewName);
                TextView textViewBadge = (TextView) cell.findViewById(R.id.textViewBadge);
                ImageView imageViewBadge = (ImageView) cell.findViewById(R.id.imageViewBadge);

                cell.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // your handler code here
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(v2Data.mPackageName, v2Data.mPackageName + "." + v2Data.mActivityName));

                            intent.setFlags(     Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

                            );
                            startActivity(intent);
                        } catch (Exception e) {

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "can't Launch ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }
                    }
                });

                if (imageViewBadge != null) {

                    if (badgeCount > 0) {
                        imageViewBadge.setVisibility(View.VISIBLE);
                    } else {
                        imageViewBadge.setVisibility(View.INVISIBLE);
                    }

                } else {

                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find imageViewBadge", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

                if (textViewBadge != null) {
                    if (badgeCount > 0) {

                        String strBadgeCount = String.valueOf(badgeCount);
                        textViewBadge.setText(strBadgeCount);
                        textViewBadge.setVisibility(View.VISIBLE);

                    } else {

                        textViewBadge.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find textViewBadge", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


                if (textViewName != null) {
                    textViewName.setText(name);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find textViewName", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                ImageView imageViewIcon = (ImageView) cell.findViewById(R.id.imageViewIcon);

                if (imageViewIcon != null) {
                    imageViewIcon.setImageDrawable(drawable);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find imageViewIcon", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                mUIFavorite.mListLayout.addView(cell);
                mUIFavorite.mListLayout.addView(cell_dummy);

            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    e.getMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    static final int SELECT_FAVORITE_ACTIVITY_REQUEST_CODE = 1234;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case SELECT_FAVORITE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    refreshFavorite();
                }
        }
    }

    void refreshAppList() {

        try {

            category1.removeAllViews();
            category2.removeAllViews();
            category3.removeAllViews();
            category4.removeAllViews();
            category5.removeAllViews();



            for (final V2_AppData v2Data : V2_AppData.msAppDatas) {

                String name = v2Data.mName;// //V2_Function.getStringByName(this, v2Data.mNameID);
                Drawable drawable = v2Data.mIcon;// V2_Function.getDrawableName(this, v2Data.mIconID );
                int badgeCount = V2_BadgeInterface.GetBadgeCount(v2Data.mPackageName);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View cell = (View) inflater.inflate(R.layout.v2_item_cell, null);
                View cell_dummy = (View) inflater.inflate(R.layout.v2_item_cell_dummy, null);
                TextView textViewName = (TextView) cell.findViewById(R.id.textViewName);
                TextView textViewBadge = (TextView) cell.findViewById(R.id.textViewBadge);
                ImageView imageViewBadge = (ImageView) cell.findViewById(R.id.imageViewBadge);


                cell.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // your handler code here

                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(v2Data.mPackageName, v2Data.mPackageName + "." + v2Data.mActivityName));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            startActivity(intent);
                        } catch (Exception e) {

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "can't Launch ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();



                        }
                    }
                });


                if (imageViewBadge != null) {
                    if (badgeCount > 0) {
                        imageViewBadge.setVisibility(View.VISIBLE);
                    } else {
                        imageViewBadge.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find imageViewBadge", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


                if (textViewBadge != null) {
                    if (badgeCount > 0) {

                        String strBadgeCount = String.valueOf(badgeCount);
                        textViewBadge.setText(strBadgeCount);
                        textViewBadge.setVisibility(View.VISIBLE);

                    } else {

                        textViewBadge.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find textViewBadge", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


                if (textViewName != null) {
                    textViewName.setText(name);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find textViewName", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                ImageView imageViewIcon = (ImageView) cell.findViewById(R.id.imageViewIcon);

                if (imageViewIcon != null) {
                    imageViewIcon.setImageDrawable(drawable);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "can't Find imageViewIcon", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


                if (v2Data.mCategory.equals("1")) {
                    category1.addView(cell);
                } else if (v2Data.mCategory.equals("2")) {
                    category2.addView(cell);
                } else if (v2Data.mCategory.equals("3")) {
                    category3.addView(cell);
                } else if (v2Data.mCategory.equals("4")) {
                    category4.addView(cell);
                } else if (v2Data.mCategory.equals("5")) {
                    category5.addView(cell);
                }

                category1.SetVisible( category1.getAppCount() > 0);
                category2.SetVisible( category2.getAppCount() > 0);
                category3.SetVisible( category3.getAppCount() > 0);
                category4.SetVisible( category4.getAppCount() > 0);
                category5.SetVisible( category5.getAppCount() > 0);

            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    e.getMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }


    void LoadingProcess(){
        refreshFavorite();
        refreshAppList();

        mLoadingGrade = LODING_FINAL;

    }

    class LoadingHandler extends Handler{
         @Override
        public void handleMessage(Message msg){
            if(mLoadingGrade >= LODING_FINAL){
                return;
            }

            LoadingProcess();
            mLoadingHandler.sendEmptyMessageDelayed(0, 10);

        }
    };

    static final int LODING_FINAL = 2;
    LoadingHandler  mLoadingHandler = new LoadingHandler();
    int             mLoadingGrade = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.v2_activity_main);
        //setContentView(R.layout.activity_main);
        getWindow().setWindowAnimations(0);

        hideNavigationBar();

        V2_ActivityDictionary.clearInstance();

        MAX_APPS_PER_LINE = getResources().getInteger(R.integer.max_apps_column);

        if( MAX_APPS_PER_LINE <= 0)  {
            MAX_APPS_PER_LINE = 1;

            Toast toast = Toast.makeText(getApplicationContext(),
                    " can't find max_apps_column ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        //mainMask
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View main_mask = (View) inflater.inflate(R.layout.v2_activity_main_mask, null);
        addContentView(main_mask, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mUIFavorite = new UIFavorite();
        {
            View tempView = findViewById(R.id.favorite);
            mUIFavorite.mListLayout = (LinearLayout) tempView.findViewById(R.id.layoutApps);
            Button buttonEdit = (Button) tempView.findViewById(R.id.buttonEdit);


            buttonEdit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // your handler code here
                    Intent i = new Intent(V2_MainActivity.this, V2_SelectFavoriteActivity.class);
                    startActivityForResult(i, SELECT_FAVORITE_ACTIVITY_REQUEST_CODE);


                }
            });
        }

        {
            View tempView = findViewById(R.id.category1);
            View tempViewDummy = findViewById(R.id.category_dummy1);
            category1 = new CategoryAppsManager(tempView,tempViewDummy, (LinearLayout) tempView.findViewById(R.id.layoutAppsLines));
            categoryMain1 = tempView;
        }

        {
            View tempView = findViewById(R.id.category2);
            View tempViewDummy = findViewById(R.id.category_dummy2);
            category2 = new CategoryAppsManager(tempView,tempViewDummy,(LinearLayout) tempView.findViewById(R.id.layoutAppsLines));
            categoryMain2 = tempView;
        }

        {
            View tempView = findViewById(R.id.category3);
            View tempViewDummy = findViewById(R.id.category_dummy3);
            category3 = new CategoryAppsManager(tempView, tempViewDummy, (LinearLayout) tempView.findViewById(R.id.layoutAppsLines));
            categoryMain3 = tempView;
        }

        {
            View tempView = findViewById(R.id.category4);
            View tempViewDummy = findViewById(R.id.category_dummy4);
            category4 = new CategoryAppsManager(tempView, tempViewDummy, (LinearLayout) tempView.findViewById(R.id.layoutAppsLines));
            categoryMain4 = tempView;
        }

        {
            View tempView = findViewById(R.id.category5);
            View tempViewDummy = null;// findViewById(R.id.categorydummy5);
            category5 = new CategoryAppsManager(tempView, tempViewDummy, (LinearLayout) tempView.findViewById(R.id.layoutAppsLines));
            categoryMain5 = tempView;
        }

        if (category1 != null) {

            category1.removeAllViews();

            TextView textViewCategory = (TextView) categoryMain1.findViewById(R.id.textViewCategory);

            if (textViewCategory != null) {
                textViewCategory.setText(getString(R.string.title_category1));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            TextView textViewCategory_Under = (TextView) categoryMain1.findViewById(R.id.textViewCategory_Under);

            if (textViewCategory_Under != null) {
                textViewCategory_Under.setText(getString(R.string.title_category_under1));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory_Under ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    " category1 is null ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


        if (category2 != null) {
            category2.removeAllViews();


            TextView textViewCategory = (TextView) categoryMain2.findViewById(R.id.textViewCategory);

            if (textViewCategory != null) {
                textViewCategory.setText(getString(R.string.title_category2));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            TextView textViewCategory_Under = (TextView) categoryMain2.findViewById(R.id.textViewCategory_Under);

            if (textViewCategory_Under != null) {
                textViewCategory_Under.setText(getString(R.string.title_category_under2));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory_Under ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    " category2 is null ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


        if (category3 != null) {
            category3.removeAllViews();


            TextView textViewCategory = (TextView) categoryMain3.findViewById(R.id.textViewCategory);

            if (textViewCategory != null) {
                textViewCategory.setText(getString(R.string.title_category3));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            TextView textViewCategory_Under = (TextView) categoryMain3.findViewById(R.id.textViewCategory_Under);

            if (textViewCategory_Under != null) {
                textViewCategory_Under.setText(getString(R.string.title_category_under3));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory_Under ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    " category3 is null ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


        if (category4 != null) {
            category4.removeAllViews();


            TextView textViewCategory = (TextView) categoryMain4.findViewById(R.id.textViewCategory);

            if (textViewCategory != null) {
                textViewCategory.setText(getString(R.string.title_category4));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            TextView textViewCategory_Under = (TextView) categoryMain4.findViewById(R.id.textViewCategory_Under);

            if (textViewCategory_Under != null) {
                textViewCategory_Under.setText(getString(R.string.title_category_under4));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory_Under ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    " category4 is null ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        if (category5 != null) {
            category5.removeAllViews();


            TextView textViewCategory = (TextView) categoryMain5.findViewById(R.id.textViewCategory);

            if (textViewCategory != null) {
                textViewCategory.setText(getString(R.string.title_category5));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


            TextView textViewCategory_Under = (TextView) categoryMain5.findViewById(R.id.textViewCategory_Under);

            if (textViewCategory_Under != null) {
                textViewCategory_Under.setText(getString(R.string.title_category_under5));

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        " can't find textViewCategory_Under ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    " category5 is null ", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        View imageButtonHome = findViewById(R.id.imageButtonHome);
        if (imageButtonHome != null) {

            //OnClickHome();

            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // your handler code here
                    OnClickHome();
                }
            });


        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "can't Find imageButtonHome", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


        //app loading tread
        class NumberThread extends Thread {
            @Override
            public void run() {


                V2_AppData.reloadAppDatas(V2_MainActivity.this);
                V2_AppData.removeNoTitle();
                V2_FavoriteData.readFavoriteFile(V2_MainActivity.this);
                V2_FavoriteData.removeNoTitle(V2_MainActivity.this);
                V2_AppData.reloadEtcAppData(V2_MainActivity.this, false);


                mLoadingHandler.sendEmptyMessageDelayed(0,10);
            }
        }

        NumberThread threa = new NumberThread();
        threa.start();
    }

    void OnClickHome() {
        finish();
    }


    void CheckRefresh()  {
        if( mLoadingGrade < LODING_FINAL )   {
            return;
        }

        if( V2_ActivityDictionary.getInstance(this).reloadApplications(this) ) {

            V2_AppData.reloadEtcAppData(this,true);
            refreshAppList();
            return;
        }

    }

    class TimeHandler extends Handler{
        public boolean mRun = false;

        @Override
        public void handleMessage(Message msg){

            if( timerHandler.mRun) {

                CheckRefresh();
                timerHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    TimeHandler timerHandler = new TimeHandler();

    @Override
    protected void onResume() {
        super.onResume();

        timerHandler.sendEmptyMessageDelayed(0, 1000);
        timerHandler.mRun = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.mRun = false;
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