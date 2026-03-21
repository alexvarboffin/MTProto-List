package com.walhalla.mtprotoloader.services;//package com.walhalla.mtprotolist.services;

import static com.walhalla.mtprotolist.Config.KEY_TKT_LOADER;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.multidex.BuildConfig;


import com.walhalla.mtprotolist.Config;
import com.walhalla.mtprotoloader.R;
import com.walhalla.mtprotoloader.activity.LinkActivity;
import com.walhalla.mtprotoloader.manager.VideoRepository;
import com.walhalla.ui.DLog;

public class ClipboardMonitor extends Service
        implements VideoRepository.Callback {

    private static final String _APPLICATION_ID_ = "com.walhalla.mtprotoloader";
    //public static final String FILE_PROVIDER = PACKAGE_BASE + ".provider";
    public static final String START_FOREGROUND_ACTION = _APPLICATION_ID_ + ".action.startforeground";
    public static final String STOP_FOREGROUND_ACTION = _APPLICATION_ID_ + ".action.stopforeground";
    private static final int NOTIFICATION_ID = 1002;


    private ClipboardManager manager;
    IBinder mBinder;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    int mStartMode;

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (manager != null) {
            manager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent.getAction() == null) ? "" : intent.getAction();
        if (START_FOREGROUND_ACTION.equals(action)) {
            if (BuildConfig.DEBUG) {
                Toast.makeText(this, "START_FOREGROUND_ACTION", Toast.LENGTH_SHORT).show();
            }
            startInForeground0(getApplicationContext());
            //none showNotification(getApplicationContext());
        } else if (STOP_FOREGROUND_ACTION.equals(action)) {
            stopForegroundService();
        } else {
            DLog.d("Null pointer" + action);
        }
        return START_STICKY;
    }


    private void stopForegroundService() {
        DLog.d("Stop foreground service.");
        prefs = getSharedPreferences(KEY_TKT_LOADER, MODE_PRIVATE);

        editor = prefs.edit();
        editor.putBoolean(Config.KEY_CLIPBOARD_MONITOR, false);
        editor.apply();

        // Stop foreground service and remove the notification.
        stopForeground(true);
        stopSelf();// Stop the foreground service.
        manager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }

    private void startInForeground0(Context context) {
        Notification notification = createStartForegroundNotification();
        prefs = getSharedPreferences(KEY_TKT_LOADER, MODE_PRIVATE);
        editor = prefs.edit();
        startForeground(NOTIFICATION_ID, notification);
    }

    private Notification createStartForegroundNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = 0;
        }

        Intent notificationIntent = new Intent(this, LinkActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Auto Download Service")
                .setContentText("Copy the link of video to start download")
                .setTicker("TICKER")
                .addAction(R.drawable.navigation_empty_icon, "Stop", makePendingIntent(STOP_FOREGROUND_ACTION))
                .setContentIntent(pendingIntent);

//        if (Build.VERSION.SDK_INT >= 26) {
//            //NotificationChannel channel = new NotificationChannel(getPackageName() + "--" + getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("Tiktok Auto Download");
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized String createChannel() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {


        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = PendingIntent.FLAG_ONE_SHOT;
        }

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, flags);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
        // this.stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DLog.d("123123");
        stopForeground(true);
        stopSelf();
        if (manager != null) {
            manager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
        prefs = getSharedPreferences(KEY_TKT_LOADER, MODE_PRIVATE);

        if (prefs.getBoolean(Config.KEY_CLIPBOARD_MONITOR, false)) {
//                Intent broadcastIntent = new Intent();
//                broadcastIntent.setAction("restartservice");
//                broadcastIntent.setClass(this, Restarter.class);
//                this.sendBroadcast(broadcastIntent);
        }
    }

    public PendingIntent makePendingIntent(String name) {
        int flags;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            flags = 0;
        }
        Intent intent = new Intent(getApplicationContext(), ClipboardMonitor.class);
        intent.setAction(name);
        return PendingIntent.getService(getApplicationContext(), 0, intent, flags);
    }

    private final ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    ClipData mm0 = manager.getPrimaryClip();
                    String newClip;
                    if (mm0 != null) {
                        newClip = mm0.getItemAt(0).getText().toString();
                        //   Toast.makeText(getApplicationContext(), newClip, Toast.LENGTH_LONG).show();
                        DLog.d(newClip + "");

                        VideoRepository mm = new VideoRepository(ClipboardMonitor.this);
                        mm.makeDownload(getApplicationContext(), newClip, false, true);
                    }
                }
            };


    @Override
    public void successResult(String result) {
        //...
    }

    public static void startClipboardMonitor(Activity context) {
//        String act0 = context.getPackageName() + START_FOREGROUND_ACTION0;
//        Intent intent = new Intent(context, ClipboardMonitor.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            intent.setAction(act0);
//            try {
//                ComponentName service = context.startForegroundService(intent);//api 26
//            } catch (Exception e) {
//
//            }
//        } else {
//            try {
//                ComponentName service = context.startService(intent);
//            } catch (Exception e) {
//
//            }
//        }

        if (!ServiceHelper.isMyServiceRunning(context, ClipboardMonitor.class)) {
            Intent intent = new Intent(context, ClipboardMonitor.class);
            intent.setAction(START_FOREGROUND_ACTION);
            //intent.putExtra(Constants.EXTRA.PLAY_EXTRA, songList.get(position).file);
            //ForegroundService.IS_SERVICE_RUNNING = true;
            //button.setText("Stop Service");
            context.startService(intent);
        }
    }
}

