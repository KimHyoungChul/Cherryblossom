package com.commax.commaxwidget.wallpad.data;

import com.commax.commaxwidget.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Items {
	public static final String ELEC = "1";
	public static final String GAS = "2";
	public static final String WATER = "3";
	public static final String HOTWATER = "4";
	public static final String HEATING = "5";
	
	public static List<ListItem> ITEMS = new ArrayList<ListItem>();
	public static Map<String, ListItem> ITEM_MAP = new HashMap<String, ListItem>();

	static {
		addItem(new ListItem(ELEC, R.string.elec, 0,"Electricity"));
		addItem(new ListItem(GAS, R.string.gas,	1,"Gas"));
		addItem(new ListItem(WATER, R.string.water, 2,"Water"));
		addItem(new ListItem(HOTWATER, R.string.hot_water, 3,"HotWater"));
		addItem(new ListItem(HEATING, R.string.heating, 4,"Heating"));
	}

	private static void addItem(ListItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	public static class ListItem {
		public String id;
		public int titleResId;
		public int index;
		public String name;
		public ListItem(String id, int titleResId, int index, String name) {
			this.id = id;
			this.titleResId = titleResId;
			this.index = index;
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
}
