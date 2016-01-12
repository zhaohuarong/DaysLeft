/**
 * custom preferences
 * */

package com.zhaohr.daysleft;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Preferences extends ContextWrapper {

    private static final String Preferences_Name = "DaysLeft";
    private static final String DATE_LIST = "DateList";

    private SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    public Preferences(Context base) {
        super(base);
        mSharedPreferences = getSharedPreferences(Preferences_Name, Activity.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void appendDate(ItemDate d) {
        ArrayList<ItemDate> lstArray = readDateList();
        lstArray.add(d);
        writeDateList(lstArray);
    }

    public void modifyDate(int index, ItemDate d) {
        ArrayList<ItemDate> lstArray = readDateList();
        if(index >= lstArray.size())
            return;
        lstArray.get(index).copyValue(d);
        Log.i("xxx", "item date1:" + d.title() + "-" + d.year() + "-" + d.month() + "-" + d.day());
        writeDateList(lstArray);
    }

    public void deleteDate(int index) {
        ArrayList<ItemDate> lstArray = readDateList();
        lstArray.remove(index);
        writeDateList(lstArray);
    }

    public ArrayList<ItemDate> readDateList() {
        ArrayList<ItemDate> lstArray = new ArrayList<ItemDate>();
        String strDate = mSharedPreferences.getString(DATE_LIST, "");
        String[] lst = strDate.split(",");
        for(String s : lst) {
            if(s.length() < 5)
                continue;
            ItemDate item = ItemDate.fromString(s);
            lstArray.add(item);
        }

        Collections.sort(lstArray, new SortByLeftDays());

        return lstArray;
    }

    public void writeDateList(ArrayList<ItemDate> lstData) {
        String strDateList = "";
        for(ItemDate d : lstData) {
            strDateList += d.toString();
            strDateList += ",";
        }
        mEditor.putString(DATE_LIST, strDateList);
        mEditor.commit();
    }

    class SortByLeftDays implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            ItemDate v1 = (ItemDate)lhs;
            ItemDate v2 = (ItemDate)rhs;
            return v1.compareTo(v2);
        }
    }
}
