/**
 * Date item
 * */

package com.zhaohr.daysleft;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.view.ViewConfiguration;

public class ItemView extends RelativeLayout {

    public int mIndex = -1;

    private ItemDate mDate;
    private RelativeLayout mainLayout = null;
    private TextView viewTitle = null;
    private TextView viewDays = null;
    private Context mContext;
    private MainActivity mMainActivity;
    private int mLastMotionX, mLastMotionY;
    private boolean isMoved;
    private Runnable mLongPressRunnable;
    private static final int TOUCH_SLOP = 20;

    public ItemView(Context context, MainActivity activity) {
        super(context);

        mContext = context;
        mMainActivity = activity;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.item, this);

        viewTitle = (TextView)findViewById(R.id.itemTitle);
        viewDays = (TextView)findViewById(R.id.itemDays);
        mainLayout = (RelativeLayout)findViewById(R.id.item_layout);

        mLongPressRunnable = new Runnable() {

            @Override
            public void run() {
                openPopMenu();
            }
        };
    }

    public void setDateInfo(ItemDate d) {
        mDate = d;
        updateView();
    }

    private void updateView() {
        viewTitle.setText(mDate.title());
        viewDays.setText("" + mDate.getLeftDays());
    }

    public void setBackgroundType(int type) {
        int resID = R.mipmap.item_bg_1;
        switch(type % 5) {
            case 0:
                resID = R.mipmap.item_bg_1;
                break;
            case 1:
                resID = R.mipmap.item_bg_2;
                break;
            case 2:
                resID = R.mipmap.item_bg_3;
                break;
            case 3:
                resID = R.mipmap.item_bg_4;
                break;
            case 4:
                resID = R.mipmap.item_bg_5;
                break;
        }
        mainLayout.setBackgroundResource(resID);
    }

    public int getLeftDays() {
        return mDate.getLeftDays();
    }

    public String getTitle() {
        return mDate.title();
    }

    public String getDateString() {
        return String.valueOf(mDate.year()) + "-" + String.valueOf(mDate.month()) + "-" + String.valueOf(mDate.day());
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                isMoved = false;
                postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoved) break;
                if(Math.abs(mLastMotionX-x) > TOUCH_SLOP || Math.abs(mLastMotionY-y) > TOUCH_SLOP) {
                    isMoved = true;
                    removeCallbacks(mLongPressRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                removeCallbacks(mLongPressRunnable);
                break;
        }
        return true;
    }

    private void openPopMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(getResources().getStringArray(R.array.item_pop_menu), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case 0: {
                        //modify
                        final DateDialog dialog1 = new DateDialog(mMainActivity);
                        dialog1.setTitle(mDate.title());
                        dialog1.setDate(mDate.year(), mDate.month(), mDate.day());
                        dialog1.show();
                        dialog1.setOnPositiveListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String strTitle = dialog1.getTitle();
                                int y = dialog1.getYear();
                                int m = dialog1.getMonth();
                                int d = dialog1.getDay();
                                Message msg = new Message();
                                msg.what = MainActivity.ITEM_MODIFY;
                                Bundle bundle = new Bundle();
                                bundle.putInt(MainActivity.INDEX, mIndex);
                                bundle.putString(MainActivity.TITLE, strTitle);
                                bundle.putInt(MainActivity.YEAR, y);
                                bundle.putInt(MainActivity.MONTH, m);
                                bundle.putInt(MainActivity.DAY, d);
                                msg.setData(bundle);
                                mMainActivity.getHandler().sendMessage(msg);
                                dialog1.dismiss();
                            }
                        });
                        dialog1.setOnNegativeListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        break;
                    } case 1: {
                        //delete
                        Message msg = new Message();
                        msg.what = MainActivity.ITEM_DELETE;
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", mIndex);
                        msg.setData(bundle);
                        mMainActivity.getHandler().sendMessage(msg);
                        break;
                    }
                }
            }
        }).show();
    }
}