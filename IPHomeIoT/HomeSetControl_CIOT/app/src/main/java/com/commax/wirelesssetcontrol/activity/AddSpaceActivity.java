package com.commax.wirelesssetcontrol.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.wirelesssetcontrol.FileEx;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.NameSpace;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.SpaceDialog;
import com.commax.wirelesssetcontrol.SpaceImage;
import com.commax.wirelesssetcontrol.data.PageData;
import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.tools.PublicTools;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;

import java.io.File;
import java.util.regex.Pattern;

public class AddSpaceActivity extends AppCompatActivity {

    static final String TAG = "AddSpaceActivity";
    String selected = "0";
    EditText et_name;
    TextView tv_title;

    ImageView iv_space_picture;

    int num_pages = -1;
    String ACTIVITY_MODE="";

    public LayoutInflater toastInflater;
    public View toastLayout;
    public TextView toastTextView;
    LinearLayout lay_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_space);

        PublicTools.hideActionBar(this);
        PublicTools.hideNavigationBar(this);

        try {
            Intent intent = getIntent();
            num_pages = intent.getIntExtra(NameSpace.NUM_PAGES, -1);
            ACTIVITY_MODE = intent.getStringExtra(NameSpace.ACTIVITY_MODE);
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "ACTIVITY_MODE " + ACTIVITY_MODE);

//        LinearLayout lay_choice = (LinearLayout) findViewById(R.id.lay_choice);
        iv_space_picture = (ImageView) findViewById(R.id.iv_space_picture);
        ImageButton bt_ok = (ImageButton) findViewById(R.id.bt_ok);
        ImageButton bt_back = (ImageButton) findViewById(R.id.bt_back);
        lay_choice = (LinearLayout) findViewById(R.id.lay_choice);
        et_name = (EditText) findViewById(R.id.et_name);
        tv_title = (TextView) findViewById(R.id.tv_title);

        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        }catch (Exception e){
            e.printStackTrace();
        }

//        InputFilter[] byteFilter = new InputFilter[]{new ByteLengthFilter(20, "KSC5601")};
//        et_name.setFilters(byteFilter);

