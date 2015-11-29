package com.Project100Pi.themusicplayer;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.Timer;

/**
 * Created by Goutham Srinivas on 19-11-2015.
 */
public class CheckForegroundService extends Service {

    Handler layoutThreadHandler;
    Context context;
    boolean isNotificationServiceStarted=false;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // mp = new MediaPlayer();


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            context=getApplicationContext();
            startLayoutThread();
            Log.d("SERVICE", "Service Started");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public void startLayoutThread(){

        layoutThreadHandler=new Handler();
        Runnable updateNowPlayingLayoutThread = new Runnable() {
            public void run() {
                if(isAppOnForeground()==false){
                   if(isNotificationServiceStarted==false) {
                        Intent notificationIntent = new Intent(getApplicationContext(), LockScreenNotification.class);
                        startService(notificationIntent);
                        isNotificationServiceStarted=true;
                    }

                }

                layoutThreadHandler.postDelayed(this, 100);



            }
        };

        layoutThreadHandler.postDelayed(updateNowPlayingLayoutThread, 100);
    }

}
