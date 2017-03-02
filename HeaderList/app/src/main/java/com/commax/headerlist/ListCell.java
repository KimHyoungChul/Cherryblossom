package com.commax.headerlist;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

public class ListCell implements Comparable<ListCell> {
	Drawable mIcon = null;
	private String name;
	private String versionName;
	private String category;
	private String PackageName;
	private boolean isSectionHeader;
	
	public ListCell(Drawable micon , String Appname, String PackageName, String versionName, String category) //String versionName,String category)
	{
		this.mIcon = micon;
		this.PackageName = PackageName;
		this.name = Appname;
		this.versionName = versionName;
		this.category = category;
		isSectionHeader = false;
	}

	public Drawable getmIcon(){
		//drawable to imageview
		return mIcon;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getCategory()
	{
		return category;
	}

	public String getVersionName()
	{
		return versionName;
	}

	public String getPackageName(){
		return PackageName;
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
		return this.category.compareTo(other.category);
	}

	public static final Comparator<ListCell> ALPHA_COMPARATOR = new Comparator<ListCell>() {
		private final Collator sCollator = Collator.getInstance();

		@Override
		public int compare(ListCell object1, ListCell object2) {
			return sCollator.compare(object1.name, object2.name);
		}
	};


}