//        try {
//            //edittext lenght MAX 12
//            InputFilter[] inputFilter = new InputFilter[2];
//            inputFilter[0] = new InputFilter.LengthFilter(12);
//            inputFilter[1] = new CustomInputFilter_language_num_ect();
//
//            et_name.setFilters(inputFilter);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        try {
            if (ACTIVITY_MODE.equalsIgnoreCase(NameSpace.EDIT_MODE)) {
                PageData pageData = PageDataManager.getInst().getPageData(num_pages);
                if (pageData != null) {
                    tv_title.setText(getString(R.string.edit_space));
                    et_name.setText(pageData.name);
                    selected = pageData.background;
                }
                else
                    selected = "1";
            } else {
                tv_title.setText(getString(R.string.add_space));
                selected = "1";
//                setSelectedItem("1");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        bt_back.setOnClickListener(mClick);
        bt_ok.setOnClickListener(mClick);

        loadDefaultBackground();
        loadSavedBackground();
        setSelectedItem(selected);
        updateCheckStatus();
    }

    private void loadDefaultBackground(){
        try{
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_1, "1");
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_2, "2");
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_3, "3");
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_4, "4");
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_5, "5");
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_6, "6");
            loadSpaceImageByResId(R.mipmap.bg_home_img_sm_7, "7");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadSavedBackground(){
        try{
            if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()+"/CMXBackground";

                File file = new File(path);
                String str;
                int num = 0;

                int imgCount = file.listFiles().length;	// 파일 총 갯수 얻어오기
                Bitmap[] map = new Bitmap[imgCount];

                if ( file.listFiles().length > 0 )
                    for ( File f : file.listFiles() ) {
                        str = f.getName();				// 파일 이름 얻어오기

                        //TODO Image 최적화 필요★★★
                        map[num] = BitmapFactory.decodeFile(path + "/" + str);
                        loadSpaceImageByBitmap(map[num], str);
                        num++;
                    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener mItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SpaceImage spaceImage = (SpaceImage)v;
            selected = spaceImage.getFileName();

            updateCheckStatus();

            setSelectedItem(selected);
        }
    };

    private void updateCheckStatus(){
        try{

            for(int i=0;i<lay_choice.getChildCount();i++) {
                SpaceImage temp = (SpaceImage)lay_choice.getChildAt(i);

                if (selected.equalsIgnoreCase("1")){
                    if (i==0)
                        temp.setSelected();
                    else
                        temp.setUnSelected();
                }else {
                    if (selected.equalsIgnoreCase(temp.getFileName())) {
                        temp.setSelected();
                    } else {
                        temp.setUnSelected();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadSpaceImageByResId(int resId, String fileName){
        SpaceImage spaceImage = new SpaceImage(getApplicationContext());
        spaceImage.setImage(ResourcesCompat.getDrawable(getResources(), resId, null), fileName);
        spaceImage.setOnClickListener(mItemClick);
        lay_choice.addView(spaceImage);
    }

    private void loadSpaceImageByBitmap(Bitmap bitmap, String fileName){
        SpaceImage spaceImage = new SpaceImage(getApplicationContext());
        spaceImage.setImage(bitmap, fileName);
        spaceImage.setOnClickListener(mItemClick);
        lay_choice.addView(spaceImage);
    }

    //한글 영문 특수문자 일부 지원
    protected class CustomInputFilter_language_num_ect implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[~!@#$%^&*()_+=?/\":;.,a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ ]+$");

            if (source.equals("") || ps.matcher(source).matches()) {
                return source;
            } else {
                showToastNotSupportedText();
                return "";
            }
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_back:  // 공간 추가 취소
                    finish();
                    break;

                case R.id.bt_ok:    // 공간 추가 완료
                    try {
                        String room_name = et_name.getText().toString();
                        if (room_name.length() > 0) {
                            saveSpace(room_name);
                            if (ACTIVITY_MODE.equalsIgnoreCase(NameSpace.EDIT_MODE)) {
                                finish();
                            }else {
//                                showCheckDeviceEditDialog();
                                finish();
                            }
                        } else {
                            showToastSpaceNameShort();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void showCheckDeviceEditDialog(){

        try{
            SpaceDialog spaceDialog = new SpaceDialog(AddSpaceActivity.this);
            spaceDialog.setCancelable(false);
            spaceDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveSpace(String room_name){
        try {
            if ((room_name.length() > 0) && (num_pages >= 0)) {
                if (ACTIVITY_MODE.equalsIgnoreCase(NameSpace.EDIT_MODE)) {
                    PageData pageData = PageDataManager.getInst().getPageData(num_pages);
                    if(pageData != null) {
                        pageData.name = room_name;
                        pageData.background = selected;
                        Log.d(TAG, "replace page = " + num_pages + " / room : " + room_name);
                        PageDataManager.getInst().replacePage(num_pages, pageData);
                    }
                }
                else{
                    PageData pageData = new PageData();
                    if(pageData != null) {
                        pageData.name = room_name;
                        pageData.background = selected;
                        Log.d(TAG, "add page = " + num_pages + " / room : " + room_name);
                        PageDataManager.getInst().addPage(pageData);
                    }
                }
            } else {
                Log.d(TAG, "saveSpace room_name is empty");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastSpaceNameShort(){
        try {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.space_name_short));
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

    private void setSelectedItem(String name){

//        selected = index;
//
//        try{
        switch (name){

            case "1":
                try {
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_1));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case "2":
                try {
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_2));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case "3":
                try{
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_3));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case "4":
                try{
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_4));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case "5":
                try{
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_5));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case "6":
                try{
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_6));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case "7":
                try{
                    iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_7));
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                try{
                    FileEx fileEx = new FileEx();
                    Bitmap bitmap = fileEx.findImage(name,
                            (int)getResources().getDimension(R.dimen.add_space_selected_picture_width),
                            (int)getResources().getDimension(R.dimen.add_space_selected_picture_height));
                    boolean failed = false;

                    if (bitmap==null){
                        failed=true;
                    }else {
                        try {
                            Drawable drawable = BitmapTool.copy(getApplicationContext(), bitmap);
                            iv_space_picture.setBackground(drawable);
                        }catch (Exception e){
                            failed=true;
                            e.getMessage();
                        }
                    }

                    if (failed){
                        iv_space_picture.setBackground(BitmapTool.copy(getApplicationContext(), R.mipmap.bg_home_img_sm_1));
                        selected="1";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private void clearNavigationBar(){

        try {
            Log.d(TAG, "clearNavigationBar");
            final int flags =
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;

            // This work only for android 4.4+
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        BitmapTool.recursive(getWindow().getDecorView().getRootView());
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearNavigationBar();
        finish();
    }
}