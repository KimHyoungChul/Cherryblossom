package com.commax.headerlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.Collections;

public class ListAdapter3 extends BaseAdapter {
	private static final String TAG = ListAdapter3.class.getSimpleName();
	UpdateButtonClick updateButton = new UpdateButtonClick();
	AboutFile aboutFile = new AboutFile();
	VersionCompare versionCompare = new VersionCompare();
	LayoutInflater inflater;
	private ArrayList<ListCell> mListData = new ArrayList<ListCell>();
	private Context mContext = null;
	private PackageManager pm;
	//for Button Text
	String Upgrade = MainActivity.getInstance().getString(R.string.upgrade);
	String Install = MainActivity.getInstance().getString(R.string.install);
	String Newest = MainActivity.getInstance().getString(R.string.newest);
	String Addinstall = MainActivity.getInstance().getString(R.string.addinstall);

	//for upgrade dialog message
	public static final int MESSAGE_DOWNLOAD_STARTING = 3;
	public static final int MESSAGE_DOWNLOAD_PROGRESS = 4;
	public static final int MESSAGE_DOWNLOAD_COMPLETE = 5;

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

	public ListAdapter3(Context context){//, ArrayList<ListCell> items) {
		super(); //context, 0, items);
		this.mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		final ListCell cell = mListData.get(position);//getItem(position);
		//If the cell is a section header we inflate the header layout
		if (cell.isSectionHeader()) {
			v = inflater.inflate(R.layout.section_header, null);

			v.setClickable(false);

			TextView header = (TextView) v.findViewById(R.id.section_header);
			header.setText(cell.getName());//TODO ??
		} else {
			v = inflater.inflate(R.layout.list_item, null);
			TextView AppName = (TextView) v.findViewById(R.id.app_name);
			TextView VersionName = (TextView) v.findViewById(R.id.app_version);

			if(cell.getCategory().equals(Newest) || cell.getCategory().equals(Upgrade))
			{
				String name = getappname(cell.getPackageName(), cell.getName());
				AppName.setText(name);
			}
			else
			{
				AppName.setText(cell.getName());
			}

			VersionName.setText(cell.getVersionName());
			final Button mUpgrade = (Button) v.findViewById(R.id.app_upgrade);
			ImageView mIcon = (ImageView) v.findViewById(R.id.app_icon);
			mIcon.setImageDrawable(cell.getmIcon());

			//VersionName 가져오기
			if (cell.getCategory().equals(Upgrade)) {
				mUpgrade.setBackgroundResource(R.drawable.btn_gen1_press);
				mUpgrade.setText(Upgrade);
				if (cell.getPackageName().equals("com.commax.headerlist")) {
					if(MainActivity.getInstance().mDialog) { // 시작시에 한번만 띄우기
						All_dialg.download_dialog(cell.getName(), cell.getVersionName(), cell.getPackageName());
						MainActivity.getInstance().mDialog = false;
					}
					Log.d(TAG, "dialog");
				}
			} else if (cell.getCategory().equals(Addinstall)) {
				mUpgrade.setText(Install);
				mUpgrade.setBackgroundResource(R.drawable.control_bg_common);
			}
			else if(cell.getCategory().equals(Newest))
			{
				mUpgrade.setBackgroundResource(R.drawable.control_bg_common);
				mUpgrade.setText(Newest);
			}
			else {
				Log.d(TAG,"category Error");
			}
			//update Button click
			mUpgrade.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mUpgrade.getText().equals(Newest)){
						Toast.makeText(MainActivity.getInstance(), MainActivity.getInstance().getString(R.string.newestversion),Toast.LENGTH_SHORT).show();
						Log.d(TAG, Newest);
					}
					else
					{
						All_dialg.progressdownload();
						Log.e(TAG,"backgroundService.CloudServerIP" + BackgroundService.CloudServerIP);
						updateButton.checkForUpdate(cell.getPackageName(), cell.getVersionName(), cell.getName() ,BackgroundService.CloudServerIP); //getChociePackagename, serverversion, Oldversionname, mAppname );
					}
					//업데이트 함수 호출
					Log.d(TAG, "serverversion : "+ cell.getVersionName());
					Log.d(TAG, cell.getName() + "   clicked");
				}
			});
			//TODO click listitem
			v.setOnClickListener(new View.OnClickListener() {
					@SuppressLint("ShowToast")
					@Override
					public void onClick(View v) {
						Toast.makeText(MainActivity.getInstance(),  cell.getName() + " 리스트 아이템이 클릭 되었습니다." , Toast.LENGTH_SHORT).show();
						Log.d(TAG, "List Item clicked : " + cell.getName());
					}
				});
		}
			return v;
	}
	/**
	 * 어플리케이션 리스트 작성 -> 배열에 있는 정보들을 list로 작성하도록 수정해주어야 한다.
	 */
	public void rebuild() {
		Log.d(TAG ,"rebuild");
		mListData.clear();
		int count = 0;
		//TODO editted  서버작업 이후 시작해야 함
		for (int i =0 ; i < JSONHelper.list_cnt ; i++)
		{
			String category = versionCompare.Compare(JSONHelper.getPackageName[i]  ,JSONHelper.getVersionName[i] , mContext );
			Log.d(TAG,"category =" +category);
			//TODO background 에서 check 하기위해 jsonhelper에서 배열 사용용
			mListData.add(new ListCell(ImageLoader.getIcon(JSONHelper.getPackageName[i]), JSONHelper.getAppName[i],
					JSONHelper.getPackageName[i], JSONHelper.getVersionName[i] , category));
			if(category.equals(Upgrade))
			{
				count++;
				Log.d(TAG, "count : " + count);
			}
		}
		Log.d(TAG,"before_badge_count : "+ count);
		//properties wirte
		aboutFile.writeFile("before_badge",String.valueOf(count));
		mListData = sortAndAddSections(mListData);
		//알파벳 정렬(지금은 필요없음)
//		Collections.sort(mListData, ListCell.ALPHA_COMPARATOR);
	}

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
				ListCell sectionCell = new ListCell(null, itemList.get(i).getCategory(),null,null, null); //??
				sectionCell.setToSectionHeader();
				//update header index = 0
				if(itemList.get(i).getCategory().equals(Upgrade))
				{
					tempList.add(0,sectionCell);
				}
				else if(itemList.get(i).getCategory().equals(Newest))
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
			if(header.equals(Upgrade))
			{
				//update header = index 0
				//update group index = 1 부터
				tempList.add(1, itemList.get(i));
				arraycount++;
				Log.e(TAG, "arraycount ++ : "+arraycount);
			}
			else if(header.equals(Newest))
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

	String getappname(String packageName , String appname)
	{
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
					Log.d(TAG,"time  = " + time);
					MainActivity.getInstance().progressDialog.setProgress(time);
					Log.d(TAG," 다운로드 중 : " + msg.arg1 + " /" + msg.arg2);
					break;
				case MESSAGE_DOWNLOAD_COMPLETE :
					Log.d(TAG, "다운로드 완료.");
					MainActivity.getInstance().progressDialog.dismiss();
					break;
			}
			super.handleMessage(msg);
		}
	};
}
