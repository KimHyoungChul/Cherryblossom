package com.commax.controlsub.DeviceListEdit;

import java.text.Collator;
import java.util.Comparator;

public class ControlName_Edit_ListCell implements Comparable<ControlName_Edit_ListCell> {

	public String nickname;
	public String category;
	public String RootUuid;
	public String update_nickname;
	public boolean isable = true ;	//isable 이 false 이면 해당 디바이스는 삭제될 것이기때문에 disable 시킨다.
	public boolean is_all_selected = false; // 전체 체크박스 선택 default : false , 선택된것 : true
	public boolean nickname_null_chekc = false ;  //닉네임명이 null 이면 해당 flag 는 true , defale : false


	public boolean updated; //닉네임 업데이트 유무

	private boolean isSectionHeader;
	
	public ControlName_Edit_ListCell(String name, String category , String rootUuid) //String versionName,String category)
	{
		this.nickname = name;
		this.category = category;
		this.RootUuid = rootUuid;
		isSectionHeader = false;
	}
	
	public String getNickName()
	{
		return nickname;
	}
	
	public String getCategory()
	{
		return category;
	}

	public String getRootUuid()
	{
		return RootUuid;
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
	public int compareTo(ControlName_Edit_ListCell other) {
		return this.category.compareTo(other.category);
	}

	/**
	 * 알파벳 이름으로 정렬
	 */
	public static final Comparator<ControlName_Edit_ListCell> ALPHA_COMPARATOR = new Comparator<ControlName_Edit_ListCell>() {
		private final Collator sCollator = Collator.getInstance();
		@Override
		public int compare(ControlName_Edit_ListCell object1, ControlName_Edit_ListCell object2) {

			return sCollator.compare(object1.nickname, object2.nickname);
//			return 0;
		}
	};

}
