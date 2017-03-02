package com.commax.controlsub.Connection_Guide;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

public class Connection_Guide_ListCell implements Comparable<Connection_Guide_ListCell> {

	Drawable mIcon = null;
	Drawable mDeviceImage = null;
	public String DeviceName;
	public String CompanyName;
	public String guide_text;

	public Connection_Guide_ListCell(String DeviceName, String CompanyName )
	{
		this.DeviceName = DeviceName;
		this.CompanyName = CompanyName;
	}

	public Connection_Guide_ListCell(Drawable micon , String DeviceName, String CompanyName)
	{
		this.mIcon = micon;
		this.DeviceName = DeviceName;
		this.CompanyName = CompanyName;
	}
	public Connection_Guide_ListCell(Drawable micon , String DeviceName, String CompanyName, String guide_text , Drawable mDeviceImage)
	{
		this.mIcon = micon;
		this.DeviceName = DeviceName;
		this.CompanyName = CompanyName;
		this.guide_text = guide_text;
		this.mDeviceImage = mDeviceImage;
	}

	public Drawable getmIcon(){
		//drawable to imageview
		return mIcon;
	}

	public Drawable getmDeviceImage()
	{
		return mDeviceImage;
	}

	public String getDeviceName()
	{
		return DeviceName;
	}
	
	public String getCompanyName()
	{
		return CompanyName;
	}

	@Override
	public int compareTo(Connection_Guide_ListCell other) {
		return this.DeviceName.compareTo(other.DeviceName);
	}

	public static final Comparator<Connection_Guide_ListCell> ALPHA_COMPARATOR = new Comparator<Connection_Guide_ListCell>() {
		private final Collator sCollator = Collator.getInstance();

		@Override
		public int compare(Connection_Guide_ListCell object1, Connection_Guide_ListCell object2) {
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
			return sCollator.compare(object1.DeviceName, object2.DeviceName);
		}
	};


}
