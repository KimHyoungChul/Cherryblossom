package com.commax.updatemanager.GetAPPList_Download;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.updatemanager.ImageLoad.ImageRoader;
import com.commax.updatemanager.MainActivity;
import com.commax.updatemanager.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ListHeaderAdapter extends BaseAdapter {//ArrayAdapter<ListCell> {
	private static final String TAG = ListHeaderAdapter.class.getSimpleName();
	UpdateButtonClick updateButton;
	VersionCompare versionCompare = new VersionCompare();

	LayoutInflater inflater;
	private ArrayList<ListCell> mListData = new ArrayList<ListCell>();
	private Context mContext = null;
	private PackageManager pm;
	//fot badgecount
	int listupdatecount =0;
	Handler mHanler;

	//for version compare
	String[] serversion; // "." 을 기준으로 파싱해서 값 비교하기 때문에 배열
	String[] oldversion; // "."을 기준으로 파싱해서 값 비교하기 위해 만들 배열
	String version;

	//for Button Text
	String Upgrade = MainActivity.getInstance().getString(R.string.upgrade);
	String Install = MainActivity.getInstance().getString(R.string.install);
	String Newest = MainActivity.getInstance().getString(R.string.newest);

	String LocalServer = null;

	//for upgrade dialog message
	public static final int MESSAGE_DOWNLOAD_STARTING = 3;
	public static final int MESSAGE_DOWNLOAD_PROGRESS = 4;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 5;

	int updatecount = 1;

	//TODO 인터넷 커넥션을 먼저 체크 후 알려주어야 하려나?
	ConnectivityManager manager;
	NetworkInfo wifi;


	public long getItemId(int position) {
//        return 0;
		return position;
	}
	public int getCount() {
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
		//return null;
	}

	public ListHeaderAdapter(Context context){//, ArrayList<ListCell> items) {
		super(); //context, 0, items);
		this.mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		updateButton = new UpdateButtonClick(mContext);
	}

	public ListHeaderAdapter(Context context , String localServer){
		super(); //context, 0, items);
		this.mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		updateButton = new UpdateButtonClick(mContext , localServer);
		LocalServer = localServer;

	}

	public ListHeaderAdapter(Context context , String localServer , Handler handler){
		super(); //context, 0, items);
		this.mContext = context;
		mHanler = handler;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		updateButton = new UpdateButtonClick(mContext , localServer , handler);

		manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		LocalServer = localServer;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView");

		View v = convertView;
		final ListCell cell = mListData.get(position);//getItem(position);

		//If the cell is a section header we inflate the header layout
		if (cell.isSectionHeader()) {
			//헤더
			v = inflater.inflate(R.layout.list_header_layout, null);
			v.setClickable(false);
			TextView header = (TextView) v.findViewById(R.id.list_header_title);
			header.setText(cell.getCategory());
			Log.d(TAG, "cell.category : " +  cell.getCategory());
		}
		else
		{
			// List Item
			v = inflater.inflate(R.layout.list_item_layout, null);
			TextView AppName = (TextView) v.findViewById(R.id.app_name);
			TextView VersionName = (TextView) v.findViewById(R.id.app_version);
			TextView PackageName =(TextView)v.findViewById(R.id.app_package);

			//이미 월패드에 설치되어 있는 APP은 다국어 대응을 위해 앱 이름을 가져온다.
			//월패드에 설치되어 있지 않은 App은 서버에서 가져온 영어 AppName 으로 사용한다.
			if(cell.getCategory().equals(Newest) || cell.getCategory().equals(Upgrade))
			{
				String name = getappname(cell.getPackageName(), cell.getAppName());
				AppName.setText(name);
			}
			else
			{
				AppName.setText(cell.getAppName());
			}
			VersionName.setText(cell.getVersionName());
			PackageName.setText(cell.getPackageName());

			final Button mUpgrade = (Button) v.findViewById(R.id.app_upgrade);
			ImageView mIcon = (ImageView) v.findViewById(R.id.app_icon);
			//VersionName 가져오기
			if (cell.getCategory().equals(Upgrade)) {
				try
				{
					mIcon.setImageDrawable( getIcon(cell.getPackageName()));
				}
				catch (Exception e)
				{
					e.printStackTrace();
					mIcon.setImageResource(R.mipmap.ic_launcher);
					Log.e(TAG, " cat't get upgrade icon ");
				}
				mUpgrade.setBackgroundResource(R.drawable.btn_gen1_press);
				mUpgrade.setText(Upgrade);

				if (cell.getPackageName().equals("com.commax.updatemanagerdemo"))
				{
					//본 앱이 업데이트가 필요하면 다이얼로그 띄워서 업데이트를 시킨다.
					if(MainActivity.getInstance().mDialog)
					{
						dialog(cell.getAppName(), cell.getVersionName(), cell.getPackageName());
						MainActivity.getInstance().mDialog = false;
					}
					Log.d(TAG, "dialog");
				}
			}
			else if (cell.getCategory().equals(Install)) {
				mUpgrade.setText(Install);
				mUpgrade.setBackgroundResource(R.drawable.control_bg_common);
				try {
					//서버에서 아이콘 이미지 받아오기
					mIcon.setImageBitmap(new ImageRoader(LocalServer).
							getBitmapImg(cell.getPackageName() + ".png"));
					Log.d(TAG, " install icon try");
				} catch (Exception e) {
					//아이콘 다운로드 실패하면 기본 이미지로
					Log.e(TAG, " download install icon fail in server ");
					mIcon.setImageResource(R.mipmap.ic_launcher);
				}
			}
			else if(cell.getCategory().equals(Newest))
			{
				try{
					mIcon.setImageDrawable(getIcon(cell.getPackageName()));
				}catch (Exception e)
				{
					e.printStackTrace();
					mIcon.setImageResource(R.mipmap.ic_launcher);
				}
				mUpgrade.setBackgroundResource(R.drawable.control_bg_common);
				mUpgrade.setText(Newest);
			}
			else {
				mIcon.setImageResource(R.mipmap.ic_launcher);
				Log.e(TAG,"header else  , use defalt icon");
			}

			//update Button click
			mUpgrade.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mUpgrade.getText().equals(Newest)){
						Toast.makeText(mContext, MainActivity.getInstance().getString(R.string.newestversion),Toast.LENGTH_SHORT).show();
						Log.d(TAG, Newest);
					}
					else
					{
						wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
						try
						{
							// wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
							if (wifi.isConnected()) {
								Log.i("연결됨" , "연결이 되었습니다.");
								progressdownload();
								//업데이트 함수 호출
								updateButton.checkForUpdate(cell.getPackageName(), cell.getVersionName(), Arrays.toString(oldversion), cell.getAppName());
							} else {
								Log.i("연결 안 됨" , "연결이 다시 한번 확인해주세요");
								Toast.makeText(mContext , MainActivity.getInstance().getString(R.string.network_connect_error), Toast.LENGTH_LONG).show();
							}

						}catch (Exception e)
						{
							e.printStackTrace();
						}
						//oldversion 초기화
						oldversion = null;
					}
					Log.d(TAG, "serverversion : "+ cell.getVersionName() +" , " +  cell.getAppName() + "   clicked");
				}
			});
			//TODO click listitem
			v.setOnClickListener(new View.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View v) {
						Log.d(TAG, "List Item clicked : " + cell.getAppName());
					}
				});
		}
			return v;
	}

	/**
	 * 어플리케이션 리스트 작성 -> 배열에 있는 정보들을 list로 작성하도록 수정해주어야 한다.
	 */
	public void rebuild() {
		Log.d(TAG,"rebuild");
		mListData.clear();
		try
		{
			if(0 == GetAppList.getInstance().getAppName.length)
			{
				Toast.makeText(mContext, "no data in Server ", Toast.LENGTH_LONG).show();
				Log.e(TAG, " no data recieved in server");
			}
			else {
				for (int i =0 ; i < GetAppList.getInstance().getAppName.length ; i++)//JSONHelper.list_cnt ; i++)
				{
					mListData.add(new ListCell(GetAppList.getInstance().getAppName[i],
							GetAppList.getInstance().getPackageName[i], GetAppList.getInstance().getVersionName[i] ,
							versionCompare.Compare(GetAppList.getInstance().getPackageName[i]  ,GetAppList.getInstance().getVersionName[i] , mContext)));
					//versionCompare(GetAppList.getInstance().getPackageName[i]  ,GetAppList.getInstance().getVersionName[i] )));
				}
				mListData = sortAndAddSections(mListData);
				Log.d(TAG, "mList Data " + mListData);

				//알파벳 정렬(지금은 필요없음)
				//Collections.sort(mListData, ListCell.ALPHA_COMPARATOR);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}



	/* 앱 리스트 들이 서버에서 불러오는 순서의 역순으로 정렬이 된다. 영어 app name 이름 순으로 */
/*
	private ArrayList<ListCell> sortAndAddSections(ArrayList<ListCell> itemList)
	{
		ArrayList<ArrayList<ListCell>> total = new ArrayList<ArrayList<ListCell>>();
		ArrayList<ListCell> tempupdate = new ArrayList<ListCell>();
		ArrayList<ListCell> tempList = new ArrayList<ListCell>();
		//First we sort the array
		Collections.sort(itemList);
		Collections.reverse(itemList);

		//Loops thorugh the list and add a section before each sectioncell start
		String header = "";
		String update = MainActivity.getInstance().getString(R.string.update);

		for(int i = 0; i < itemList.size(); i++)
		{
			//If it is the start of a new section we create a new listcell and add it to our array
			if(header != itemList.get(i).getCategory()){
				ListCell sectionCell = new ListCell(null ,null,null, itemList.get(i).getCategory()); //??
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
				//update header = index 0
				//update group index = 1 부터
				tempList.add(1, itemList.get(i));
			}
			else{
				tempList.add(itemList.get(i));
			}
		}
//		Collections.sort(tempList, ListCell.ALPHA_COMPARATOR);
		return tempList;
	}
*/

	/* 서버에서 불러오는 영어  app name 의 정순 으로 정렬이 된다.  */
	private ArrayList<ListCell> sortAndAddSections(ArrayList<ListCell> itemList)
	{
		ArrayList<ListCell> tempList = new ArrayList<ListCell>();
		Collections.sort(itemList);
		int arraycount = 0;
		//Loops thorugh the list and add a section before each sectioncell start
		String header = "";
		for(int i = 0; i < itemList.size(); i++)
		{
			//If it is the start of a new section we create a new listcell and add it to our array
			if(header != itemList.get(i).getCategory()){
				ListCell sectionCell = new ListCell(null, null,null,null, itemList.get(i).getCategory()); //??
				sectionCell.setToSectionHeader();
				//update header index = 0
				if(itemList.get(i).getCategory().equals(Install))
				{
					tempList.add(0,sectionCell);
				}
				else if(itemList.get(i).getCategory().equals(Upgrade))
				{
					if(arraycount == 0)
					{
						tempList.add(0 ,sectionCell );
					}
					else
					{
						tempList.add(arraycount+1, sectionCell);
					}
					Log.d(TAG," categirt newest arraycount :" + arraycount);
				}
				else
				{
					tempList.add(sectionCell);
				}
				header = itemList.get(i).getCategory();
			}
			//update category index = 0
			if(header.equals(Install))
			{
				//update header = index 0
				//update group index = 1 부터
				tempList.add(1, itemList.get(i));
				arraycount++;
				Log.e(TAG, "arraycount ++ : "+arraycount);
			}
			else if(header.equals(Upgrade))
			{
				if(arraycount == 0)
				{
					tempList.add(1, itemList.get(i));
				}else
				{
					tempList.add(arraycount+2, itemList.get(i));
				}
				Log.d(TAG ,"arraycount : " + arraycount);
			}
			else{
				tempList.add(itemList.get(i));
			}
		}
		return tempList;
	}


	//Cmx update manager update dialog
	void dialog(final String appName, String versionName , final String PackageName)
	{
		final String fileName = PackageName + "."+ versionName + ".apk";
		new AlertDialog.Builder(mContext)
				.setTitle(R.string.title)
				.setMessage(R.string.updatedialog_message)
				.setPositiveButton(R.string.update,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialoginterface, int i)
							{
								Log.d(TAG , "dialog update exe");
								wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
								//업데이트 진행
								if(wifi.isConnected())
								{

									progressdownload();
									updateButton.downloadUpdate(updateButton.APPLICATION_DOWNLOAD_URL + fileName, fileName,
											updateButton.Downloaddirectory, appName , PackageName);
								}
								else
								{
									Toast.makeText(mContext , MainActivity.getInstance().getString(R.string.network_connect_error), Toast.LENGTH_LONG).show();
								}
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

	// 설치 되어 있는 app의 아이콘 가져오기
	Drawable getIcon(String packageName)
	{
		pm = mContext.getPackageManager();
		Drawable d = null;
		try {
			d = pm.getApplicationIcon(packageName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return d;
	}

	String getappname(String packageName , String appname)
	{
		//다국어 지원하기 위해서 앱 이름을 가져온다.
		pm = mContext.getPackageManager();
		String name;
		try
		{
			name = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName,PackageManager.GET_META_DATA));

		}catch (Exception e)
		{
			name = appname;
		}
		return name;
	}

	public static Handler viewUpdateHandler = new Handler(){
		int time = 0;
		public void handleMessage(final Message msg) {

			switch(msg.what)
			{
				case MESSAGE_DOWNLOAD_STARTING :
					Log.d(TAG, "프로그레스바 시작");
					break;
				case MESSAGE_DOWNLOAD_PROGRESS :
					time = (int)msg.arg1*100/msg.arg2;
//					Log.d(TAG,"time  = " + time);
					MainActivity.getInstance().progressDialog.setProgress(time);
//					Log.d(TAG," 다운로드 중 : " + msg.arg1 + " /" + msg.arg2);
					break;
				case MESSAGE_DOWNLOAD_COMPLETE :
					Log.d(TAG, "다운로드 완료.");
					MainActivity.getInstance().progressDialog.dismiss();
					break;
			}
			super.handleMessage(msg);
		}
	};

	void progressdownload(){
		MainActivity.getInstance().progressDialog.setCancelable(true);
		MainActivity.getInstance().progressDialog.setMessage(MainActivity.getInstance().getString(R.string.networkdownloading));
		MainActivity.getInstance().progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		MainActivity.getInstance().progressDialog.setProgress(0);
		MainActivity.getInstance().progressDialog.setMax(100);
		MainActivity.getInstance().progressDialog.show();
	}
}
