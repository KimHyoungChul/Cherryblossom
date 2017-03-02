package com.commax.headerlist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by OWNER on 2016-06-27.
 */
public class All_dialg {
    All_dialg(){

    }
    private static final String TAG = All_dialg.class.getSimpleName();
    static UpdateButtonClick updateButtonClick = new UpdateButtonClick();
    static String Upgrade = MainActivity.getInstance().getString(R.string.upgrade);
    //preferences ip value
    static int ip_number_check = 0;
    //ip maximum length
    private static final int LENGTH = 15;

    //Cmx update manager update dialog
    static void download_dialog(final String appName, String versionName, String PackageName)
    {
        final String fileName = PackageName + "."+ versionName + ".apk";
        //TODO update dialog
        new AlertDialog.Builder(MainActivity.getInstance())
                .setTitle(R.string.title)
                .setMessage(R.string.updatedialog_message)
                .setPositiveButton(Upgrade,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i)
                            {
                                Log.d(TAG , "dialog update exe");
                                //업데이트 진행
                                progressdownload();
                                //TODO error
                                Log.e(TAG,"url : " + UpdateButtonClick.APPLICATION_DOWNLOAD_URL + "fileName :" + fileName + "appName" + appName );
                                updateButtonClick.downloadUpdate(updateButtonClick.APPLICATION_DOWNLOAD_URL + fileName, fileName,updateButtonClick.Downloaddirectory, appName);
                        }
        }).show();
     /* //TODO update and cancle dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // 제목셋팅
        alertDialogBuilder.setTitle(MainActivity.getInstance().getString(R.string.title));

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(MainActivity.getInstance().getString(string.updatedialog_message))
                .setCancelable(false)
                .setPositiveButton(Update,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                //업데이트 진행
                                progressdownload();
                                updateButton.downloadUpdate(updateButton.APPLICATION_DOWNLOAD_URL + fileName, fileName,
                                        updateButton.Downloaddirectory, appName);
//                                updateButton.downloadUpdate();
                            }
                        })
                .setNegativeButton(Cancle,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                //다이얼로그 취소 버튼
                                dialog.cancel();
                                Log.d(TAG,"취소");
                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
        */

    }
    static void progressdownload(){
        MainActivity.getInstance().progressDialog.setCancelable(true);
        MainActivity.getInstance().progressDialog.setMessage(MainActivity.getInstance().getString(R.string.networkdownloading));
        MainActivity.getInstance().progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        MainActivity.getInstance().progressDialog.setProgress(0);
        MainActivity.getInstance().progressDialog.setMax(100);
        MainActivity.getInstance().progressDialog.show();
    }

    public static void ip_setting(){
        ip_number_check ++;
        if(ip_number_check == 2)
        {
            Log.d(TAG , "onIPsetting");
            LayoutInflater inflater= MainActivity.getInstance().getLayoutInflater();
            final View dialogView= inflater.inflate(R.layout.ipsetting_custom_dialog, null);
            //멤버의 세부내역 입력 Dialog 생성 및 보이기
            final AlertDialog.Builder buider= new AlertDialog.Builder(MainActivity.getInstance()); //AlertDialog.Builder 객체 생성
            final EditText edit_ip= (EditText)dialogView.findViewById(R.id.ipsetting);

            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(LENGTH);
            edit_ip.setFilters(filterArray); //입력길이 제한
            edit_ip.setTextIsSelectable(true);//keyborad hide

            buider.setTitle(MainActivity.getInstance().getString(R.string.ip_setting_title)); //Dialog 제목
            buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
            buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
            edit_ip.setText(BackgroundService.CloudServerIP);
            edit_ip.setSelection(edit_ip.length()); // 커서 맨 뒤로
            buider.setPositiveButton(MainActivity.getInstance().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            buider.setNegativeButton(MainActivity.getInstance().getString(R.string.cancle) , new DialogInterface.OnClickListener() {
                //Dialog에 "Cancel"이라는 타이틀의 버튼을 설정
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.getInstance(), "취소되었습니다." , Toast.LENGTH_SHORT).show();
                }
            });
            //설정한 값으로 AlertDialog 객체 생성
            final AlertDialog dialog=buider.create();
            //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
            dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
            //Dialog 보이기
            dialog.show();
            //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(IPisValid.isValid(edit_ip))
                    {
                        Log.d(TAG, edit_ip + "is valid");
                        BackgroundService.CloudServerIP = edit_ip.getText().toString();
                        MainActivity.getInstance().editor.putString("ServerIP",BackgroundService.CloudServerIP);
                        Log.d(TAG, "putString");
                        MainActivity.getInstance().editor.commit();
                        Log.d(TAG,"commit()");
                        Toast.makeText(MainActivity.getInstance() , BackgroundService.CloudServerIP , Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        MainActivity.getInstance().startTask();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.getInstance() , "IP다시 입력해 주세요" , Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Log.d(TAG, "CloudServerIP" + BackgroundService.CloudServerIP);
            ip_number_check = 0;
        }
    }
}
