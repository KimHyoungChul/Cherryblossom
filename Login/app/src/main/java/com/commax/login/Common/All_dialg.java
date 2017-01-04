package com.commax.login.Common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.commax.login.R;
import com.commax.login.SubActivity;

import java.util.regex.Pattern;

/**
 * Created by OWNER on 2016-06-27.
 */
public class All_dialg {

    AboutFile aboutFile = new AboutFile();
    Handler mHandler;

    public All_dialg(Handler handler) {
        mHandler = handler;
    }

    private static final String TAG = All_dialg.class.getSimpleName();
    //preferences ip value
    static int ip_number_check = 0;
    //ip maximum length
    private static final int LENGTH = 20;

    //Cmx update manager update dialog
    void id_initial_check_dialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SubActivity.getInstance());
        // 제목셋팅
        alertDialogBuilder.setTitle(SubActivity.getInstance().getString(R.string.user_initial));
        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(SubActivity.getInstance().getString(R.string.user_id_initial_message))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                Log.d(TAG, "사용자 초기화");
                                String initialize[] = {"v1", "user/me", "14", aboutFile.readFile("token")};
                                SubActivity.getInstance().startTask(initialize);
                            }
                        })
                .setNegativeButton(R.string.cancle,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                //다이얼로그 취소 버튼
                                dialog.cancel();
                                Log.d(TAG, "취소");
                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }

    public void change_password_dialog() {

        Log.d(TAG, "change password dialog");
        LayoutInflater inflater = SubActivity.getInstance().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.password_change_dialog, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        final AlertDialog.Builder buider = new AlertDialog.Builder(SubActivity.getInstance()); //AlertDialog.Builder 객체 생성
        final EditText pwd = (EditText) dialogView.findViewById(R.id.pwd_dialog_change);
        final EditText pwd_check = (EditText) dialogView.findViewById(R.id.pwd_dialog_change_check);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(LENGTH);
        pwd.setFilters(filterArray); //입력길이 제한
        pwd.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num_ect()}); //숫자와 알파벳 특수기호 입력
        pwd.setPrivateImeOptions("defaultInputmode=english;");                 //영어 키패드 먼저 나오기

        pwd_check.setFilters(filterArray); //입력길이 제한
        pwd_check.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num_ect()}); //숫자와 알파벳 특수기호 입력
        pwd_check.setPrivateImeOptions("defaultInputmode=english;");                 //영어 키패드 먼저 나오기
//        pwd.setTextIsSelectable(true);//keyborad hide
//        pwd_check.setTextIsSelectable(true);//keyborad hide

        buider.setTitle(SubActivity.getInstance().getString(R.string.password_change)); //Dialog 제목
//        buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
//        pwd.setText(aboutFile.readFile("password"));
        pwd.setSelection(pwd.length()); // 커서 맨 뒤로
        buider.setPositiveButton(SubActivity.getInstance().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        buider.setNegativeButton(SubActivity.getInstance().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            //Dialog에 "Cancel"이라는 타이틀의 버튼을 설정
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "취소");
            }
        });
        //설정한 값으로 AlertDialog 객체 생성
        final AlertDialog dialog = buider.create();
        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
        //Dialog 보이기
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pwd.getText().toString().equals(pwd_check.getText().toString())) {
                    Log.d(TAG, " pwd : " + pwd.getText().toString());
                    String[] pwd_change = {"v1", "user/me", "13", pwd.getText().toString()};
                    SubActivity.getInstance().startTask(pwd_change);
//                    new AppTask().execute("v1" , "user/me" , "13" , pwd.getText().toString());
                    dialog.dismiss();
                } else {
                    //handler 처리
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.password_passwordcheck_different));
                    mHandler.sendMessage(msg);
                }
            }
        });
    }


    protected class CustomInputFilter_alpha_num_ect implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[!@#$%^&*+=-_a-zA-Z0-9]+$");

            if (source.equals("") || ps.matcher(source).matches()) {

                return source;
            } else {
               /* MainActivity.getInstance().mToast.setText(SubActivity.getInstance().getString(R.string.edit_alpha_num_etc_only));
                MainActivity.getInstance().mToast.show();*/
//                Toast.makeText(SubActivity.getInstance().getApplicationContext() ,SubActivity.getInstance().getString(R.string.edit_alpha_num_etc_only), Toast.LENGTH_SHORT).show();
                return "";
            }
        }
    }

}
