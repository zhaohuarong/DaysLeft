/**
 * MainActivity
 * */

package com.zhaohr.daysleft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    LinearLayout mainLayout = null;
    ScrollView contentView = null;
    public static final int MSG_NewDate = 3;
    public static final String TITLE = "title";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String INDEX = "index";

    private NotiServiceConnection conn;
    private MainService.NotificationBinder mBinder = null;
    Preferences mp = null;
    public final static int ITEM_DELETE = 1;
    public final static int ITEM_MODIFY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mp = new Preferences(MainActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.msg1, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mainLayout = (LinearLayout)findViewById(R.id.content_layout);
        contentView = (ScrollView)findViewById(R.id.content_view);

//        showNotification("aaa", "bbb");
        Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
        startService(serviceIntent);

        conn = new NotiServiceConnection();
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);

        updateView();
    }

    public Handler getHandler() {
        return mHandler;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NewDate: {
                    Bundle bundle = msg.getData();
                    String strTitle = bundle.getString(TITLE, "");
                    int year = bundle.getInt(YEAR, 2000);
                    int month = bundle.getInt(MONTH, 1);
                    int day = bundle.getInt(DAY, 1);
                    mp.appendDate(new ItemDate(strTitle, year, month, day));
                    updateView();
                    break;
                }
                case ITEM_DELETE: {
                    int itemIndex = msg.getData().getInt("index");
                    mp.deleteDate(itemIndex);
                    updateView();
                    break;
                }
                case ITEM_MODIFY: {
                    Bundle bundle = msg.getData();
                    String strTitle = bundle.getString(TITLE);
                    int index = bundle.getInt(INDEX);
                    int year = bundle.getInt(YEAR);
                    int month = bundle.getInt(MONTH);
                    int day = bundle.getInt(DAY);
                    mp.modifyDate(index, new ItemDate(strTitle, year, month, day));
                    updateView();
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
//            conn.updateNoti("xxxx", 5);
            return true;
        } else if(id == R.id.action_new) {
            Calendar c = Calendar.getInstance();
            openDateDialog("", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openDateDialog(String strTitle, int nYear, int nMonth, int nDay) {
        final DateDialog dialog = new DateDialog(MainActivity.this);
        dialog.setTitle(strTitle);
        dialog.setDate(nYear, nMonth, nDay);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTitle = dialog.getTitle();
                int y = dialog.getYear();
                int m = dialog.getMonth();
                int d = dialog.getDay();
                Message msg = new Message();
                msg.what = MSG_NewDate;
                Bundle bundle = new Bundle();
                bundle.putString(TITLE, strTitle);
                bundle.putInt(YEAR, y);
                bundle.putInt(MONTH, m);
                bundle.putInt(DAY, d);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateView() {
        //clear all
        mainLayout.removeAllViews();

        //add items
        ArrayList<ItemDate> lstDate = mp.readDateList();
        ArrayList<ItemView> lstView = new ArrayList<ItemView>();
        for(int i = 0; i < lstDate.size(); i ++) {
            ItemDate d = lstDate.get(i);
            ItemView r = new ItemView(MainActivity.this, this);
            r.mIndex = i;
            r.setDateInfo(d);
            lstView.add(r);
            mainLayout.addView(r);
        }

        if(lstView.size() > 0) {
            ItemView v = lstView.get(0);
            String title = v.getTitle();
            int daysLeft = v.getLeftDays();
            String strDate = v.getDateString();
            Log.i("xxx", "--" + title + daysLeft);
            //conn.updateNoti(title, daysLeft);
            showNotification(title, daysLeft, strDate);
        }
    }

    private void showNotification(String strTitle, int leftDays, String strDate) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        PendingIntent contentIndent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(MainActivity.this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIndent) .setSmallIcon(R.mipmap.icon_0)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_0))
//                .setTicker("AAAAAAA")
//                .setWhen(System.currentTimeMillis())
//                .setAutoCancel(true)
                .setContentTitle(strTitle)
                .setContentText(getString(R.string.notification1) + " " + String.valueOf(leftDays) + " " + getString(R.string.notification2) + " " + strDate);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;   //常驻任务栏
        notification.when = 0;  //不显示系统通知时间
        notificationManager.notify(MainService.NOTIFICATION_ID, notification);
    }

    public class NotiServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MainService.NotificationBinder) service;
        }

        public void updateNoti(String strTitle, int daysLeft, String strDate) {
            if(mBinder != null)
                mBinder.updateNoti(strTitle, daysLeft, strDate);
            else
                Log.i("xxx", "mBinder = null");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
