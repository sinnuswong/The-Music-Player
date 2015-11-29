package com.Project100Pi.themusicplayer;

/**
 * Created by Goutham Srinivas on 05-10-2015.
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String LOG = "de.vogella.android.widget.example";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        //    Log.w(LOG, "onUpdate method called");
        Toast.makeText(context,"WIthin update method", Toast.LENGTH_SHORT).show();
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Build the intent to call the service
        Intent intent = new Intent(context.getApplicationContext(),
                UpdateWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

     //   intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update the widgets via the service
        context.startService(intent);
   /*     if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (!PlayHelperFunctions.isSongPlaying) {
                        PlayHelperFunctions.pauseMusicPlayer();
                    } else {
                        PlayHelperFunctions.startMusicPlayer();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    PlayHelperFunctions.nextAction();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    PlayHelperFunctions.prevAction();
                    break;
            }
        } else {
            if (intent.getAction().equals(LockScreenNotification.NOTIFY_PLAY)) {
                PlayHelperFunctions.startMusicPlayer();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_PAUSE)) {
                PlayHelperFunctions.pauseMusicPlayer();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_NEXT)) {
                PlayHelperFunctions.nextAction();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_DELETE)) {
                Intent i = new Intent(context, LockScreenNotification.class);
                context.stopService(i);
                Intent in = new Intent(context, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_PREVIOUS)) {
                PlayHelperFunctions.prevAction();
            }


            // Update the widgets via the service
            context.startService(intent);
        }*/
    }
/*
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"WIthin Receive method", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(!PlayHelperFunctions.isSongPlaying){
                        PlayHelperFunctions.pauseMusicPlayer();
                    }else{
                        PlayHelperFunctions.startMusicPlayer();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    PlayHelperFunctions.nextAction();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    PlayHelperFunctions.prevAction();
                    break;
            }
        }  else{
            if (intent.getAction().equals(LockScreenNotification.NOTIFY_PLAY)) {
                PlayHelperFunctions.startMusicPlayer();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_PAUSE)) {
                PlayHelperFunctions.pauseMusicPlayer();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_NEXT)) {
                PlayHelperFunctions.nextAction();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_DELETE)) {
                Intent i = new Intent(context, LockScreenNotification.class);
                context.stopService(i);
                Intent in = new Intent(context, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
            }else if (intent.getAction().equals(LockScreenNotification.NOTIFY_PREVIOUS)) {
                PlayHelperFunctions.prevAction();
            }
        }
    }*/
}