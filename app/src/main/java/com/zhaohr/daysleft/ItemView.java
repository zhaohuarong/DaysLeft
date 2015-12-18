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

public class ItemView extends RelativeLayout {

    public int mIndex = -1;

    private ItemDate mDate;
    private TextView viewTitle = null;
    private TextView viewDays = null;
//    private LongPressRunnable longPressRunnable = new LongPressRunnable();
    Context mContext;
    MainActivity mMainActivity;

    public ItemView(Context context, MainActivity activity) {
        super(context);

        mContext = context;
        mMainActivity = activity;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parent = inflater.inflate(R.layout.item, this);

        viewTitle = (TextView)findViewById(R.id.itemTitle);
        viewDays = (TextView)findViewById(R.id.itemDays);
    }

    public void setDateInfo(ItemDate d) {
        mDate = d;
        updateView();
    }

    private void updateView() {
        viewTitle.setText(mDate.title());
        viewDays.setText("" + mDate.getLeftDays());
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

    private int count = 0;
    private boolean isDialogShow = false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isDialogShow = false;
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            count = 0;
        }
        if(!isDialogShow && count > 10) {
            isDialogShow = true;
            openPopMenu();
        } else {
            count++;
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