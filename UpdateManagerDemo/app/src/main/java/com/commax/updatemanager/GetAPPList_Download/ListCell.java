package com.commax.updatemanager.GetAPPList_Download;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

public class ListCell implements Comparable<ListCell> {

	Drawable mIcon = null;

	public String AppName;
	public String versionName;
	public String PackageName;
	public String Category;
	public boolean isSectionHeader;
	
	public ListCell(String Appname, String PackageName , String versionName , String category) //String versionName,String category)
	{
		this.AppName = Appname;
		this.versionName = versionName;
		this.PackageName= PackageName;
		this.Category = category;
		isSectionHeader = false;
	}

	public ListCell(Drawable micon , String Appname, String PackageName, String versionName, String category) //String versionName,String category)
	{
		this.mIcon = micon;
		this.PackageName = PackageName;
		this.AppName = Appname;
		this.versionName = versionName;
		this.Category = category;
		isSectionHeader = false;
	}



	public Drawable getmIcon(){
		//drawable to imageview
		return mIcon;
	}

	public String getAppName()
	{
		return AppName;
	}
	
	public String getPackageName()
	{
		return PackageName;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public String getCategory()
	{
		return Category;
	}
	
	public void setToSectionHeader()
	{
		isSectionHeader = true;
	}
	
	public boolean isSectionHeader()
	{
		return isSectionHeader;
	}
	
	@Override
	public int compareTo(ListCell other) {
		return this.Category.compareTo(other.Category);
	}

	public static final Comparator<ListCell> ALPHA_COMPARATOR = new Comparator<ListCell>() {
		private final Collator sCollator = Collator.getInstance();

		@Override
		public int compare(ListCell object1, ListCell object2) {
//            Log.d(TAG,"compare name list by alphabet");
//			if(object1.getCategory().equals(object2.getCategory()))
//			{
//				Log.d("test","compare :" + sCollator.compare(object1.name, object2.name))
//				return sCollator.compare(object1.name, object2.name);
//			}
//			else
//			{
//				return sCollator.compare(object1.name, object2.name);
//			}
			return sCollator.compare(object1.AppName, object2.AppName);
		}
	};


}
