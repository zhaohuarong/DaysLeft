package com.zhaohr.daysleft;

import java.util.Calendar;
import java.util.Date;

public class ItemDate implements Comparable {

    private String strTitle;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mLeftDays = 0;
    private static final String SIGN = "#-#";

    public ItemDate() {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDaysLeft();
    }

    public ItemDate(String title, int year, int month, int day) {
        strTitle = title;
        mYear = year;
        mMonth = month;
        mDay = day;
        updateDaysLeft();
    }

    public ItemDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        updateDaysLeft();
    }

    public String toString() {
        String ret = strTitle + SIGN + String.valueOf(mYear) + SIGN + String.valueOf(mMonth) + SIGN + String.valueOf(mDay);
        return ret;
    }

    public static ItemDate fromeString(String strData) {
        String[] lst = strData.split(SIGN);
        if(lst.length != 4)
            return new ItemDate();
        return new ItemDate(lst[0], Integer.parseInt(lst[1]), Integer.parseInt(lst[2]), Integer.parseInt(lst[3]));
    }

    String title() {
        return strTitle;
    }

    int year() {
        return mYear;
    }

    int month() {
        return mMonth;
    }

    int day() {
        return mDay;
    }

    void setTitle(String s) {
        strTitle = s;
    }

    void setYear(int n) {
        mYear = n;
        updateDaysLeft();
    }

    void setMonth(int n) {
        mMonth = n;
        updateDaysLeft();
    }

    void setDay(int n) {
        mDay = n;
        updateDaysLeft();
    }

    public int getLeftDays() {
        return mLeftDays;
    }

    void copyValue(ItemDate d) {
        strTitle = d.title();
        mYear = d.year();
        mMonth = d.month();
        mDay = d.day();
        updateDaysLeft();
    }

    void updateDaysLeft() {
        Calendar c = Calendar.getInstance();
        long l = new Date(mYear, mMonth, mDay).getTime() - new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)).getTime();
        long dd = l / (24*60*60*1000);
        mLeftDays = (int)dd;
    }

    @Override
    public int compareTo(Object another) {
        ItemDate b = (ItemDate) another;
        return this.getLeftDays() - b.getLeftDays();
    }
}
