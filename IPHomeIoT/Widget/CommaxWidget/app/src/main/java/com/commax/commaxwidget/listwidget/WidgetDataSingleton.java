package com.commax.commaxwidget.listwidget;

import java.util.ArrayList;

/**
 * Created by OWNER on 2017-02-10.
 */
public class WidgetDataSingleton {
    private static WidgetDataSingleton uniqueInstance;
    private ArrayList<ListWidgetItemData> mDataArray;

    private WidgetDataSingleton() {}
    public static WidgetDataSingleton getInstance() {
        if(uniqueInstance == null) {
            uniqueInstance = new WidgetDataSingleton();
        }
        return uniqueInstance;
    }

    public void setmDataArray(ArrayList<ListWidgetItemData> array){
        mDataArray = array;
    }

    public ArrayList<ListWidgetItemData> getmDataArray(){
        return  mDataArray;
    }
}
