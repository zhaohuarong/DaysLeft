package com.zhaohr.daysleft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {

    private static final String TAG = "xxx";
    public static final int NOTIFICATION_ID = 0x000012;
    private static final int TIMER_PERIOD = 60 * 60 * 1000;
    private static final int TIMER_MSG = 0x000001;
//    private int oldDay = 0;
    Preferences mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Log.v(TAG, "ServiceDemo onBind");
        return new NotificationBinder();
    }

    @Override
    public void onCreate() {
        //Log.v(TAG, "ServiceDemo onCreate");
        super.onCreate();

//        oldDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        mp = new Preferences(MainService.this);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run () {
                Message message = new Message();
                message.what = TIMER_MSG;
                handler.sendMessage(message);
            }
        };

        timer.schedule(task, 1000, TIMER_PERIOD);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.v(TAG, "ServiceDemo --onStartCommand");
        //showNotification("aaaa", "bbbb");
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(String strTitle, int leftDays, String strDate) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        PendingIntent contentIndent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(MainService.this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIndent).setSmallIcon(R.mipmap.icon_0)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_0))
//                .setTicker("AAAAAAA")
//                .setWhen(System.currentTimeMillis())
//                .setAutoCancel(true)
                .setContentTitle(strTitle)
                .setContentText(getString(R.string.notification1) + " " + String.valueOf(leftDays) + " " + getString(R.string.notification2) + " " + strDate);
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;   //常驻任务栏
        notification.when = 0;  //不显示系统通知时间
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIMER_MSG:
                    //int newDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    //if(newDay != oldDay) {
                    //showNotification(mTitle, String.valueOf(mDaysLeft));
                    ArrayList<ItemDate> lstDate = mp.readDateList();
                    if(lstDate.size() > 0) {
                        ItemDate d = lstDate.get(0);
                        showNotification(d.title(), d.getLeftDays(), String.valueOf(d.year()) + "-" + String.valueOf(d.month()) + "-" + String.valueOf(d.day()));
                    }
                    //    oldDay = newDay;
                    //}
                    //Log.i("xxx", "alarm timeout");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void updateNotification(String strTitle, int daysLeft, String strDate) {
        showNotification(strTitle, daysLeft, strDate);
    }

    public class NotificationBinder extends Binder {
        public void updateNoti(String strTitle, int daysLeft, String strDate) {
            updateNotification(strTitle, daysLeft, strDate);
        }
    }
}
